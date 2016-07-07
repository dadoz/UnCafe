package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.util.Log;

import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.android.gms.common.api.GoogleApiClient;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by davide on 21/03/16.
 */
public class PlaceApiManager {
    private static GoogleApiClient mGoogleApiClient;
    private static WeakReference<OnHandlePlaceApiResult> listener;
    private static PlaceApiManager instance;
    private static WeakReference<Context> contextWeakRef;

    public enum RequestType {PLACE_INFO, MORE_PLACE_INFO, PLACE_REVIEWS, PLACE_CITES, PLACE_PHOTO};
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
    public Subscription retrievePlacesAsync(String location, String rankBy, String type) {
        requestType = RequestType.PLACE_INFO;
        return initObservable(RetrofitManager.getInstance(contextWeakRef)
                .listPlacesByLocationAndType(location, rankBy, type));
    }


    /**
     * main function to retrieve place reviews data from google api
     * @param placeId
     */
    public Subscription retrieveReviewsAsync(String placeId) {
        requestType = RequestType.PLACE_REVIEWS;
        return initObservable(RetrofitManager.getInstance(contextWeakRef)
                .listReviewsByPlaceId(placeId));
    }

    /**
     * main function to retrieve place reviews data from google api
     * @param pageToken
     */
    public void retrieveMorePlacesAsync(String pageToken) {
        requestType = RequestType.MORE_PLACE_INFO;
        initObservable(RetrofitManager.getInstance(contextWeakRef)
                .listMorePlacesByPageToken(pageToken));
    }

    /**
     * main function to retrieve places data from google api
     * @param find
     */
    public Subscription retrieveCitiesAsync(String find) {
        requestType = RequestType.PLACE_CITES;
        return initObservable(RetrofitManager.getInstance(contextWeakRef)
                .listCitiesByFind(find));
    }

    /**
     *
     * @param observable
     */
    private Subscription initObservable(Observable<ArrayList<Object>> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Object>>() {

                    @Override
                    public void onCompleted() {
//                        Log.e("TAG", "completed observable");
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Log.e("BLA", "error" + e.getMessage());
                        if (listener.get() != null) {
                            listener.get().onPlaceApiError(requestType);
                        }
                    }

                    @Override
                    public void onNext(ArrayList<Object> list) {
                        if ((list == null || list.size() == 0) &&
                                listener.get() != null) {
                            listener.get().onPlaceApiEmptyResult();
                        } else if (listener.get() != null) {
                            requestType = updateRequestType(list.get(0));
                            listener.get().onPlaceApiSuccess(list, requestType);
                        }
                    }
                });
    }

    private RequestType updateRequestType(Object o) {
        if (o.getClass() == City.class) {
            return RequestType.PLACE_CITES;
        } else if (o.getClass() == CoffeePlace.class) {
                return requestType == RequestType.MORE_PLACE_INFO ? RequestType.MORE_PLACE_INFO : RequestType.PLACE_INFO;
        } else if (o.getClass() == Review.class) {
            return RequestType.PLACE_REVIEWS;
        }
        return null;
    }


    /**
     * handle
     */
    public interface OnHandlePlaceApiResult {
        void onPlaceApiSuccess(Object list, RequestType type);
        void onPlaceApiEmptyResult();
        void onPlaceApiError(RequestType type);
    }
}
