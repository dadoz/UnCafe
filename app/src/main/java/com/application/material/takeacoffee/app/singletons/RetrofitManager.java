package com.application.material.takeacoffee.app.singletons;

import android.util.Log;

import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.android.gms.location.places.Place;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by davide on 24/12/14.
 */
public class RetrofitManager {
    private static final String API_KEY = "AIzaSyAd3e75KuRKgMLj34I0AT-MEUKGmhErLls";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static RetrofitManager instance = new RetrofitManager();
    private final PlacesAPiWebService service;

    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        service = retrofit.create(PlacesAPiWebService.class);
    }

    /**
     *
     * @return
     */
    public static RetrofitManager getInstance() {
        return instance;
    }

    /**
     *
     * @param placeId
     */
    public List<Review> listReviewByPlaceId(String placeId) {
        try {
            Log.e("TAG","asdfasd");
            List<Review> temp = service.listReviewByPlaceId(placeId).execute().body();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param location
     * @param radius
     * @param type
     * @return
     */
    public List<CoffeePlace> listPlacesByLocationAndType(String location, String radius, String type) {
        try {
            Log.e("TAG","asdfasd");
            List<CoffeePlace> temp = service.listPlacesByLocationAndType(location, radius, type).execute().body();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    public interface PlacesAPiWebService {
        @GET("place/details/json?placeid={placeid}&key=" + API_KEY)
        Call<List<Review>> listReviewByPlaceId(@Path("placeid") String placeIs);

        @GET("place/details/json?location={location}&radius={radius}&type={type}&key=" + API_KEY)
        Call<List<CoffeePlace>>  listPlacesByLocationAndType(@Path("location") String location,
                                                             @Path("radius") String radius,
                                                             @Path("type") String type);
    }
}
