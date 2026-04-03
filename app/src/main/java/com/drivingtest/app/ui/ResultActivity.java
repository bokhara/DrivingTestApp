package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;

public class ResultActivity extends AppCompatActivity {
    
    private TextView tvScore;
    private TextView tvResult;
    private TextView tvCorrect;
    private TextView tvWrong;
    private Button btnReview;
    private Button btnBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("考试结果");
        
        tvScore = findViewById(R.id.tvScore);
        tvResult = findViewById(R.id.tvResult);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        btnReview = findViewById(R.id.btnReview);
        btnBack = findViewById(R.id.btnBack);
        
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 100);
        
        displayResult(score, total);
        
        btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, PracticeActivity.class);
            intent.putExtra("mode", "wrong");
            startActivity(intent);
        });
        
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void displayResult(int score, int total) {
        int percentage = (score * 100) / total;
        
        tvScore.setText(String.format("%d分", percentage));
        tvCorrect.setText(String.format("答对：%d题", score));
        tvWrong.setText(String.format("答错：%d题", total - score));
        
        if (percentage >= 90) {
            tvResult.setText("恭喜通过！");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvResult.setText("未通过，继续加油！");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
