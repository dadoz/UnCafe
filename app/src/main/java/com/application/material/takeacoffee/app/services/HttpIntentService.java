package com.application.material.takeacoffee.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.parsers.JSONParserToObject;
import com.application.material.takeacoffee.app.restServices.CustomErrorHandler;
import com.application.material.takeacoffee.app.singletons.BusSingleton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.application.material.takeacoffee.app.services.HttpIntentService.ParseAction.*;

//import it.ennova.myhd.BusSingleton;
//import it.ennova.myhd.Constants;
//import it.ennova.myhd.GlobalVarSingleton;
//import it.ennova.myhd.networking.response.BaseResponse;
//import it.ennova.myhd.networking.response.FirstSlot;
//import it.ennova.myhd.networking.response.ListSlots;
//import it.ennova.myhd.networking.response.ListTickets;
//import it.ennova.myhd.networking.response.Message;
//import it.ennova.myhd.networking.response.PostTicket;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class HttpIntentService extends IntentService {
    private static final String TAG = "HttpIntentService" ;

    private static final String REVIEW_REQUEST = "REVIEW_REQUEST";
    private static final String MORE_REVIEW_REQUEST = "MORE_REVIEW_REQUEST";
    private static final String ADD_REVIEW_BY_PARAMS_REQUEST = "ADD_REVIEW_BY_PARAMS_REQUEST";
    private static final String UPDATE_REVIEW_REQUEST = "UPDATE_REVIEW_REQUEST";
    private static final String DELETE_REVIEW_REQUEST = "DELETE_REVIEW_REQUEST";
    private static final String COFFEE_MACHINE_STATUS_REQUEST = "COFFEE_MACHINE_STATUS_REQUEST";
    private static final String USER_REQUEST = "USER_REQUEST";
    private static final String COFFEE_MACHINE_REQUEST = "COFFEE_MACHINE_REQUEST";
    private static final String UPDATE_USER_REQUEST = "UPDATE_USER_REQUEST";
    private static final String ADD_USER_BY_PARAMS_REQUEST = "ADD_USER_BY_PARAMS_REQUEST";
    private static final String DELETE_USER_REQUEST = "DELETE_USER_REQUEST";

    private static String EXTRA_REVIEW = "EXTRA_REVIEW";
    private static String EXTRA_USER = "EXTRA_REVIEW";
    private static String EXTRA_REVIEW_PARAMS = "EXTRA_REVIEW_PARAMS";
    private static String EXTRA_REVIEW_ID = "EXTRA_REVIEW_ID";
    private static String EXTRA_USER_ID = "EXTRA_USER_ID";
    private static String EXTRA_USER_PARAMS = "EXTRA_USER_PARAMS";
    private static String EXTRA_TIMESTAMP = "EXTRA_TIMESTAMP";
    private static String EXTRA_COFFEE_MACHINE_ID = "EXTRA_COFFEE_MACHINE_ID";;
    private static String CHECK_USER_REQUEST = "CHECK_USER_REQUEST";
    private static String EXTRA_USER_LIST = "EXTRA_USER_LIST";

//    RequestInterceptor requestInterceptor = new RequestInterceptor() {
//        @Override
//        public void intercept(RequestFacade request) {
//            request.addHeader("X-Parse-Application-Id", getResources().getString(R.string.parseApplicationId));
//            request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
//            request.addHeader("Content-Type", "application/json");
//
//        }
//    };
    private static boolean isConnected;

    public static void reviewListRequest(Context context,
                                         String coffeeMachineId, long timestamp) {
        isConnected = isConnected(context);
        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(REVIEW_REQUEST);
        intent.putExtra(EXTRA_COFFEE_MACHINE_ID, coffeeMachineId);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
//        intent.putExtra(EXTRA_COFFEE_MACHINE_ID, "PZrB82ZWVl");
//        intent.putExtra(EXTRA_TIMESTAMP, Double.parseDouble("1410696082045"));
        context.startService(intent);
    }

    public static void moreReviewListRequest(Context context,
                                         String coffeeMachineId, String fromReviewId) {
        isConnected = isConnected(context);
        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(MORE_REVIEW_REQUEST);
        intent.putExtra(EXTRA_COFFEE_MACHINE_ID, coffeeMachineId);
        intent.putExtra(EXTRA_REVIEW_ID, fromReviewId);
        context.startService(intent);
    }

    public static void userListRequest(Context context,
                                       ArrayList<String> userIdList) {
        isConnected = isConnected(context);
        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(USER_REQUEST);
        intent.putExtra(EXTRA_USER_LIST, userIdList);
        context.startService(intent);
    }

    public static void coffeeMachineRequest(Context context) {
        isConnected = isConnected(context);
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(COFFEE_MACHINE_REQUEST);
        context.startService(intent);
    }

    public static void updateReviewRequest(Context context,
                                      Review review) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(UPDATE_REVIEW_REQUEST);
        intent.putExtra(EXTRA_REVIEW, review);
        context.startService(intent);
    }

    public static void addReviewRequest(Context context,
                                      Review review) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(ADD_REVIEW_BY_PARAMS_REQUEST);
        intent.putExtra(EXTRA_REVIEW, review);
        context.startService(intent);
    }

    public static void deleteReviewRequest(Context context,
                                      String reviewId) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(DELETE_REVIEW_REQUEST);
        intent.putExtra(EXTRA_REVIEW_ID, reviewId);
        context.startService(intent);
    }

    public static void coffeeMachineStatusRequest(Context context,
                                                  String coffeeMachineId,
                                                  long timestamp) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(COFFEE_MACHINE_STATUS_REQUEST);
        intent.putExtra(EXTRA_COFFEE_MACHINE_ID, coffeeMachineId);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        context.startService(intent);
    }

    public static void updateUserRequest(Context context,
                                           User user) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(UPDATE_USER_REQUEST);
        intent.putExtra(EXTRA_USER, user);
        context.startService(intent);
    }

    public static void addUserRequest(Context context,
                                                   User userToBeRegistered) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(ADD_USER_BY_PARAMS_REQUEST);
        intent.putExtra(EXTRA_USER, userToBeRegistered);
        context.startService(intent);
    }

    public static void deleteUserRequest(Context context,
                                           String userId) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(DELETE_USER_REQUEST);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }

    public static void checkUserRequest(Context context,
                                        String userId) {
        isConnected = isConnected(context);

        Intent intent = new Intent(context, HttpIntentService.class);

        intent.setAction(CHECK_USER_REQUEST);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }



    private RetrofitServiceInterface service;

    public HttpIntentService() {
        super("HttpIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Type typeOfListOfCategory = new com.google.gson.reflect.TypeToken<ArrayList<CoffeeMachine>>(){}.getType();
//        Type typeOfListOfCategoryUser = new com.google.gson.reflect.TypeToken<ArrayList<User>>(){}.getType();
//        Type typeOfListOfCategoryReview = new com.google.gson.reflect.TypeToken<ReviewDataContainer>(){}.getType();
//
//        Gson gson = new GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .registerTypeAdapter(typeOfListOfCategory, new CustomDeserializer())
//                .registerTypeAdapter(typeOfListOfCategoryUser, new CustomDeserializerUser())
//                .registerTypeAdapter(typeOfListOfCategoryReview, new CustomDeserializerReview())
//                .create();
//
//        RestAdapter restAdapter =  new RestAdapter.Builder()
//                .setEndpoint("https://api.parse.com") // set baseUrl
//                .setRequestInterceptor(requestInterceptor)
//                .setConverter(new GsonConverter(gson))
//                .setErrorHandler(new CustomErrorHandler())
//                .build();
//
//        BusSingleton.getInstance().register(this);
//
//        service = restAdapter.create(RetrofitServiceInterface.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Log.e(TAG, "onHandleIntent called" + intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();

            if (REVIEW_REQUEST.equals(action)) {
                try {
                    if(! isConnected) {
                        //TODO MOCKUP
                        ReviewDataContainer reviewDataContainer = JSONParserToObject
                                .getReviewListParser(JSONParserToObject.
                                        getMockupData(this.getApplicationContext().getAssets(),
                                                "reviews.json"));
                        BusSingleton.getInstance().post(reviewDataContainer);
                        return;
                    }

                    String coffeeMachineId = (String) intent.getExtras().get(EXTRA_COFFEE_MACHINE_ID);
                    Long timestamp = (Long) intent.getExtras().get(EXTRA_TIMESTAMP);
                    Review.Params params = new Review.Params(coffeeMachineId, timestamp);
//                    BusSingleton.getInstance().post(service.listReview(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (MORE_REVIEW_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        //TODO MOCKUP
                        ReviewDataContainer reviewDataContainer = JSONParserToObject
                                .getReviewListParser(JSONParserToObject.
                                        getMockupData(this.getApplicationContext().getAssets(),
                                                "prev_reviews.json"));
                        BusSingleton.getInstance().post(reviewDataContainer);
                        return;
                    }

                    String coffeeMachineId = intent.getExtras().getString(EXTRA_COFFEE_MACHINE_ID);
                    String fromReviewId = intent.getExtras().getString(EXTRA_REVIEW_ID);
                    Review.MoreReviewsParams params = new Review.MoreReviewsParams(coffeeMachineId, fromReviewId);
//                    BusSingleton.getInstance().post(service.listMoreReview(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (USER_REQUEST.equals(action)) {
                try {
                    if(! isConnected) {
                        ArrayList<User> userList = JSONParserToObject
                                .getUserListParser(JSONParserToObject.
                                        getMockupData(this.getApplicationContext().getAssets(),
                                                "user.json"));
                        BusSingleton.getInstance().post(userList);
                        return;
                    }

//                    String [] array = {"4nmvMJNk1R", "K8bwZOSmNo", "8e2XwXZUKL"};
//                    User.Params params = new User.Params(new ArrayList(Arrays.asList(array)));
                    ArrayList<String> userList = (ArrayList<String>) intent.getExtras().get(EXTRA_USER_LIST);
                    User.Params params = new User.Params(userList);
//                    BusSingleton.getInstance().post(service.listUserByIdList(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (COFFEE_MACHINE_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        ArrayList<CoffeeMachine> coffeeMachineList = JSONParserToObject
                                .coffeeMachineParser(JSONParserToObject.
                                        getMockupData(this.getApplicationContext().getAssets(),
                                                "coffee_machines.json"));
                        BusSingleton.getInstance().post(coffeeMachineList);
                        return;
                    }

//                    BusSingleton.getInstance().post(service.listCoffeeMachine());
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (UPDATE_REVIEW_REQUEST.equals(action)) {
                try {
                    if(! isConnected) {
                        Review review = null;
//                        BusSingleton.getInstance().post(service.updateReview("LerbzRfN95", review));
                        return;
                    }

                    Review review = (Review) intent.getExtras().get(EXTRA_REVIEW);
//                    BusSingleton.getInstance().post(service.updateReview(review.getId(), review));
                    //{"updatedAt":"2014-12-30T12:43:00.235Z"}
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (ADD_REVIEW_BY_PARAMS_REQUEST.equals(action)){
                try {
                    if(! isConnected) {
                        Review review = (Review) intent.getExtras().get(EXTRA_REVIEW);
                        BusSingleton.getInstance().post(review);
                        return;
                    }

                    Review review = (Review) intent.getExtras().get(EXTRA_REVIEW);
//                    BusSingleton.getInstance().post(service.addReviewByParams(review));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (DELETE_REVIEW_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        String reviewId = "LerbzRfN95";
//                        BusSingleton.getInstance().post(service.deleteReview(reviewId));
                        return;
                    }

                    String reviewId = intent.getExtras().getString(EXTRA_REVIEW_ID);
//                    BusSingleton.getInstance().post(new Review.DeletedResponse(service.deleteReview(reviewId)));
                    //{}
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (COFFEE_MACHINE_STATUS_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        CoffeeMachineStatus coffeeMachineStatus = JSONParserToObject
                                .coffeeMachineStatusParser(JSONParserToObject.
                                        getMockupData(this.getApplicationContext().getAssets(),
                                                "review_status.json"));
                        BusSingleton.getInstance().post(coffeeMachineStatus);
                        return;
                    }

                    String coffeeMachineId = intent.getExtras().getString(EXTRA_COFFEE_MACHINE_ID);
                    long timestamp = intent.getExtras().getLong(EXTRA_TIMESTAMP);
                    CoffeeMachineStatus.Params params = new CoffeeMachineStatus.Params(coffeeMachineId, timestamp);
//                    BusSingleton.getInstance().post(service.getCoffeeMachineStatus(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (UPDATE_USER_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        User user = (User) intent.getExtras().get(EXTRA_USER);
//                        BusSingleton.getInstance().post(service.updateUser("LerbzRfN95", null));
                        return;
                    }

                    User user = (User) intent.getExtras().get(EXTRA_USER);
//                    BusSingleton.getInstance().post(service.updateUser(user.getId(), user));
//                  {"updatedAt":"2014-12-30T12:50:55.468Z"}
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (ADD_USER_BY_PARAMS_REQUEST.equals(action)){
                try {
                    if(! isConnected) {
                        User user = (User) intent.getExtras().get(EXTRA_USER);
//                        new User("4nmvMJNk1R", null, username);
                        user.setId("4nmvMJNk1R");
                        BusSingleton.getInstance().post(user);
                        return;
                    }

                    User userLocal = (User) intent.getExtras().get(EXTRA_USER);
//                    BusSingleton.getInstance().post(user != null ? user.getId() : User.EMPTY_ID);
//                    {"createdAt":"2014-12-30T12:49:26.916Z","objectId":"f140rB6aRo"}
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (DELETE_USER_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        String userId = "LerbzRfN95";
//                        BusSingleton.getInstance().post(service.deleteUser(userId));
                        return;
                    }

                    String userId = intent.getExtras().getString(EXTRA_USER_ID);
//                    BusSingleton.getInstance().post(new User.DeletedResponse(service.deleteUser(userId)));
                    //{}
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
            }

            if (CHECK_USER_REQUEST.equals(action)) {
                try{
                    if(! isConnected) {
                        String userId = intent.getExtras().getString(EXTRA_USER_ID);
                        BusSingleton.getInstance().post(new User(userId, null, null, "Not connected User"));
                        return;
                    }

                    String userId = intent.getExtras().getString(EXTRA_USER_ID);
//                    BusSingleton.getInstance().post(service.checkUser(userId));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
            }

        }
    }


    public static interface RetrofitServiceInterface {
//
//        //get coffee machine
//        @GET("/" + CLASSES + COFFEE_MACHINE)
//        ArrayList<CoffeeMachine> listCoffeeMachine();
//
//        @POST("/" + FUNCTIONS + MORE_REVIEW)
//        ReviewDataContainer listMoreReview(@Body Review.MoreReviewsParams params);
//
//        @POST("/" + FUNCTIONS + WEEK_REVIEWS)
//        ReviewDataContainer listReview(@Body Review.Params reviewParams);
//
//        @POST("/" + FUNCTIONS + USER_BY_ID_LIST)
//        ArrayList<User> listUserByIdList(@Body User.Params userParams);
//
//        /**** REVIEW ACTIONS ****/
//        @PUT("/" + CLASSES + REVIEW + "/" + "{reviewId}")
//        Review updateReview(@Path("reviewId") String reviewId, @Body Review review);
//
//        @POST("/" + CLASSES + REVIEW)
//        Review addReviewByParams(@Body Review review);
//
//        @DELETE("/" + CLASSES + REVIEW + "/" + "{reviewId}")
//        Object deleteReview(@Path("reviewId") String reviewId);
//
//        /**** USER ACTIONS ****/
//        @PUT("/" + CLASSES + USER + "/" + "{userId}")
//        Object updateUser(@Path("userId") String userId, @Body User user);
//
//        @POST("/" + CLASSES + USER)
//        User addUserByParams(@Body User user);
//
//        @DELETE("/" + CLASSES + USER + "/" + "{userId}")
//        Object deleteUser(@Path("userId") String userId);
//
//        @POST("/" + FUNCTIONS + GET_COFFEE_MACHINE_STATUS)
//        CoffeeMachineStatus getCoffeeMachineStatus(@Body CoffeeMachineStatus.Params params);
//
//        @GET("/" + CLASSES + USER + "/" + "{userId}")
//        User checkUser(@Path("userId") String userId);

    }



/*    private static interface APIServiceInterface {
        @FormUrlEncoded
        @POST("/createTicket")
        PostTicket createTicket(@Field("fascia") String fascia
                ,@Field("giorno") String giorno
                ,@Field("timestamp") String timeStamp
                ,@Field("modalita") String modalita
                ,@Field("subject") String subject);

        @GET("/getFirstSlotAvaible")
        FirstSlot getFirstSlot(@Query("req") String subject);

        @GET("/getListSlotsAvaible")
        ListSlots getAvailableSlots(@Query("req") String subject);

        @FormUrlEncoded
        @POST("/rescheduleTicket")
        PostTicket rescheduleTicket(@Field("ticketId") String ticketID
                , @Field("timestamp") String timestamp);

        @FormUrlEncoded
        @POST("/deleteTicket")
        PostTicket revokeTicket(@Field("ticketId") String ticketID);

        @FormUrlEncoded
        @POST("/createInAppAndroid")
        BaseResponse createInAppAndroid(@Field("SendApp[tel_number]") String tel_number
                , @Field("myhd_version") String myhd_version);

        @GET("/getTickets")
        ListTickets getListTicket();

        @GET("/getMessages")
        Message getListMessage();


    }*/
//    public static class CustomDeserializer implements JsonDeserializer<ArrayList<CoffeeMachine>> {
//
//        @Override
//        public ArrayList<CoffeeMachine> deserialize(JsonElement jsonElement, Type type,
//                                               JsonDeserializationContext jsonDeserializationContext)
//                throws JsonParseException {
//            Log.e(TAG, type.toString());
//            ArrayList<CoffeeMachine> coffeeMachineList = new ArrayList<CoffeeMachine>();
//            JsonArray jsonArray = jsonElement.getAsJsonObject().get("results").getAsJsonArray();
//            for(int i = 0; i < jsonArray.size(); i++) {
//                coffeeMachineList.add(jsonDeserializationContext.<CoffeeMachine>deserialize(jsonArray.get(i), CoffeeMachine.class));
//            }
//            return coffeeMachineList;
//
//        }
//    }
//
//    public static class CustomDeserializerReview implements JsonDeserializer<ReviewDataContainer> {
//
//        @Override
//        public ReviewDataContainer deserialize(JsonElement jsonElement, Type type,
//                                               JsonDeserializationContext jsonDeserializationContext)
//                throws JsonParseException {
////            Log.e(TAG, jsonElement.toString() + " - " + type.toString());
//            Log.e(TAG, type.toString());
////            Class<? extends Type> classType = type.getClass();
//            ArrayList<Review> reviewList = new ArrayList<Review>();
//            JsonArray jsonArray = jsonElement.getAsJsonObject().get("result").getAsJsonObject().get("data").getAsJsonArray();
//            for(int i = 0; i < jsonArray.size(); i++) {
//                Review review = jsonDeserializationContext.<Review>deserialize(jsonArray.get(i), Review.class);
//                review.setId(((JsonObject) jsonArray.get(i)).get("objectId").getAsString());
//                reviewList.add(review);
//            }
//            boolean hasMoreReviews = jsonElement.getAsJsonObject().get("result").getAsJsonObject().get("hasMoreReviews").getAsBoolean();
//
//            return  new ReviewDataContainer(hasMoreReviews, reviewList);
//
////            return reviewList;
//
//        }
//    }
//
//    public static class CustomDeserializerUser implements JsonDeserializer<ArrayList<User>> {
//
//        @Override
//        public ArrayList<User> deserialize(JsonElement jsonElement, Type type,
//                                      JsonDeserializationContext jsonDeserializationContext)
//                throws JsonParseException {
//            Log.e(TAG, type.toString());
//            ArrayList<User> userList = new ArrayList<User>();
//            JsonArray jsonArray = jsonElement.getAsJsonObject().get("result").getAsJsonArray();
//            for(int i = 0; i < jsonArray.size(); i++) {
//                User user = jsonDeserializationContext.<User>deserialize(jsonArray.get(i), User.class);
//                user.setId(((JsonObject) jsonArray.get(i)).get("objectId").getAsString());
//                userList.add(user);
//            }
//            return userList;
//
//        }
//    }

    public static class ParseAction {
        public static final String CLASSES = "1/classes/";
        public static final String FUNCTIONS = "1/functions/";

        //action model
        public static final String COFFEE_MACHINE = "coffee_machines";
        public static final String REVIEW = "reviews";
        public static final String USER = "users";
        public static final String WEEK_REVIEWS = "getWeekReviews";
        public static final String USER_BY_ID_LIST= "getUsersByUserIdList";
        public static final String MORE_REVIEW = "getMoreReviews";
        public static final String GET_COFFEE_MACHINE_STATUS = "getCoffeeMachineStatus";
//        public static final String REVIEW_COUNTER_TIMESTAMP = "countOnReviewsWithTimestamp";
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

}