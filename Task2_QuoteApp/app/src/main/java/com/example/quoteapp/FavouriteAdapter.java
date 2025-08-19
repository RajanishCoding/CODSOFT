package com.example.quoteapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FavouriteAdapter extends ListAdapter<Quote, FavouriteAdapter.QuoteViewHolder> {

    private Context context;
    private RoomDao roomDao;
    private ViewModel viewModel;
    private FragmentActivity activity;

    public static DiffUtil.ItemCallback<Quote> diffCallback = new DiffUtil.ItemCallback<Quote>() {
        @Override
        public boolean areItemsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
            Log.d("tagge", "areItemsTheSame1: " + newItem.getAuthor() + (oldItem.getId() == newItem.getId()));
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
            Log.d("tagge", "areItemsTheSame2: " + newItem.getAuthor() + (oldItem.equals(newItem)));
            return false;
        }
    };

    public FavouriteAdapter(Context context, FragmentActivity activity) {
        super(diffCallback);
        this.context = context;
        this.activity = activity;
        viewModel = new ViewModelProvider(activity).get(ViewModel.class);
        roomDao = RoomDB.getDatabase(context).roomDao();
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView author;
        TextView date;
        ImageButton favorB;

        public QuoteViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.contentT);
            author = view.findViewById(R.id.authorT);
            date = view.findViewById(R.id.dateT);
            favorB = view.findViewById(R.id.favorB);
        }
    }

    @NonNull
    @Override
    public FavouriteAdapter.QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        QuoteViewHolder holder = new QuoteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.QuoteViewHolder holder, int position) {
        Quote quote = getItem(position);

        holder.content.setText(quote.getContent());
        holder.author.setText(quote.getAuthor());
        holder.date.setText(getFormattedDate(quote.getDateAddedMillis()));
        holder.favorB.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_favorite));

        holder.favorB.setOnClickListener(v -> {
            int p = holder.getAdapterPosition();
            if (p != RecyclerView.NO_POSITION){
                Quote quote1 = getItem(p);
                holder.favorB.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_favorite_border));
                new Thread(() -> roomDao.delete(quote1)).start();
                viewModel.sendAdapterEvent(quote1.getId());
                Toast.makeText(context, "Removed from Favourites!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int p = holder.getAdapterPosition();
            if (p != RecyclerView.NO_POSITION){
                Quote quote1 = getItem(p);
                QuoteDialog quoteDialog = new QuoteDialog(quote1);
                quoteDialog.show(activity.getSupportFragmentManager(), "tag");
            }
        });
    }

    private String getFormattedDate(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy • hh:mm a", Locale.getDefault());
        String date = simpleDateFormat.format(new Date(millis));
        return date;
    }
}
