package com.example.todolist.Completed;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CompletedTaskAdapter extends ListAdapter<Task, CompletedTaskAdapter.TaskViewHolder> {

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


    public CompletedTaskAdapter(Context context, FragmentManager fragmentManager) {
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
        holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.dueDateT.setVisibility(View.GONE);

        if (task.getDetail().isEmpty()) holder.details.setVisibility(View.GONE);
        else {
            holder.details.setText(task.getDetail());
            holder.details.setVisibility(View.VISIBLE);
        }

        holder.checkB.setChecked(task.isCompleted());

        if (task.isImportant()) holder.starB.setImageResource(R.drawable.round_star);
        else holder.starB.setImageResource(R.drawable.round_star_outline);

        holder.leftDaysT.setText("Completed: " + getFullDateFromMillis(task.getCompletedDateinMillis()));

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            TaskDialog taskDialog = new TaskDialog(2, getItem(pos), pos);
            taskDialog.show(fragmentManager, taskDialog.getTag());
        });

        holder.checkB.setOnCheckedChangeListener((v, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Task taskC = getItem(pos);
            if (!isChecked) {
                taskC.setCompletion(false);
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

    public String getFullDateFromMillis(Long millis) {
        if (millis == null) return null;

        Date date = new Date(millis);
        SimpleDateFormat str = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
        return str.format(date);
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }
}
