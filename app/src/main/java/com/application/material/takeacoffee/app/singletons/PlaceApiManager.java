package com.application.material.takeacoffee.app.singletons;

import android.app.job.JobScheduler;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
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
import java.util.Set;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    public static PlaceApiManager getInstance(WeakReference<OnHandlePlaceApiResult> placeApiListener) {
        listener = placeApiListener;
        return instance == null ?
                instance = new PlaceApiManager(mGoogleApiClient) : instance;
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
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {

                        PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();
                        if (photoMetadataBuffer != null &&
                                photoMetadataBuffer.getCount() > 0) {
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
    public void retrievePlacesAsync(String location, String radius, String type) {
        //TODO this run on MAIN_THREAD
        Observable<ArrayList<CoffeePlace>> temp = RetrofitManager.getInstance()
                .listPlacesByLocationAndType(location, radius, type);

                temp
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ArrayList<CoffeePlace>>() {
                            @Override
                            public void onCompleted() {
                                Log.e("TAG", "completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TAG", "error" + e.getMessage());

                            }

                            @Override
                            public void onNext(ArrayList<CoffeePlace> coffeePlacesList) {
                                listener.get().onSetCoffeePlaceInfoOnListCallback(coffeePlacesList);
                            }
                        });
    }

    /**
     * main function to retrieve reviews data by placeId from google api
     * @param placeId
     */
    public ArrayList<Review> getReviewByPlaceId(String placeId) {
        //TODO this run on MAIN_THREAD
        RetrofitManager.getInstance()
                .listReviewByPlaceId(placeId);
        return null;
    }

    /**
     * handle
     */
    public interface OnHandlePlaceApiResult {
        void onSetCoffeePlaceInfoOnListCallback(ArrayList<CoffeePlace> place);
        void onUpdatePhotoOnListCallback();
        void handleEmptyList();
    }
}
