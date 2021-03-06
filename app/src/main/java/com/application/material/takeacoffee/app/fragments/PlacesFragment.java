package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.UserDictionary;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.observer.CoffeePlaceAdapterObserver;
import com.application.material.takeacoffee.app.presenters.ChangeLocationAutocompleteFilterPresenter;
import com.application.material.takeacoffee.app.presenters.PlaceFilterPresenter;
import com.application.material.takeacoffee.app.scrollListeners.EndlessRecyclerOnScrollListener;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
import com.application.material.takeacoffee.app.singletons.GeocoderManager;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager.OnHandlePlaceApiResult;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager.RequestType;
import com.application.material.takeacoffee.app.utils.ConnectivityUtils;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.application.material.takeacoffee.app.utils.PermissionManager.OnEnablePositionCallbackInterface;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.application.material.takeacoffee.app.models.CoffeePlace.BAR_PLACE_TYPE;
import static com.application.material.takeacoffee.app.models.CoffeePlace.PLACE_RANKBY;

public class PlacesFragment extends Fragment implements
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        PlacesGridViewAdapter.CustomItemClickListener,
        PermissionManager.OnHandleGrantPermissionCallbackInterface, View.OnClickListener,
        OnEnablePositionCallbackInterface,
        PermissionManager.OnEnableNetworkCallbackInterface,
        SwipeRefreshLayout.OnRefreshListener, OnHandlePlaceApiResult,
        PlacesActivity.OnHandleFilterBackPressedInterface, GeocoderManager.OnHandleGeocoderResult {
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
    @Bind(R.id.changePlaceConfirmButtonId)
    View changePlaceConfirmButton;
    @Bind(R.id.changePlaceFilterAutocompleteTextviewId)
    AutoCompleteTextView changePlaceFilterAutocompleteTextview;
    @Bind(R.id.changePlaceTextInputLayoutId)
    TextInputLayout changePlaceTextInputLayout;
    @Bind(R.id.placePositionFilterEditButtonId)
    ImageView placePositionFilterEditButton;
    @Bind(R.id.changePlaceFilterCardviewId)
    View changePlaceFilterCardview;
    @Bind(R.id.coffeePlaceFilterBackgroundFrameLayoutId)
    View coffeePlaceFilterBackgroundFrameLayout;
    @Bind(R.id.coffeePlaceFilterBackgroundProgressbarId)
    View coffeePlaceFilterBackgroundProgressbar;
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
    private EndlessRecyclerOnScrollListener scrollListener;
    private PlaceFilterPresenter placeFilterPresenter;

    @State
    public ArrayList<CoffeePlace> placeList = new ArrayList<>();
    private Subscription obsSubscription;
    private String selectedLocationName;
    private ChangeLocationAutocompleteFilterPresenter changePlaceAutocompletePresenter;
    private View mainView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef = (PlacesActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_places_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstance) {
        ButterKnife.bind(this, view);
        Icepick.restoreInstanceState(this, savedInstance);

        permissionManager = PermissionManager.getInstance();
        changePlaceAutocompletePresenter = ChangeLocationAutocompleteFilterPresenter
                .getInstance(new WeakReference<>(getContext()),
                        new WeakReference<OnHandlePlaceApiResult>(this),
                        new View[] {changePlaceFilterAutocompleteTextview, changePlaceTextInputLayout});
        initView(savedInstance);
        mainView = view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //handle listener (since it still equal to the one on review activity)
        placesApiManager = PlaceApiManager.getInstance(new WeakReference<OnHandlePlaceApiResult>(this),
                new WeakReference<Context>(getActivity().getApplicationContext()));
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
//        Log.e("placelist size ", "" + placeList.size());
        if (placeList.size() != 0) {
            handleInfo(placeList);
            return;
        }

        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
    }

    /**
     *
     */
    private void initFilters() {
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
        Utils.hideKeyboard(new WeakReference<>(getActivity().getApplicationContext()),
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
                //force to edit mode
                placeFilterPresenter.setEditMode();
                handleFilterBackPressed();
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
                .getInstance(new WeakReference<OnHandlePlaceApiResult>(this),
                        new WeakReference<>(getActivity().getApplicationContext()));
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
                    Toast.makeText(getContext(), getString(R.string.retrieving_more_place),
                            Toast.LENGTH_LONG).show();
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
//        Log.e("TAG", type.toString());
        if (type == RequestType.PLACE_INFO) {
            ArrayList<CoffeePlace> list = (ArrayList<CoffeePlace>) result;
            handleInfo(list);
            placeList.addAll(list);
        } else if (type == RequestType.MORE_PLACE_INFO) {
            scrollListener.setLoadingEnabled(true);
            ArrayList<CoffeePlace> list = (ArrayList<CoffeePlace>) result;
            handleMoreInfo(list);
            placeList.addAll(list);
        } else if (type == RequestType.PLACE_CITES) {
            changePlaceAutocompletePresenter.onCitiesRetrieveSuccess(result, type);
        }
    }

    @Override
    public void onPlaceApiEmptyResult() {
        //add type
        showErrorMessage();
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
        placeList.clear();
    }

    /**
     *
     */
    private void showErrorMessage() {
        try {
            Utils.showSnackbar(new WeakReference<>(getContext()), mainView,
                    R.string.no_place_found);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaceApiError(RequestType type) {
        //TODO LEAK - if changing activity u must unsuscribe observer
        scrollListener.setLoadingEnabled(true);
        if (type == RequestType.PLACE_INFO) {
            showErrorMessage();
            ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        } else if (type == RequestType.PLACE_CITES) {
            changePlaceAutocompletePresenter.onCitiesRetrieveError(type);
        }
//        Log.e("TAG", "ERROR on retrieve result");
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
        if (placeFilterPresenter.isCollapsed()) {
            placeFilterPresenter.onExpand();
        }
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
        retrievePlacesAndUpdateUI();
    }

    /**
     *
     */
    private void retrievePlacesAndUpdateUI() {
        //TODO big issue over here - position still not available
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(false);
        cleanRecyclerViewData();
        final String latLngString = SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);
        showHideLocationServiceLayout(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                obsSubscription = placesApiManager.retrievePlacesAsync(latLngString,
                        PLACE_RANKBY, BAR_PLACE_TYPE);
            }
        }, 2000);

    }

    /**
     *
     */
    private void cleanRecyclerViewData() {
        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).clearAllItems();
        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        placeList.clear();
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
            case R.id.coffeePlaceFilterCardviewId:
                startActivity(new Intent(getActivity().getApplicationContext(), MapActivity.class));
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
                    setActionbarHomeButtonEnabled(true);
                }
                break;
            case R.id.noLocationServiceButtonId:
                permissionManager
                        .enablePosition(new WeakReference<>((AppCompatActivity) mainActivityRef));
                break;
            case R.id.noNetworkServiceButtonId:initPermissionChainResponsibility();
                break;
            case R.id.emptyResultButtonId:
                coffeePlacesEmptyResult.setVisibility(View.GONE);
                retrievePlacesAndUpdateUI();
                break;
        }
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
        selectedLocationName = changePlaceFilterAutocompleteTextview.getText().toString();
        GeocoderManager.getInstance(new WeakReference<GeocoderManager.OnHandleGeocoderResult>(this),
                new WeakReference<>(getContext()))
                .getLatLongByLocationName(selectedLocationName);

    }

    /**
     *
     */
    private void setActionbarHomeButtonEnabled(boolean isEnabled) {
        if (getActivity() == null) {
            return;
        }

        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(isEnabled);
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

    @Override
    public boolean handleFilterBackPressed() {
        if (placeFilterPresenter.isEdit()) {
            placeFilterPresenter.onHideEditPlace();
            setActionbarHomeButtonEnabled(false);
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

    @Override
    public void onGeocoderSuccess(LatLng latLng) {
        if (placeFilterPresenter.isLoadingEdit()) {
            coffeePlaceFilterBackgroundProgressbar.setVisibility(View.GONE);
            placeFilterPresenter.onExpandEdit();
            setActionbarHomeButtonEnabled(false);
            clearEditText();
            saveLocationOnStorage(latLng);
            retrievePlacesAndUpdateUI();
            updateFiltersPlaceLocation();
        }
    }

    //TODO move on presenter
    @Override
    public void onGeocoderError() {
        if (placeFilterPresenter.isLoadingEdit()) {
            coffeePlaceFilterBackgroundProgressbar.setVisibility(View.GONE);
            changePlaceTextInputLayout.setErrorEnabled(true);
            changePlaceTextInputLayout.setError(getString(R.string.no_place_from_geocode_found));
            placeFilterPresenter.onOnlyShowEditPlace();
        }
    }

    /**
     * on action done
     */
    private void saveLocationOnStorage(LatLng latLng) {
        if (getActivity() == null) {
            return;
        }
//        Log.e("PICK", "start activity location ->" + Utils.getLatLngString(latLng));
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(new WeakReference<>(getContext()));
        sharedPref.setValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY,
                Utils.getLatLngString(latLng));
        sharedPref.setValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY,
                Utils.capitalize(selectedLocationName));
    }

}
