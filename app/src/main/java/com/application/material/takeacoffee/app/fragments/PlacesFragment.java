package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.observer.CoffeePlaceAdapterObserver;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager.OnHandlePlaceApiResult;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 3/13/14.
 */
public class PlacesFragment extends Fragment implements
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        PlacesGridViewAdapter.CustomItemClickListener,
        PermissionManager.OnHandleGrantPermissionCallbackInterface, View.OnClickListener,
        PermissionManager.OnEnablePositionCallbackInterface,
        PermissionManager.OnEnableNetworkCallbackInterface,
        SwipeRefreshLayout.OnRefreshListener, OnHandlePlaceApiResult {
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private ArrayList<CoffeePlace> coffeePlacesList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private PermissionManager permissionManager;
    private PlaceApiManager placesApiManager;

    @Bind(R.id.coffeePlacesRecyclerViewId)
    RecyclerView coffeePlacesRecyclerview;
    @Bind(R.id.coffeePlacesProgressId)
    ProgressBar coffeePlacesProgress;
    @Bind(R.id.coffeePlacesEmptyResultId)
    View coffeePlacesEmptyResult;
    @Bind(R.id.coffeePlaceFilterLayoutId)
    View coffeePlaceFilterLayout;
    @Bind(R.id.noLocationServiceLayoutId)
    View noLocationServiceLayout;
    @Bind(R.id.noNetworkServiceLayoutId)
    View noNetworkServiceLayout;
    @Bind(R.id.noNetworkServiceButtonId)
    View noNetworkServiceButton;
    @Bind(R.id.noLocationServiceButtonId)
    View noLocationServiceButton;
    @Bind(R.id.coffeePlaceSwipeRefreshLayoutId)
    SwipeRefreshLayout coffeePlaceSwipeRefreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef = (PlacesActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View coffeeMachineView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_places_layout, container, false);
        ButterKnife.bind(this, coffeeMachineView);

        permissionManager = PermissionManager.getInstance();
        initView();
        return coffeeMachineView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
        setHasOptionsMenu(true);
        coffeePlaceFilterLayout.setOnClickListener(this);
        coffeePlaceSwipeRefreshLayout.setOnRefreshListener(this);
        if (false && BuildConfig.DEBUG) {
            coffeePlacesList = getCoffeePlacesListTest();
            initGridViewAdapter();
            return;
        }
        initGridViewAdapter();
        initGooglePlaces();
        initPermissionChainResponsibility();
    }

    /**
     *
     */
    private void initPermissionChainResponsibility() {
        initNetworkPermission();
//        initLocationPermission();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    @Override
    public void onItemClick(int pos, View v) {
        CoffeePlace place = coffeePlacesList.get(pos);
        //TODO handle bundle
        Bundle bundle = new Bundle();
        bundle.putString(CoffeePlace.COFFEE_PLACE_ID_KEY, place.getId());
        bundle.putString(CoffeePlace.COFFEE_PLACE_NAME_KEY, place.getName());
        changeActivity(bundle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.coffee_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_position:
                changeFragment(new MapFragment(),
                        MapFragment.MAP_FRAG_TAG);
                break;
            case R.id.action_settings:
                changeFragment(new SettingListFragment(),
                        SettingListFragment.SETTING_LIST_FRAG_TAG);
                break;
        }
        return true;
    }

    /**
     *
     * @param fragment
     * @param tag
     */
    private void changeFragment(Fragment fragment, String tag) {
        getActivity()
            .getSupportFragmentManager().beginTransaction()
            .replace(R.id.coffeeAppFragmentContainerId, fragment, tag)
            .addToBackStack("TAG")
            .commit();
    }

    /**
     * change activity on reviewList
     * @param bundle
     */
    private void changeActivity(Bundle bundle) {
        EventBusSingleton.getInstance().postSticky(bundle);
        startActivity(new Intent(getActivity(), ReviewListActivity.class));
    }

    /**
     * samplePlacesApi
     */
    private void initGooglePlaces() {
        if (mGoogleApiClient != null) {
            return;
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        //TODO add weakref
        placesApiManager = PlaceApiManager
                .getInstance(new WeakReference<OnHandlePlaceApiResult>(this), mGoogleApiClient);

    }

    /**
     * location permission
     */
    private void initNetworkPermission() {
        WeakReference<AppCompatActivity> activityRef =
                new WeakReference<>((AppCompatActivity) mainActivityRef);
        permissionManager.checkNetworkServiceIsEnabled(activityRef, this);
    }

    /**
     *
     */
    public void initLocationPermission() {
        WeakReference<AppCompatActivity> activityRef =
                new WeakReference<>((AppCompatActivity) mainActivityRef);
        permissionManager.onRequestPermissions(activityRef, this);
    }

    /**
     *
     */
    public void handleLocationServiceEnabled() {
        WeakReference<AppCompatActivity> activityRef =
                new WeakReference<>((AppCompatActivity) mainActivityRef);
        permissionManager.checkLocationServiceIsEnabled(activityRef, this);
    }

    /**
     * init grid view adapter
     */
    private void initGridViewAdapter() {
        PlacesGridViewAdapter adapter = new PlacesGridViewAdapter(new WeakReference<>(getContext()),
                coffeePlacesList);
        adapter.registerAdapterDataObserver(new CoffeePlaceAdapterObserver(new WeakReference<>(adapter),
                coffeePlacesProgress, coffeePlacesEmptyResult));
        adapter.setOnItemClickListener(this);
        coffeePlacesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        coffeePlacesRecyclerview.setAdapter(adapter);
        coffeePlacesRecyclerview.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.small_padding));
    }

    /**
     * init action bar
     */
    public void initActionBar() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setTitle(getResources().getString(R.string.coffee_place_actionbar));
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<CoffeePlace> getCoffeePlacesListTest() {
        ArrayList<CoffeePlace> tmp = new ArrayList<CoffeePlace>();
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        return tmp;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO handle it
    }

    /**
     *
     */
    @Override
    public void onUpdatePhotoOnListCallback() {
        synchronized (coffeePlacesRecyclerview.getAdapter()) {
            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void handleEmptyList() {
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
    }

    /**
     *
     * @param place
     */
    @Override
    public void onSetCoffeePlaceInfoOnListCallback(Place place) {
        if (isValidPlaceType(place.getPlaceTypes())) {
            Log.e("------id", place.getId());
            coffeePlacesList.add(new CoffeePlace(place.getId(), place.getName().toString().toLowerCase(),
                    place.getAddress().toString().toLowerCase(), null));
        }
    }

    @Override
    public void onHandleGrantPermissionCallback() {
        handleLocationServiceEnabled();
    }

    @Override
    public void onEnablePositionCallback() {
        //TODO big issue over here - position still not available
        //TODO handle this in a service :)
        showHideLocationServiceLayout(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                placesApiManager.retrievePlaces();
            }
        }, 2000);
    }

    @Override
    public void onEnablePositionErrorCallback() {
        showHideLocationServiceLayout(false);
    }

    @Override
    public void onEnableNetworkCallback() {
        showHideNetworkServiceLayout(true);
        //chain
        initLocationPermission();
    }

    @Override
    public void onEnableNetworkErrorCallback() {
        showHideNetworkServiceLayout(false);
    }

    /**
     *
     * @param isEnabled
     */
    public void showHideLocationServiceLayout(boolean isEnabled) {
        noLocationServiceLayout.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        noLocationServiceButton.setOnClickListener(isEnabled ? null : this);
        coffeePlacesProgress.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    /**
     *
     * @param isEnabled
     */
    public void showHideNetworkServiceLayout(boolean isEnabled) {
        noNetworkServiceLayout.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        noNetworkServiceButton.setOnClickListener(isEnabled ? null : this);
        coffeePlacesProgress.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    /**
     *
     */
    private void handleRefreshInitCallback() {
        coffeePlacesList.clear();
        synchronized (coffeePlacesRecyclerview.getAdapter()) {
            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     *
     */
    private void handleRefreshEndCallback() {
        coffeePlaceSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffeePlaceFilterLayoutId:
                Toast.makeText(getContext(), "hey you call filter handlet", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.noLocationServiceButtonId:
                permissionManager
                        .enablePosition(new WeakReference<>((AppCompatActivity) mainActivityRef));
                break;
            case R.id.noNetworkServiceButtonId:
                initPermissionChainResponsibility();
                break;
        }
    }

    @Override
    public void onRefresh() {
//        handleRefreshInitCallback();
//        handleRefreshEndCallback();
//        placesApiManager.retrievePlaces();
    }

    /**
     *
     * @param placeTypes
     * @return
     */
    public boolean isValidPlaceType(List<Integer> placeTypes) {
        for (int placeType : placeTypes) {
            if (placeType == Place.TYPE_CAFE ||
                placeType == Place.TYPE_BAR) {
                return true;
            }
        }
        return false;
    }

    /**
     * item offste to handle margin btw cardview
     */
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mItemOffset;

        /**
         *
         * @param itemOffset
         */
        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        /**
         *
         * @param context
         * @param itemOffsetId
         */
        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
