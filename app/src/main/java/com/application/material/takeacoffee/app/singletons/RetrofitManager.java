package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.util.Log;

import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.cacheInterceptor.CacheControlApplicationInterceptor;
import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.CoffeePlace.PageToken;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by davide on 24/12/14.
 */
public class RetrofitManager {
    private static final String API_KEY = "AIzaSyAd3e75KuRKgMLj34I0AT-MEUKGmhErLls";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final int MAX_WIDTH_PIC = 600;
    private static RetrofitManager instance;
    private final PlacesAPiWebService service;
    private static WeakReference<Context> contextWeakRef;
    private String CITIES_TYPE = "(cities)";

    private RetrofitManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(getGsonConverter())
                .build();

        service = retrofit.create(PlacesAPiWebService.class);
    }

    /**
     *
     * @return
     */
    public static RetrofitManager getInstance(WeakReference<Context> ctx) {
        contextWeakRef = ctx;
        return instance == null ? instance = new RetrofitManager() : instance;
    }

    /**
     * TODO maybe move out
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
    public Observable<ArrayList<Object>> listReviewsByPlaceId(String placeId) {
        try {
            return service.listReviewByPlaceId(placeId).map(new Func1<ArrayList<Review>, ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(ArrayList<Review> reviews) {
                    return new ArrayList<Object>(reviews);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param pageToken
     */
    public Observable<ArrayList<Object>> listMorePlacesByPageToken(String pageToken) {
        try {
            return service.listMorePlacesByPageToken(pageToken).map(new Func1<ArrayList<CoffeePlace>, ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(ArrayList<CoffeePlace> list) {
                    return new ArrayList<Object>(list);
                }
            });
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
    public Observable<ArrayList<Object>> listPlacesByLocationAndType(String location, String rankby, String type) {
        try {
            return service.listPlacesByLocationAndType(location, rankby, type).map(new Func1<ArrayList<CoffeePlace>, ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(ArrayList<CoffeePlace> list) {
                    setTimestampRequest();
                    return new ArrayList<Object>(list);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    private void setTimestampRequest() {
        SharedPrefManager.getInstance(contextWeakRef).setValueByKey(SharedPrefManager.TIMESTAMP_REQUEST_KEY,
                Long.toString(new Date().getTime()));
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
                .registerTypeAdapter(new TypeToken<ArrayList<City>>() {}.getType(),
                    new CitiesDeserializer())
                        .create());
    }

    /**
     *
     * @return
     */
    public OkHttpClient getClient() {
        //TODO leak
        Cache cache = ((CoffeePlacesApplication) contextWeakRef.get()
                .getApplicationContext()).getCache();
        return new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new CacheControlApplicationInterceptor(contextWeakRef)) //app
//                .addNetworkInterceptor(new CacheControlNetworkInterceptor(contextWeakRef)) //network
                .build();
    }

    /**
     *
     * @param find
     * @return
     */
    public Observable<ArrayList<Object>> listCitiesByFind(String find) {
        return service.listCitiesByFind(find, CITIES_TYPE).map(new Func1<ArrayList<City>, ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call(ArrayList<City> cities) {
                return new ArrayList<Object>(cities);
            }
        });
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
            ArrayList<CoffeePlace> list = new Gson().fromJson(resultArray,
                    new TypeToken<ArrayList<CoffeePlace>>() {
                    }.getType());

            //TODO refactor
            PageToken pageToken = PageToken.getInstance(json.getAsJsonObject()
                    .get("next_page_token") == null ? null :
                        json.getAsJsonObject().get("next_page_token").getAsString());
            for (CoffeePlace coffeePlace: list) {
                coffeePlace.setPageToken(pageToken);
            }
            return list;
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
            JsonElement reviewsObj = json.getAsJsonObject().get("result").getAsJsonObject()
                    .get("reviews");
            return new Gson().fromJson(reviewsObj == null ? null : reviewsObj.getAsJsonArray(),
                    new TypeToken<ArrayList<Review>>(){}.getType());
        }
    }
    /**
     * review deserializer
     */
    public class CitiesDeserializer implements JsonDeserializer<ArrayList<City>> {

        @Override
        public ArrayList<City>  deserialize(final JsonElement json, final Type typeOfT,
                                                final JsonDeserializationContext context)
                throws JsonParseException {
            Log.e("TAG", json.toString());
            JsonArray citiesArray = json.getAsJsonObject().get("predictions").getAsJsonArray();
            return new Gson().fromJson(citiesArray,
                    new TypeToken<ArrayList<City>>(){}.getType());
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
        @GET("place/nearbysearch/json?key=" + API_KEY)
        Observable<ArrayList<CoffeePlace>> listMorePlacesByPageToken(@Query("pagetoken") String pagetoken);

        @GET("place/autocomplete/json?key=" + API_KEY)
        Observable<ArrayList<City>> listCitiesByFind(@Query("input") String input,
                                                                  @Query("type") String type);
    }
}
