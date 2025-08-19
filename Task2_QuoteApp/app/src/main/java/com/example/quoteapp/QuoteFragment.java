package com.example.quoteapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Random;

public class QuoteFragment extends Fragment {

    private CardView quoteView;
    private TextView quoteText;
    private TextView authorText;
    private LinearLayout buttonsLayout;
    private Button favorB;
    private Button shareB;
    private ImageButton copyB;

    private String content, author;

    private boolean isRandom;
    private ViewModel viewModel;

    private boolean isFavourite;

    private RoomDao roomDao;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

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
        copyB = view.findViewById(R.id.copyB);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomDao = RoomDB.getDatabase(requireContext()).roomDao();

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        isFavourite = prefs.getBoolean("isFav", false);


        String storedDate = prefs.getString("date", "");
        String todayDate = getTodayDate();

        isRandom = !storedDate.equals(todayDate);
        showQuote(isRandom);
        startAnimation();

        viewModel.getToolbarEvent().observe(getViewLifecycleOwner(), event -> {
            if ("refresh".equals(event)) {
                showQuote(true);
                startAnimation2();
            }
        });

        viewModel.getAdapterEvent().observe(getViewLifecycleOwner(), id -> {
            if (id.equals(content + author)) {
                TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textGrey)));
                isFavourite = false;
            }
        });

        shareB.setOnClickListener(v -> {
            String shareText = content + "\n-" + author;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            startActivity(Intent.createChooser(shareIntent, "Share quote via"));
        });

        favorB.setOnClickListener(v -> {
            Quote quote = new Quote(content, author);

            if (isFavourite) {
                TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textGrey)));
                isFavourite = false;
                new Thread(() -> roomDao.delete(quote)).start();
                Toast.makeText(requireContext(), "Removed from Favourites!", Toast.LENGTH_SHORT).show();
            }
            else {
                TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red)));
                isFavourite = true;
                new Thread(() -> roomDao.insert(quote)).start();
                Toast.makeText(requireContext(), "Added to Favourites!", Toast.LENGTH_SHORT).show();
            }
        });

        copyB.setOnClickListener(v -> {
            String copyText = content + "\n-" + author;
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", copyText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Quote Copied!", Toast.LENGTH_SHORT).show();
        });
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

    private void startAnimation2() {
        quoteText.setAlpha(0f);
        quoteText.setScaleX(0f);
        quoteText.setScaleY(0f);
        authorText.setAlpha(0f);
        authorText.setScaleX(0f);

        quoteText.animate()
//                .withStartAction(() -> quoteText.setAlpha(1f))
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(0)
                .setDuration(600)
                .start();

        authorText.animate()
                .withStartAction(() -> quoteText.setAlpha(1f))
                .scaleX(1f)
                .alpha(1f)
                .setStartDelay(600)
                .setDuration(400)
                .start();
    }

    private void showQuote(boolean isRandom) {
       JSONObject quote;
       JSONArray quotesList = getQuotesList();

       try {
           if (isRandom) {
               Random random = new Random();
               int randomIndex = random.nextInt(quotesList.length());
               quote = quotesList.getJSONObject(randomIndex);

               editor.putString("date", getTodayDate());
               editor.putInt("index", randomIndex);
               editor.apply();
           }
           else {
               int index = prefs.getInt("index", 0);
               quote = quotesList.getJSONObject(index);
           }

           content = quote.getString("content");
           author = quote.getString("author");

           quoteText.setText(content);
           authorText.setText("-- " + author + " --");
       }
       catch (JSONException e) {
           e.printStackTrace();
       }

       new Thread(() -> {
            if (roomDao.findQuote(content + author) > 0) {
                TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red)));
                isFavourite = true;
            }
            else {
                TextViewCompat.setCompoundDrawableTintList(favorB, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.textGrey)));
                isFavourite = false;
            }
        }).start();
   }

    private JSONArray getQuotesList() {
        JSONArray quoteArray = null;
        String jsonString = JsonParser.loadJSONFromAsset(requireContext(), "quoteList.json");

        try {
            JSONObject root = new JSONObject(jsonString);
            quoteArray = root.getJSONArray("results");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return quoteArray;
    }

    public String getTodayDate() {
        LocalDate today = LocalDate.now();
        return today.toString();
    }
}