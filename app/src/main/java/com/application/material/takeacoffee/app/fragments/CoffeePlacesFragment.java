package com.application.material.takeacoffee.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;



/**
 * Created by davide on 3/13/14.
 */
public class CoffeePlacesFragment extends Fragment implements
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<AutocompletePredictionBuffer>, PlacesGridViewAdapter.CustomItemClickListener, PermissionManager.OnHandleGrantPermissionCallbackInterface {
    private static final String TAG = "coffeeMachineFragment";
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private View coffeeMachineView;
    @Bind(R.id.coffeePlacesRecyclerViewId)
    RecyclerView coffeePlacesRecyclerview;
    private ArrayList<CoffeeMachine> coffeePlacesList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Object photo;
    private PermissionManager permissionManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef = (CoffeePlacesActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        coffeeMachineView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_machine, container, false);
        ButterKnife.bind(this, coffeeMachineView);
        initView();
        return coffeeMachineView;
    }

    @Override
    public void onResume() {
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
        setHasOptionsMenu(true);
//        if (BuildConfig.DEBUG) {
//            coffeePlacesList = getcoffeePlacesListTest();
//        }
        initGooglePlaces();
        initGridViewAdapter();
        findCoffeePlacesByGooglePlaces();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        CoffeeMachine coffeeMachine = (CoffeeMachine) adapterView.getAdapter().getItem(position);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY, coffeeMachine);
        changeActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.coffee_machine, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_position:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new MapFragment(), null,
                                MapFragment.MAP_FRAG_TAG);
                break;
            case R.id.action_settings:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new SettingListFragment(), null,
                                SettingListFragment.SETTING_LIST_FRAG_TAG);
                break;
        }
        return true;
    }

    /**
     * change actiity on reviewList
     */
    private void changeActivity() {
        startActivity(new Intent(getActivity(), ReviewListActivity.class));
    }


    /**
     * samplePlacesApi
     */
    private void initGooglePlaces() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

    }

    /**
     * samplePlacesApi
     */
    public void findCoffeePlacesByGooglePlaces() {
        permissionManager = PermissionManager.getInstance();
        permissionManager.onRequestPermissions(
                new WeakReference<>((AppCompatActivity) mainActivityRef), this);
    }


    /**
     * init grid view adapter
     */
    private void initGridViewAdapter() {
        PlacesGridViewAdapter adapter = new PlacesGridViewAdapter(new WeakReference<>(getContext()),
                coffeePlacesList);
        adapter.setOnItemClickListener(this);
        coffeePlacesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        coffeePlacesRecyclerview.setAdapter(adapter);
        coffeePlacesRecyclerview.addItemDecoration(new ItemOffsetDecoration(getContext(),
                R.dimen.small_padding));
    }

    /**
     * init action bar
     */
    public void initActionBar() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setTitle(getString(R.string.app_name));
        }

    }

    /**
     *
     * @return
     */
    public ArrayList<CoffeeMachine> getcoffeePlacesListTest() {
        ArrayList<CoffeeMachine> tmp = new ArrayList<CoffeeMachine>();
        tmp.add(new CoffeeMachine("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeeMachine("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeeMachine("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeeMachine("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeeMachine("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeeMachine("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        return tmp;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
        for (AutocompletePrediction item : autocompletePredictions) {
            Log.e("INFO result", item.getFullText(null).toString());
            String placeId = item.getPlaceId();
            getPhoto(placeId);
            getInfo(placeId);
        }
        autocompletePredictions.release();
    }
    /**
     *
     * @param placeId
     */
    public void getInfo(final String placeId) {
        //get name and address
        Places.GeoDataApi
            .getPlaceById(mGoogleApiClient, placeId)
            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer placeBuffer) {
                    addCoffeePlaceInfoOnGridView(placeBuffer.get(0));
                    placeBuffer.release();
                }
            });
    }

    /**
     *
     * @param place
     */
    private void addCoffeePlaceInfoOnGridView(Place place) {
        setCoffeePlaceInfoOnList(place);
        synchronized (coffeePlacesRecyclerview.getAdapter()) {
            coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
        }

    }

    /**
     *
     * @param place
     */
    private void setCoffeePlaceInfoOnList(Place place) {
        coffeePlacesList.add(new CoffeeMachine(place.getId(), place.getName().toString().toLowerCase(),
                place.getAddress().toString().toLowerCase(), null));
    }

    /**
     *
     * @param placeId
     */
    public void getPhoto(final String placeId) {
        //get photo
        PendingResult<PlacePhotoMetadataResult> result1 = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId);
        result1.setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    photoMetadataBuffer.get(0).getPhoto(mGoogleApiClient).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult photo) {
                            if (photo.getStatus().isSuccess()) {
                                Log.e("BLA", "HEY :)");
                                setPhotoOnList(placeId, photo.getBitmap());
                            }
                        }
                    });
                    photoMetadataBuffer.release();
                }
            }
        });
    }

    /**
     *
     * @param placeId
     * @param photoBitmap
     */
    private void setPhotoOnList(String placeId, Bitmap photoBitmap) {
        CoffeeMachine item = findPlaceOnListById(placeId);
        if (item != null) {
            item.setPhoto(photoBitmap);
            synchronized (coffeePlacesRecyclerview.getAdapter()) {
                coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
            }
        }

    }

    /**
     *
     * @param placeId
     */
    private CoffeeMachine findPlaceOnListById(String placeId) {
        for (CoffeeMachine item : coffeePlacesList) {
            if (item.getId().equals(placeId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onItemClick(int pos, View v) {
        Log.e("TAG", "DOING job");
    }

//    @Override
//    public void onHandleGrantPermissionCallback() {
        //        String query = "coffee";
//        LatLngBounds bounds = new LatLngBounds(
//                new LatLng(45.06, 7.68),
//                new LatLng(45.10, 7.7));
//        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
//                .build();
//        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi
//                .getAutocompletePredictions(mGoogleApiClient, query, bounds, autocompleteFilter);
//    }

    @Override
    public void onHandleGrantPermissionCallback() {
        try {
            PlaceFilter autocompleteFilter = new PlaceFilter();
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, autocompleteFilter);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    for (PlaceLikelihood itemPlace : placeLikelihoods) {
                        Place item = itemPlace.getPlace();
                        Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                                itemPlace.getPlace().getName(),
                                itemPlace.getLikelihood()));
                        String placeId = item.getId();
                        getPhoto(placeId);
                        getInfo(placeId);
                    }
                    placeLikelihoods.release();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    @Subscribe
    public void onNetworkRespose(ArrayList<CoffeeMachine> coffeeMachinesList) {
//        Log.d(TAG, "get response from bus");
//        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//
//        if(coffeeMachinesList == null) {
//            //TODO handle adapter with empty data
//            return;
//        }
//
//        this.coffeePlacesList = coffeeMachinesList;
//        initView();
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
