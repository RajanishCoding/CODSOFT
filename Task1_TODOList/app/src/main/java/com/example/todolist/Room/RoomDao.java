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

    @Update
    void updateTasks(List<Task> taskList);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table WHERE id = :id")
    Task getTaskById(String id);



    @Query("select * from task_table where isCompleted = 0 order by pos desc")
    LiveData<List<Task>> getTasksAsc();

    @Query("select * from task_table where isCompleted = 0 order by pos asc")
    LiveData<List<Task>> getTasksDsc();

    @Query("select * from task_table where isCompleted = 0 order by creationDateinMillis asc")
    LiveData<List<Task>> getTasksByCreateAsc();

    @Query("select * from task_table where isCompleted = 0 order by creationDateinMillis desc")
    LiveData<List<Task>> getTasksByCreateDsc();

    @Query("select * from task_table where isCompleted = 0 order by dateInMillis asc")
    LiveData<List<Task>> getTasksByDueAsc();

    @Query("select * from task_table where isCompleted = 0 order by dateInMillis desc")
    LiveData<List<Task>> getTasksByDueDsc();


    @Query("select * from task_table where isCompleted = 1 order by pos desc")
    LiveData<List<Task>> getCompletedTasksAsc();

    @Query("select * from task_table where isCompleted = 1 order by pos asc")
    LiveData<List<Task>> getCompletedTasksDsc();

    @Query("select * from task_table where isCompleted = 1 order by creationDateinMillis asc")
    LiveData<List<Task>> getCompletedTasksByCreateAsc();

    @Query("select * from task_table where isCompleted = 1 order by creationDateinMillis desc")
    LiveData<List<Task>> getCompletedTasksByCreateDsc();

    @Query("select * from task_table where isCompleted = 1 order by completedDateinMillis asc")
    LiveData<List<Task>> getCompletedTasksByCompAsc();

    @Query("select * from task_table where isCompleted = 1 order by completedDateinMillis desc")
    LiveData<List<Task>> getCompletedTasksByCompDsc();


    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by pos desc")
    LiveData<List<Task>> getImportantTasksAsc();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by pos asc")
    LiveData<List<Task>> getImportantTasksDsc();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by dateInMillis asc")
    LiveData<List<Task>> getImportantTasksByDueAsc();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by dateInMillis desc")
    LiveData<List<Task>> getImportantTasksByDueDsc();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by starredDateinMillis asc")
    LiveData<List<Task>> getImportantTasksByStarAsc();

    @Query("select * from task_table where isImportant = 1 and isCompleted = 0 order by starredDateinMillis desc")
    LiveData<List<Task>> getImportantTasksByStarDsc();
}