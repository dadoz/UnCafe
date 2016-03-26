package com.application.material.takeacoffee.app.singletons;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.application.material.takeacoffee.app.utils.CacheManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Set;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by davide on 21/03/16.
 */
public class PlaceApiManager {
    private static final int MAX_HEIGHT = 0;
    private static final int MAX_WIDTH = 300;
    private static GoogleApiClient mGoogleApiClient;
    private static WeakReference<OnHandlePlaceApiResult> listener;
    private static PlaceApiManager instance;

    /**
     *
     * @param googleApiClient
     */
    private PlaceApiManager(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    /**
     *
     * @param placeApiListener
     * @return
     */
    public static PlaceApiManager getInstance(WeakReference<OnHandlePlaceApiResult> placeApiListener,
                                              GoogleApiClient mGoogleApiClient) {
        listener = placeApiListener;
        return instance == null ?
                instance = new PlaceApiManager(mGoogleApiClient) : instance;
    }

    /**
     *
     * @param placeId
     */
    public void getInfo(final String placeId) {
        Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(placeBuffer -> {
                    listener.get().onSetCoffeePlaceInfoOnListCallback(placeBuffer.get(0));
                    listener.get().onUpdatePhotoOnListCallback();
                    placeBuffer.release();
                });
    }

    /**
     *
     * @param placeId
     */
    public void getPhoto(final String placeId) {
        if (CacheManager.getInstance().getBitmapFromMemCache(placeId) == null) {
            retrievePhotoFromApi(placeId);
            return;
        }
        listener.get().onUpdatePhotoOnListCallback();
    }

    /**
     *
     * @param placeId
     */
    public void retrievePhotoFromApi(final String placeId) {
        //TODO refactor it
        Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(placePhotoMetadataResult -> {
                    PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();
                    if (photoMetadataBuffer.getCount() > 0) {
                        photoMetadataBuffer.get(0).getScaledPhoto(mGoogleApiClient,
                                MAX_WIDTH, MAX_HEIGHT)
                                .setResultCallback(photo -> {
                                    if (photo.getStatus().isSuccess()) {
                                        CacheManager.getInstance().addBitmapToMemoryCache(placeId,
                                                photo.getBitmap());
                                        listener.get().onUpdatePhotoOnListCallback();
                                    }
                                });
                        photoMetadataBuffer.release();
                    }
                });

    }

    /**
     * main function to retrieve places data from google api
     */
    public void retrievePlaces() {
        try {
            //TODO filter place tipe
            Set<Integer> restrictToPlaceTypes = PlaceTypes.ALL;
            PlaceFilter filter = new PlaceFilter(restrictToPlaceTypes, false, null, null);
            final PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, filter);
            result.setResultCallback(placeLikelihoods -> {
                Observable
                        .just(placeLikelihoods)
                        .filter(placeLikelihoods1 -> {
                            boolean isEmpty = placeLikelihoods1.getCount() != 0;
                            if (isEmpty) {
                                listener.get().handleEmptyList();
                            }
                            return isEmpty;
                        })
                        .flatMap(Observable::from)
                        .flatMap(placeLikelihood -> Observable.just(placeLikelihood.getPlace()))
                        .subscribe(place -> {
                            getPhoto(place.getId());
                            getInfo(place.getId());
                        });
            });

        } catch (SecurityException e) {
            e.printStackTrace();
            listener.get().handleEmptyList();
        }
    }


//    public void setPlacesFromPlacesApi() {
    //        String query = "coffee";
//        LatLngBounds bounds = new LatLngBounds(
//                new LatLng(45.06, 7.68),
//                new LatLng(45.10, 7.7));
//        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
//                .build();
//        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi
//                .getAutocompletePredictions(mGoogleApiClient, query, bounds, autocompleteFilter);
//            result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
//                @Override
//                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
//                    for (AutocompletePrediction item : autocompletePredictions) {
//                        Log.e("INFO result", item.getFullText(null).toString());
//                        String placeId = item.getPlaceId();
//                        getPhoto(placeId);
//                        getInfo(placeId);
//                    }
//                    autocompletePredictions.release();
//                }
//            });
//    }

    /**
     * handle
     */
    public interface OnHandlePlaceApiResult {
        void onSetCoffeePlaceInfoOnListCallback(Place place);
        void onUpdatePhotoOnListCallback();
        void handleEmptyList();
    }
}
