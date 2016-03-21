package com.application.material.takeacoffee.app.observer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 10/03/16.
 */
public class CoffeePlaceAdapterObserver extends RecyclerView.AdapterDataObserver {
    private final WeakReference<PlacesGridViewAdapter> adapterWeakRef;
    private final ProgressBar progress;
    private final View noResultView;

    public CoffeePlaceAdapterObserver(WeakReference<PlacesGridViewAdapter> adapterWeakRef,
                                      ProgressBar coffeePlacesProgress,
                                      View noResultView) {
        this.adapterWeakRef = adapterWeakRef;
        this.progress = coffeePlacesProgress;
        this.noResultView = noResultView;
    }

    @Override
    public void onChanged() {
//        Log.e("OBSERVER", "change data " + adapterWeakRef.get().getItemCount());
        boolean isEmpty = adapterWeakRef.get().getItemCount() == 0 &&
                !adapterWeakRef.get().isEmptyResult();
        progress.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        noResultView.setVisibility(adapterWeakRef.get().isEmptyResult() ? View.VISIBLE : View.GONE);
    }
}