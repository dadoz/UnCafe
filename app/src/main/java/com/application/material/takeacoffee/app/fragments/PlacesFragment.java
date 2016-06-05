package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;
import com.application.material.takeacoffee.app.decorators.ItemOffsetDecoration;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.observer.CoffeePlaceAdapterObserver;
import com.application.material.takeacoffee.app.presenters.PlaceFilterPresenter;
import com.application.material.takeacoffee.app.scrollListeners.EndlessRecyclerOnScrollListener;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager.OnHandlePlaceApiResult;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager.RequestType;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.application.material.takeacoffee.app.utils.PermissionManager.OnEnablePositionCallbackInterface;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.BAR_PLACE_TYPE;
import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.PLACE_RANKBY;

public class PlacesFragment extends Fragment implements
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        PlacesGridViewAdapter.CustomItemClickListener,
        PermissionManager.OnHandleGrantPermissionCallbackInterface, View.OnClickListener,
        OnEnablePositionCallbackInterface,
        PermissionManager.OnEnableNetworkCallbackInterface,
        SwipeRefreshLayout.OnRefreshListener, OnHandlePlaceApiResult {
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private PermissionManager permissionManager;
    private PlaceApiManager placesApiManager;

    @Bind(R.id.coffeePlacesRecyclerViewId)
    RecyclerView coffeePlacesRecyclerview;
    @Bind(R.id.coffeePlacesProgressId)
    ProgressBar coffeePlacesProgress;
    @Bind(R.id.coffeePlacesEmptyResultId)
    View coffeePlacesEmptyResult;
    @Bind(R.id.coffeePlaceFilterBackgroundId)
    View coffeePlaceFilterBackground;
    @Bind(R.id.coffeePlaceFilterCardviewId)
    View coffeePlaceFilterCardview;
    @Bind(R.id.noLocationServiceLayoutId)
    View noLocationServiceLayout;
    @Bind(R.id.noNetworkServiceLayoutId)
    View noNetworkServiceLayout;
    @Bind(R.id.emptyResultButtonId)
    View emptyResultButton;
    @Bind(R.id.noNetworkServiceButtonId)
    View noNetworkServiceButton;
    @Bind(R.id.noLocationServiceButtonId)
    View noLocationServiceButton;
    @Bind(R.id.coffeePlaceSwipeRefreshLayoutId)
    SwipeRefreshLayout coffeePlaceSwipeRefreshLayout;
    @Bind(R.id.placePositionFilterTextViewId)
    TextView placePositionFilterTextView;
    @Bind(R.id.coffeePlaceFilterClearPositionButtonId)
    View coffeePlaceFilterClearPositionButton;
    private EndlessRecyclerOnScrollListener scrollListener;
    private PlaceFilterPresenter placeFilterPresenter;
    private View coffeeMachineView;

    @State
    public ArrayList<CoffeePlace> placeList = new ArrayList<>();
    private Subscription obsSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef = (PlacesActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Icepick.restoreInstanceState(this, savedInstance);
        coffeeMachineView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_places_layout, container, false);
        ButterKnife.bind(this, coffeeMachineView);

        permissionManager = PermissionManager.getInstance();
        initView(savedInstance);
        return coffeeMachineView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        unsubscribeObservable();
        super.onDestroy();
    }

    /**
     * init view
     * @param savedInstance
     */
    public void initView(Bundle savedInstance) {
        emptyResultButton.setOnClickListener(this);
        initActionBar();
        setHasOptionsMenu(true);
        coffeePlaceSwipeRefreshLayout.setOnRefreshListener(this);
        initFilters();
        initGridViewAdapter();
        initGooglePlaces();
        if (savedInstance == null) {
            initPermissionChainResponsibility();
            return;
        }
        //init from saved instance
        initViewFromSavedInstance();
    }

    /**
     *
     */
    private void initViewFromSavedInstance() {
        handleInfo(placeList);
    }

    /**
     *
     */
    private void initFilters() {
        coffeePlaceFilterClearPositionButton.setOnClickListener(this);
        coffeePlaceFilterCardview.setOnClickListener(this);
        placePositionFilterTextView.setText(SharedPrefManager
                .getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY));
        placeFilterPresenter = PlaceFilterPresenter.getInstance(new WeakReference<>(getContext()),
                new View[] {coffeePlaceFilterCardview, coffeePlaceFilterBackground, coffeePlaceSwipeRefreshLayout});
        placeFilterPresenter.onCollapse();
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
        Bundle bundle = createBundleByPlacePosition(pos);
        changeActivity(bundle);
    }

    /**
     *
     * @param pos
     */
    private Bundle createBundleByPlacePosition(int pos) {
        CoffeePlace place = ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
                .getItem(pos);
        Bundle bundle = new Bundle();
        bundle.putString(CoffeePlace.COFFEE_PLACE_ID_KEY, place.getId());
        bundle.putString(CoffeePlace.COFFEE_PLACE_NAME_KEY, place.getName());
        bundle.putString(CoffeePlace.COFFEE_PLACE_PHOTO_REFERENCE_KEY, place.getPhotoReference());
        bundle.putString(CoffeePlace.COFFEE_PLACE_LATLNG_REFERENCE_KEY,
                place.getGeometry().getLocation().getLat() + "," +
                place.getGeometry().getLocation().getLng());
        return bundle;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.coffee_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_policy:
                showInfoDialog();
                break;
            case R.id.action_settings:
                unsubscribeObservable();
                changeFragment(new SettingListFragment(),
                        SettingListFragment.SETTING_LIST_FRAG_TAG);
                break;
        }
        return true;
    }

    /**
     *
     */
    private void unsubscribeObservable() {
        if (obsSubscription != null) {
            obsSubscription.unsubscribe();
        }
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
     *
     */
    private void changeActivity(Bundle bundle) {
        EventBusSingleton.getInstance().postSticky(bundle);
        startActivity(new Intent(getActivity(), ReviewListActivity.class));
    }

    /**
     * samplePlacesApi
     */
    private void initGooglePlaces() {
        placesApiManager = PlaceApiManager
                .getInstance(new WeakReference<OnHandlePlaceApiResult>(this), new WeakReference<>(getContext()));
    }

    /**
     * location permission
     */
    private void initNetworkPermission() {
        WeakReference<Context> activityRef =
                new WeakReference<>((Context) mainActivityRef);
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
        permissionManager.checkLocationServiceIsEnabled(activityRef,
                new WeakReference<OnEnablePositionCallbackInterface>(this));
    }

    /**
     * init grid view adapter
     */
    private void initGridViewAdapter() {
        PlacesGridViewAdapter adapter = new PlacesGridViewAdapter(new WeakReference<>(getContext()),
                new ArrayList<CoffeePlace>());
        adapter.registerAdapterDataObserver(new CoffeePlaceAdapterObserver(new WeakReference<>(adapter),
                coffeePlacesProgress, coffeePlacesEmptyResult));
        adapter.setOnItemClickListener(this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        coffeePlacesRecyclerview.setLayoutManager(layoutManager);
        coffeePlacesRecyclerview.setAdapter(adapter);
        coffeePlacesRecyclerview.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.small_padding));
        initScrollListener(layoutManager);
        coffeePlacesRecyclerview.addOnScrollListener(scrollListener);
        //TODO add footer or header to handle more review spinner and also get map button!
    }

    /**
     * TODO refactor
     * @param layoutManager
     */
    private void initScrollListener(StaggeredGridLayoutManager layoutManager) {
        scrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            protected void isFirstItemVisible() {
                if (placeFilterPresenter.isCollapsed()) {
                    placeFilterPresenter.onExpand();
                }
            }

            @Override
            protected void isFirstItemNotVisible() {
                if (!placeFilterPresenter.isCollapsed()) {
                    placeFilterPresenter.onCollapse();
                }
            }

            @Override
            public void onLoadMore(int currentPage) {
                final String pageToken = ((PlacesGridViewAdapter) coffeePlacesRecyclerview
                        .getAdapter()).getPageToken();
                if (pageToken != null) {
                    Toast.makeText(getContext(), getString(R.string.retrieving_more_place), Toast.LENGTH_LONG).show();
                    placesApiManager.retrieveMorePlacesAsync(pageToken);
                }
            }
        };
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Icepick.saveInstanceState(this, savedInstanceState);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO handle it
    }

    /**
     *
     */
    @Override
    public void onPlaceApiSuccess(Object result, RequestType type) {
        if (type == RequestType.PLACE_INFO) {
            handleInfo((ArrayList<CoffeePlace>) result);
            placeList.addAll((ArrayList<CoffeePlace>) result);
        } else if (type == RequestType.MORE_PLACE_INFO) {
            scrollListener.setLoadingEnabled(true);
            handleMoreInfo((ArrayList<CoffeePlace>) result);
            placeList.addAll((ArrayList<CoffeePlace>) result);
        }
    }


    @Override
    public void onEmptyResult() {
        showErrorMessage();
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
    }

    /**
     *
     */
    private void showErrorMessage() {
        try {
            Utils.showSnackbar(new WeakReference<>(getContext()), coffeeMachineView,
                    R.string.no_place_found);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResult(RequestType type) {
        //TODO LEAK - if changing activity u must unsuscribe observer
        scrollListener.setLoadingEnabled(true);
        if (type == RequestType.PLACE_INFO) {
            showErrorMessage();
            ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        }
        Log.e("TAG", "ERROR on retrieve result");
    }

    /**
     *
     * @param placeList
     */
    private void handleMoreInfo(ArrayList<CoffeePlace> placeList) {
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
                .appendAllItems(placeList);
        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();

    }

    /**
     *
     * @param placeList
     */
    public void handleInfo(ArrayList<CoffeePlace> placeList) {
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
                .addAllItems(placeList);
        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onHandleGrantPermissionCallback() {
        handleLocationServiceEnabled();
    }

    @Override
    public void onEnablePositionCallback() {
        //TODO big issue over here - position still not available
        final String latLngString = SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);
        showHideLocationServiceLayout(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                obsSubscription = placesApiManager.retrievePlacesAsync(latLngString, PLACE_RANKBY, BAR_PLACE_TYPE);
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
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).clearAllItems();
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
            case R.id.coffeePlaceFilterClearPositionButtonId:
                clearStoredLocation();
                break;
            case R.id.coffeePlaceFilterCardviewId:
                startActivity(new Intent(getContext(), MapActivity.class));
                break;
            case R.id.noLocationServiceButtonId:
                permissionManager
                        .enablePosition(new WeakReference<>((AppCompatActivity) mainActivityRef));
                break;
            case R.id.noNetworkServiceButtonId:initPermissionChainResponsibility();
                break;
            case R.id.emptyResultButtonId:
                coffeePlacesEmptyResult.setVisibility(View.GONE);
                onEnablePositionCallback();
                break;
        }
    }

    @Override
    public void onRefresh() {
        //TODO need to be implemented
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                coffeePlaceSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
//        handleRefreshInitCallback();
//        handleRefreshEndCallback();
//        final String latLngString = SharedPrefManager.getInstance(new WeakReference<>(getContext()))
//                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);
//        placesApiManager.retrievePlacesAsync(latLngString, PLACE_RANKBY, BAR_PLACE_TYPE);
    }

    /**
     *
     */
    public void showInfoDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                R.style.CustomAlertDialogStyle)
                .setView(R.layout.dialog_info_layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    /**
     *
     */
    private void clearStoredLocation() {
        SharedPrefManager.getInstance(new WeakReference<>(getContext())).clearAll();
        startActivity(new Intent(getContext(), PickPositionActivity.class));
        getActivity().finish();
    }
}
