package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
    public void getLatLongByLocationName(String locationName) {
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 5);
            LatLng latLng = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
            if (listener.get() != null) {
                listener.get().onGeocoderSuccess(latLng);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener.get() != null) {
                listener.get().onGeocoderErrorResult();
            }
        }
    }
    /**
     * handle
     */
    public interface OnHandleGeocoderResult {
        void onGeocoderSuccess(LatLng latLng);
        void onGeocoderErrorResult();
    }
}
