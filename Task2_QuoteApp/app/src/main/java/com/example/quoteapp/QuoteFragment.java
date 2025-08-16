package com.example.quoteapp;

import android.animation.TimeInterpolator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuoteFragment extends Fragment {

    private CardView quoteView;
    private TextView quoteText;
    private TextView authorText;
    private LinearLayout buttonsLayout;
    private Button favorB;
    private Button shareB;

    public QuoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote, container, false);

        quoteView = view.findViewById(R.id.quoteView);
        quoteText = view.findViewById(R.id.quoteText);
        authorText = view.findViewById(R.id.authorText);
        buttonsLayout = view.findViewById(R.id.buttonsLayout);
        favorB = view.findViewById(R.id.favorB);
        shareB = view.findViewById(R.id.shareB);

        quoteView.setAlpha(0f);
        quoteView.setScaleY(0f);
        quoteText.setAlpha(0f);
        quoteText.setScaleY(0f);
        authorText.setAlpha(0f);
        authorText.setScaleX(0f);
        buttonsLayout.setAlpha(0f);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startAnimation();


    }

    private void startAnimation() {
        quoteView.animate()
                .scaleY(1f)
                .alpha(1f)
                .setDuration(800)
//                .setInterpolator(new BounceInterpolator())
                .start();

        quoteText.setPivotY(0f);
        quoteText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleY(1f)
                .setStartDelay(800)
                .setDuration(600)
                .start();

        authorText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleX(1f)
                .alpha(1f)
                .setStartDelay(1200)
                .setDuration(600)
                .start();

        buttonsLayout.animate()
                .alpha(1f)
                .setStartDelay(1600)
                .setDuration(600)
                .start();
   }
}