package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.utils.CacheManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 09/03/16.
 */
public class ReviewRecyclerViewAdapter extends
        RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<Review> itemList;
    private CustomItemClickListener listener;
    private ReviewRecyclerViewAdapter.ViewHolder holder;
    private String GUEST_USER = "Guest";

    public ReviewRecyclerViewAdapter(WeakReference<Context> context,
                                     ArrayList<Review> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_template, parent, false);
        holder = new ReviewRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.usernameText.setText(itemList.get(position).getUser() == null ? GUEST_USER :
                itemList.get(position).getUser().getUsername());
        holder.dateText.setText("" + itemList.get(position).getTimestamp());
        holder.reviewText.setText(itemList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setEmptyResult(boolean emptyResult) {
        Log.e("TAG", "empty result");
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView usernameText;
        private final TextView dateText;
        private final TextView reviewText;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameText = ((TextView) itemView.findViewById(R.id.usernameTextId));
            dateText = ((TextView) itemView.findViewById(R.id.dateTextId));
            reviewText = (TextView) itemView.findViewById(R.id.reviewTextId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    /**
     *
     * @param listener
     */
    public void setOnItemClickListener(CustomItemClickListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    public interface CustomItemClickListener {
        void onItemClick(int pos, View v);
    }

    public void addAllItems(ArrayList<Review> list) {
        itemList.clear();
        itemList.addAll(list);
        this.notifyDataSetChanged();
    }
}
