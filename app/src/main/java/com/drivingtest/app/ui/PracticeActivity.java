package com.drivingtest.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PracticeActivity extends AppCompatActivity {
    
    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private TextView tvTagType;
    private TextView tvTagCategory;
    private TextView tvTagImage;
    private ImageView ivQuestionImage;
    private RadioGroup radioGroupOptions;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private Button btnSubmit;
    private Button btnNext;
    private CardView cardExplanation;
    private TextView tvExplanation;
    private ImageButton btnFavorite;
    private LinearLayout layoutOptions;
    
    private List<Question> questions;
    private int currentIndex = 0;
    private boolean isAnswered = false;
    private AppDatabase database;
    private String practiceMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        
        // Initialize database
        database = AppDatabase.getInstance(this);
        
        // Get practice mode from intent
        practiceMode = getIntent().getStringExtra("mode");
        String category = getIntent().getStringExtra("category");
        
        // Initialize views
        initViews();
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getModeTitle());
        
        // Load questions
        loadQuestions(category);
        
        // Show first question
        if (!questions.isEmpty()) {
            showQuestion(0);
        } else {
            Toast.makeText(this, "暂无题目", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTagType = findViewById(R.id.tvTagType);
        tvTagCategory = findViewById(R.id.tvTagCategory);
        tvTagImage = findViewById(R.id.tvTagImage);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        cardExplanation = findViewById(R.id.cardExplanation);
        tvExplanation = findViewById(R.id.tvExplanation);
        btnFavorite = findViewById(R.id.btnFavorite);
        layoutOptions = findViewById(R.id.layoutOptions);
        
        btnSubmit.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }
    
    private String getModeTitle() {
        switch (practiceMode) {
            case "sequential": return "顺序练习";
            case "random": return "随机练习";
            case "wrong": return "错题练习";
            case "favorite": return "收藏练习";
            case "category": return "分类练习";
            default: return "练习";
        }
    }
    
    private void loadQuestions(String category) {
        questions = new ArrayList<>();
        
        new Thread(() -> {
            switch (practiceMode) {
                case "sequential":
                    questions.addAll(database.questionDao().getAllQuestions());
                    break;
                case "random":
                    questions.addAll(database.questionDao().getRandomQuestions(100));
                    break;
                case "wrong":
                    questions.addAll(database.questionDao().getWrongQuestions());
                    break;
                case "favorite":
                    questions.addAll(database.questionDao().getFavoriteQuestions());
                    break;
                case "category":
                    if (category != null) {
                        questions.addAll(database.questionDao().getQuestionsByCategory(category));
                    }
                    break;
            }
            
            // Shuffle for random mode
            if ("random".equals(practiceMode)) {
                Collections.shuffle(questions);
            }
            
            runOnUiThread(() -> {
                if (!questions.isEmpty()) {
                    showQuestion(0);
                }
            });
        }).start();
    }
    
    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        
        currentIndex = index;
        Question question = questions.get(index);
        isAnswered = false;
        
        // Update question number
        tvQuestionNumber.setText(String.format("%d / %d", index + 1, questions.size()));
        
        // Update question text
        tvQuestion.setText(question.getQuestion());
        tvTagType.setText(question.getType());
        tvTagCategory.setText(question.getCategory());
        
        // Load third-party image when available
        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            tvTagImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(question.getImageUrl()).into(ivQuestionImage);
        } else {
            ivQuestionImage.setVisibility(View.GONE);
            tvTagImage.setVisibility(View.GONE);
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
        
        // Reset radio group
        radioGroupOptions.clearCheck();
        radioGroupOptions.setEnabled(true);
        
        // Hide explanation
        cardExplanation.setVisibility(View.GONE);
        
        // Update buttons
        btnSubmit.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(true);
        btnNext.setVisibility(View.GONE);
        
        // Update favorite button
        updateFavoriteButton(question.isFavorite());
        
        // Increment practice count
        new Thread(() -> {
            database.questionDao().incrementPracticeCount(question.getId());
        }).start();
    }
    
    private void checkAnswer() {
        if (isAnswered) return;
        
        int selectedId = radioGroupOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "请选择答案", Toast.LENGTH_SHORT).show();
            return;
        }
        
        isAnswered = true;
        Question question = questions.get(currentIndex);
        
        String selectedAnswer = "";
        if (selectedId == R.id.rbOptionA) selectedAnswer = "A";
        else if (selectedId == R.id.rbOptionB) selectedAnswer = "B";
        else if (selectedId == R.id.rbOptionC) selectedAnswer = "C";
        else if (selectedId == R.id.rbOptionD) selectedAnswer = "D";
        
        boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
        
        // Show correct/wrong indication
        RadioButton selectedRadio = findViewById(selectedId);
        if (isCorrect) {
            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            selectedRadio.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            // Highlight correct answer
            RadioButton correctRadio = null;
            switch (question.getCorrectAnswer()) {
                case "A": correctRadio = rbOptionA; break;
                case "B": correctRadio = rbOptionB; break;
                case "C": correctRadio = rbOptionC; break;
                case "D": correctRadio = rbOptionD; break;
            }
            if (correctRadio != null) {
                correctRadio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
            
            // Mark as wrong in database
            new Thread(() -> {
                database.questionDao().markAsWrong(question.getId(), System.currentTimeMillis());
            }).start();
        }
        
        // Disable radio group
        radioGroupOptions.setEnabled(false);
        
        // Show explanation
        tvExplanation.setText("答案解析：\n" + question.getExplanation());
        cardExplanation.setVisibility(View.VISIBLE);
        
        // Update buttons
        btnSubmit.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        
        if (currentIndex == questions.size() - 1) {
            btnNext.setText("完成");
        }
    }
    
    private void nextQuestion() {
        if (currentIndex < questions.size() - 1) {
            showQuestion(currentIndex + 1);
        } else {
            finish();
        }
    }
    
    private void toggleFavorite() {
        Question question = questions.get(currentIndex);
        boolean newFavorite = !question.isFavorite();
        question.setFavorite(newFavorite);
        
        new Thread(() -> {
            database.questionDao().setFavorite(question.getId(), newFavorite);
        }).start();
        
        updateFavoriteButton(newFavorite);
        
        Toast.makeText(this, newFavorite ? "已收藏" : "取消收藏", Toast.LENGTH_SHORT).show();
    }
    
    private void updateFavoriteButton(boolean isFavorite) {
        btnFavorite.setImageResource(isFavorite ? 
            R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
