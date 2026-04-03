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
    private static final String KEY_TOTAL_ANSWERED = "total_answered";
    private static final String KEY_TOTAL_CORRECT = "total_correct";

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

    public void recordAnswer(boolean correct) {
        int totalAnswered = prefs.getInt(KEY_TOTAL_ANSWERED, 0) + 1;
        int totalCorrect = prefs.getInt(KEY_TOTAL_CORRECT, 0) + (correct ? 1 : 0);
        prefs.edit().putInt(KEY_TOTAL_ANSWERED, totalAnswered).putInt(KEY_TOTAL_CORRECT, totalCorrect).apply();
    }

    public int getAccuracy() {
        int totalAnswered = prefs.getInt(KEY_TOTAL_ANSWERED, 0);
        int totalCorrect = prefs.getInt(KEY_TOTAL_CORRECT, 0);
        if (totalAnswered == 0) return 0;
        return (totalCorrect * 100) / totalAnswered;
    }

    public int getTotalAnswered() {
        return prefs.getInt(KEY_TOTAL_ANSWERED, 0);
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
