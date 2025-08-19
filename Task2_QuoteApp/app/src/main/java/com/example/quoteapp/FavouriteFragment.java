package com.example.quoteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouriteFragment extends Fragment {

    private RoomDao roomDao;
    private RecyclerView recyclerView;
    private FavouriteAdapter adapter;

    private ViewModel viewModel;
    LiveData<List<Quote>> liveData;

    private boolean isAsc;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        roomDao = RoomDB.getDatabase(requireContext()).roomDao();
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = prefs.edit();

        adapter = new FavouriteAdapter(requireContext(), requireActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        isAsc = prefs.getBoolean("isAsc", false);

        setObserver();

        viewModel.getFragmentEvent().observe(getViewLifecycleOwner(), i -> {
            if (liveData != null) liveData.removeObservers(getViewLifecycleOwner());

            isAsc = !isAsc;
            editor.putBoolean("isAsc", isAsc);
            editor.apply();

            setObserver();
        });

    }

    private void setObserver() {
        liveData = isAsc ? roomDao.getAllQuotesAsc() : roomDao.getAllQuotesDesc();
        liveData.observe(getViewLifecycleOwner(), quotes -> {
            adapter.submitList(quotes);
            recyclerView.post(() -> recyclerView.scrollToPosition(0));
        });
    }
}