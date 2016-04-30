package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.singletons.PicassoSingleton;
import com.application.material.takeacoffee.app.singletons.RetrofitManager;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 09/03/16.
 */
public class PlacesGridViewAdapter extends RecyclerView.Adapter<PlacesGridViewAdapter.ViewHolder> {
    private final ArrayList<CoffeePlace> itemList;
    private final WeakReference<Context> contextWeakRef;
    private CustomItemClickListener listener;
    private PlacesGridViewAdapter.ViewHolder holder;
    private boolean isEmptyResult;


    public PlacesGridViewAdapter(WeakReference<Context> context, ArrayList<CoffeePlace> itemList) {
        this.itemList = itemList;
        this.contextWeakRef = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coffee_place, parent, false);
        holder = new PlacesGridViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTextView.setText(itemList.get(position).getName());
        holder.addressTextView.setText(itemList.get(position).getAddress());
        holder.ratingTextView.setText(getRatingByPos(position));
        String photoRef = itemList.get(position).getPhotoReference();
        setPhotoByUrl(photoRef, holder.iconImageView);
    }

    /**
     *
     * @param position
     * @return
     */
    private String getRatingByPos(int position) {
        int rating = itemList.get(position).getRating();
        return rating == 0 ? "-" : rating + ".0";
    }

    /**
     *
     * @param photoRef
     */
    private void setPhotoByUrl(String photoRef, final ImageView imageView) {
        Drawable defaultIcon = getColoredDrawable();
        if (photoRef == null) {
            imageView.setImageDrawable(defaultIcon);
            return;
        }
        PicassoSingleton.getInstance(contextWeakRef, null)
                .setPhotoAsync(imageView, photoRef, defaultIcon);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

//    /**
//     *
//     * @param placeId
//     */
//    public Bitmap getPicture(String placeId) {
//        Bitmap cachedPic = CacheManager.getInstance().getBitmapFromMemCache(placeId);
//        return cachedPic == null ? BitmapFactory
//                .decodeResource(contextWeakRef.get().getResources(),R.drawable.coffee_cup_icon) :
//                cachedPic;
//    }

    /**
     *
     * @param value
     */
    public void setEmptyResult(boolean value) {
        this.isEmptyResult = value;
    }

    /**
     *
     */
    public boolean isEmptyResult() {
        return this.isEmptyResult;
    }

    /**
     *
     * @param pos
     * @return
     */
    public CoffeePlace getItem(int pos) {
        return itemList.get(pos);
    }

    /**
     *
     */
    public void clearAllItems() {
        itemList.clear();
    }

    /**
     * TODO move out
     * @return
     */
    public Drawable getColoredDrawable() {
        Drawable defaultIcon = contextWeakRef.get().getResources()
                .getDrawable(R.drawable.ic_local_see_black_48dp);
        defaultIcon.setColorFilter(contextWeakRef.get().getResources().getColor(R.color.material_brown200),
                PorterDuff.Mode.SRC_ATOP);
        return defaultIcon;
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iconImageView;
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final View itemView;
        private final TextView ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = ((TextView) itemView.findViewById(R.id.coffeeMachineNameTextId));
            addressTextView = ((TextView) itemView.findViewById(R.id.coffeeMachineAddressTextId));
            iconImageView = (ImageView) itemView.findViewById(R.id.coffeeIconId);
            ratingTextView = (TextView) itemView.findViewById(R.id.coffeePlaceRatingId);
            this.itemView = itemView;
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

    /**
     *
     * @param data
     */
    public void addAllItems(ArrayList<CoffeePlace> data) {
        itemList.clear();
        itemList.addAll(data);
    }
}
