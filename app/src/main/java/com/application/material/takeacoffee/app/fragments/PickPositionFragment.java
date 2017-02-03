package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.material.takeacoffee.app.PlacesActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.presenters.LocationAutocompletePresenter;
import com.application.material.takeacoffee.app.presenters.LocationAutocompletePresenter.PickLocationInterface;
import com.application.material.takeacoffee.app.singletons.GeocoderManager;
import com.application.material.takeacoffee.app.singletons.GeocoderManager.OnHandleGeocoderResult;
import com.application.material.takeacoffee.app.utils.ConnectivityUtils;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.rx.ObservableFactory;
import rx.Observable;
import rx.functions.Action1;

public class PickPositionFragment extends Fragment implements
        View.OnClickListener, PickLocationInterface, OnHandleGeocoderResult,
        PermissionManager.OnEnablePositionCallbackInterface {
    private static final String TAG = "PickPositionFragment";
    @BindView(R.id.locationAutocompleteTextViewId)
    AutoCompleteTextView locationAutocompleteTextView;
    @BindView(R.id.locationTextInputLayoutId)
    TextInputLayout locationTextInputLayout;
    @BindView(R.id.locationDoneButtonId)
    View locationDoneButton;
    @BindView(R.id.findPositionButtonId)
    View findPositionButton;
    @BindView(R.id.pickLocationProgressId)
    ProgressBar pickLocationProgress;
    @BindView(R.id.locationSelectedTextviewId)
    View locationSelectedTextview;
    @BindView(R.id.pickDescriptionId)
    View pickDescription;
    @BindView(R.id.successPickIconId)
    View successPickIcon;
    @BindView(R.id.errorPickIconId)
    View errorPickIcon;
    @BindView(R.id.pickIconId)
    View locationPickIcon;
    @BindView(R.id.pickViewId)
    View pickView;
    private LocationAutocompletePresenter locationAutocompletePres;
    private String selectedLocationName;
    private GeocoderManager geocoder;

    private enum LocationActionTypeEnum {LOCATION_DONE, LOCATION_CURRENT}
    private LocationActionTypeEnum locationActionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.pick_position_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        locationAutocompletePres = LocationAutocompletePresenter
                .getInstance(new WeakReference<>(getContext()),
                        new WeakReference<PickLocationInterface>(this),
                        locationAutocompleteTextView,
                        locationTextInputLayout,
                        new View[]{pickDescription, errorPickIcon, successPickIcon,
                                locationPickIcon, locationDoneButton, null,
                                pickLocationProgress, findPositionButton, locationSelectedTextview});

        Observable<Location> locationObservable = ObservableFactory.from(SmartLocation.with(getContext()).location());
        locationObservable.subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {
                // Do your stuff here :)
                saveLocationOnStorage(new LatLng(location.getLatitude(), location.getLongitude()));

            }
        });

        geocoder = GeocoderManager
                .getInstance(new WeakReference<OnHandleGeocoderResult>(this),
                        new WeakReference<>(getContext()));

        initView();

        if (!ConnectivityUtils.isConnected(new WeakReference<>(getActivity().getApplicationContext()))) {
            handleNoConnectivity(view);
        }
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
        locationAutocompletePres.init();
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
                locationAutocompletePres.updatePositionNotSelected();
                locationAutocompletePres.updateUIOnFindPosition();
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
                .checkLocationServiceIsEnabled(new WeakReference<Activity>(getActivity()),
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
//        Log.e("PICK", "start activity location ->" + Utils.getLatLngString(latLng));
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(new WeakReference<>(getContext()));
        sharedPref.setValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY,
                        Utils.getLatLngString(latLng));
        sharedPref.setValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY,
                Utils.capitalize(selectedLocationName));
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
        locationAutocompletePres.updateUIOnFindPositionSuccess(selectedLocationName);
    }


    @Override
    public void onGeocoderError() {
        locationAutocompletePres.updateUIOnFindPositionError();
    }

    @Override
    public void onEnablePositionErrorCallback() {
        locationAutocompletePres.updateUIOnFindPositionError();
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

    /**
     *
     */
    private void handleNoConnectivity(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    Utils.showSnackbar(new WeakReference<>(getActivity().getApplicationContext()),
                            view, R.string.no_internet_connection);
                }
            }
        }, 2000);
    }
}
