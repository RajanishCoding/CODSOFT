package com.example.quoteapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private final MutableLiveData<String> toolbarEvent = new MutableLiveData<>();
    private final MutableLiveData<String> adapterEvent = new MutableLiveData<>();

    public LiveData<String> getToolbarEvent() {
        return toolbarEvent;
    }

    public void sendToolbarEvent(String button) {
        toolbarEvent.setValue(button);
    }

    public LiveData<String> getAdapterEvent() {
        return adapterEvent;
    }

    public void sendAdapterEvent(String id) {
        adapterEvent.setValue(id);
    }
}
