package com.example.todolist;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList);

        taskList.add(new Task("Hello World", "No Details", "14/10/2025"));
        taskList.add(new Task("Hello World1", "No Details", "14/10/2025"));
        taskList.add(new Task("Hello World2", "No Details", "14/10/2025"));
        taskList.add(new Task("Hello World3", "No Details", "14/10/2025"));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025"));

        recyclerView.setAdapter(adapter);
    }
}