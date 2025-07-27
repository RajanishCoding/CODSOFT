package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey
    @NonNull
    private String id;

    private int pos;
    private String title;
    private String detail;
    private String dueDate;

    private int leftDays;

    private Long dateInMillis;

    private boolean isCompleted = false;
    private boolean isImportant = false;

    Task(String title, String detail, String dueDate, Long dateInMillis) {
        this.title = title;
        this.detail = detail;
        this.dueDate = dueDate;
        this.dateInMillis = dateInMillis;
        id = title + dueDate;
    }


    @NonNull
    public String getId() { return id; }

    public void setId(@NonNull String id) { this.id = id; }

    public int getPos() { return pos; }

    public void setPos(int pos) { this.pos = pos; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public Long getDateInMillis() {
        return dateInMillis;
    }

    public int getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(int leftDays) {
        this.leftDays = leftDays;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isImportant() { return isImportant; }

    public void setImportant(boolean important) { isImportant = important; }
}
