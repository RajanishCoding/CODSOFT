package com.example.quoteapp;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

@Entity(tableName = "quotes")
public class Quote {

    @NonNull
    @PrimaryKey
    private String id;

    private String content;
    private String author;

    private long dateAddedMillis;

    public Quote(String content, String author) {
        this.content = content;
        this.author = author;
        id = content + author;
        dateAddedMillis = getDateinMillis();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getDateAddedMillis() {
        return dateAddedMillis;
    }

    public void setDateAddedMillis(long dateAddedMillis) {
        this.dateAddedMillis = dateAddedMillis;
    }

    private long getDateinMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;

        Quote other = (Quote) obj;
        return id == other.id &&
                Objects.equals(content, other.content);
    }
}
