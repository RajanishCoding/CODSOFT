package com.example.todolist;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SortViewModel extends ViewModel {
    private final MutableLiveData<Pair<Integer, Boolean>> sortConfig = new MutableLiveData<>(new Pair<>(0, true));
    private Integer pos;

    public LiveData<Pair<Integer, Boolean>> getSortConfig() {
        return sortConfig;
    }

    public void setSortConfig(int sortType, boolean isAsc) {
        sortConfig.setValue(new Pair<>(sortType, isAsc));
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}

