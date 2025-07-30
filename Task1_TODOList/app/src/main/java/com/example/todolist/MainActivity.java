package com.example.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPageAdapter viewPageAdapter;
    private ViewPager2 viewPager;

    private Toolbar toolbar;

    private ImageButton sortB;
    private ImageButton modeB;

    private int sortType;
    private boolean sortOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.toolbar));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        Editor editor = prefs.edit();


        toolbar = findViewById(R.id.toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        sortB = findViewById(R.id.sortB);
        modeB = findViewById(R.id.modeB);


        viewPageAdapter = new ViewPageAdapter(this);
        viewPager.setAdapter(viewPageAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.round_star);
                    break;
                case 1:
                    tab.setText("Task");
                    break;
                case 2:
                    tab.setText("Completed");
                    break;
            }
        }).attach();

        viewPager.setCurrentItem(1, true);


        sortB.setOnClickListener(v -> {
            sortType = prefs.getInt("sortType", 0);
            sortOrder = prefs.getBoolean("sortOrder", true);
            BottomDialog bottomSheet = new BottomDialog(sortType, sortOrder);
            bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");
        });
    }
}