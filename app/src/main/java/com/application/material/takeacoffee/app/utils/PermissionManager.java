package com.application.material.takeacoffee.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 10/03/16.
 */
public class PermissionManager {
    private static final int PERMISSIONS_TO_REQUEST_FINE_LOCATION = 0;
    public static final int ACTION_LOCATION_SOURCE_SETTINGS = 1;
    private static PermissionManager instance;
    private OnHandleGrantPermissionCallbackInterface listener;
    private WeakReference<OnEnablePositionCallbackInterface> locationListener;

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
     *
     * @param activityWeakRef
     */
    public void checkLocationServiceIsEnabled(WeakReference<AppCompatActivity> activityWeakRef,
                                              WeakReference<OnEnablePositionCallbackInterface> locationListener) {
        this.locationListener = locationListener;
        final LocationManager manager = (LocationManager) activityWeakRef.get()
                .getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(activityWeakRef);
            return;
        }

        locationListener.get().onEnablePositionCallback();
    }

    /**
     *
     * @param activityWeakRef
     */
    private void buildAlertMessageNoGps(final WeakReference<AppCompatActivity> activityWeakRef) {
        final AlertDialog alert = new AlertDialog.Builder(activityWeakRef.get(),
                   R.style.CustomAlertDialogStyle)
                .setMessage("Your GPS is not activated,\n do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        enablePosition(activityWeakRef);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        locationListener.get().onEnablePositionErrorCallback();
                    }
                })
                .create();
        alert.show();
    }

    /**
     *
     * @param activityWeakRef
     */
    public void enablePosition(final WeakReference<AppCompatActivity> activityWeakRef) {
        activityWeakRef.get()
                .startActivityForResult(new Intent(android.provider.Settings
                        .ACTION_LOCATION_SOURCE_SETTINGS), ACTION_LOCATION_SOURCE_SETTINGS);
    }

    /**
     *
     * @param activityWeakRef
     * @param networkListener
     */
    public void checkNetworkServiceIsEnabled(WeakReference<Context> activityWeakRef,
                                             OnEnableNetworkCallbackInterface networkListener) {

        if (((CoffeePlacesApplication) activityWeakRef.get().getApplicationContext()).isCacheValid() ||
                ConnectivityUtils.checkConnectivity(activityWeakRef)) {
            networkListener.onEnableNetworkCallback();
            return;
        }
        networkListener.onEnableNetworkErrorCallback();
    }

    /**
     *
     */
    public void onEnablePositionResult() {
        locationListener.get().onEnablePositionCallback();
    }

    /**
     * handle callback
     */
    public interface OnEnablePositionCallbackInterface {
        void onEnablePositionCallback();
        void onEnablePositionErrorCallback();
    }

    /**
     * handle callback
     */
    public interface OnEnableNetworkCallbackInterface {
        void onEnableNetworkCallback();
        void onEnableNetworkErrorCallback();
    }

    /**
     * handle callback
     */
    public interface OnHandleGrantPermissionCallbackInterface {
        void onHandleGrantPermissionCallback();
    }

}
