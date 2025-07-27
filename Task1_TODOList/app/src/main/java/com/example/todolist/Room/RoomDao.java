package com.example.todolist.Room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todolist.Task;


@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);


    @Query("select * from task_table where isCompleted = 0 order by pos desc")
    LiveData<List<Task>> getTasks();

    @Query("select * from task_table where isCompleted = 1 order by pos desc")
    LiveData<List<Task>> getCompletedTasks();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by pos desc")
    LiveData<List<Task>> getImportantTasks();


    @Query("select * from task_table order by pos asc")
    List<Task> getAllTasksByPositionAsc();

    @Query("select * from task_table order by pos desc")
    List<Task> getAllTasksByPositionDesc();

    @Query("select * from task_table order by dateInMillis asc")
    List<Task> getAllTasksByDueDateAsc();

    @Query("select * from task_table order by dateInMillis desc")
    List<Task> getAllTasksByDueDateDesc();
}