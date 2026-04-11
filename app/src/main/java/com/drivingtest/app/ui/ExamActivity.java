package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.model.Question;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {
    
    private TextView tvTimer;
    private TextView tvProgress;
    private TextView tvQuestion;
    private ImageView ivQuestionImage;
    private RadioGroup radioGroupOptions;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private Button btnSubmit;
    
    private List<Question> examQuestions;
    private int currentIndex = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private long timeRemaining = 45 * 60 * 1000; // 45 minutes
    private CountDownTimer timer;
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        
        database = AppDatabase.getInstance(this);
        
        initViews();
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("模拟考试");
        }
        
        // Load 100 random questions for exam
        loadExamQuestions();
        
        // Start timer
        startTimer();
    }
    
    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        btnSubmit = findViewById(R.id.btnSubmit);
        
        // 手动处理 RadioButton 选中逻辑
        View.OnClickListener optionListener = v -> {
            int id = v.getId();
            rbOptionA.setChecked(id == R.id.rbOptionA);
            rbOptionB.setChecked(id == R.id.rbOptionB);
            rbOptionC.setChecked(id == R.id.rbOptionC);
            rbOptionD.setChecked(id == R.id.rbOptionD);
        };
        
        rbOptionA.setOnClickListener(optionListener);
        rbOptionB.setOnClickListener(optionListener);
        rbOptionC.setOnClickListener(optionListener);
        rbOptionD.setOnClickListener(optionListener);
        
        btnSubmit.setOnClickListener(v -> checkAndNext());
    }
    
    private void loadExamQuestions() {
        new Thread(() -> {
            examQuestions = database.questionDao().getRandomQuestions(100);
            runOnUiThread(() -> {
                if (!examQuestions.isEmpty()) {
                    showQuestion(0);
                } else {
                    Toast.makeText(this, "题目加载失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }
    
    private void showQuestion(int index) {
        if (index >= examQuestions.size()) {
            finishExam();
            return;
        }
        
        currentIndex = index;
        Question question = examQuestions.get(index);
        
        tvProgress.setText(String.format("%d / 100", index + 1));
        tvQuestion.setText(question.getQuestion());
        
        // Load image if available
        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(question.getImageUrl()).into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }
        
        // Update options
        rbOptionA.setText("A. " + question.getOptionA());
        rbOptionB.setText("B. " + question.getOptionB());
        
        if (question.getOptionC() != null && !question.getOptionC().isEmpty()) {
            rbOptionC.setVisibility(View.VISIBLE);
            rbOptionC.setText("C. " + question.getOptionC());
        } else {
            rbOptionC.setVisibility(View.GONE);
        }
        
        if (question.getOptionD() != null && !question.getOptionD().isEmpty()) {
            rbOptionD.setVisibility(View.VISIBLE);
            rbOptionD.setText("D. " + question.getOptionD());
        } else {
            rbOptionD.setVisibility(View.GONE);
        }
        
        // Reset selections
        rbOptionA.setChecked(false);
        rbOptionB.setChecked(false);
        rbOptionC.setChecked(false);
        rbOptionD.setChecked(false);
        
        if (index == examQuestions.size() - 1) {
            btnSubmit.setText("交卷");
        } else {
            btnSubmit.setText("下一题");
        }
    }
    
    private void checkAndNext() {
        int selectedId = -1;
        if (rbOptionA.isChecked()) selectedId = R.id.rbOptionA;
        else if (rbOptionB.isChecked()) selectedId = R.id.rbOptionB;
        else if (rbOptionC.isChecked()) selectedId = R.id.rbOptionC;
        else if (rbOptionD.isChecked()) selectedId = R.id.rbOptionD;
        
        if (selectedId == -1) {
            Toast.makeText(this, "请选择答案", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Question question = examQuestions.get(currentIndex);
        String selectedAnswer = "";
        if (selectedId == R.id.rbOptionA) selectedAnswer = "A";
        else if (selectedId == R.id.rbOptionB) selectedAnswer = "B";
        else if (selectedId == R.id.rbOptionC) selectedAnswer = "C";
        else if (selectedId == R.id.rbOptionD) selectedAnswer = "D";
        
        boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
        if (isCorrect) {
            correctCount++;
        } else {
            wrongCount++;
            // 记录错题
            new Thread(() -> {
                database.questionDao().markAsWrong(question.getId(), System.currentTimeMillis());
            }).start();
        }
        
        // 【核心修复】：记录答题进度
        new Thread(() -> {
            database.questionDao().incrementPracticeCount(question.getId());
        }).start();
        
        // Go to next
        showQuestion(currentIndex + 1);
    }
    
    private void startTimer() {
        timer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }
            
            @Override
            public void onFinish() {
                finishExam();
            }
        }.start();
    }
    
    private void finishExam() {
        if (timer != null) {
            timer.cancel();
        }
        
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", correctCount);
        intent.putExtra("total", 100);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
