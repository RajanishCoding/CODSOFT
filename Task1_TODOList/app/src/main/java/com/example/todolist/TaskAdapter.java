package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
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



        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.title.setText(task.getTitle());
        holder.details.setText(task.getDetail());
        holder.dueDateT.setText(task.getDueDate());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
