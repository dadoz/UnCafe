package com.application.material.takeacoffee.app.helper;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.lang.ref.WeakReference;

import static com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;
import static com.google.android.gms.common.api.CommonStatusCodes.SUCCESS;
import static com.google.android.gms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE;

public class LocationHelper {

    public static final int REQUEST_CHECK_SETTINGS = 111;

    public static void displayLocationSettingsRequest(GoogleApiClient googleApiClient,
                                                      final WeakReference<Activity> lst, final WeakReference<DisplayLocationCallbacks> listCallbacks) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case SUCCESS:
                        if (listCallbacks.get() != null)
                            listCallbacks.get().onDisplayLocationSuccessCallback();
                        break;
                    case RESOLUTION_REQUIRED:
                        try {
                            if (lst.get() != null)
                                status.startResolutionForResult(lst.get(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            if (listCallbacks.get() != null)
                                listCallbacks.get().onDisplayLocationErrorCallback();
                        }
                        break;
                    case SETTINGS_CHANGE_UNAVAILABLE:
                        if (listCallbacks.get() != null)
                            listCallbacks.get().onDisplayLocationErrorCallback();
                        break;
                }
            }
        });
    }

    public interface DisplayLocationCallbacks {
        void onDisplayLocationSuccessCallback();
        void onDisplayLocationErrorCallback();
    }
}
