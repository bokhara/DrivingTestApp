package com.drivingtest.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.drivingtest.app.R;
import com.drivingtest.app.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private ListView listView;
    private final List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("分类练习");

        listView = findViewById(R.id.listCategories);

        new Thread(() -> {
            categories.addAll(AppDatabase.getInstance(this).questionDao().getAllCategories());
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((p, v, pos, id) -> {
                    Intent intent = new Intent(this, PracticeActivity.class);
                    intent.putExtra("mode", "category");
                    intent.putExtra("category", categories.get(pos));
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
