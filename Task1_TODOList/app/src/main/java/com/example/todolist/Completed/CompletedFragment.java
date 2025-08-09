package com.example.todolist.Completed;

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

public class CompletedFragment extends Fragment {

    //    private RoomDB roomDB;
    private RoomDao roomDao;
    private List<Task> taskList;

    private RecyclerView recyclerView;
    private CompletedTaskAdapter adapter;

    private TextView notfoundT;

    private int sortType;
    private boolean sortOrder;

    private boolean isScroll;

    private LiveData<List<Task>> liveData;


    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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


        adapter = new CompletedTaskAdapter(requireContext(), getChildFragmentManager());
        recyclerView.setAdapter(adapter);


        sortViewModel.getSortConfig().observe(getViewLifecycleOwner(), config -> {
            sortType = config.first;
            sortOrder = config.second;

            if (sortViewModel.getPos() != null && sortViewModel.getPos() != 2) return;

            editor.putInt("sortType_comp", sortType);
            editor.putBoolean("sortOrder_comp", sortOrder);
            editor.apply();

            if (liveData != null) {
                liveData.removeObservers(getViewLifecycleOwner());
            }

            if (sortType == 0) liveData = sortOrder ? roomDao.getCompletedTasksAsc() : roomDao.getCompletedTasksDsc();
            else if (sortType == 1) liveData = sortOrder ? roomDao.getCompletedTasksByCompAsc() : roomDao.getCompletedTasksByCompDsc();
            else liveData = sortOrder ? roomDao.getCompletedTasksByCreateAsc() : roomDao.getCompletedTasksByCreateDsc();

            liveData.observe(getViewLifecycleOwner(), tasks -> {
                adapter.submitList(tasks, () -> {
                    if (isScroll) {
                        recyclerView.post(() -> recyclerView.smoothScrollToPosition(0));
                        isScroll = false;
                    }
                });

                setNotFoundView(tasks.isEmpty());
            });
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


    @Override
    public void onResume() {
        super.onResume();

        Log.d("hellwosj", "onResume2: ");

        TaskDialog.addTaskListener(task -> {
            Log.d("added", "onTaskAdded: " + "added");
            new Thread(() -> {
                int totalCount = roomDao.getTotalTaskCount();
                task.setPos(totalCount);

                roomDao.insert(task);

                requireActivity().runOnUiThread(() -> {
                    if (task.isCompleted())
                        isScroll = true;
                });
            }).start();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("hellwosj", "onPause2: ");
        TaskDialog.addTaskListener(null);
    }
}