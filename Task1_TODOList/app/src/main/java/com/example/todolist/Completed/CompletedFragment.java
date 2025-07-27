package com.example.todolist.Completed;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todolist.R;
import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;
import com.example.todolist.Task;
import com.example.todolist.Active.TaskAdapter;
import com.example.todolist.TaskDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletedFragment extends Fragment {

    //    private RoomDB roomDB;
    private RoomDao roomDao;
    private List<Task> taskList;

    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    private TextView notfoundT;

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

        adapter = new TaskAdapter(getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
//            List<Task> tasks = roomDao.getCompletedTasks();

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) return;

                taskList = new ArrayList<>();

                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));
                taskList.add(new Task("jngg", "", "jfdj", 848484L));

//                if (tasks.isEmpty()) {
//                    notfoundT.setVisibility(View.VISIBLE);
//                }
            });
        }).start();


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

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int itemCount = state.getItemCount();

                if (position == itemCount - 1) {
                    outRect.bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, parent.getResources().getDisplayMetrics());
                }
            }
        });

    }
}