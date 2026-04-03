package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.data.QuestionDataManager;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    
    private TextView tvTotalQuestions;
    private TextView tvWrongQuestions;
    private TextView tvFavoriteQuestions;
    private MaterialCardView cardSequential;
    private MaterialCardView cardRandom;
    private MaterialCardView cardWrong;
    private MaterialCardView cardFavorite;
    private MaterialCardView cardExam;
    private MaterialCardView cardCategory;
    
    private AppDatabase database;
    private QuestionDataManager dataManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize database and data manager
        database = AppDatabase.getInstance(this);
        dataManager = new QuestionDataManager(this);
        
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
        cardSequential = findViewById(R.id.cardSequential);
        cardRandom = findViewById(R.id.cardRandom);
        cardWrong = findViewById(R.id.cardWrong);
        cardFavorite = findViewById(R.id.cardFavorite);
        cardExam = findViewById(R.id.cardExam);
        cardCategory = findViewById(R.id.cardCategory);
    }
    
    private void setupClickListeners() {
        cardSequential.setOnClickListener(v -> startPractice("sequential", null));
        cardRandom.setOnClickListener(v -> startPractice("random", null));
        cardWrong.setOnClickListener(v -> startPractice("wrong", null));
        cardFavorite.setOnClickListener(v -> startPractice("favorite", null));
        cardExam.setOnClickListener(v -> startExam());
        cardCategory.setOnClickListener(v -> showCategories());
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
        // Show category selection dialog
        new Thread(() -> {
            // Load categories from database
            // Show dialog on UI thread
            runOnUiThread(() -> {
                // For now, just start practice with a default category
                startPractice("category", "交通法规");
            });
        }).start();
    }
    
    private void updateStatistics() {
        new Thread(() -> {
            int total = database.questionDao().getQuestionCount();
            int wrong = database.questionDao().getWrongQuestionCount();
            int favorite = database.questionDao().getFavoriteQuestionCount();
            
            runOnUiThread(() -> {
                tvTotalQuestions.setText(String.valueOf(total));
                tvWrongQuestions.setText(String.valueOf(wrong));
                tvFavoriteQuestions.setText(String.valueOf(favorite));
            });
        }).start();
    }
}
