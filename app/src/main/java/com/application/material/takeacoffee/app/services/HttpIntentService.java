package com.application.material.takeacoffee.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.restServices.CustomErrorHandler;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.google.gson.*;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
//    private static final String ACTION_CREATE_TICKET = "it.ennova.myhd.networking.action.CREATE_TICKET";
//    private static final String ACTION_GET_FIRST_SLOT = "it.ennova.myhd.networking.action.GET_FIRST_SLOT";
//    private static final String ACTION_GET_AVAILABLE_SLOTS = "it.ennova.myhd.networking.action.GET_AVAILABLE_SLOTS";
//    private static final String ACTION_REVOKE_TICKET = "it.ennova.myhd.networking.action.REVOKE_TICKET";
//    private static final String ACTION_RESCHEDULE_TICKET = "it.ennova.myhd.networking.action.RESCHEDULE_TICKET";
//    private static final String ACTION_CREATE_IN_APP = "it.ennova.myhd.networking.action.CREATE_IN_APP";
//    private static final String ACTION_GET_LIST_TICKET = "it.ennova.myhd.netowrking.action.GET_LIST_TICKET";
//    private static final String ACTION_GET_MESSAGES = "it.ennova.myhd.networking.action.GET_MESSAGES";


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

//    private static final String EXTRA_NUMBER = "it.ennova.myhd.networking.extra.NUMBER";
//    private static final String EXTRA_TICKET_ID = "it.ennova.myhd.networking.extra.TICKETID";
//    private static final String EXTRA_TIMESTAMP = "it.ennova.myhd.networking.extra.TIMESTAMP";
//    private static final String EXTRA_GIORNO = "it.ennova.myhd.networking.extra.GIORNO";
//    private static final String EXTRA_SUBJECT = "it.ennova.myhd.networking.extra.SUBJCT";
//    private static final String EXTRA_FASCIA = "it.ennova.myhd.networking.extra.FASCIA";

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-Parse-Application-Id", "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY");
            request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
            request.addHeader("Content-Type", "application/json");

        }
    };
/*
    public static void startActionCreateTicket(Context context
            , String fascia
            , String timeStamp
            , String giorno, String subject){
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_CREATE_TICKET);
        intent.putExtra(EXTRA_FASCIA, fascia);
        intent.putExtra(EXTRA_TIMESTAMP,timeStamp);
        intent.putExtra(EXTRA_GIORNO,giorno);
        intent.putExtra(EXTRA_SUBJECT,subject);
        context.startService(intent);
    }

    public static void startActionGetFirstSlot(Context context, String subject) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_GET_FIRST_SLOT);
        intent.putExtra(EXTRA_SUBJECT,subject);
        context.startService(intent);
    }

    public static void startActionGetAvailableSlots(Context context, String data) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_GET_AVAILABLE_SLOTS);
        intent.putExtra(EXTRA_SUBJECT,data);
        context.startService(intent);
    }

    public static void startActionRevokeTicket(Context context, String ticketID) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_REVOKE_TICKET);
        intent.putExtra(EXTRA_TICKET_ID, ticketID);
        context.startService(intent);
    }

    public static void startActionRescheduleTicket(Context context, String ticketID, String timesStamp) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_RESCHEDULE_TICKET);
        intent.putExtra(EXTRA_TICKET_ID,ticketID);
        intent.putExtra(EXTRA_TIMESTAMP,timesStamp);
        context.startService(intent);
    }

    public static void startActionCreateInApp(Context context , String number) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_CREATE_IN_APP);
        intent.putExtra(EXTRA_NUMBER, number);
        context.startService(intent);
    }

    public static void startActionGetListTicket(Context context) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_GET_LIST_TICKET);
        context.startService(intent);
    }

    public static void startActionGetListMessage(Context context) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(ACTION_GET_MESSAGES);
        context.startService(intent);
    }
*/
    public static void reviewListInitParamsRequest(Context context,
                                                   String coffeeMachineId, long timestamp) {
        Intent intent = new Intent(context, HttpIntentService.class);
        Review.Params params = new Review.Params("PZrB82ZWVl",
                Double.parseDouble("1410696082045"));

        intent.setAction(REVIEW_REQUEST);
        intent.putExtra(EXTRA_REVIEW, params);
        context.startService(intent);
    }

    public static void coffeeMachineInitParamsRequest(Context context) {
        Intent intent = new Intent(context, HttpIntentService.class);
        intent.setAction(COFFEE_MACHINE_REQUEST);
        context.startService(intent);
    }



    private RetrofitServiceInterface service;

    public HttpIntentService() {
        super("HttpIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Type typeOfListOfCategory = new com.google.gson.reflect.TypeToken<List<CoffeeMachine>>(){}.getType();
        Type typeOfListOfCategoryUser = new com.google.gson.reflect.TypeToken<List<User>>(){}.getType();
        Type typeOfListOfCategoryReview = new com.google.gson.reflect.TypeToken<List<Review>>(){}.getType();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(typeOfListOfCategory, new CustomDeserializer())
                .registerTypeAdapter(typeOfListOfCategoryUser, new CustomDeserializerUser())
                .registerTypeAdapter(typeOfListOfCategoryReview, new CustomDeserializerReview())
                .create();

        RestAdapter restAdapter =  new RestAdapter.Builder()
                .setEndpoint("https://api.parse.com") // set baseUrl
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new CustomErrorHandler())
                .build();

        BusSingleton.getInstance().register(this);

        service = restAdapter.create(RetrofitServiceInterface.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (REVIEW_REQUEST.equals(action)) {
                try {
                    Review.Params params = (Review.Params) intent.getExtras().get(EXTRA_REVIEW);
                    BusSingleton.getInstance().post(
                            service.listReview(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (MORE_REVIEW_REQUEST.equals(action)) {
                try{
                    Review.MoreReviewsParams params = new Review.MoreReviewsParams("PZrB82ZWVl", "qaeWjprTDF");
                    BusSingleton.getInstance().post(service.listMoreReview(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (USER_REQUEST.equals(action)) {
                try {
                    String [] array = {"4nmvMJNk1R", "K8bwZOSmNo", "8e2XwXZUKL"};
                    User.Params params = new User.Params(new ArrayList(Arrays.asList(array)));
                    BusSingleton.getInstance().post(service.listUserByIdList(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (COFFEE_MACHINE_REQUEST.equals(action)) {
                try{
                    BusSingleton.getInstance().post(service.listCoffeeMachine());
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (UPDATE_REVIEW_REQUEST.equals(action)) {
                try{
                    Review review = null;
                    BusSingleton.getInstance().post(service.updateReview("LerbzRfN95", review));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (ADD_REVIEW_BY_PARAMS_REQUEST.equals(action)){
                try {
//                    final String number = intent.getStringExtra(EXTRA_NUMBER);
//                    Review review = params.getParcelable(Review.REVIEW_PARAMS_KEY);
                    Review review = null;
                    BusSingleton.getInstance().post(service.addReviewByParams(review));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (DELETE_REVIEW_REQUEST.equals(action)) {
                try{
                    String reviewId = "LerbzRfN95";
                    BusSingleton.getInstance().post(service.deleteReview(reviewId));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (COFFEE_MACHINE_STATUS_REQUEST.equals(action)) {
                try{
                    String coffeeMachineId = "PZrB82ZWVl";
                    CoffeeMachineStatus.Params params = new CoffeeMachineStatus.Params(coffeeMachineId);
                    BusSingleton.getInstance().post(service.getCoffeeMachineStatus(params));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (UPDATE_USER_REQUEST.equals(action)) {
                try{
                    User user = null;
                    BusSingleton.getInstance().post(service.updateUser("LerbzRfN95", user));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (ADD_USER_BY_PARAMS_REQUEST.equals(action)){
                try {
//                    User user = params.getParcelable(User.USER_OBJ_KEY);
                    User user = null;
                    BusSingleton.getInstance().post(service.addUserByParams(user));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
                return;
            }

            if (DELETE_USER_REQUEST.equals(action)) {
                try{
                    String userId = "LerbzRfN95";
                    BusSingleton.getInstance().post(service.deleteUser(userId));
                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }
            }

        }
    }


    public static interface RetrofitServiceInterface {

        //get coffee machine
        @GET("/" + CLASSES + COFFEE_MACHINE)
        List<CoffeeMachine> listCoffeeMachine();

        @POST("/" + FUNCTIONS + MORE_REVIEW)
        List<Review> listMoreReview(@Body Review.MoreReviewsParams user);

        @POST("/" + FUNCTIONS + WEEK_REVIEWS)
        ReviewDataContainer listReview(@Body Review.Params reviewParams);

        @POST("/" + FUNCTIONS + USER_BY_ID_LIST)
        List<User> listUserByIdList(@Body User.Params userParams);

        /**** REVIEW ACTIONS ****/
        @PUT("/" + CLASSES + REVIEW + "/" + "{reviewId}")
        Object updateReview(@Path("reviewId") String reviewId, @Body Review review);

        @POST("/" + CLASSES + REVIEW)
        Object addReviewByParams(@Body Review review);

        @DELETE("/" + CLASSES + REVIEW + "/" + "{reviewId}")
        Object deleteReview(@Path("reviewId") String reviewId);

        /**** USER ACTIONS ****/
        @PUT("/" + CLASSES + USER + "/" + "{userId}")
        Object updateUser(@Path("userId") String userId, @Body User user);

        @POST("/" + CLASSES + USER)
        Object addUserByParams(@Body User user);

        @DELETE("/" + CLASSES + USER + "/" + "{userId}")
        Object deleteUser(@Path("userId") String userId);

        @POST("/" + FUNCTIONS + GET_COFFEE_MACHINE_STATUS)
        CoffeeMachineStatus getCoffeeMachineStatus(@Body CoffeeMachineStatus.Params coffeeMachineId);
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
    public static class CustomDeserializer implements JsonDeserializer<List<CoffeeMachine>> {

        @Override
        public List<CoffeeMachine> deserialize(JsonElement jsonElement, Type type,
                                               JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
    //            Log.e(TAG, jsonElement.toString() + " - " + type.toString());
            Log.e(TAG, type.toString());
    //            Class<? extends Type> classType = type.getClass();
            ArrayList<CoffeeMachine> coffeeMachineList = new ArrayList<CoffeeMachine>();
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("results").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++) {
                coffeeMachineList.add(jsonDeserializationContext.<CoffeeMachine>deserialize(jsonArray.get(i), CoffeeMachine.class));
            }
            return coffeeMachineList;

        }
    }

    public static class CustomDeserializerReview implements JsonDeserializer<ReviewDataContainer> {

        @Override
        public ReviewDataContainer deserialize(JsonElement jsonElement, Type type,
                                               JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
//            Log.e(TAG, jsonElement.toString() + " - " + type.toString());
            Log.e(TAG, type.toString());
//            Class<? extends Type> classType = type.getClass();
            ArrayList<Review> reviewList = new ArrayList<Review>();
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("result").getAsJsonObject().get("data").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++) {
                reviewList.add(jsonDeserializationContext.<Review>deserialize(jsonArray.get(i), Review.class));
            }
            boolean hasMoreReviews = jsonElement.getAsJsonObject().get("result").getAsJsonObject().get("hasMoreReviews").getAsBoolean();

            return  new ReviewDataContainer(hasMoreReviews, reviewList);

//            return reviewList;

        }
    }

    public static class CustomDeserializerUser implements JsonDeserializer<List<User>> {

        @Override
        public List<User> deserialize(JsonElement jsonElement, Type type,
                                      JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
//            Log.e(TAG, jsonElement.toString() + " - " + type.toString());
            Log.e(TAG, type.toString());
//            Class<? extends Type> classType = type.getClass();
            ArrayList<User> userList = new ArrayList<User>();
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("result").getAsJsonArray();
            for(int i = 0; i < jsonArray.size(); i++) {
                userList.add(jsonDeserializationContext.<User>deserialize(jsonArray.get(i), User.class));
            }
            return userList;

        }
    }

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


}