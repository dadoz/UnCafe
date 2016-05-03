package com.application.material.takeacoffee.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.application.material.takeacoffee.app.PlacesActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.presenters.LocationAutocompletePresenter;
import com.application.material.takeacoffee.app.presenters.LocationAutocompletePresenter.PickLocationInterface;
import com.application.material.takeacoffee.app.singletons.GeocoderManager;
import com.application.material.takeacoffee.app.singletons.GeocoderManager.OnHandleGeocoderResult;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PickPositionFragment extends Fragment implements
        View.OnClickListener, PickLocationInterface, OnHandleGeocoderResult, PermissionManager.OnEnablePositionCallbackInterface {
    private static final String TAG = "PickPositionFragment";
    @Bind(R.id.locationAutocompleteTextViewId)
    AutoCompleteTextView locationAutocompleteTextView;
    @Bind(R.id.locationDoneButtonId)
    View locationDoneButton;
    @Bind(R.id.findCurrentPositionButtonId)
    View findCurrentPositionButton;
    @Bind(R.id.pickLocationProgressId)
    ProgressBar pickLocationProgress;
    private LocationAutocompletePresenter autocompletePresenter;
    private String selectedLocationName;
    private GeocoderManager geocoder;

    private enum LocationActionTypeEnum {LOCATION_DONE, LOCATION_CURRENT}
    private LocationActionTypeEnum locationActionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO move out
        if (isLocationSet()) {
            startPlaceActivity();
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.pick_position_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        autocompletePresenter = LocationAutocompletePresenter
                .getInstance(new WeakReference<>(getContext()),
                        new WeakReference<PickLocationInterface>(this),
                        locationAutocompleteTextView,
                        locationDoneButton);
        geocoder = GeocoderManager
                .getInstance(new WeakReference<OnHandleGeocoderResult>(this),
                        new WeakReference<>(getContext()));

        initView();
        return view;
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
        locationDoneButton.setOnClickListener(this);
        findCurrentPositionButton.setOnClickListener(this);
        autocompletePresenter.init();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationDoneButtonId:
                checkPositionPermissionAndTriggerAction(LocationActionTypeEnum.LOCATION_DONE);
//                setPositionByLocationName();
                break;
            case R.id.findCurrentPositionButtonId:
                checkPositionPermissionAndTriggerAction(LocationActionTypeEnum.LOCATION_DONE);
//                setCurrentPosition();
                break;
        }
    }

    private void checkPositionPermissionAndTriggerAction(LocationActionTypeEnum actionType) {
        this.locationActionType = actionType;
        PermissionManager.getInstance().checkLocationServiceIsEnabled(new WeakReference<>((AppCompatActivity) getActivity()),
                new WeakReference<PermissionManager.OnEnablePositionCallbackInterface>(this));
    }


    /**
     * on check position
     */
    private void setPositionByLocationName() {
        pickLocationProgress.setVisibility(View.VISIBLE);
        geocoder.getLatLongByLocationName(selectedLocationName);
    }

    /**
     *
     */
    private void setCurrentPosition() {
//        pickLocationProgress.setVisibility(View.VISIBLE);
        geocoder.getCurrentLatLong();
//        pickLocationProgress.setVisibility(View.GONE);
    }


    /**
     * on action done
     */
    private void saveLocationOnStorage(LatLng latLng) {
        Log.e("PICK", "start activity location ->" + Utils.getLatLngString(latLng));
        SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                .setValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY,
                        Utils.getLatLngString(latLng));
    }


    /**
     * on action done
     */
    private void onActionDone() {
        locationDoneButton.setVisibility(View.VISIBLE);
        startPlaceActivity();
    }

    /**
     *
     */
    private void startPlaceActivity() {
        startActivity(new Intent(getContext(), PlacesActivity.class));
        getActivity().finish();
    }

    /**
     *
     * @return
     */
    public boolean isLocationSet() {
        return SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY) != null;
    }

    /**
     *
     */
    public void showMessageBySnackbar(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
//                .setAction("Undo", mOnClickListener)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void pickLocationSuccess(String locationName) {
        selectedLocationName = locationName;
    }

    @Override
    public void pickLocationError() {
        //disable continue button
        locationDoneButton.setVisibility(View.GONE);
    }

    @Override
    public void onGeocoderSuccess(LatLng latLng) {
        //TODO only the original thread can touch its view
        pickLocationProgress.setVisibility(View.GONE);
        saveLocationOnStorage(latLng);
        onActionDone();
    }

    @Override
    public void onGeocoderErrorResult() {
        //TODO only the original thread can touch its view
        pickLocationProgress.setVisibility(View.GONE);
        locationDoneButton.setVisibility(View.GONE);
    }

    @Override
    public void onEnablePositionCallback() {
        if (locationActionType == LocationActionTypeEnum.LOCATION_DONE) {
            setPositionByLocationName();
        } else if (locationActionType == LocationActionTypeEnum.LOCATION_CURRENT) {
            setCurrentPosition();
        }
    }

    @Override
    public void onEnablePositionErrorCallback() {
        Log.e("TAG", "error on enable position");
        showMessageBySnackbar("error on enable position");
    }
}
