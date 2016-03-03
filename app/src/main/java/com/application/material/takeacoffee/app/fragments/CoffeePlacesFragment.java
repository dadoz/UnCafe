package com.application.material.takeacoffee.app.fragments;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


/**
 * Created by davide on 3/13/14.
 */
public class CoffeePlacesFragment extends Fragment implements
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<AutocompletePredictionBuffer> {
    private static final String TAG = "coffeeMachineFragment";
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private View coffeeMachineView;
    @Bind(R.id.coffeeMachineGridLayoutId) GridView coffeeMachineGridLayout;
    private ArrayList<CoffeeMachine> coffeePlacesList;
    private GoogleApiClient mGoogleApiClient;
    private Object photo;

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
    public void onResume(){
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
        setHasOptionsMenu(true);
        if (BuildConfig.DEBUG) {
            coffeePlacesList = getcoffeePlacesListTest();
        }
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
    private void findCoffeePlacesByGooglePlaces() {

        String query = "coffee";
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(45.0, 7.7),
                new LatLng(45.0, 7.7));
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi
                .getAutocompletePredictions(mGoogleApiClient, query, bounds, autocompleteFilter);
        result.setResultCallback(this);
    }

    /**
     * init grid view adapter
     */
    private void initGridViewAdapter() {
        coffeeMachineGridLayout.setAdapter(new CoffeeMachineGridAdapter(this.getActivity(),
                R.layout.coffee_machine_template, coffeePlacesList));
        coffeeMachineGridLayout.setOnItemClickListener(this);
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
        tmp.add(new CoffeeMachine("0", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("1", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("2", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("3", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("4", "balllala", "hey", null));
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
        AutocompletePrediction item = autocompletePredictions.get(0);
        Log.e("TAG", item.getFullText(null).toString());
        String placeId = item.getPlaceId();

//        getPhoto(placeId);
        getInfo(placeId);
    }
    /**
     *
     * @param placeId
     */
    public void getInfo(final String placeId) {
        //get name and address
        final PendingResult<PlaceBuffer> result = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        result.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer placeBuffer) {
                Place place = placeBuffer.get(0);
                Log.e("TAG", place.getName().toString() + place.getAddress().toString());
                coffeePlacesList.get(0).setName(place.getName().toString().toLowerCase());
                coffeePlacesList.get(0).setAddress(place.getAddress().toString().toLowerCase());
                synchronized (coffeeMachineGridLayout.getAdapter()) {
                    ((ArrayAdapter) coffeeMachineGridLayout.getAdapter()).notifyDataSetChanged();
                }

            }
        });
    }

    /**
     *
     * @param placeId
     */
    public void getPhoto(String placeId) {
        //get photo
        PendingResult<PlacePhotoMetadataResult> result1 = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId);
        result1.setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {
                PlacePhotoMetadata photoMetadata = placePhotoMetadataResult.getPhotoMetadata().get(0);
                photoMetadata.getPhoto(mGoogleApiClient).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                    @Override
                    public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
//                        placePhotoResult.getBitmap();
                        Log.e("TAG", "u got photo");
                        coffeePlacesList.get(0).setPhoto(placePhotoResult.getBitmap());
                        synchronized (coffeeMachineGridLayout.getAdapter()) {
                            ((ArrayAdapter) coffeeMachineGridLayout.getAdapter()).notifyDataSetChanged();
                        }
                    }
                });
            }
        });
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



}
