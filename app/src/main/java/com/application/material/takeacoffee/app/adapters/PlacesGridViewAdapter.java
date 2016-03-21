package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.utils.CacheManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 09/03/16.
 */
public class PlacesGridViewAdapter extends RecyclerView.Adapter<PlacesGridViewAdapter.ViewHolder> {
    private final ArrayList<CoffeeMachine> itemList;
    private final WeakReference<Context> contextWeakRef;
    private CustomItemClickListener listener;
    private PlacesGridViewAdapter.ViewHolder holder;
    private boolean isEmptyResult;


    public PlacesGridViewAdapter(WeakReference<Context> context, ArrayList<CoffeeMachine> itemList) {
        this.itemList = itemList;
        this.contextWeakRef = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coffee_place_template, parent, false);
        holder = new PlacesGridViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTextView.setText(itemList.get(position).getName());
        holder.addressTextView.setText(itemList.get(position).getAddress());
        holder.iconImageView.setImageBitmap(getPicture(itemList.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     *
     * @return
     */
    public int getCardviewWidth() {
        return holder.itemView.getWidth();
    }

    /**
     *
     * @param placeId
     */
    public Bitmap getPicture(String placeId) {
        Bitmap cachedPic = CacheManager.getInstance().getBitmapFromMemCache(placeId);
        return cachedPic == null ? BitmapFactory
                .decodeResource(contextWeakRef.get().getResources(),R.drawable.coffee_cup_icon) :
                cachedPic;
    }

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
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iconImageView;
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = ((TextView) itemView.findViewById(R.id.coffeeMachineNameTextId));
            addressTextView = ((TextView) itemView.findViewById(R.id.coffeeMachineAddressTextId));
            iconImageView = (ImageView) itemView.findViewById(R.id.coffeeIconId);
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
    public void addAllItems(ArrayList<CoffeeMachine> data) {
        itemList.addAll(data);
    }
}
