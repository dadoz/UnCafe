package com.application.material.takeacoffee.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.takeacoffee.app.PlacesActivity;
import com.application.material.takeacoffee.app.R;

import butterknife.ButterKnife;

/**
 * Created by davide on 19/11/14.
 */
public class PickPositionFragment extends Fragment {
    private static final String TAG = "PickPositionFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View mapView = inflater.inflate(R.layout.pick_position_fragment, container, false);
        ButterKnife.bind(this, mapView);
        setHasOptionsMenu(true);

        initView();
        return mapView;
    }

    /**
     * init actionbar
     */
    public void initActionBar() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(getString(R.string.pick_pos_name));
        }
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.pick_position, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                startActivity(new Intent(getContext(), PlacesActivity.class));

                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
