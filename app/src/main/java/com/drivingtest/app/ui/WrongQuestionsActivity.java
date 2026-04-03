package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;
import com.drivingtest.app.model.Question;

import java.util.ArrayList;
import java.util.List;

public class WrongQuestionsActivity extends AppCompatActivity {
    private final List<Question> questions = new ArrayList<>();
    private final List<Question> filteredQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("错题本");

        ListView listView = findViewById(R.id.listWrongQuestions);
        Button btnFilterAll = findViewById(R.id.btnFilterAll);
        Button btnFilterImage = findViewById(R.id.btnFilterImage);
        new Thread(() -> {
            questions.addAll(AppDatabase.getInstance(this).questionDao().getWrongQuestions());
            runOnUiThread(() -> {
                filteredQuestions.clear();
                filteredQuestions.addAll(questions);
                refreshList(listView, filteredQuestions);
                btnFilterAll.setOnClickListener(v -> {
                    filteredQuestions.clear();
                    filteredQuestions.addAll(questions);
                    refreshList(listView, filteredQuestions);
                });
                btnFilterImage.setOnClickListener(v -> {
                    filteredQuestions.clear();
                    for (Question q : questions) {
                        if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) filteredQuestions.add(q);
                    }
                    refreshList(listView, filteredQuestions);
                });
                listView.setOnItemClickListener((p, v, pos, id) -> {
                    Intent intent = new Intent(this, PracticeActivity.class);
                    intent.putExtra("mode", "wrong");
                    startActivity(intent);
                });
            });
        }).start();
    }

    private void refreshList(ListView listView, List<Question> src) {
        List<String> titles = new ArrayList<>();
        for (Question q : src) {
            String suffix = (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) ? " [图片题]" : "";
            titles.add(q.getQuestion() + suffix);
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
