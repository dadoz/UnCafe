package com.application.material.takeacoffee.app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.BAR_PLACE_TYPE;
import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.PLACE_RANKBY;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PlaceApiManager.OnHandlePlaceApiResult {
    private static final float ZOOM_LEVEL = 16;
    @Bind(R.id.mapToolbarId)
    public Toolbar toolbar;
    private PlaceApiManager placesApiManager;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        placesApiManager = PlaceApiManager
                .getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
                        new WeakReference<>(getApplicationContext()));

        initView();
    }

    /**
     * init actionbar
     */
    public void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(getString(R.string.map_name));
        }
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
        initMap();
    }

    /**
     *
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //TODO add marker
        //getAsyncMarker();
        final String latLngString = SharedPrefManager.getInstance(new WeakReference<>(getApplicationContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);
        placesApiManager.retrievePlacesAsync(latLngString, PLACE_RANKBY, BAR_PLACE_TYPE);
        map = googleMap;
    }

    /**
     *
     */
    public void addMarkerOnMap(float lat, float lng, String title) {
        if (map != null) {
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title(title));
        }
    }

    /**
     *
     * @param lat
     * @param lng
     */
    private void centerCameraMapOnLatLng(float lat, float lng) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
        map.moveCamera(center);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(ZOOM_LEVEL);
        map.animateCamera(zoom);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlaceApiSuccess(Object list, PlaceApiManager.RequestType type) {
        setMarkerByCoffeePlaceList((ArrayList<CoffeePlace>) list);
    }

    /**
     *
     * @param list
     */
    private void setMarkerByCoffeePlaceList(ArrayList<CoffeePlace> list) {
        //TODO move to observer
        int k = 0;
        for (CoffeePlace coffeePlace : list) {
            float lat = coffeePlace.getGeometry().getLocation().getLat();
            float lng = coffeePlace.getGeometry().getLocation().getLng();
            addMarkerOnMap(lat, lng, coffeePlace.getName());
            if (k == 0) {
                centerCameraMapOnLatLng(lat, lng);
                k++;
            }
        }

    }


    @Override
    public void onEmptyResult() {

    }

    @Override
    public void onErrorResult() {

    }
}
