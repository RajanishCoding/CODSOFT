package com.example.todolist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey
    @NonNull
    private String id;

    private int pos;

    private String title;
    private String detail;
    private String dueDate;
    private String dueTime;
    private int priority;
    private Long dateInMillis;
    private Long creationDateinMillis;

    private MyPair<Integer, Integer> time;

    private Long completedDateinMillis;
    private Long starredDateinMillis;

    private int leftDays;
    
    private boolean isCompleted;
    private boolean isImportant;

    public Task(String title, String detail, String dueDate, String dueTime, int priority, Long dateInMillis, Long creationDateinMillis) {
        this.title = title;
        this.detail = detail;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.dateInMillis = dateInMillis;
        this.creationDateinMillis = creationDateinMillis;
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

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public Long getDateInMillis() {
        return dateInMillis;
    }

    public void setTime(MyPair<Integer, Integer> time) {
        this.time = time;
    }

    public MyPair<Integer, Integer> getTime() {
        return time;
    }

    public Long getCreationDateinMillis() {
        return creationDateinMillis;
    }


    public Long getCompletedDateinMillis() {
        return completedDateinMillis;
    }

    public void setCompletedDateinMillis(Long completedDateinMillis) {
        this.completedDateinMillis = completedDateinMillis;
    }

    public Long getStarredDateinMillis() {
        return starredDateinMillis;
    }

    public void setStarredDateinMillis(Long starredDateinMillis) {
        this.starredDateinMillis = starredDateinMillis;
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

    public void setCompletion(boolean completed) {
        isCompleted = completed;
        setCompletedDateinMillis();
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }
    
    public void setImportants(boolean important) {
        isImportant = important;
        setStarredDateinMillis();
    }


    public void setCompletedDateinMillis() {
        if (!isCompleted) {
            completedDateinMillis = null;
            return;
        }
        Calendar calendar = Calendar.getInstance();
        completedDateinMillis = calendar.getTimeInMillis();
    }

    public void setStarredDateinMillis() {
        if (!isImportant) {
            starredDateinMillis = null;
            return;
        }
        Calendar calendar = Calendar.getInstance();
        starredDateinMillis = calendar.getTimeInMillis();
    }
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;

        Task task = (Task) obj;

        Log.d("hdhdhd" , "equals: " + title + task.getTitle());
        return title.equals(task.title) &&
                detail.equals(task.detail) &&
                dateInMillis.equals(task.dateInMillis) &&
                priority == task.priority &&
                isCompleted == task.isCompleted &&
                isImportant == task.isImportant;
    }
}
