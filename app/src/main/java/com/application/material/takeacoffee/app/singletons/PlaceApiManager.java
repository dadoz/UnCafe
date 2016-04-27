package com.application.material.takeacoffee.app.singletons;

import android.util.Log;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.android.gms.common.api.GoogleApiClient;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
    public enum RequestType {PLACE_INFO, PLACE_REVIEWS, PLACE_PHOTO};

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
     * main function to retrieve places data from google api
     * @param location
     * @param rankBy
     * @param type
     */
    public void retrievePlacesAsync(String location, String rankBy, String type) {
        RetrofitManager.getInstance()
                .listPlacesByLocationAndType(location, rankBy, type)
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
                        listener.get().onErrorResult();
                    }

                    @Override
                    public void onNext(ArrayList<CoffeePlace> coffeePlacesList) {
                        if (coffeePlacesList.size() == 0) {
                            listener.get().onEmptyResult();
                            return;
                        }
                        listener.get().onPlaceApiSuccess(coffeePlacesList, RequestType.PLACE_INFO);
                    }
                });
    }

    /**
     * main function to retrieve place reviews data from google api
     * @param placeId
     */
    public void retrieveReviewsAsync(String placeId) {
        RetrofitManager.getInstance()
                .listReviewsByPlaceId(placeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Review>>() {
                    @Override
                    public void onCompleted() {
                        Log.e("TAG", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "error" + e.getMessage());
                        listener.get().onErrorResult();
                    }

                    @Override
                    public void onNext(ArrayList<Review> reviewList) {
                        if (reviewList.size() == 0) {
                            listener.get().onEmptyResult();
                            return;
                        }
                        listener.get().onPlaceApiSuccess(reviewList, RequestType.PLACE_REVIEWS);
                    }
                });
    }

    /**
     * handle
     */
    public interface OnHandlePlaceApiResult {
        void onPlaceApiSuccess(Object list, RequestType type);
        void onEmptyResult();
        void onErrorResult();
    }
}
