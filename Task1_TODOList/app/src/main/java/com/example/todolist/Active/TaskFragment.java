package com.example.todolist.Active;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todolist.R;
import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;
import com.example.todolist.SortViewModel;
import com.example.todolist.Task;
import com.example.todolist.TaskDialog;

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
    private CheckBox markCompletedB;
    private ImageButton markStarB;

    private TextView notfoundT;

    private int sortType;
    private boolean sortOrder;

    private LiveData<List<Task>> liveData;


    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        addTaskB = view.findViewById(R.id.addTaskB);
        notfoundT = view.findViewById(R.id.notFoundT);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        roomDao = RoomDB.getDatabase(requireContext()).roomDao();
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        SortViewModel sortViewModel = new ViewModelProvider(requireActivity()).get(SortViewModel.class);


        sortType = prefs.getInt("sortType", 0);
        sortOrder = prefs.getBoolean("sortOrder", true);

        sortViewModel.setSortConfig(sortType, sortOrder);
        adapter = new TaskAdapter(requireContext(), getChildFragmentManager());
        recyclerView.setAdapter(adapter);


        sortViewModel.getSortConfig().observe(getViewLifecycleOwner(), config -> {
            sortType = config.first;
            sortOrder = config.second;

            if (sortViewModel.getPos() != null && sortViewModel.getPos() != 1) return;

            editor.putInt("sortType_task", sortType);
            editor.putBoolean("sortOrder_task", sortOrder);
            editor.apply();

            Log.d("ddjdjd", "onViewCreated: " + sortType + sortOrder);

            if (liveData != null) {
                liveData.removeObservers(getViewLifecycleOwner());
            }

            if (sortType == 0) liveData = sortOrder ? roomDao.getTasksAsc() : roomDao.getTasksDsc();
            else if (sortType == 1) liveData = sortOrder ? roomDao.getTasksByCreateAsc() : roomDao.getTasksByCreateDsc();
            else liveData = sortOrder ? roomDao.getTasksByDueAsc() : roomDao.getTasksByDueDsc();

            liveData.observe(getViewLifecycleOwner(), tasks -> {
                Log.d("ddjd34jd", "onViewCreated: " + sortType + sortOrder);
                adapter.submitList(tasks);
                setNotFoundView(tasks.isEmpty());
            });
        });


        addTaskB.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(1, null, -1);
            taskDialog.show(getChildFragmentManager(), taskDialog.getTag());
        });

        TaskDialog.addTaskListener(task -> {
            Log.d("added", "onTaskAdded: " + "added");
            task.setPos(adapter.getItemCount());
            new Thread(() -> {
                roomDao.insert(task);
                requireActivity().runOnUiThread(() -> recyclerView.smoothScrollToPosition(0));
            }).start();
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (sortType == 1) return 0;
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int dragIndex = viewHolder.getAdapterPosition();
                int targetIndex = target.getAdapterPosition();

                List<Task> list = adapter.getCurrentList();
                List<Task> mlist = new ArrayList<>(list);

                Task t1 = list.get(dragIndex);
                Task t2 = list.get(targetIndex);

                int temp = t1.getPos();
                t1.setPos(t2.getPos());
                t2.setPos(temp);

                Collections.swap(mlist, dragIndex, targetIndex);
                adapter.submitList(mlist);

                new Thread(() -> {
                    roomDao.update(t1);
                    roomDao.update(t2);
                }).start();

                return true;
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

    private void setNotFoundView(boolean isEmpty){
        if (isEmpty) notfoundT.setVisibility(View.VISIBLE);
        else notfoundT.setVisibility(View.GONE);
    }
}