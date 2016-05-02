package com.application.material.takeacoffee.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.presenters.LocationAutocompletePresenter;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davide on 19/11/14.
 */
public class PickPositionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PickPositionFragment";
    @Bind(R.id.locationAutocompleteTextViewId)
    AutoCompleteTextView locationAutocompleteTextView;
    @Bind(R.id.findCurrentLocationButtonId)
    Button findCurrentLocationButton;
    @Bind(R.id.locationDoneButtonId)
    Button locationDoneButton;
    private LocationAutocompletePresenter autocompletePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View mapView = inflater.inflate(R.layout.pick_position_fragment, container, false);
        ButterKnife.bind(this, mapView);
        setHasOptionsMenu(true);

        autocompletePresenter = LocationAutocompletePresenter
                .getInstance(new WeakReference<>(getContext()), locationAutocompleteTextView,
                        locationDoneButton);
        initView();
        return mapView;
    }

    /**
     * init actionbar
     */
    public void initActionBar() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
        findCurrentLocationButton.setOnClickListener(this);
        autocompletePresenter.init();
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
                onActionDone();
                break;
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void onClick(View v) {
        Log.e("TAG", "find current location");
    }

    /**
     * action done
     */
    private void onActionDone() {
        //                startActivity(new Intent(getContext(), PlacesActivity.class));
//        getActivity().finish();
        Log.e("PICK", "start activity location ->" + autocompletePresenter.getSelectedLocation());
    }

}
