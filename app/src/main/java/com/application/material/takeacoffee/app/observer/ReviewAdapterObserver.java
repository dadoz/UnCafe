package com.application.material.takeacoffee.app.observer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 18/05/16.
 */
public class ReviewAdapterObserver extends RecyclerView.AdapterDataObserver {
    private final WeakReference<RecyclerView> adapterWeakRef;
    private final ProgressBar progressbar;
    private final View noResultView;

    public ReviewAdapterObserver(WeakReference<RecyclerView> adapterWeakRef,
                                 ProgressBar progressbar,
                                 View noResultView) {
        this.adapterWeakRef = adapterWeakRef;
        this.progressbar = progressbar;
        this.noResultView = noResultView;
    }

    @Override
    public void onChanged() {
        boolean isEmpty = true;
//        boolean isEmpty = adapterWeakRef.get().getItemCount() == 0 &&
//                !adapterWeakRef.get().isEmptyResult();
        progressbar.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        noResultView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

    }
}
