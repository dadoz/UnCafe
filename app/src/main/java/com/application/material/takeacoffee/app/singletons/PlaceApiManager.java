package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.util.Log;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.android.gms.common.api.GoogleApiClient;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.Observable;
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
    private static WeakReference<Context> contextWeakRef;
    private static final String CAFE_PLACE_TYPE = "cafe";
    public static final String BAR_PLACE_TYPE = "bar";
    public static final String PLACE_RANKBY = "distance";

    public enum RequestType {PLACE_INFO, MORE_PLACE_INFO, PLACE_REVIEWS, PLACE_PHOTO};
    public RequestType requestType;

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
                                              WeakReference<Context> ctx) {
        listener = placeApiListener;
        contextWeakRef = ctx;
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
        requestType = RequestType.PLACE_INFO;

        setObservable(RetrofitManager.getInstance(contextWeakRef)
                .listPlacesByLocationAndType(location, rankBy, type));
    }

    /**
     * main function to retrieve place reviews data from google api
     * @param placeId
     */
    public void retrieveReviewsAsync(String placeId) {
        requestType = RequestType.PLACE_REVIEWS;
        setObservable(RetrofitManager.getInstance(contextWeakRef)
                .listReviewsByPlaceId(placeId));
    }

    /**
     * main function to retrieve place reviews data from google api
     * @param pageToken
     */
    public void retrieveMorePlacesAsync(String pageToken) {
        requestType = RequestType.MORE_PLACE_INFO;
        setObservable(RetrofitManager.getInstance(contextWeakRef)
                .listMorePlacesByPageToken(pageToken));
    }

    /**
     *
     * @param observable
     */
    private void setObservable(Observable<ArrayList<Object>> observable) {
        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Object>>() {

                    @Override
                    public void onCompleted() {
                        Log.e("TAG", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "error" + e.getMessage());
                        if (listener.get() != null) {
                            listener.get().onErrorResult();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<Object> list) {
                        if (list == null ||
                                list.size() == 0) {
                            if (listener.get() != null) {
                                listener.get().onEmptyResult();
                            }
                            return;
                        }
                        if (listener.get() != null) {
                            listener.get().onPlaceApiSuccess(list, requestType);
                        }
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
