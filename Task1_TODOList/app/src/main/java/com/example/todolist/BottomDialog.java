package com.example.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDialog extends BottomSheetDialogFragment {

    public interface BottomSheetDialogListeners {
        void onMyOrderClicked(boolean isAsc);
        void onDueDateClicked(boolean isAsc);
    }

    private static BottomSheetDialogListeners listener;

    private int fragmentPos;
    private int pos;
    private boolean isAsc;

    private Button orderB;
    private Button order1B;
    private Button order2B;
    private Button order3B;

    private Drawable asc;
    private Drawable desc;
    private Drawable check;


    public BottomDialog(int fragmentPos, int selectedPos, boolean isAsc) {
        this.fragmentPos = fragmentPos;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        orderB = view.findViewById(R.id.orderB);
        order1B = view.findViewById(R.id.order1B);
        order2B = view.findViewById(R.id.order2B);
        order3B = view.findViewById(R.id.order3B);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (fragmentPos) {
            case 0:
                order2B.setText("Starred Recently");
                break;

            case 2:
                order2B.setText("Completion Date");
                order3B.setText("Creation Date");
                break;

            default: break;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        asc = ContextCompat.getDrawable(requireContext(), R.drawable.asc_sort);
        desc = ContextCompat.getDrawable(requireContext(), R.drawable.desc_sort);
        check = ContextCompat.getDrawable(requireContext(), R.drawable.round_check);

        SortViewModel sortViewModel = new ViewModelProvider(requireActivity()).get(SortViewModel.class);
        sortViewModel.setPos(fragmentPos);

        if (pos == 0) order1B.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
        else if (pos == 1) order2B.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
        else order3B.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);

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

        order1B.setOnClickListener(v -> {
            sortViewModel.setSortConfig(0, isAsc);
            dismiss();
        });

        order2B.setOnClickListener(v -> {
            sortViewModel.setSortConfig(1, isAsc);
            dismiss();
        });

        order3B.setOnClickListener(v -> {
            sortViewModel.setSortConfig(2, isAsc);
            dismiss();
        });
    }
}
