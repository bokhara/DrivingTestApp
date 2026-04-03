package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.model.Question;

import java.util.ArrayList;
import java.util.List;

public class FavoriteQuestionsActivity extends AppCompatActivity {
    private final List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的收藏");

        ListView listView = findViewById(R.id.listFavoriteQuestions);
        new Thread(() -> {
            questions.addAll(AppDatabase.getInstance(this).questionDao().getFavoriteQuestions());
            runOnUiThread(() -> {
                List<String> titles = new ArrayList<>();
                for (Question q : questions) titles.add(q.getQuestion());
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
                listView.setOnItemClickListener((p, v, pos, id) -> {
                    Intent intent = new Intent(this, PracticeActivity.class);
                    intent.putExtra("mode", "favorite");
                    startActivity(intent);
                });
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
