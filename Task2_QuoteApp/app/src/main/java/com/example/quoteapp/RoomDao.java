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

    @Query("SELECT * FROM quotes order by dateAddedMillis asc")
    LiveData<List<Quote>> getAllQuotesAsc();

    @Query("SELECT * FROM quotes order by dateAddedMillis desc")
    LiveData<List<Quote>> getAllQuotesDesc();

    @Query("select COUNT(*) from quotes where id = :id")
    int findQuote(String id);
}
