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

//    private RoomDB roomDB;
    private RoomDao roomDao;
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
        roomDao = RoomDB.getDatabase(requireContext()).roomDao();

        new Thread(() -> {
            List<Task> tasks = roomDao.getTasks();

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                taskList = new ArrayList<>(tasks);
                Log.d("tasksList", "onViewCreated: " + taskList);

//                Task task1 = new Task("Hello World1", "", "Sun, 27 Jul, 2025", 1753587083655L);
//                task1.setPos(0);
//                taskList.add(task1);
//                new Thread(() -> roomDao.insert(task1)).start();
//
//                Task task2 = new Task("Hello World2", "", "Sun, 27 Jul, 2025", 1753587083655L);
//                task1.setPos(1);
//                taskList.add(task2);
//                new Thread(() -> roomDao.insert(task2)).start();
//
//                Task task3 = new Task("Hello World3", "Detail", "Sun, 27 Jul, 2025", 1753587083655L);
//                task1.setPos(2);
//                taskList.add(task3);
//                new Thread(() -> roomDao.insert(task3)).start();


                adapter = new TaskAdapter(getChildFragmentManager(), taskList);
                recyclerView.setAdapter(adapter);
            });
        }).start();

        addTaskB.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(1, null, -1);
            taskDialog.show(getChildFragmentManager(), taskDialog.getTag());
        });

        TaskDialog.addTaskListener(new TaskDialog.TaskListener() {
            @Override
            public void onTaskAdded(Task task) {
                task.setPos(taskList.size());
                taskList.add(0, task);
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                new Thread(() -> roomDao.insert(task)).start();
            }

            @Override
            public void onTaskUpdated(Task task, int taskIndex) {
                taskList.set(taskIndex, task);
                adapter.notifyItemChanged(taskIndex);
                new Thread(() -> roomDao.update(task)).start();
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