package com.application.material.takeacoffee.app.loaders;

import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;


/**
 * Created by davide on 17/10/14.
 */
public interface RetrofitServiceInterface {

    //get coffee machine
    @GET("/" + RestLoaderRetrofit.HTTPAction.CLASSES +
            RestLoaderRetrofit.HTTPAction.COFFEE_MACHINE)
    List<CoffeeMachine> listCoffeeMachine();

    @POST("/users/{user}/repos")
    Object listMoreReview(@Body User user, Callback<User> cb);

    @POST("/users/{user}/repos")
    Object listReview();

    @POST("/users/{user}/repos")
    Object mapReviewCount();

    @POST("/" + RestLoaderRetrofit.HTTPAction.CLASSES + "reviews")
    boolean addReviewByParams(@Body Review review, Callback<Review> callback);
    //volley http call

    //add user

    //remove user
//    getUserById
}
