package com.application.material.takeacoffee.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.BAR_PLACE_TYPE;
import static com.application.material.takeacoffee.app.singletons.PlaceApiManager.PLACE_RANKBY;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PlaceApiManager.OnHandlePlaceApiResult, GoogleMap.OnMarkerClickListener {
    private static final float ZOOM_LEVEL = 15;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        map = googleMap;
        final String latLngString = SharedPrefManager.getInstance(new WeakReference<>(getApplicationContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY);

        placesApiManager.retrievePlacesAsync(latLngString, PLACE_RANKBY, BAR_PLACE_TYPE);
        centerMap(latLngString);
    }

    /**
     *
     */
    public void addMarkerOnMap(float lat, float lng, String title, String descritpion) {
        if (map != null) {
            map.setOnMarkerClickListener(this);
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.coffee_cup_icon_old))
                    .alpha(0.7f)
                    .snippet(descritpion)
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
        if (type == PlaceApiManager.RequestType.PLACE_INFO) {
            final String pageToken = ((ArrayList<CoffeePlace>) list).get(0).getPageToken().getToken();
            if (pageToken != null) {
                placesApiManager.retrieveMorePlacesAsync(pageToken);
            }
        }
        setMarkerByCoffeePlaceList((ArrayList<CoffeePlace>) list);
    }

    /**
     *
     * @param latLngString
     */
    private void centerMap(String latLngString) {
        String[] resultArray = latLngString.split(",");
        centerCameraMapOnLatLng(Float.parseFloat(resultArray[0]), Float.parseFloat(resultArray[1]));
    }
    /**
     *
     * @param list
     */
    private void setMarkerByCoffeePlaceList(ArrayList<CoffeePlace> list) {
        //TODO move to observer
        for (CoffeePlace coffeePlace : list) {
            float lat = coffeePlace.getGeometry().getLocation().getLat();
            float lng = coffeePlace.getGeometry().getLocation().getLng();
            addMarkerOnMap(lat, lng, coffeePlace.getName(), coffeePlace.getAddress());
        }

    }


    @Override
    public void onEmptyResult() {

    }

    @Override
    public void onErrorResult(PlaceApiManager.RequestType type) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
