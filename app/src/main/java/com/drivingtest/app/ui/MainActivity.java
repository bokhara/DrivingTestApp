package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.data.QuestionDataManager;
import com.drivingtest.app.data.StudyStatsManager;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvTotalQuestions;
    private TextView tvWrongQuestions;
    private TextView tvFavoriteQuestions;
    private TextView tvStudyStreak;
    private TextView tvProgressLabel;
    private TextView tvAccuracy;
    private ProgressBar progressStudy;
    private MaterialCardView cardSequential;
    private MaterialCardView cardRandom;
    private MaterialCardView cardWrong;
    private MaterialCardView cardFavorite;
    private MaterialCardView cardExam;
    private MaterialCardView cardCategory;
    private MaterialButton btnQuickStart;
    
    private AppDatabase database;
    private QuestionDataManager dataManager;
    private StudyStatsManager studyStatsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize database and data manager
        database = AppDatabase.getInstance(this);
        dataManager = new QuestionDataManager(this);
        studyStatsManager = new StudyStatsManager(this);
        
        // Initialize views
        initViews();
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Set click listeners
        setupClickListeners();
        
        // Load initial questions if needed
        dataManager.loadInitialQuestions();
        
        // Update statistics
        updateStatistics();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }
    
    private void initViews() {
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvWrongQuestions = findViewById(R.id.tvWrongQuestions);
        tvFavoriteQuestions = findViewById(R.id.tvFavoriteQuestions);
        tvStudyStreak = findViewById(R.id.tvStudyStreak);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        progressStudy = findViewById(R.id.progressStudy);
        cardSequential = findViewById(R.id.cardSequential);
        cardRandom = findViewById(R.id.cardRandom);
        cardWrong = findViewById(R.id.cardWrong);
        cardFavorite = findViewById(R.id.cardFavorite);
        cardExam = findViewById(R.id.cardExam);
        cardCategory = findViewById(R.id.cardCategory);
        btnQuickStart = findViewById(R.id.btnQuickStart);
    }
    
    private void setupClickListeners() {
        cardSequential.setOnClickListener(v -> startPractice("sequential", null));
        cardRandom.setOnClickListener(v -> startPractice("random", null));
        cardWrong.setOnClickListener(v -> startActivity(new Intent(this, WrongQuestionsActivity.class)));
        cardFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteQuestionsActivity.class)));
        cardExam.setOnClickListener(v -> startExam());
        cardCategory.setOnClickListener(v -> showCategories());
        btnQuickStart.setOnClickListener(v -> {
            if (tvWrongQuestions.getText() != null && !"0".contentEquals(tvWrongQuestions.getText())) {
                startPractice("wrong", null);
            } else {
                startPractice("random", null);
            }
        });
    }
    
    private void startPractice(String mode, String category) {
        Intent intent = new Intent(this, PracticeActivity.class);
        intent.putExtra("mode", mode);
        if (category != null) {
            intent.putExtra("category", category);
        }
        startActivity(intent);
    }
    
    private void startExam() {
        Intent intent = new Intent(this, ExamActivity.class);
        startActivity(intent);
    }
    
    private void showCategories() {
        startActivity(new Intent(this, CategoryActivity.class));
    }
    
    private void updateStatistics() {
        new Thread(() -> {
            int total = database.questionDao().getQuestionCount();
            int wrong = database.questionDao().getWrongQuestionCount();
            int favorite = database.questionDao().getFavoriteQuestionCount();
            int practiced = total - wrong;
            int progress = total == 0 ? 0 : Math.min(100, (practiced * 100 / total));
            
            runOnUiThread(() -> {
                tvTotalQuestions.setText(String.valueOf(total));
                tvWrongQuestions.setText(String.valueOf(wrong));
                tvFavoriteQuestions.setText(String.valueOf(favorite));
                tvStudyStreak.setText("连续 " + studyStatsManager.getStreak() + " 天");
                tvProgressLabel.setText("已练习 " + practiced + " / " + total + " · 最近模考 " + studyStatsManager.getLastExamScore() + " 分");
                tvAccuracy.setText("正确率 " + studyStatsManager.getAccuracy() + "% · 累计答题 " + studyStatsManager.getTotalAnswered());
                progressStudy.setProgress(progress);
            });
        }).start();
    }
}
