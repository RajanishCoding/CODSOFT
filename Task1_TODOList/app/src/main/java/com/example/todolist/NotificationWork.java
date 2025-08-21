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


    public static void scheduleTask(Context context, String id, String taskTitle, long delayTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delayTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        delayTimeMillis = calendar.getTimeInMillis();
//        long delay = delayTimeMillis - System.currentTimeMillis();
        long delay = 5;

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

    public static void updateScheduledTask(Context context, String id, String taskTitle, long dueTimeInMillis) {
        cancelScheduledTask(context, id);
        scheduleTask(context, id, taskTitle, dueTimeInMillis);
    }

    public static void cancelScheduledTask(Context context, String id) {
        WorkManager.getInstance(context).cancelAllWorkByTag(id);
    }
}
