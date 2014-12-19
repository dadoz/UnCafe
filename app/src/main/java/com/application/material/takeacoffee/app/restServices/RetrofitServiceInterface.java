package com.application.material.takeacoffee.app.restServices;

import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.CoffeeMachineStatus;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
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
    List<Review> listMoreReview(@Body User user);

    @POST("/" + FUNCTIONS + WEEK_REVIEWS)
    List<Review> listReview(@Body Review.Params reviewParams);

//    @GET("/" + FUNCTIONS + REVIEW_COUNTER_TIMESTAMP)
//    Response mapReviewCount();

    @POST("/" + CLASSES + REVIEW)
    boolean addReviewByParams(@Body Review review);

//    @GET("/" + FUNCTIONS + USER_BY_ID_LIST + "{userParams}")
//    void listUserByIdList(@Path("userParams") RetrofitLoader.UserParams userParams);

    @POST("/" + FUNCTIONS + USER_BY_ID_LIST)
    void listUserByIdList(@Body RetrofitLoader.UserParams userParams);

    @PUT("/" + FUNCTIONS + MORE_REVIEW)
    ReviewStatus saveEditReview(@Body Review review);

    @DELETE("/" + FUNCTIONS + MORE_REVIEW)
    ReviewStatus deleteReview(@Body String reviewObjectId);

    @POST("/" + FUNCTIONS + GET_COFFEE_MACHINE_STATUS + "{coffeeMachineId}")
    ReviewStatus getCoffeeMachineStatus(CoffeeMachineStatus.Params coffeeMachineId);

}
