package com.drivingtest.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudyStatsManager {
    private static final String PREF = "study_stats";
    private static final String KEY_LAST_STUDY_DATE = "last_study_date";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_LAST_EXAM_SCORE = "last_exam_score";

    private final SharedPreferences prefs;

    public StudyStatsManager(Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void markStudiedToday() {
        String today = formatDate(new Date());
        String last = prefs.getString(KEY_LAST_STUDY_DATE, "");
        int streak = prefs.getInt(KEY_STREAK, 0);
        if (!today.equals(last)) {
            streak += 1;
            prefs.edit().putString(KEY_LAST_STUDY_DATE, today).putInt(KEY_STREAK, streak).apply();
        }
    }

    public int getStreak() {
        return prefs.getInt(KEY_STREAK, 0);
    }

    public void setLastExamScore(int score) {
        prefs.edit().putInt(KEY_LAST_EXAM_SCORE, score).apply();
    }

    public int getLastExamScore() {
        return prefs.getInt(KEY_LAST_EXAM_SCORE, 0);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }
}
