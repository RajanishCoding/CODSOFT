package com.example.todolist.Active;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.NotificationAlarm;
import com.example.todolist.NotificationWork;
import com.example.todolist.R;
import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;
import com.example.todolist.Task;
import com.example.todolist.TaskDialog;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    Context context;
    FragmentManager fragmentManager;
    List<Task> taskList;

    String[] priorities = {"None", "Low", "Normal", "High", "Urgent"};

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


    public TaskAdapter(Context context, FragmentManager fragmentManager) {
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
        TextView priorityT;

        public TaskViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleT);
            details = itemView.findViewById(R.id.detailT);
            checkB = itemView.findViewById(R.id.checkB);
            starB = itemView.findViewById(R.id.starB);
//            statusT = itemView.findViewById(R.id.statusT);
            leftDaysT = itemView.findViewById(R.id.leftDaysT);
            dueDateT = itemView.findViewById(R.id.dueDateT);
            priorityT = itemView.findViewById(R.id.priorityT);
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

        if (task.getDueTime().isEmpty()) holder.dueDateT.setText(formatDueDate(task.getDueDate()));
        else holder.dueDateT.setText(formatDueDate(task.getDueDate()) + " • " + task.getDueTime());

        if (task.getDetail().isEmpty()) holder.details.setVisibility(View.GONE);
        else {
            holder.details.setText(task.getDetail());
            holder.details.setVisibility(View.VISIBLE);
        }

        holder.checkB.setChecked(task.isCompleted());

        if (task.isImportant()) holder.starB.setImageResource(R.drawable.round_star);
        else holder.starB.setImageResource(R.drawable.round_star_outline);

        holder.priorityT.setText("Priority: " + priorities[task.getPriority()]);
        switch (task.getPriority()) {
            case 0:
                holder.priorityT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetailsPriority0));
                break;
            case 1:
                holder.priorityT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetailsPriority1));
                break;
            case 2:
                holder.priorityT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetailsPriority2));
                break;
            case 3:
                holder.priorityT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetailsPriority3));
                break;
            case 4:
                holder.priorityT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetailsPriority4));
                break;
        }

        long daysLeft = getDaysLeft(task.getDateInMillis());
        if (daysLeft == 0) {
            holder.leftDaysT.setText("Active");
            holder.leftDaysT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetails2));
        }
        else if (daysLeft < 0) {
            holder.leftDaysT.setText("Overdue");
            holder.leftDaysT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetails1));
        }
        else {
            holder.leftDaysT.setText(daysLeft + "d left");
            holder.leftDaysT.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.overlayItemDetails3));
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            TaskDialog taskDialog = new TaskDialog(-1, 2, getItem(pos));
            taskDialog.show(fragmentManager, taskDialog.getTag());
        });

        holder.checkB.setOnCheckedChangeListener((v, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Task taskC = getItem(pos);
            if (isChecked) {
                taskC.setCompletion(true);
                new Thread(() -> roomDao.update(taskC)).start();

                if (!taskC.getDueTime().isEmpty())
                    NotificationAlarm.cancelScheduledTask(context, taskC.getId(), taskC.getTitle());
                else
                    NotificationWork.cancelScheduledTask(context, taskC.getId());
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
            else {
                holder.starB.setImageResource(R.drawable.round_star);
                taskS.setImportants(true);
            }
            new Thread(() -> {
                roomDao.update(taskS);
            }).start();
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

    public static String formatDueDate(String date) {
        int current = Calendar.getInstance().get(Calendar.YEAR);

        String[] parts = date.split(", ");
        int year = Integer.parseInt(parts[2]);

        if (year == current) return parts[0] + ", " + parts[1];
        else return date;
    }


    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }
}
