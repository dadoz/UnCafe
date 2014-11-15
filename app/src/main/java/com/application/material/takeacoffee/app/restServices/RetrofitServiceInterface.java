package com.application.material.takeacoffee.app.restServices;

import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.ArrayList;
import java.util.List;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.ParseAction.*;


/**
 * Created by davide on 17/10/14.
 */
public interface RetrofitServiceInterface {

    //get coffee machine
    @GET("/" + CLASSES + COFFEE_MACHINE)
    List<CoffeeMachine> listCoffeeMachine();

    @POST("/" + FUNCTIONS + MORE_REVIEW)
    Response listMoreReview(@Body User user);

    @GET("/" + FUNCTIONS + REVIEW_BY_TIMESTAMP_LIMIT)
    Response listReview();

    @GET("/" + FUNCTIONS + REVIEW_COUNTER_TIMESTAMP)
    Response mapReviewCount();

    @POST("/" + CLASSES + REVIEW)
    boolean addReviewByParams(@Body Review review);

//    @GET("/" + FUNCTIONS + USER_BY_ID_LIST + "{userParams}")
//    void listUserByIdList(@Path("userParams") RetrofitLoader.UserParams userParams);

    @POST("/" + FUNCTIONS + USER_BY_ID_LIST)
    void listUserByIdList(@Body RetrofitLoader.UserParams userParams);
}
