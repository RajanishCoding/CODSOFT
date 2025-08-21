package com.example.todolist.Room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.todolist.Converters;
import com.example.todolist.Task;

@Database(entities = {Task.class}, version = 3)
@TypeConverters({Converters.class})
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
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
