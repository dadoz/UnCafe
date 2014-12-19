package com.application.material.takeacoffee.app.loaders;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.restServices.RetrofitServiceInterface;
import com.google.gson.*;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by davide on 15/09/14.
 */
public class RetrofitLoader extends AsyncTaskLoader<RestResponse> {
    private static final String TAG = "RESTLoader";
    private final HTTPActionRequestEnum mAction;
    private final RestAdapter restAdapter;
    private final RetrofitServiceInterface retrofitService;
    private final Bundle params;

    public RetrofitLoader(FragmentActivity activity, String action, Bundle params) {
        super(activity);
        Type typeOfListOfCategory = new com.google.gson.reflect.TypeToken<List<CoffeeMachine>>(){}.getType();
        Type typeOfListOfCategoryUser = new com.google.gson.reflect.TypeToken<List<User>>(){}.getType();
        Type typeOfListOfCategoryReview = new com.google.gson.reflect.TypeToken<List<Review>>(){}.getType();


        mAction = HTTPActionRequestEnum.valueOf(action);
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(typeOfListOfCategory, new CustomDeserializer())
                .registerTypeAdapter(typeOfListOfCategoryUser, new CustomDeserializerUser())
                .registerTypeAdapter(typeOfListOfCategoryReview, new CustomDeserializerReview())
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.parse.com") // to baseUrl
//                .setEndpoint("https://192.168.130.112:8888") // to baseUrl
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                .build();

        retrofitService = restAdapter.create(RetrofitServiceInterface.class);
        this.params = params;
    }

    @Override
    public RestResponse loadInBackground() {
        try{
            if (mAction == null) {
                Log.e(TAG, "You did not define an action. REST call canceled.");
                return null; //TODO HANDLE It
            }

            Object data = null;
            switch (mAction) {
                case ADD_REVIEW_REQUEST:
                    //TODO not implemented yet
                    break;
                case REVIEW_REQUEST:
                    data = retrofitService.listReview(new Review.Params("PZrB82ZWVl", Double.parseDouble("1410696082045")));
                    break;
                case MORE_REVIEW_REQUEST:
                    data = retrofitService.listMoreReview(new Review.MoreReviewsParams("PZrB82ZWVl", "qaeWjprTDF"));
                    break;
                case USER_REQUEST:
                    String [] array = {"4nmvMJNk1R", "K8bwZOSmNo", "8e2XwXZUKL"};
                    data = retrofitService.listUserByIdList(new User.Params(new ArrayList(Arrays.asList(array))));
                    break;
                case REVIEW_COUNT_REQUEST:
                    //TODO not used anymore
                    break;
                case ADD_REVIEW_BY_PARAMS:
                    //TODO not implemented yet
                    Review review = params.getParcelable(Review.REVIEW_KEY);
                    data = retrofitService.addReviewByParams(review);
                    break;
                case COFFEE_MACHINE_REQUEST:
                    data = retrofitService.listCoffeeMachine();
                    break;
                case SAVE_EDIT_REVIEW:
                    //TODO not implemented yet
                    review = null;
                    retrofitService.saveEditReview(review);
                    break;
                case DELETE_REVIEW:
                    //TODO not implemented yet
                    String reviewId = null;
                    retrofitService.deleteReview(reviewId);
                    break;
                case GET_COFFEE_MACHINE_STATUS:
                    String coffeeMachineId = "PZrB82ZWVl";
                    data = retrofitService.getCoffeeMachineStatus(new CoffeeMachineStatus.Params(coffeeMachineId));
                    break;

            }
            return new RestResponse(data, mAction); // We send a Response back
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "RetrofitLoader error :" + e.getMessage());
        }
        return null;
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-Parse-Application-Id", "61rFqlbDy0UWBfY56RcLdiJVB1EPe8ce1yUxdAEY");
            request.addHeader("X-Parse-REST-API-Key", "J37VkDdADU7jPfZSwLluAEixwJ3BmjPQJeuR1EzJ");
            request.addHeader("Content-Type", "application/json");
        }
    };

    public enum HTTPActionRequestEnum {
        ADD_REVIEW_REQUEST,
        REVIEW_REQUEST,
        MORE_REVIEW_REQUEST,
        USER_REQUEST,
        REVIEW_COUNT_REQUEST,
        ADD_REVIEW_BY_PARAMS,
        COFFEE_MACHINE_REQUEST,
        SAVE_EDIT_REVIEW,
        DELETE_REVIEW,
        GET_COFFEE_MACHINE_STATUS
    }

//    public class UserCallback implements  Callback<List<User>>  {
//        @Override
//        public void success(List<User> s, Response response) {
//            Log.e(TAG, "success");
//        }
//
//        @Override
//        public void failure(RetrofitError retrofitError) {
//            Log.e(TAG, "success" + retrofitError);
//
//        }
//    }

    public static String getActionByActionRequestEnum(int ordinal) {
        try {
            return RetrofitLoader.HTTPActionRequestEnum.values()[ordinal].name();
        } catch (Exception e) {
            return null;
        }
    }

    public class ParseAction {
        public static final String CLASSES = "1/classes/";
        public static final String FUNCTIONS = "1/functions/";

        //action model
        public static final String COFFEE_MACHINE = "coffee_machines";
        public static final String REVIEW = "reviews";
        public static final String WEEK_REVIEWS = "getWeekReviews";
        public static final String USER_BY_ID_LIST= "getUsersByUserIdList";
        public static final String MORE_REVIEW = "getMoreReviews";
        public static final String GET_COFFEE_MACHINE_STATUS = "getCoffeeMachineStatus";
//        public static final String REVIEW_COUNTER_TIMESTAMP = "countOnReviewsWithTimestamp";
    }

    //TODO TEST
    public static String getJSONDataMockup(FragmentActivity fragmentActivity, String filename) {
        AssetManager assetManager = fragmentActivity.getAssets();
        InputStream input;
        try {
            input = assetManager.open("data/" + filename);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            return new String(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

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


}
