package com.example.quoteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuoteFragment extends Fragment {

    private CardView quoteView;
    private TextView quoteText;
    private TextView authorText;
    private LinearLayout buttonsLayout;
    private Button favorB;
    private Button shareB;

    private boolean isRandom;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

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

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        String storedDate = prefs.getString("date", "");
        String todayDate = getTodayDate();

        isRandom = !storedDate.equals(todayDate);
        showQuote();
        startAnimation();

        shareB.setOnClickListener(v -> {
            String content = quoteText.getText().toString();
            String author= authorText.getText().toString();
            String shareText = content + "\n-" + author.substring(3, author.length()-3);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Share quote via"));
        });

        favorB.setOnClickListener(v -> {
;
        });
    }

    private void startAnimation() {
        quoteView.animate()
                .scaleY(1f)
                .alpha(1f)
                .setDuration(600)
//                .setInterpolator(new BounceInterpolator())
                .start();

        quoteText.setPivotY(0f);
        quoteText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleY(1f)
                .setStartDelay(600)
                .setDuration(600)
                .start();

        authorText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleX(1f)
                .alpha(1f)
                .setStartDelay(1000)
                .setDuration(400)
                .start();

        buttonsLayout.animate()
                .alpha(1f)
                .setStartDelay(1400)
                .setDuration(500)
                .start();
   }

    private void showQuote() {
       String content, author;
       JSONObject quote;
       String jsonString = JsonParser.loadJSONFromAsset(requireContext(), "quoteList.json");

       try {
           JSONObject root = new JSONObject(jsonString);
           JSONArray results = root.getJSONArray("results");

           if (isRandom) {
               Random random = new Random();
               int randomIndex = random.nextInt(results.length());
               quote = results.getJSONObject(randomIndex);

               editor.putString("date", getTodayDate());
               editor.putInt("index", randomIndex);
               editor.apply();
           }
           else {
               int index = prefs.getInt("index", 0);
               quote = results.getJSONObject(index);
           }

           content = quote.getString("content");
           author = quote.getString("author");

           quoteText.setText(content);
           authorText.setText("-- " + author + " --");
       }
       catch (JSONException e) {
           e.printStackTrace();
       }
   }

    public String getTodayDate() {
        LocalDate today = LocalDate.now();
        return today.toString();
    }
}