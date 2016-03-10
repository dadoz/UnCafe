package com.application.material.takeacoffee.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.application.material.takeacoffee.app.CoffeePlacesActivity;
import com.application.material.takeacoffee.app.fragments.CoffeePlacesFragment;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 10/03/16.
 */
public class PermissionManager {

    private static final int PERMISSIONS_TO_REQUEST_FINE_LOCATION = 0;
    private static PermissionManager instance;
    private OnHandleGrantPermissionCallbackInterface listener;
    public static PermissionManager getInstance() {
        return instance == null ?
                instance = new PermissionManager() :
                instance;
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_TO_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listener.onHandleGrantPermissionCallback();
                }
            }
        }

    }

    /**
     *
     * @param listener
     */
    public void onRequestPermissions(WeakReference<AppCompatActivity> activityWeakRef,
            OnHandleGrantPermissionCallbackInterface listener) {
        this.listener = listener;
        if (ContextCompat.checkSelfPermission(activityWeakRef.get(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activityWeakRef.get(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_TO_REQUEST_FINE_LOCATION);
            return;
        }

        listener.onHandleGrantPermissionCallback();
    }

    /**
     * handle callback
     */
    public interface OnHandleGrantPermissionCallbackInterface {
        void onHandleGrantPermissionCallback();
    }

}
