package com.example.quoteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private ImageButton refreshB;
    private ViewModel viewModel;

    private boolean isAsc;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700));

        prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        refreshB = findViewById(R.id.refreshB);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Quote");
                    break;
                case 1:
                    tab.setIcon(R.drawable.round_favorite);
                    break;
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    refreshB.setImageResource(R.drawable.round_refresh);
                }
                else if (tab.getPosition() == 1) {
                    isAsc = prefs.getBoolean("isAsc", false);
                    refreshB.setImageResource(isAsc ? R.drawable.asc_sort : R.drawable.desc_sort);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        refreshB.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0)
                viewModel.sendToolbarEvent("refresh");
            else {
                isAsc = prefs.getBoolean("isAsc", false);
                viewModel.sendFragmentEvent(isAsc);
                refreshB.setImageResource(isAsc ? R.drawable.desc_sort : R.drawable.asc_sort);
            }
        });
    }
}