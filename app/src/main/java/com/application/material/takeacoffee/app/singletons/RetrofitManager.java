package com.application.material.takeacoffee.app.singletons;

import com.application.material.takeacoffee.app.models.Review;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

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
            return service.listReviewByPlaceId(placeId).execute().body();
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
    }
}
