package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TaskDialog extends DialogFragment {

    public interface TaskListener {
        void onTaskAdded(Task task);
        void onTaskUpdated(Task task, int taskIndex);
    }

    public static TaskListener taskListener;

    private int mode;
    private Task task;
    private int taskIndex;

    private EditText titleE;
    private EditText detE;
    private EditText dateE;
    private Button cancelB;
    private Button doneB;

    public long selectedTimeMillis;

    public TaskDialog(int mode, Task task, int taskIndex) {
        this.mode = mode;
        this.task = task;
        this.taskIndex = taskIndex;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_dialog, container, false);

        titleE = view.findViewById(R.id.titleE);
        detE = view.findViewById(R.id.detE);
        dateE = view.findViewById(R.id.datePickerE);

        cancelB = view.findViewById(R.id.decline_button);
        doneB = view.findViewById(R.id.accept_button);

        setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mode == 2) {
            titleE.setText(task.getTitle());
            detE.setText(task.getDetail());
            dateE.setText(task.getDueDate());
            selectedTimeMillis = task.getDateInMillis();
        }

        Calendar calendar = Calendar.getInstance();
        int yr = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        dateE.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (vw, year, month, dayOfMonth) -> {
                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(year, month, dayOfMonth);

                        selectedTimeMillis = selectedCal.getTimeInMillis();

                        // Format to: "Mon, 22 Jul, 2025"
                        String formattedDate = new SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault())
                                .format(new Date(selectedTimeMillis));
                        dateE.setText(formattedDate);

                    }, yr, m, d);
            datePickerDialog.show();
        });

        cancelB.setOnClickListener(v -> dismiss());

        doneB.setOnClickListener(v -> {
            if (isTaskValid()) {
                if (mode == 1) {
                    Task task = new Task(titleE.getText().toString().trim(), detE.getText().toString().trim(), dateE.getText().toString().trim(), selectedTimeMillis);
                    taskListener.onTaskAdded(task);
                }
                else {
                    task.setTitle(titleE.getText().toString().trim());
                    task.setDetail(detE.getText().toString().trim());
                    task.setDueDate(dateE.getText().toString().trim());
                    task.setDateInMillis(selectedTimeMillis);
                    taskListener.onTaskUpdated(task, taskIndex);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels), // 85% screen width
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
    
    private boolean isTaskValid() {
        if (titleE.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please write the Title!", Toast.LENGTH_SHORT).show();
        }
        else if (dateE.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please select the Date!", Toast.LENGTH_SHORT).show();
        }
        else {
            return true;
        }
        return false;
    }

    public static void addTaskListener(TaskListener taskListener) {
        TaskDialog.taskListener = taskListener;
    }
}
