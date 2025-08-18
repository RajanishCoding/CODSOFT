package com.example.quoteapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomDao {

    @Insert
    void insert(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("SELECT * FROM quotes")
    LiveData<List<Quote>> getAllQuotes();

    @Query("select COUNT(*) from quotes where id = :id")
    int findQuote(String id);
}
