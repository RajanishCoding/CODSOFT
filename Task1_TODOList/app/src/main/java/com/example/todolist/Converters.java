package com.example.todolist;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static MyPair<Integer, Integer> toMyPair(String value) {
        if (value == null) return null; // only check for null
        String[] parts = value.split(":");
        return new MyPair<>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @TypeConverter
    public static String fromMyPair(MyPair<Integer, Integer> pair) {
        if (pair == null) return null;
        return pair.first + ":" + pair.second;
    }
}
