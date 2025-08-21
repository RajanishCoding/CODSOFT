package com.example.todolist;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationWork extends Worker {

    public NotificationWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String id = getInputData().getString("id");
        String taskTitle = getInputData().getString("taskTitle");
        NotificationBuilder.showNotification(getApplicationContext(), id, taskTitle);
        return Result.success();
    }


    public static void scheduleTask(Context context, String id, String taskTitle, long dueTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        dueTimeMillis = calendar.getTimeInMillis();
        long delay = dueTimeMillis - System.currentTimeMillis();

        Data data = new Data.Builder()
                .putString("id", id)
                .putString("taskTitle", taskTitle)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWork.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .addTag(id)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }

    public static void updateScheduledTask(Context context, String id, String taskTitle, long dueTimeMillis) {
        cancelScheduledTask(context, id);
        scheduleTask(context, id, taskTitle, dueTimeMillis);
    }

    public static void cancelScheduledTask(Context context, String id) {
        WorkManager.getInstance(context).cancelAllWorkByTag(id);
    }
}
