package com.example.todolist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TaskFragment extends Fragment {

    private List<Task> taskList;

    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    private Button addTaskB;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        addTaskB = view.findViewById(R.id.addTaskB);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(getChildFragmentManager(), taskList);
        recyclerView.setAdapter(adapter);

        taskList.add(new Task("Hello World", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World1", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World2", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World3", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World4", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World5", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World6", "No Details", "14/10/2025", 1000L));
        taskList.add(new Task("Hello World7", "No Details", "14/10/2025", 1000L));


        addTaskB.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(1, null, -1);
            taskDialog.show(getChildFragmentManager(), taskDialog.getTag());
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int dragIndex = viewHolder.getAdapterPosition();
                int targetIndex = target.getAdapterPosition();

                Collections.swap(taskList, dragIndex, targetIndex);
                adapter.notifyItemMoved(dragIndex, targetIndex);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.START) {
                    taskList.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}