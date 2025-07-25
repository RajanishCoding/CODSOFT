package com.example.todolist;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private List<Task> taskList;

    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    private Button addTaskB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.toolbar));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTaskB = findViewById(R.id.addTaskB);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(getSupportFragmentManager(), taskList);

        taskList.add(new Task("Hello World", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World1", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World2", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World3", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025", 1000L));

        recyclerView.setAdapter(adapter);


        addTaskB.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(1, null, -1);
            taskDialog.show(getSupportFragmentManager(), taskDialog.getTag());
        });

        TaskDialog.addTaskListener(new TaskDialog.TaskListener() {
            @Override
            public void onTaskAdded(Task task) {
                taskList.add(0, task);
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onTaskUpdated(Task task, int taskIndex) {
                Log.d("hdhdh", "onTaskUpdated: " + taskIndex);
                taskList.set(taskIndex, task);
                adapter.notifyItemChanged(taskIndex);
            }
        });
    }
}