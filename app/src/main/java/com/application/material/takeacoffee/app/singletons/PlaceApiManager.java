package com.application.material.takeacoffee.app.singletons;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

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
import com.google.android.gms.location.places.Places;

import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

/**
 * Created by davide on 21/03/16.
 */
public class PlaceApiManager {
    private static final int MAX_HEIGHT = 0;
    private static final int MAX_WIDTH = 300;
    private GoogleApiClient mGoogleApiClient;
    private WeakReference<OnHandlePlaceApiResult> listener;
    private static PlaceApiManager instance;

    /**
     *
     * @param googleApiClient
     * @param listener
     */
    private PlaceApiManager(GoogleApiClient googleApiClient,
                         WeakReference<OnHandlePlaceApiResult> listener) {
        this.mGoogleApiClient = googleApiClient;
        this.listener = listener;
    }

    /**
     *
     * @param googleApiClient
     * @param listener
     * @return
     */
    public static PlaceApiManager getInstance(GoogleApiClient googleApiClient,
                                              WeakReference<OnHandlePlaceApiResult> listener) {
        return instance == null ?
                instance = new PlaceApiManager(googleApiClient, listener) : instance;
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
                        listener.get().onSetCoffeePlaceInfoOnListCallback(placeBuffer.get(0));
                        listener.get().onUpdatePhotoOnListCallback();
                        placeBuffer.release();
                    }
                });
    }

    /**
     *
     * @param placeId
     */
    public void getPhoto(final String placeId) {
        Bitmap cachedBitmap = CacheManager.getInstance().getBitmapFromMemCache(placeId);
        if (cachedBitmap == null) {
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
        //get photo
        PendingResult<PlacePhotoMetadataResult> result1 = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId);
        result1.setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    photoMetadataBuffer.get(0).getScaledPhoto(mGoogleApiClient,
                            MAX_WIDTH, MAX_HEIGHT)
                            .setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                @Override
                                public void onResult(@NonNull PlacePhotoResult photo) {
                                    if (photo.getStatus().isSuccess()) {
                                        CacheManager.getInstance().addBitmapToMemoryCache(placeId,
                                                photo.getBitmap());
                                        listener.get().onUpdatePhotoOnListCallback();
                                    }
                                }
                            });
                    photoMetadataBuffer.release();
                }
            }
        });

    }

    /**
     * main function to retrieve places data from google api
     */
    public void retrievePlaces() {
        try {
//            Collection<Integer> restrictToPlaceTypes = 0;
//            PlaceFilter filter = new PlaceFilter(restrictToPlaceTypes, false, null, null);
            PlaceFilter filter = new PlaceFilter();
            final PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, filter);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    for (PlaceLikelihood itemPlace : placeLikelihoods) {
                        String placeId = itemPlace.getPlace().getId();
                        getPhoto(placeId);
                        getInfo(placeId);
                    }
                }
            });

        } catch (SecurityException e) {
            e.printStackTrace();
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
    }


}
