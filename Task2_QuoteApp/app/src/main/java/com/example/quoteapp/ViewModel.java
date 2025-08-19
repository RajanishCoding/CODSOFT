package com.example.quoteapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private final MutableLiveData<String> toolbarEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fragmentEvent = new MutableLiveData<>();
    private final MutableLiveData<String> adapterEvent = new MutableLiveData<>();

    public LiveData<String> getToolbarEvent() {
        return toolbarEvent;
    }

    public void sendToolbarEvent(String button) {
        toolbarEvent.setValue(button);
    }

    public LiveData<Boolean> getFragmentEvent() {
        return fragmentEvent;
    }

    public void sendFragmentEvent(Boolean isAsc) {
        fragmentEvent.setValue(isAsc);
    }

    public LiveData<String> getAdapterEvent() {
        return adapterEvent;
    }

    public void sendAdapterEvent(String id) {
        adapterEvent.setValue(id);
    }
}
