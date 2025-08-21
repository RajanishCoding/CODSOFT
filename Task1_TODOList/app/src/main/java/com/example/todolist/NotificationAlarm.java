package com.example.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class NotificationAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("id");
        String taskTitle = intent.getStringExtra("taskTitle");
        NotificationBuilder.showNotification(context, id, taskTitle);
    }


    private static PendingIntent getPendingIntent(Context context, String id, String taskTitle) {
        Intent intent = new Intent(context, NotificationAlarm.class);
        intent.putExtra("id", id);
        intent.putExtra("taskTitle", taskTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return pendingIntent;
    }

    public static void scheduleTask(Context context, String id, String taskTitle, long targetTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5, getPendingIntent(context, id, taskTitle));
            }
            else {
                NotificationWork.scheduleTask(context, id, taskTitle, 5);
                Intent intent1 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent1.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent1);
            }
        }
    }

    public static void updateScheduledTask(Context context, String id, String taskTitle, long dueTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cancelScheduledTask(context, id, taskTitle);
        scheduleTask(context, id, taskTitle, dueTimeInMillis);
    }

    public static void cancelScheduledTask(Context context, String id, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context, id, taskTitle));
    }

}
