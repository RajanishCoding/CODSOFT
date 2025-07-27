package com.example.todolist.Room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todolist.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class RoomDB extends RoomDatabase {

    private static volatile RoomDB INSTANCE;

    public abstract RoomDao roomDao();

    public static RoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RoomDB.class,
                            "task_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
