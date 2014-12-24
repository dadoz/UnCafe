//package com.application.material.takeacoffee.app.restServices;
//
//import com.application.material.takeacoffee.app.restServices.loaders.RetrofitLoader;
//import com.application.material.takeacoffee.app.models.*;
//import retrofit.http.*;
//
//import java.util.List;
//
//import static com.application.material.takeacoffee.app.restServices.loaders.ParseAction.*;
//
//
///**
// * Created by davide on 17/10/14.
// */
//public interface RetrofitServiceInterface {
//
//    //get coffee machine
//    @GET("/" + CLASSES + COFFEE_MACHINE)
//    List<CoffeeMachine> listCoffeeMachine();
//
//    @POST("/" + FUNCTIONS + MORE_REVIEW)
//    List<Review> listMoreReview(@Body Review.MoreReviewsParams user);
//
//    @POST("/" + FUNCTIONS + WEEK_REVIEWS)
//    ReviewDataContainer listReview(@Body Review.Params reviewParams);
//
////    @GET("/" + FUNCTIONS + REVIEW_COUNTER_TIMESTAMP)
////    Response mapReviewCount();
//
////    @GET("/" + FUNCTIONS + USER_BY_ID_LIST + "{userParams}")
////    void listUserByIdList(@Path("userParams") RetrofitLoader.UserParams userParams);
//
//    @POST("/" + FUNCTIONS + USER_BY_ID_LIST)
//    List<User> listUserByIdList(@Body User.Params userParams);
//
//    /**** REVIEW ACTIONS ****/
//    @PUT("/" + CLASSES + REVIEW + "/" + "{reviewId}")
//    void updateReview(@Path("reviewId") String reviewId, @Body Review review);
//
//    @POST("/" + CLASSES + REVIEW)
//    void addReviewByParams(@Body Review review);
//
//    @DELETE("/" + CLASSES + REVIEW + "/" + "{reviewId}")
//    void deleteReview(@Path("reviewId") String reviewId);
//
//    /**** USER ACTIONS ****/
//    @PUT("/" + CLASSES + USER + "/" + "{userId}")
//    void updateUser(@Path("userId") String userId, @Body User user);
//
//    @POST("/" + CLASSES + USER)
//    void addUserByParams(@Body User user);
//
//    @DELETE("/" + CLASSES + USER + "/" + "{userId}")
//    void deleteUser(@Path("userId") String userId);
//
//    @POST("/" + FUNCTIONS + GET_COFFEE_MACHINE_STATUS)
//    CoffeeMachineStatus getCoffeeMachineStatus(@Body CoffeeMachineStatus.Params coffeeMachineId);
//}
