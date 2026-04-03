package com.drivingtest.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.drivingtest.app.model.Question;

@Database(entities = {Question.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "driving_test_db";
    private static AppDatabase instance;
    
    public abstract QuestionDao questionDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
