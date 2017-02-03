package com.application.material.takeacoffee.app.helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.application.material.takeacoffee.app.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MaterialSearchManager implements MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {
    /**
     * @param searchItem
     * @param searchView
     */
    public void initSearchView(@NonNull MenuItem searchItem, @NonNull MaterialSearchView searchView) {
        try {
            searchView.setMenuItem(searchItem);
            searchView.setOnQueryTextListener(this);
            searchView.setOnSearchViewListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }
}
