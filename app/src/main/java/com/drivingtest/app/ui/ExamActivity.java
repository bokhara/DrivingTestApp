package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

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
    
    private List<Question> examQuestions;
    private int currentIndex = 0;
    private int correctCount = 0;
    private long timeRemaining = 45 * 60 * 1000; // 45 minutes
    private CountDownTimer timer;
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        
        database = AppDatabase.getInstance(this);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("模拟考试");
        
        tvTimer = findViewById(R.id.tvTimer);
        tvProgress = findViewById(R.id.tvProgress);
        
        // Load 100 random questions for exam
        loadExamQuestions();
        
        // Start timer
        startTimer();
    }
    
    private void loadExamQuestions() {
        new Thread(() -> {
            examQuestions = database.questionDao().getRandomQuestions(100);
            runOnUiThread(() -> {
                if (!examQuestions.isEmpty()) {
                    showQuestion(0);
                }
            });
        }).start();
    }
    
    private void showQuestion(int index) {
        currentIndex = index;
        tvProgress.setText(String.format("%d / 100", index + 1));
        
        // Similar to PracticeActivity, show question
        // For brevity, implementation is simplified
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
