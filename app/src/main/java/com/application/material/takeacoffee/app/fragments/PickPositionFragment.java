package com.application.material.takeacoffee.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
    @Bind(R.id.locationTextInputLayoutId)
    TextInputLayout locationTextInputLayout;
    @Bind(R.id.locationDoneButtonId)
    View locationDoneButton;
    @Bind(R.id.locationDoneBorderLayoutId)
    View locationDoneBorderLayout;
    @Bind(R.id.findPositionButtonId)
    View findPositionButton;
    @Bind(R.id.pickLocationProgressId)
    ProgressBar pickLocationProgress;
    @Bind(R.id.locationSelectedTextviewId)
    View locationSelectedTextview;
    @Bind(R.id.pickDescriptionId)
    View pickDescription;
    @Bind(R.id.successPickIconId)
    View successPickIcon;
    @Bind(R.id.errorPickIconId)
    View errorPickIcon;
    @Bind(R.id.pickIconId)
    View locationPickIcon;
    @Bind(R.id.pickViewId)
    View pickView;
    private LocationAutocompletePresenter locaionAutocompletePres;
    private String selectedLocationName;
    private GeocoderManager geocoder;
    private View view;

    private enum LocationActionTypeEnum {LOCATION_DONE, LOCATION_CURRENT}
    private LocationActionTypeEnum locationActionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        view = inflater.inflate(R.layout.pick_position_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        locaionAutocompletePres = LocationAutocompletePresenter
                .getInstance(new WeakReference<>(getContext()),
                        new WeakReference<PickLocationInterface>(this),
                        locationAutocompleteTextView,
                        locationTextInputLayout,
                        new View[]{pickDescription, errorPickIcon, successPickIcon,
                                locationPickIcon, locationDoneButton, locationDoneBorderLayout,
                                pickLocationProgress, findPositionButton, locationSelectedTextview,
                                pickView});
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
        findPositionButton.setOnClickListener(this);
        locaionAutocompletePres.init();
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
                onActionDone();
                break;
            case R.id.findPositionButtonId:
                locaionAutocompletePres.updateUIOnFindPosition();
                break;
        }
    }

    /**
     *
     * @param actionType
     */
    private void checkPositionPermissionAndTriggerAction(LocationActionTypeEnum actionType) {
        this.locationActionType = actionType;
        PermissionManager.getInstance()
                .checkLocationServiceIsEnabled(new WeakReference<>((AppCompatActivity) getActivity()),
                new WeakReference<PermissionManager.OnEnablePositionCallbackInterface>(this));
    }


    /**
     * on check position
     */
    private void setPositionFromGeocoder() {
        pickLocationProgress.setVisibility(View.VISIBLE);
        geocoder.getLatLongByLocationName(selectedLocationName);
    }

    /**
     * on action done
     */
    private void saveLocationOnStorage(LatLng latLng) {
        Log.e("PICK", "start activity location ->" + Utils.getLatLngString(latLng));
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(new WeakReference<>(getContext()));
        sharedPref.setValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY,
                        Utils.getLatLngString(latLng));
        sharedPref.setValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY,
                        selectedLocationName);
    }


    /**
     * on action done
     */
    private void onActionDone() {
        startPlaceActivity();
    }

    /**
     *
     */
    private void startPlaceActivity() {
        startActivity(new Intent(getContext(), PlacesActivity.class));
        getActivity().finish();
    }


    @Override
    public void pickLocationSuccess(String locationName) {
        selectedLocationName = locationName;
    }

    @Override
    public void onGeocoderSuccess(LatLng latLng) {
        saveLocationOnStorage(latLng);
        locaionAutocompletePres.updateUIOnFindPositionSuccess(selectedLocationName);
    }


    @Override
    public void onGeocoderError() {
        locaionAutocompletePres.updateUIOnFindPositionError();
    }

    @Override
    public void onEnablePositionErrorCallback() {
        locaionAutocompletePres.updateUIOnFindPositionError();
    }

    @Override
    public void onEnablePositionCallback() {
        if (locationActionType == LocationActionTypeEnum.LOCATION_DONE) {
            setPositionFromGeocoder();
        }
    }

    @Override
    public void onUpdateUIOnFindPositionCallback() {
        setPositionFromGeocoder();
//        checkPositionPermissionAndTriggerAction(LocationActionTypeEnum.LOCATION_DONE);
    }
}
