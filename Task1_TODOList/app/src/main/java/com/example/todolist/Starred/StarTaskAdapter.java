package com.example.todolist.Starred;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;
import com.example.todolist.Task;
import com.example.todolist.TaskDialog;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StarTaskAdapter extends ListAdapter<Task, StarTaskAdapter.TaskViewHolder> {

    Context context;
    FragmentManager fragmentManager;
    List<Task> taskList;

    private RoomDao roomDao;

    public static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    };


    public StarTaskAdapter(Context context, FragmentManager fragmentManager) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView details;
        CheckBox checkB;
        ImageButton starB;
//        TextView statusT;
        TextView leftDaysT;
        TextView dueDateT;

        public TaskViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleT);
            details = itemView.findViewById(R.id.detailT);
            checkB = itemView.findViewById(R.id.checkB);
            starB = itemView.findViewById(R.id.starB);
//            statusT = itemView.findViewById(R.id.statusT);
            leftDaysT = itemView.findViewById(R.id.leftDaysT);
            dueDateT = itemView.findViewById(R.id.dueDateT);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        roomDao = RoomDB.getDatabase(context).roomDao();

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);

        if (task == null) return;

        holder.title.setText(task.getTitle());
        holder.dueDateT.setText(String.valueOf(task.getDueDate()));

        if (task.getDetail().isEmpty()) holder.details.setVisibility(View.GONE);
        else {
            holder.details.setText(task.getDetail());
            holder.details.setVisibility(View.VISIBLE);
        }

        holder.checkB.setChecked(task.isCompleted());

        if (task.isImportant()) holder.starB.setImageResource(R.drawable.round_star);
        else holder.starB.setImageResource(R.drawable.round_star_outline);

        long daysLeft = getDaysLeft(task.getDateInMillis());
        if (daysLeft == 0) {
            holder.leftDaysT.setText("Active");
        }
        else if (daysLeft < 0) {
            holder.leftDaysT.setText("Overdue");
        }
        else {
            holder.leftDaysT.setText(daysLeft + "d left");
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            TaskDialog taskDialog = new TaskDialog(2, getItem(pos), pos);
            taskDialog.show(fragmentManager, taskDialog.getTag());
        });

        holder.checkB.setOnCheckedChangeListener((v, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Task taskC = getItem(holder.getAdapterPosition());
            if (isChecked) {
                taskC.setCompletion(true);
                new Thread(() -> roomDao.update(taskC)).start();
            }
        });

        holder.starB.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Task taskS = getItem(pos);
            if (taskS.isImportant()) {
                holder.starB.setImageResource(R.drawable.round_star_outline);
                taskS.setImportants(false);
            }
            new Thread(() -> roomDao.update(taskS)).start();
        });
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    private long getDaysLeft(long millis) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar taskDay = Calendar.getInstance();
        taskDay.setTimeInMillis(millis);
        taskDay.set(Calendar.HOUR_OF_DAY, 0);
        taskDay.set(Calendar.MINUTE, 0);
        taskDay.set(Calendar.SECOND, 0);
        taskDay.set(Calendar.MILLISECOND, 0);

        long diffMillis = taskDay.getTimeInMillis() - today.getTimeInMillis();

        return TimeUnit.MILLISECONDS.toDays(diffMillis);
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }
}
