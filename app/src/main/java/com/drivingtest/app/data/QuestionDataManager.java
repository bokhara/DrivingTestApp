package com.drivingtest.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.drivingtest.app.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuestionDataManager {
    
    private static final String PREFS_NAME = "QuestionDataPrefs";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final String KEY_QUESTION_VERSION = "question_version";
    
    private Context context;
    private SharedPreferences prefs;
    private AppDatabase database;
    
    public QuestionDataManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.database = AppDatabase.getInstance(context);
    }
    
    // 从 assets 加载初始题目数据
    public void loadInitialQuestions() {
        if (getQuestionCount() == 0) {
            try {
                String json = loadJSONFromAsset("questions.json");
                if (json != null) {
                    List<Question> questions = parseQuestions(json);
                    database.questionDao().insertAll(questions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // 从 assets 读取 JSON 文件
    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
    
    // 解析 JSON 数据
    private List<Question> parseQuestions(String json) throws JSONException {
        List<Question> questions = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Question question = new Question();
            
            question.setQuestion(obj.getString("question"));
            question.setOptionA(obj.optString("optionA", ""));
            question.setOptionB(obj.optString("optionB", ""));
            question.setOptionC(obj.optString("optionC", ""));
            question.setOptionD(obj.optString("optionD", ""));
            question.setCorrectAnswer(obj.getString("correctAnswer"));
            question.setExplanation(obj.optString("explanation", ""));
            question.setImageUrl(obj.optString("imageUrl", ""));
            question.setCategory(obj.getString("category"));
            question.setType(obj.optString("type", "单选题"));
            question.setDifficulty(obj.optInt("difficulty", 1));
            question.setWrong(false);
            question.setWrongCount(0);
            question.setFavorite(false);
            question.setPracticeCount(0);
            
            questions.add(question);
        }
        
        return questions;
    }
    
    // 检查是否需要更新题库
    public boolean shouldUpdateQuestions() {
        long lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0);
        long currentTime = System.currentTimeMillis();
        // 每7天检查一次更新
        return (currentTime - lastUpdate) > (7 * 24 * 60 * 60 * 1000);
    }
    
    // 更新题库（从服务器下载新题目）
    public void updateQuestions(String jsonData) throws JSONException {
        List<Question> newQuestions = parseQuestions(jsonData);
        
        // 保留用户的错题和收藏记录
        List<Question> oldWrongQuestions = database.questionDao().getWrongQuestions();
        List<Question> oldFavoriteQuestions = database.questionDao().getFavoriteQuestions();
        
        // 清空并插入新题目
        database.questionDao().deleteAll();
        database.questionDao().insertAll(newQuestions);
        
        // 恢复错题和收藏标记
        for (Question oldQ : oldWrongQuestions) {
            // 根据题目内容匹配恢复标记
            // 这里简化处理，实际应该使用更稳定的ID匹配
        }
        
        // 保存更新时间
        prefs.edit().putLong(KEY_LAST_UPDATE, System.currentTimeMillis()).apply();
    }
    
    // 获取题目数量
    public int getQuestionCount() {
        return database.questionDao().getQuestionCount();
    }
    
    // 获取当前题库版本
    public int getQuestionVersion() {
        return prefs.getInt(KEY_QUESTION_VERSION, 1);
    }
    
    // 设置题库版本
    public void setQuestionVersion(int version) {
        prefs.edit().putInt(KEY_QUESTION_VERSION, version).apply();
    }
}
