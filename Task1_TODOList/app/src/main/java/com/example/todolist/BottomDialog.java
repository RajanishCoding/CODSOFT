package com.example.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDialog extends BottomSheetDialogFragment {

    public interface BottomSheetDialogListeners {
        void onMyOrderClicked(boolean isAsc);
        void onDueDateClicked(boolean isAsc);
    }

    private static BottomSheetDialogListeners listener;

    private int pos;
    private boolean isAsc;

    private Button orderB;
    private Button myOrderB;
    private Button duedateB;

    private Drawable asc;
    private Drawable desc;
    private Drawable check;


    public BottomDialog(int selectedPos, boolean isAsc) {
        this.pos = selectedPos;
        this.isAsc = isAsc;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        orderB = view.findViewById(R.id.orderB);
        myOrderB = view.findViewById(R.id.myOrderB);
        duedateB = view.findViewById(R.id.dueDateB);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        asc = ContextCompat.getDrawable(requireContext(), R.drawable.asc_sort);
        desc = ContextCompat.getDrawable(requireContext(), R.drawable.desc_sort);
        check = ContextCompat.getDrawable(requireContext(), R.drawable.round_check);

        SortViewModel sortViewModel = new ViewModelProvider(requireActivity()).get(SortViewModel.class);

        if (pos == 0) {
            myOrderB.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
        }
        else {
            duedateB.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
        }

        if (isAsc) {
            orderB.setText("ASC");
            orderB.setCompoundDrawablesWithIntrinsicBounds(asc, null, null, null);
        }
        else {
            orderB.setText("DESC");
            orderB.setCompoundDrawablesWithIntrinsicBounds(desc, null, null, null);
        }


        orderB.setOnClickListener(v -> {
            if (orderB.getText().toString().equals("ASC")) {
                orderB.setText("DESC");
                orderB.setCompoundDrawablesWithIntrinsicBounds(desc, null, null, null);
                isAsc = false;
            }
            else {
                orderB.setText("ASC");
                orderB.setCompoundDrawablesWithIntrinsicBounds(asc, null, null, null);
                isAsc = true;
            }
        });


        myOrderB.setOnClickListener(v -> {
            sortViewModel.setSortConfig(0, isAsc);
            editor.putInt("sortType", 0);
            editor.putBoolean("sortOrder", isAsc);
            editor.apply();
            dismiss();
        });

        duedateB.setOnClickListener(v -> {
            sortViewModel.setSortConfig(1, isAsc);
            editor.putInt("sortType", 1);
            editor.putBoolean("sortOrder", isAsc);
            editor.apply();
            dismiss();
        });
    }
}
