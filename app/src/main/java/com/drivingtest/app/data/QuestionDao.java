package com.drivingtest.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.drivingtest.app.model.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    
    @Query("SELECT * FROM questions")
    List<Question> getAllQuestions();
    
    @Query("SELECT * FROM questions WHERE category = :category")
    List<Question> getQuestionsByCategory(String category);
    
    @Query("SELECT * FROM questions WHERE isWrong = 1 ORDER BY wrongCount DESC")
    List<Question> getWrongQuestions();
    
    @Query("SELECT * FROM questions WHERE isFavorite = 1")
    List<Question> getFavoriteQuestions();
    
    @Query("SELECT * FROM questions WHERE id = :id")
    Question getQuestionById(int id);
    
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    List<Question> getRandomQuestions(int limit);
    
    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT :limit")
    List<Question> getRandomQuestionsByCategory(String category, int limit);
    
    @Query("SELECT COUNT(*) FROM questions")
    int getQuestionCount();
    
    @Query("SELECT COUNT(*) FROM questions WHERE isWrong = 1")
    int getWrongQuestionCount();
    
    @Query("SELECT COUNT(*) FROM questions WHERE isFavorite = 1")
    int getFavoriteQuestionCount();
    
    @Query("SELECT DISTINCT category FROM questions")
    List<String> getAllCategories();
    
    @Insert
    void insert(Question question);
    
    @Insert
    void insertAll(List<Question> questions);
    
    @Update
    void update(Question question);
    
    @Delete
    void delete(Question question);
    
    @Query("DELETE FROM questions")
    void deleteAll();
    
    @Query("UPDATE questions SET isWrong = 1, wrongCount = wrongCount + 1, lastWrongTime = :time WHERE id = :id")
    void markAsWrong(int id, long time);
    
    @Query("UPDATE questions SET isWrong = 0 WHERE id = :id")
    void markAsCorrect(int id);
    
    @Query("UPDATE questions SET isFavorite = :favorite WHERE id = :id")
    void setFavorite(int id, boolean favorite);
    
    @Query("UPDATE questions SET practiceCount = practiceCount + 1 WHERE id = :id")
    void incrementPracticeCount(int id);
}
