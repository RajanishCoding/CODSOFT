package com.example.quoteapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class QuoteDialog extends DialogFragment {

    private CardView quoteView;
    private TextView quoteText;
    private TextView authorText;
    private LinearLayout buttonsLayout;
    private Button favorB;
    private Button shareB;
    private ImageButton copyB;

    private ViewModel viewModel;

    private Quote quote;

    private String content, author;

    private RoomDao roomDao;


    public QuoteDialog(Quote quote) {
        this.quote = quote;
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
        View view = inflater.inflate(R.layout.quote_dialog, container, false);

        quoteView = view.findViewById(R.id.quoteView);
        quoteText = view.findViewById(R.id.quoteText);
        authorText = view.findViewById(R.id.authorText);
        buttonsLayout = view.findViewById(R.id.buttonsLayout);
        favorB = view.findViewById(R.id.favorB);
        shareB = view.findViewById(R.id.shareB);
        copyB = view.findViewById(R.id.copyB);

        setCancelable(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        roomDao = RoomDB.getDatabase(requireContext()).roomDao();

        showQuote();
        startAnimation();


        shareB.setOnClickListener(v -> {
            String shareText = content + "\n-" + author;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Share quote via"));
        });

        favorB.setOnClickListener(v -> {
            Quote quote = new Quote(content, author);
            TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textGrey)));
            new Thread(() -> roomDao.delete(quote)).start();
            viewModel.sendAdapterEvent(quote.getId());
            Toast.makeText(requireContext(), "Removed from Favourites!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        copyB.setOnClickListener(v -> {
            String copyText = content + "\n-" + author;
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", copyText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Quote Copied!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showQuote() {
        content = quote.getContent();
        author = quote.getAuthor();
        quoteText.setText(content);
        authorText.setText("-- " + author + " --");
    }

    private void startAnimation() {
        quoteView.setAlpha(0f);
        quoteView.setScaleY(0f);
        quoteText.setAlpha(0f);
        quoteText.setScaleX(0f);
        quoteText.setScaleY(0f);
        authorText.setAlpha(0f);
        authorText.setScaleX(0f);
        buttonsLayout.setAlpha(0f);

        quoteView.animate()
                .scaleY(1f)
                .alpha(1f)
                .setDuration(600)
//                .setInterpolator(new BounceInterpolator())
                .start();

//        quoteText.setPivotX(quoteText.getWidth()/2f);
//        quoteText.setPivotY(quoteText.getHeight()/2f);
        quoteText.animate()
//                .withStartAction(() -> quoteText.setAlpha(1f))
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(600)
                .setDuration(600)
                .start();

        authorText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleX(1f)
                .alpha(1f)
                .setStartDelay(1200)
                .setDuration(400)
                .start();

        buttonsLayout.animate()
                .alpha(1f)
                .setStartDelay(1600)
                .setDuration(500)
                .start();
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

}
