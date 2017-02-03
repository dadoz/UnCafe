package com.application.material.takeacoffee.app.presenters;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.google.android.gms.common.ConnectionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;

import static com.application.material.takeacoffee.app.models.CoffeePlace.BAR_PLACE_TYPE;
import static com.application.material.takeacoffee.app.models.CoffeePlace.PLACE_RANKBY;

public class PlaceListPresenter implements PlaceApiManager.OnHandlePlaceApiResult {
    @Override
    public void onPlaceApiSuccess(Object list, PlaceApiManager.RequestType type) {

    }

    @Override
    public void onPlaceApiEmptyResult() {

    }

    @Override
    public void onPlaceApiError(PlaceApiManager.RequestType type) {

    }
//    private final WeakReference ctx;
//    @BindView(R.id.noLocationServiceLayoutId)
//    View noLocationServiceLayout;
//    @BindView(R.id.noNetworkServiceLayoutId)
//    View noNetworkServiceLayout;
//    @BindView(R.id.emptyResultButtonId)
//    View emptyResultButton;
//    @BindView(R.id.noNetworkServiceButtonId)
//    View noNetworkServiceButton;
//    @BindView(R.id.noLocationServiceButtonId)
//    View noLocationServiceButton;
//    @BindView(R.id.coffeePlacesEmptyResultId)
//    View coffeePlacesEmptyResult;
//
//    private PermissionManager permissionManager;
//    private PlaceApiManager placesApiManager;
//
//    public PlaceListPresenter(WeakReference context) {
//        ctx = context;
//    }
//
//    public void init() {
//        permissionManager = PermissionManager.getInstance();
//        //handle listener (since it still equal to the one on review activity)
//        placesApiManager = PlaceApiManager.getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
//                ctx);
//
//    }
//    /**
//     *
//     */
//    private void initPermissionChainResponsibility() {
//        initNetworkPermission();
////        initLocationPermission();
//    }
//
//
//    /**
//     * samplePlacesApi
//     */
//    private void initGooglePlaces() {
//        placesApiManager = PlaceApiManager
//                .getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
//                        ctx);
//    }
//
//    /**
//     * location permission
//     */
//    private void initNetworkPermission() {
//        permissionManager.checkNetworkServiceIsEnabled(ctx,
//                new WeakReference<PermissionManager.OnEnableNetworkCallbackInterface>(this));
//    }
//
//    /**
//     *
//     */
//    public void initLocationPermission() {
//        permissionManager.onRequestPermissions(new WeakReference<Activity>(getActivity()),
//                new WeakReference<PermissionManager.OnHandleGrantPermissionCallbackInterface>(this));
//    }
//
//    /**
//     *
//     */
//    public void handleLocationServiceEnabled() {
//        permissionManager.checkLocationServiceIsEnabled(new WeakReference<Activity>(getActivity()),
//                new WeakReference<PermissionManager.OnEnablePositionCallbackInterface>(this));
//    }
//    /**
//     *
//     */
//    @Override
//    public void onPlaceApiSuccess(Object result, PlaceApiManager.RequestType type) {
//        handleInfo((ArrayList<CoffeePlace>) result, type == PlaceApiManager.RequestType.MORE_PLACE_INFO);
//        placeList.addAll((ArrayList<CoffeePlace>) result);
////        else if (type == RequestType.PLACE_CITES) {
////            changePlaceAutocompletePresenter.onCitiesRetrieveSuccess(result, type);
////        }
//    }
//
//    @Override
//    public void onPlaceApiEmptyResult() {
//        //add type
//        showErrorMessage();
//        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
//        placeList.clear();
//    }
//
//    @Override
//    public void onPlaceApiError(PlaceApiManager.RequestType type) {
//        //TODO LEAK - if changing activity u must unsuscribe observer
////        scrollListener.setLoadingEnabled(true);
//        if (type == PlaceApiManager.RequestType.PLACE_INFO) {
//            showErrorMessage();
//            ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
//            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
//        }
////        else if (type == RequestType.PLACE_CITES) {
////            changePlaceAutocompletePresenter.onCitiesRetrieveError(type);
////        }
//    }
//
//    /**
//     *
//     * @param placeList
//     * @param isMoreInfo
//     */
//    public void handleInfo(ArrayList<CoffeePlace> placeList, boolean isMoreInfo) {
//        if (isMoreInfo) {
//            ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
//                    .appendAllItems(placeList);
//        } else {
//            ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
//                    .addAllItems(placeList);
//        }
//        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
////            placeFilterPresenter.onExpand();
//    }
//
//    /**
//     *
//     */
//    private void retrievePlacesAndUpdateUI() {
//        //TODO big issue over here - position still not available
//        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(false);
//        cleanRecyclerViewData();
//        final String latLngString = SharedPrefManager.getInstance((ctx))
//                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);
//        showHideLocationServiceLayout(true);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                obsSubscription = placesApiManager.retrievePlacesAsync(latLngString,
//                        PLACE_RANKBY, BAR_PLACE_TYPE);
//            }
//        }, 2000);
//
//    }
//
//    /**
//     *
//     */
//    private void cleanRecyclerViewData() {
//        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).clearAllItems();
//        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
//        placeList.clear();
//    }
//
//    @Override
//    public void onHandleGrantPermissionCallback() {
//        handleLocationServiceEnabled();
//    }
//
//    @Override
//    public void onEnablePositionCallback() {
//        retrievePlacesAndUpdateUI();
//    }
//
//
//    @Override
//    public void onEnablePositionErrorCallback() {
//        showHideLocationServiceLayout(false);
//    }
//
//    @Override
//    public void onEnableNetworkCallback() {
//        showHideNetworkServiceLayout(true);
//        //chain
//        initLocationPermission();
//    }
//
//    @Override
//    public void onEnableNetworkErrorCallback() {
//        showHideNetworkServiceLayout(false);
//    }
//
//    /**
//     *
//     * @param isEnabled
//     */
//    public void showHideLocationServiceLayout(boolean isEnabled) {
//        noLocationServiceLayout.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
//        noLocationServiceButton.setOnClickListener(isEnabled ? null : this);
//        coffeePlacesProgress.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     *
//     * @param isEnabled
//     */
//    public void showHideNetworkServiceLayout(boolean isEnabled) {
//        noNetworkServiceLayout.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
//        noNetworkServiceButton.setOnClickListener(isEnabled ? null : this);
//        coffeePlacesProgress.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
//    }
//
//    /**
//     *
//     */
//    private void handleRefreshInitCallback() {
//        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).clearAllItems();
//        synchronized (coffeePlacesRecyclerview.getAdapter()) {
//            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        showErrorMessage();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.noLocationServiceButtonId:
//                permissionManager.enablePosition(new WeakReference<Activity>(getActivity()));
//                break;
//            case R.id.noNetworkServiceButtonId:
//                initPermissionChainResponsibility();
//                break;
//            case R.id.emptyResultButtonId:
//                coffeePlacesEmptyResult.setVisibility(View.GONE);
//                retrievePlacesAndUpdateUI();
//                break;
//        }
//    }

}
