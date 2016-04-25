package com.application.material.takeacoffee.app.singletons;

import android.util.Log;

import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Notification;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by davide on 24/12/14.
 */
public class RetrofitManager {
    private static final String API_KEY = "AIzaSyAd3e75KuRKgMLj34I0AT-MEUKGmhErLls";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static RetrofitManager instance = new RetrofitManager();
    private final PlacesAPiWebService service;

    private RetrofitManager() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new HttpLoggingInterceptor())
//                .build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(new TypeToken<ArrayList<CoffeePlace>>() {
                }.getType(),
                new PlaceDeserializer()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(httpClient)
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
    public Observable<List<Review>> listReviewByPlaceId(String placeId) {
        try {
            return service.listReviewByPlaceId(placeId);//.execute().body();
        } catch (Exception e) {
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
    public Observable<ArrayList<CoffeePlace>> listPlacesByLocationAndType(String location, String radius, String type) {
        try {
            return service.listPlacesByLocationAndType(location, radius, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * place deserializer
     */
    public class PlaceDeserializer implements JsonDeserializer<List<CoffeePlace>> {

        @Override
        public List<CoffeePlace>  deserialize(final JsonElement json, final Type typeOfT,
                                                final JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray resultArray = json.getAsJsonObject().get("results").getAsJsonArray();
            return new Gson().fromJson(resultArray,
                    new TypeToken<ArrayList<CoffeePlace>>(){}.getType());
        }
    }

    /**
     *
     */
    public interface PlacesAPiWebService {
        @GET("place/details/json?placeid={placeid}&key=" + API_KEY)
        Observable<List<Review>> listReviewByPlaceId(@Path("placeid") String placeIs);

        @GET("place/nearbysearch/json?key=" + API_KEY)
        Observable<ArrayList<CoffeePlace>> listPlacesByLocationAndType(@Query("location") String location,
                                                                  @Query("radius") String radius,
                                                                  @Query("type") String type);
    }
}
