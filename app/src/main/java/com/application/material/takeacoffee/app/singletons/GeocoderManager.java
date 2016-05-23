package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by davide on 21/03/16.
 */
public class GeocoderManager {
    private static WeakReference<Context> contextWeakRefer;
    private static WeakReference<OnHandleGeocoderResult> listener;
    private static GeocoderManager instance;
    private final Geocoder geocoder;
    private Object lastKnowLocationCustom;

    /**
     *
     * @param ctx
     */
    private GeocoderManager(WeakReference<Context> ctx) {
        contextWeakRefer = ctx;
        geocoder = new Geocoder(contextWeakRefer.get(), Locale.getDefault());
    }

    /**
     *
     * @param geocoderListener
     * @return
     */
    public static GeocoderManager getInstance(WeakReference<OnHandleGeocoderResult> geocoderListener,
                                              WeakReference<Context> ctx) {
        listener = geocoderListener;
        contextWeakRefer = ctx;
        return instance == null ?
                instance = new GeocoderManager(contextWeakRefer) : instance;
    }

    /**
     *
     * @param locationName
     */
    public void getLatLongByLocationName(final String locationName) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
                    final LatLng latLng = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener.get() != null) {
                                listener.get().onGeocoderSuccess(latLng);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener.get() != null) {
                                listener.get().onGeocoderError();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     *
     */
    public void getCurrentLatLong() {
        Location location = getLastKnowLocationCustom();
        if (location == null) {
            listener.get().onGeocoderError();
            return;
        }
        listener.get().onGeocoderSuccess(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     *
     * @return
     */
    public Location getLastKnowLocationCustom() {
        LocationManager locationManager = (LocationManager) contextWeakRefer.get()
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null &&
                    (bestLocation == null ||
                        location.getAccuracy() < bestLocation.getAccuracy())) {
                    bestLocation = location;
                }
        }
        return bestLocation;
    }

    /**
     * handle
     */
    public interface OnHandleGeocoderResult {
        void onGeocoderSuccess(LatLng latLng);
        void onGeocoderError();
    }
}
