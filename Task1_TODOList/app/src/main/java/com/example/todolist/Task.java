package com.example.todolist;

public class Task {
    private String id;
    private String title;
    private String detail;
    private String dueDate;

    private int leftDays;

    private Long dateInMillis;

    private boolean isCompleted;

    Task(String title, String detail, String dueDate, Long dateInMillis) {
        this.title = title;
        this.detail = detail;
        this.dueDate = dueDate;
        this.dateInMillis = dateInMillis;
        id = title + dueDate;
    }


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

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(int leftDays) {
        this.leftDays = leftDays;
    }


    public void setDateInMillis(Long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }


    public Long getDateInMillis() {
        return dateInMillis;
    }
}
