package com.drivingtest.app.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Question {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String question;           // 题目内容
    private String optionA;            // 选项A
    private String optionB;            // 选项B
    private String optionC;            // 选项C
    private String optionD;            // 选项D
    private String correctAnswer;      // 正确答案 (A/B/C/D)
    private String explanation;        // 答案解析
    private String imageUrl;           // 图片题 URL（第三方网站图片）
    private String category;           // 分类 (交通法规/交通信号/安全驾驶等)
    private String type;               // 题型 (单选题/多选题/判断题)
    private int difficulty;            // 难度 (1-3)
    private boolean isWrong;           // 是否错题
    private int wrongCount;            // 错误次数
    private long lastWrongTime;        // 最后错误时间
    private boolean isFavorite;        // 是否收藏
    private int practiceCount;         // 练习次数
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    
    public boolean isWrong() { return isWrong; }
    public void setWrong(boolean wrong) { isWrong = wrong; }
    
    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }
    
    public long getLastWrongTime() { return lastWrongTime; }
    public void setLastWrongTime(long lastWrongTime) { this.lastWrongTime = lastWrongTime; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    
    public int getPracticeCount() { return practiceCount; }
    public void setPracticeCount(int practiceCount) { this.practiceCount = practiceCount; }
}
