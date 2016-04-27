package com.application.material.takeacoffee.app.singletons;

import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by davide on 24/12/14.
 */
public class RetrofitManager {
    private static final String API_KEY = "AIzaSyAd3e75KuRKgMLj34I0AT-MEUKGmhErLls";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final int MAX_WIDTH_PIC = 600;
    private static RetrofitManager instance = new RetrofitManager();
    private final PlacesAPiWebService service;

    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(getGsonConverter())
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
     * @param reference
     */
    public String getPlacePhotoUrlByReference(String reference) {
        return BASE_URL + "place/photo?maxwidth=" + MAX_WIDTH_PIC +
            "&key=" + API_KEY +
            "&photoreference=" + reference;
    }
    /**
     *
     * @param placeId
     */
    public Observable<ArrayList<Review>> listReviewsByPlaceId(String placeId) {
        try {
            return service.listReviewByPlaceId(placeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param location
     * @param rankby
     * @param type
     * @return
     */
    public Observable<ArrayList<CoffeePlace>> listPlacesByLocationAndType(String location, String rankby, String type) {
        try {
            return service.listPlacesByLocationAndType(location, rankby, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Converter.Factory getGsonConverter() {
        return GsonConverterFactory.create(new GsonBuilder()
                .registerTypeAdapter(new TypeToken<ArrayList<CoffeePlace>>() {}.getType(),
                    new PlaceDeserializer())
                .registerTypeAdapter(new TypeToken<ArrayList<Review>>() {}.getType(),
                    new ReviewsDeserializer())
                        .create());
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
     * review deserializer
     */
    public class ReviewsDeserializer implements JsonDeserializer<ArrayList<Review>> {

        @Override
        public ArrayList<Review>  deserialize(final JsonElement json, final Type typeOfT,
                                                final JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray resultArray = json.getAsJsonObject().get("result").getAsJsonObject()
                    .get("reviews").getAsJsonArray();
            return new Gson().fromJson(resultArray,
                    new TypeToken<ArrayList<Review>>(){}.getType());
        }
    }

    /**
     *
     */
    public interface PlacesAPiWebService {
        @GET("place/details/json?key=" + API_KEY)
        Observable<ArrayList<Review>> listReviewByPlaceId(@Query("placeid") String placeid);

        @GET("place/nearbysearch/json?key=" + API_KEY)
        Observable<ArrayList<CoffeePlace>> listPlacesByLocationAndType(@Query("location") String location,
                                                                  @Query("rankby") String rankby,
                                                                  @Query("type") String type);
    }
}
