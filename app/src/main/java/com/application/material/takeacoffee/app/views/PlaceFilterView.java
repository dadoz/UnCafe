package com.application.material.takeacoffee.app.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.material.takeacoffee.app.MapActivity;
import com.application.material.takeacoffee.app.PickPositionActivity;
import com.application.material.takeacoffee.app.PlacesActivity;
import com.application.material.takeacoffee.app.PlacesActivity.OnHandleFilterBackPressedInterface;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.presenters.ChangeLocationAutocompleteFilterPresenter;
import com.application.material.takeacoffee.app.presenters.PlaceFilterPresenter;
import com.application.material.takeacoffee.app.scrollListeners.EndlessRecyclerOnScrollListener;
import com.application.material.takeacoffee.app.singletons.GeocoderManager;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

import butterknife.BindView;

public class PlaceFilterView extends LinearLayout implements View.OnClickListener,
        OnHandleFilterBackPressedInterface, GeocoderManager.OnHandleGeocoderResult {
    @BindView(R.id.coffeePlaceFilterBackgroundId)
    View coffeePlaceFilterBackground;
    @BindView(R.id.coffeePlaceFilterCardviewId)
    View coffeePlaceFilterCardview;
    @BindView(R.id.changePlaceConfirmButtonId)
    View changePlaceConfirmButton;
    @BindView(R.id.changePlaceFilterAutocompleteTextviewId)
    AutoCompleteTextView changePlaceFilterAutocompleteTextview;
    @BindView(R.id.changePlaceTextInputLayoutId)
    TextInputLayout changePlaceTextInputLayout;
    @BindView(R.id.placePositionFilterEditButtonId)
    ImageView placePositionFilterEditButton;
    @BindView(R.id.changePlaceFilterCardviewId)
    View changePlaceFilterCardview;
    @BindView(R.id.coffeePlaceFilterBackgroundFrameLayoutId)
    View coffeePlaceFilterBackgroundFrameLayout;
    @BindView(R.id.coffeePlaceFilterBackgroundProgressbarId)
    View coffeePlaceFilterBackgroundProgressbar;
    @BindView(R.id.placePositionFilterTextViewId)
    TextView placePositionFilterTextView;
    private EndlessRecyclerOnScrollListener scrollListener;
    private PlaceFilterPresenter placeFilterPresenter;
    private ChangeLocationAutocompleteFilterPresenter changePlaceAutocompletePresenter;
    private String selectedLocationName;

    public PlaceFilterView(Context context) {
        super(context);
        initView();
    }

    public PlaceFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PlaceFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.place_filter_layout, this);

    }

    @Override
    public boolean handleFilterBackPressed() {
        if (placeFilterPresenter.isEdit()) {
            placeFilterPresenter.onHideEditPlace();
//            setActionbarHomeButtonEnabled(false);
            clearEditText();
            Utils.hideKeyboard(new WeakReference<>(getContext()),
                    changePlaceFilterAutocompleteTextview);
            return true;
        }

        //TODO HANDLE IT (maybe return to prev status)
        if (placeFilterPresenter.isLoadingEdit()) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    private void initFilters() {
        View coffeePlaceSwipeRefreshLayout = null; //TODO handle it

        placePositionFilterEditButton.setOnClickListener(this);
        placePositionFilterEditButton.setImageDrawable(Utils
                .getColoredDrawable(placePositionFilterEditButton.getDrawable(),
                        ContextCompat.getColor(getContext(),R.color.material_brown800)));
        changePlaceConfirmButton.setOnClickListener(this);
        coffeePlaceFilterCardview.setOnClickListener(this);
        coffeePlaceFilterBackgroundFrameLayout.setOnClickListener(this);
        updateFiltersPlaceLocation();
        //init presenter
        placeFilterPresenter = PlaceFilterPresenter.getInstance(new WeakReference<>(getContext()),
                new View[] {coffeePlaceFilterCardview, changePlaceFilterCardview, coffeePlaceFilterBackground,
                        coffeePlaceSwipeRefreshLayout, coffeePlaceFilterBackgroundFrameLayout});
        placeFilterPresenter.init();
        changePlaceAutocompletePresenter.init();
    }

    /**
     *
     */
    private void updateFiltersPlaceLocation() {
        placePositionFilterTextView.setText(SharedPrefManager
                .getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY));
        changePlaceFilterAutocompleteTextview.clearFocus();
        Utils.hideKeyboard(new WeakReference<>(getContext()),
                changePlaceFilterAutocompleteTextview);
    }
    /**
     *
     */
    private void clearEditText() {
        changePlaceFilterAutocompleteTextview.setText("");
    }
    /**
     *
     */
    private void changingPlaceUI() {
        placeFilterPresenter.onLoadEditPlace();
        coffeePlaceFilterBackgroundProgressbar.setVisibility(View.VISIBLE);
        Utils.hideKeyboard(new WeakReference<>(getContext()),
                changePlaceFilterAutocompleteTextview);
    }

    /**
     * TODO move on presenter
     */
    private void changingPlace() {
        String selectedLocationName = changePlaceFilterAutocompleteTextview.getText().toString();
        GeocoderManager.getInstance(new WeakReference<GeocoderManager.OnHandleGeocoderResult>(this),
                new WeakReference<>(getContext()))
                .getLatLongByLocationName(selectedLocationName);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffeePlaceFilterCardviewId:
//                startActivity(new Intent(getActivity().getApplicationContext(), MapActivity.class));
                break;
            case R.id.changePlaceConfirmButtonId:
                if (placeFilterPresenter.isEdit()) {
                    changingPlace();
                    changingPlaceUI();
                }
                break;
            case R.id.placePositionFilterEditButtonId:
                if (!placeFilterPresenter.isEdit()) {
                    placeFilterPresenter.onShowEditPlace();
//                    setActionbarHomeButtonEnabled(true);
                }
                break;
        }
    }



    @Override
    public void onGeocoderSuccess(LatLng latLng) {
        if (placeFilterPresenter.isLoadingEdit()) {
            coffeePlaceFilterBackgroundProgressbar.setVisibility(View.GONE);
            placeFilterPresenter.onExpandEdit();
//            setActionbarHomeButtonEnabled(false);
            clearEditText();
            saveLocationOnStorage(latLng);
//            retrievePlacesAndUpdateUI();
            updateFiltersPlaceLocation();
        }
    }

    //TODO move on presenter
    @Override
    public void onGeocoderError() {
        if (placeFilterPresenter.isLoadingEdit()) {
            coffeePlaceFilterBackgroundProgressbar.setVisibility(View.GONE);
            changePlaceTextInputLayout.setErrorEnabled(true);
            changePlaceTextInputLayout.setError(getContext()
                    .getString(R.string.no_place_from_geocode_found));
            placeFilterPresenter.onOnlyShowEditPlace();
        }
    }

    /**
     *
     */
    private void clearStoredLocation() {
        SharedPrefManager.getInstance(new WeakReference<>(getContext())).clearAll();
//        startActivity(new Intent(getContext(), PickPositionActivity.class));
//        getActivity().finish();
    }


    /**
     * on action done
     */
    private void saveLocationOnStorage(LatLng latLng) {
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(new WeakReference<>(getContext()));
        sharedPref.setValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY,
                Utils.getLatLngString(latLng));
        sharedPref.setValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY,
                Utils.capitalize(selectedLocationName));
    }



}
