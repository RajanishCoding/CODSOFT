package com.example.todolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TaskDialog extends DialogFragment {

    public interface TaskListener {
        void onTaskAdded(Task task);
//        void onTaskUpdated(Task task, int taskIndex);
    }

    public static TaskListener taskListener;

    private RoomDao roomDao;

    private int mode;
    private Task task;
    private int taskIndex;

    private TextView title;
    private EditText titleE;
    private EditText detE;
    private EditText dateE;
    private Button cancelB;
    private Button doneB;
    private ImageButton infoB;
    private CheckBox compB;
    private ImageButton starB;
    private ImageButton delB;

    private boolean isCompleted;
    private boolean isImportant;

    public long selectedTimeMillis;
    public long creationTimeMillis;

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

        title = view.findViewById(R.id.titleText);
        titleE = view.findViewById(R.id.titleE);
        detE = view.findViewById(R.id.detE);
        dateE = view.findViewById(R.id.datePickerE);

        cancelB = view.findViewById(R.id.decline_button);
        doneB = view.findViewById(R.id.accept_button);

        infoB = view.findViewById(R.id.infoB);
        compB = view.findViewById(R.id.completeB);
        starB = view.findViewById(R.id.starB);
        delB = view.findViewById(R.id.delB);

        roomDao = RoomDB.getDatabase(requireContext()).roomDao();

        setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mode == 2) {
            title.setText("Edit Task");

            titleE.setText(task.getTitle());
            detE.setText(task.getDetail());
            dateE.setText(task.getDueDate());

            isCompleted = task.isCompleted();
            isImportant = task.isImportant();
            compB.setChecked(isCompleted);
            starB.setImageResource(isImportant ? R.drawable.round_star : R.drawable.round_star_outline);
            selectedTimeMillis = task.getDateInMillis();
        }
        else {
            delB.setVisibility(View.GONE);
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

        infoB.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View viewD = getLayoutInflater().inflate(R.layout.info_dialog, null);
            builder.setView(viewD);

            TextView title = viewD.findViewById(R.id.title);
            TextView det = viewD.findViewById(R.id.detail);
            TextView create = viewD.findViewById(R.id.create);
            TextView due = viewD.findViewById(R.id.due);
            TextView left = viewD.findViewById(R.id.left);
            TextView comp = viewD.findViewById(R.id.complete);
            TextView star = viewD.findViewById(R.id.starred);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            });
            dialog.show();

            title.setText(task.getTitle());

            if (task.getDetail().isEmpty()) det.setText("No Details");

            else det.setText(task.getDetail());

            create.setText(getFullDateTimeFromMillis(task.getCreationDateinMillis()));

            due.setText(getFullDateFromMillis(task.getDateInMillis()));

            long days = getDaysLeft(task.getDateInMillis());
            if (days == 0) left.setText("Active • Today");
            else if (days < 0) left.setText("Overdue • " + -days + " days ago");
            else left.setText(days + " days left");

            if (task.getCompletedDateinMillis() == null) comp.setText("Not Completed yet");
            else comp.setText(getFullDateTimeFromMillis(task.getCompletedDateinMillis()));

            if (task.getStarredDateinMillis() == null) star.setText("Not Marked as Important");
            else star.setText(getFullDateTimeFromMillis(task.getStarredDateinMillis()));
        });

        delB.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View viewD = getLayoutInflater().inflate(R.layout.del_alert_dialog, null);
            builder.setView(viewD);

            Button cancel = viewD.findViewById(R.id.cancel_button);
            Button delete = viewD.findViewById(R.id.del_button);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            });
            dialog.show();

            cancel.setOnClickListener(v1 -> dialog.dismiss());
            delete.setOnClickListener(v1 -> {
                new Thread(() -> roomDao.delete(task)).start();
                dialog.dismiss();
                dismiss();
            });
        });

        starB.setOnClickListener(v -> {
            if (isImportant) {
                starB.setImageResource(R.drawable.round_star_outline);
                isImportant = false;
            }
            else {
                starB.setImageResource(R.drawable.round_star);
                isImportant = true;
            }
        });

        compB.setOnCheckedChangeListener((v, isChecked) -> {
            isCompleted = isChecked;
        });


        cancelB.setOnClickListener(v -> dismiss());

        doneB.setOnClickListener(v -> {
            if (isTaskValid()) {
                if (mode == 1) {
                    creationTimeMillis = getCreationDateinMillis();
                    Task task = new Task(titleE.getText().toString().trim(), detE.getText().toString().trim(),
                            dateE.getText().toString().trim(), selectedTimeMillis, creationTimeMillis);
                    task.setImportant(isImportant);
                    task.setCompleted(isCompleted);
                    taskListener.onTaskAdded(task);
                }
                else {
                    Task newTask = new Task(titleE.getText().toString().trim(), detE.getText().toString().trim(),
                            dateE.getText().toString().trim(), selectedTimeMillis, task.getCreationDateinMillis());
                    newTask.setId(task.getId());
                    newTask.setPos(task.getPos());

                    if (isCompleted != task.isCompleted()) newTask.setCompletion(isCompleted);
                    else {
                        newTask.setCompleted(isCompleted);
                        newTask.setCompletedDateinMillis(task.getCompletedDateinMillis());
                    }
                    if (isImportant != task.isImportant()) newTask.setImportants(isImportant);
                    else {
                        newTask.setImportant(isImportant);
                        newTask.setStarredDateinMillis(task.getStarredDateinMillis());
                    }

                    new Thread(() -> roomDao.update(newTask)).start();
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

    private long getCreationDateinMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }


    public String getFullDateTimeFromMillis(Long millis) {
        if (millis == null) return null;

        Date date = new Date(millis);
        SimpleDateFormat str = new SimpleDateFormat("EEEE, dd MMMM, yyyy\nhh:mm a", Locale.getDefault());
        return str.format(date);
    }

    public String getFullDateFromMillis(Long millis) {
        if (millis == null) return null;

        Date date = new Date(millis);
        SimpleDateFormat str = new SimpleDateFormat("EEEE, dd MMMM, yyyy", Locale.getDefault());
        return str.format(date);
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


    public static void addTaskListener(TaskListener taskListener) {
        TaskDialog.taskListener = taskListener;
    }
}
