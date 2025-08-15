package com.example.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Editor editor;

    private TabLayout tabLayout;
    private ViewPageAdapter viewPageAdapter;
    private ViewPager2 viewPager;
    private int selectedPos;

    private Toolbar toolbar;

    private ImageButton addTaskB;
    private ImageButton sortB;
    private ImageButton modeB;

    private int themeMode;

    private int sortType;
    private boolean sortOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        themeMode = prefs.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setThemeModeStart();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedPos = savedInstanceState != null ? savedInstanceState.getInt("itemPos", 1) : 1;
        Log.d("darkdkd", "onSaveInstanceState11: " + selectedPos);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.toolbarG));


        toolbar = findViewById(R.id.toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        addTaskB = findViewById(R.id.addTaskB);
        sortB = findViewById(R.id.sortB);
        modeB = findViewById(R.id.modeB);


        modeB.setImageResource(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO ? R.drawable.round_dark_mode : R.drawable.round_light_mode);

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

        viewPager.setCurrentItem(selectedPos, true);
        tabLayout.selectTab(tabLayout.getTabAt(selectedPos));

        addTaskB.setOnClickListener(v -> {
            TaskDialog taskDialog = new TaskDialog(viewPager.getCurrentItem(), 1, null);
            taskDialog.show(getSupportFragmentManager(), taskDialog.getTag());
        });

        sortB.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0) {
                openStarSortDialog();
            }
            else if (viewPager.getCurrentItem() == 1) {
                openTaskSortDialog();
            }
            else {
                openCompleteSortDialog();
            }
        });

        modeB.setOnClickListener(v -> {
            if (themeMode == 2) themeMode = 1;
            else themeMode = 2;
            setThemeMode(themeMode);
        });
    }

    private void setThemeModeStart() {
        int current = AppCompatDelegate.getDefaultNightMode();

        if (themeMode != current) {
            AppCompatDelegate.setDefaultNightMode(themeMode);
        }

        // -1 - follow, 1- Light, 2 - Dark
    }

    private void setThemeMode(int mode) {
        int saved = prefs.getInt("themeMode", -1);

        if (saved == mode) {
            return;
        }

        editor.putInt("themeMode", mode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void openTaskSortDialog() {
        sortType = prefs.getInt("sortType_task", 0);
        sortOrder = prefs.getBoolean("sortOrder_task", true);
        BottomDialog bottomSheet = new BottomDialog(1, sortType, sortOrder);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");
    }

    private void openStarSortDialog() {
        sortType = prefs.getInt("sortType_star", 0);
        sortOrder = prefs.getBoolean("sortOrder_star", true);
        BottomDialog bottomSheet = new BottomDialog(0, sortType, sortOrder);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");
    }

    private void openCompleteSortDialog() {
        sortType = prefs.getInt("sortType_comp", 0);
        sortOrder = prefs.getBoolean("sortOrder_comp", true);
        BottomDialog bottomSheet = new BottomDialog(2, sortType, sortOrder);
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheet");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("itemPos", viewPager.getCurrentItem());
        Log.d("darkdkd", "onSaveInstanceState: " + viewPager.getCurrentItem());
    }
}