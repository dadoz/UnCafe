package com.application.material.takeacoffee.app.scrollListeners;

/**
 * Created by davide on 30/04/16.
 */
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    private final StaggeredGridLayoutManager layoutManager;

    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    int visibleItemCount, totalItemCount;
    private int pastVisibleItems;
    private int currentPage = 1;

    /**
     *
     * @param layoutManager
     */
    public EndlessRecyclerOnScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = layoutManager.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        int[] firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
        if (firstVisibleItems != null &&
                firstVisibleItems.length > 0) {
            pastVisibleItems = firstVisibleItems[0];
        }

        if (loading &&
                (visibleItemCount + pastVisibleItems) >= totalItemCount) {
            loading = false;
            onLoadMore(currentPage);
            currentPage++;
        }
    }

    /**
     * 
     * @param currentPage
     */
    public abstract void onLoadMore(int currentPage);

    /**
     *
     * @param val
     */
    public void setLoadingEnabled(boolean val) {
        loading = val;
    }
}