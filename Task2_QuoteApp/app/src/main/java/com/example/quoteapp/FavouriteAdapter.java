package com.example.quoteapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteAdapter extends ListAdapter<Quote, FavouriteAdapter.QuoteViewHolder> {

    private Context context;
    private RoomDao roomDao;
    private ViewModel viewModel;

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
        viewModel = new ViewModelProvider(activity).get(ViewModel.class);
        roomDao = RoomDB.getDatabase(context).roomDao();
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView author;
        ImageButton favorB;

        public QuoteViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.contentT);
            author = view.findViewById(R.id.authorT);
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
        holder.favorB.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_favorite));

        holder.favorB.setOnClickListener(v -> {
            int p = (holder.getAdapterPosition());
            if (p != RecyclerView.NO_POSITION){
                Quote quote1 = getItem(p);
                holder.favorB.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.round_favorite_border));
                new Thread(() -> roomDao.delete(quote1)).start();
                viewModel.sendAdapterEvent(quote1.getId());
            }
        });
    }
}
