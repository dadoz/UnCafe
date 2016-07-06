package com.application.material.takeacoffee.app.observer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.application.material.takeacoffee.app.adapters.ReviewRecyclerViewAdapter;

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
        boolean isEmpty = ((ReviewRecyclerViewAdapter) adapterWeakRef.get().getAdapter()).isEmpty();
        boolean hasError = ((ReviewRecyclerViewAdapter) adapterWeakRef.get().getAdapter()).getItemCount() == 0;
        progressbar.setVisibility(View.GONE);
        noResultView.setVisibility(isEmpty || hasError ? View.VISIBLE : View.GONE);
    }
}
