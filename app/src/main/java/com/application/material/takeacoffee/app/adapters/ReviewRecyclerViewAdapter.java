package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.singletons.PicassoSingleton;
import com.application.material.takeacoffee.app.utils.ExpandableTextView;
import com.application.material.takeacoffee.app.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 09/03/16.
 */
public class ReviewRecyclerViewAdapter extends
        RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<Review> itemList;
    private final WeakReference<Context> contextWeakRef;
    private CustomItemClickListener listener;
    private ReviewRecyclerViewAdapter.ViewHolder holder;
    private String GUEST_USER = "Guest";
    private boolean empty;

    public ReviewRecyclerViewAdapter(WeakReference<Context> ctx,
                                     ArrayList<Review> itemList) {
        this.itemList = itemList;
        this.contextWeakRef = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        holder = new ReviewRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review item = itemList.get(position);
        holder.usernameText.setText(item.getUser() == null ? GUEST_USER :
                item.getUser());
        holder.dateText.setText(Utils.getFormattedTimestamp(item.getTimestamp()));
        holder.reviewText.setText(item.getComment());
        holder.reviewRating.setText(item.getRating() + ".0"); //TODO change it
        setProfilePicByUrl(item.getProfilePhotoUrl(), holder.profilePictureImageView);
    }

    /**
     *
     * @param photoRef
     */
    private void setProfilePicByUrl(String photoRef, final ImageView imageView) {
        Drawable defaultIcon = ContextCompat.getDrawable(contextWeakRef.get(),
                R.drawable.ic_perm_identity_black_48dp);
        if (photoRef == null) {
            imageView.setImageDrawable(defaultIcon);
            return;
        }
        PicassoSingleton.getInstance(contextWeakRef, null)
                .setProfilePictureAsync(imageView, photoRef, defaultIcon);
    }
    
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     *
     * @param emptyResult
     */
    public void setEmptyResult(boolean emptyResult) {
        this.empty = emptyResult;
        Log.e("TAG", "empty result");
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView usernameText;
        private final TextView dateText;
        private final ExpandableTextView reviewText;
        private final TextView reviewRating;
        private final ImageView profilePictureImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameText = ((TextView) itemView.findViewById(R.id.usernameTextId));
            dateText = ((TextView) itemView.findViewById(R.id.dateTextId));
            reviewText = (ExpandableTextView) itemView.findViewById(R.id.reviewTextId);
            reviewRating = (TextView) itemView.findViewById(R.id.reviewRatingTextViewId);
            profilePictureImageView = (ImageView) itemView.findViewById(R.id.profilePictureViewId);
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
