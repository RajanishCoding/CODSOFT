package com.example.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.todolist.Room.RoomDB;
import com.example.todolist.Room.RoomDao;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TaskDialog extends DialogFragment {

    public interface TaskListener {
        void onTaskAdded(Task task);
//        void onTaskUpdated(Task task, int taskIndex);
    }

    public static TaskListener taskListener;

    private RoomDao roomDao;

    private int fragmentPos;
    private int mode;
    private Task task;
//    private int taskIndex;

    private TextView title;
    private TextInputEditText titleE;
    private TextInputEditText detE;
    private TextInputLayout dateL;
    private TextInputEditText dateE;
    private TextInputLayout timeL;
    private TextInputEditText timeE;
    private AutoCompleteTextView priorityDropdown;
    private CheckBox checkB;
    private Button cancelB;
    private Button doneB;
    private LinearLayout btnLayout;

    private ImageButton infoB;
    private CheckBox compB;
    private ImageButton starB;
    private ImageButton delB;

    private boolean isCompleted;
    private boolean isImportant;

    public long selectedTimeMillis;
    public long creationTimeMillis;
    private MyPair<Integer, Integer> time;
    public int priority = 0;

    private Context context;

    public TaskDialog(int fragmentPos, int mode, Task task) {
        this.fragmentPos = fragmentPos;
        this.mode = mode;
        this.task = task;
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
        dateL = view.findViewById(R.id.dateInputL);
        dateE = view.findViewById(R.id.datePickerE);
        timeL = view.findViewById(R.id.timeInputL);
        timeE = view.findViewById(R.id.timePickerE);
        priorityDropdown = view.findViewById(R.id.priorityDropdown);
        checkB = view.findViewById(R.id.checkB);

        cancelB = view.findViewById(R.id.decline_button);
        doneB = view.findViewById(R.id.accept_button);
        btnLayout = view.findViewById(R.id.buttonsLayout);

        infoB = view.findViewById(R.id.infoB);
        compB = view.findViewById(R.id.completeB);
        starB = view.findViewById(R.id.starB);
        delB = view.findViewById(R.id.delB);

        context = requireContext();
        roomDao = RoomDB.getDatabase(context).roomDao();

        setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startAnimation(view);

        String[] priorities = {"None", "Low", "Normal", "High", "Urgent"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.dropdown_layout, priorities);
        priorityDropdown.setAdapter(adapter);

        if (mode == 2) {
            title.setText("Edit Task");

            titleE.setText(task.getTitle());
            detE.setText(task.getDetail());
            dateE.setText(task.getDueDate());

            if (!task.getDueTime().isEmpty()){
                timeL.setVisibility(View.VISIBLE);
                timeE.setText(task.getDueTime());
            }

            priority = task.getPriority();
            priorityDropdown.setText(priorities[priority], false);

            time = task.getTime();
            isCompleted = task.isCompleted();
            isImportant = task.isImportant();
            compB.setChecked(isCompleted);
            starB.setImageResource(isImportant ? R.drawable.round_star : R.drawable.round_star_outline);
            selectedTimeMillis = task.getDateInMillis();
        }
        else {
            delB.setVisibility(View.GONE);
            infoB.setVisibility(View.GONE);

            if (fragmentPos == 0) {
                starB.setImageResource(R.drawable.round_star);
                isImportant = true;
            }
            else if (fragmentPos == 2) {
                compB.setChecked(true);
                isCompleted = true;
            }
        }


        priorityDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            priority = position;
        });

        dateE.setOnClickListener(v -> {
            showDatePicker();
        });

        dateL.setEndIconOnClickListener(v -> {
            if (timeL.getVisibility() == View.GONE) {
                if (time != null) showTimePicker(time.first, time.second);
                else showTimePicker(11, 0);
            }
            else {
                timeL.setVisibility(View.GONE);
                timeE.setText(null);
                time = null;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedTimeMillis);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                selectedTimeMillis = calendar.getTimeInMillis();
            }
        });

        timeE.setOnClickListener(v -> {
            showTimePicker(time.first, time.second);
        });

        infoB.setOnClickListener(v -> {
            onInfoClicked();
        });

        delB.setOnClickListener(v -> {
            onDeleteCLicked();
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
                            dateE.getText().toString().trim(), timeE.getText().toString().trim(),
                            priority, selectedTimeMillis, creationTimeMillis);

                    task.setTime(time);

                    if (isImportant) task.setImportants(true);
                    else task.setImportant(false);
                    if (isCompleted) task.setCompletion(true);
                    else task.setCompleted(false);

                    taskListener.onTaskAdded(task);

                    if (!isCompleted && task.getDateInMillis() > System.currentTimeMillis()) {
                        if (timeL.getVisibility() == View.VISIBLE)
                            NotificationAlarm.scheduleTask(context, task.getId(), task.getTitle(), task.getDateInMillis());
                        else
                            NotificationWork.scheduleTask(context, task.getId(), task.getTitle(), task.getDateInMillis());
                    }
                }
                else {
                    Task newTask = new Task(titleE.getText().toString().trim(), detE.getText().toString().trim(),
                            dateE.getText().toString().trim(), timeE.getText().toString().trim(),
                            priority, selectedTimeMillis, task.getCreationDateinMillis());

                    newTask.setId(task.getId());
                    newTask.setPos(task.getPos());

                    newTask.setTime(time);

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

                    if (isCompleted || newTask.getDateInMillis() < System.currentTimeMillis()) {
                        if (timeL.getVisibility() == View.VISIBLE)
                            NotificationAlarm.cancelScheduledTask(context, newTask.getId(), newTask.getTitle());
                        else
                            NotificationWork.cancelScheduledTask(context, newTask.getId());
                    }
                    else {
                        if (timeL.getVisibility() == View.VISIBLE)
                            NotificationAlarm.updateScheduledTask(context, newTask.getId(), newTask.getTitle(), newTask.getDateInMillis());
                        else
                            NotificationWork.updateScheduledTask(context, newTask.getId(), newTask.getTitle(), newTask.getDateInMillis());
                    }
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


    private void onInfoClicked() {
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
    }

    private void onDeleteCLicked() {
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
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (time != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                calendar.set(Calendar.HOUR_OF_DAY, time.first);
                calendar.set(Calendar.MINUTE, time.second);
                calendar.set(Calendar.SECOND, 0);
                selectedTimeMillis = calendar.getTimeInMillis();
            } else selectedTimeMillis = selection;

            // Format: "Mon, 22 Jul, 2025"
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault());
            String str = dateFormat.format(new Date(selectedTimeMillis));
            dateE.setText(str);

//            if (!dateE.getText().toString().isEmpty()) {
//                String s1 = "Send a Notification ";
//                String s2 =  dateE.getText().toString();
//                String s3 = timeE.getText().toString().isEmpty() ? "around 11:00 AM." : "at " + timeE.getText();
//                checkB.setText(at " +  + formatDueDate(str) + );
//            }
        });

        datePicker.show(getChildFragmentManager(), "date");
    }

    private void showTimePicker(int hr, int m) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hr)
                .setMinute(m)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(view -> {
            time = new MyPair<>(timePicker.getHour(), timePicker.getMinute());

            if (selectedTimeMillis != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedTimeMillis);
                calendar.set(Calendar.HOUR_OF_DAY, time.first);
                calendar.set(Calendar.MINUTE, time.second);
                calendar.set(Calendar.SECOND, 0);
                selectedTimeMillis = calendar.getTimeInMillis();
            }

            // Format: "11:55 AM"
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String str = dateFormat.format(new Date(selectedTimeMillis));
            timeE.setText(str.toUpperCase());
            timeL.setVisibility(View.VISIBLE);


        });

        timePicker.show(getChildFragmentManager(), "time");
    }

    private void startAnimation(View root) {
        root.setScaleX(0.5f);
        root.setScaleY(0.5f);
        root.setTranslationY(50);
        root.setAlpha(0f);
        root.animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

//        LinearLayout rootl = (LinearLayout) root;

        titleE.setAlpha(0f);
        titleE.animate()
                .alpha(1f)
                .setStartDelay(150)
                .setDuration(300)
                .start();

        detE.setAlpha(0f);
        detE.animate()
                .alpha(1f)
                .setStartDelay(250)
                .setDuration(300)
                .start();

        dateE.setAlpha(0f);
        dateE.animate()
                .alpha(1f)
                .setStartDelay(350)
                .setDuration(300)
                .start();

        timeE.setAlpha(0f);
        timeE.animate()
                .alpha(1f)
                .setStartDelay(450)
                .setDuration(300)
                .start();

        priorityDropdown.setAlpha(0f);
        priorityDropdown.animate()
                .alpha(1f)
                .setStartDelay(550)
                .setDuration(300)
                .start();

        btnLayout.setAlpha(0f);
        btnLayout.animate()
                .alpha(1f)
                .setStartDelay(650)
                .setDuration(300)
                .start();
    }


    private boolean isTaskValid() {
        if (titleE.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Please write the Title!", Toast.LENGTH_SHORT).show();
        }
        else if (dateE.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Please select the Date!", Toast.LENGTH_SHORT).show();
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

    public static String formatDueDate(String date) {
        int current = Calendar.getInstance().get(Calendar.YEAR);

        String[] parts = date.split(", ");
        int year = Integer.parseInt(parts[1]);

        if (year == current) return parts[1];
        else return parts[1] + ", " + parts[2];
    }


    public static void addTaskListener(TaskListener taskListener) {
        TaskDialog.taskListener = taskListener;
    }
}
