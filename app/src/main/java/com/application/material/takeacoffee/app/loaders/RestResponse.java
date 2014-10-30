package com.application.material.takeacoffee.app.loaders;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.application.material.takeacoffee.app.loaders.RestLoaderRetrofit.HTTPActionRequestEnum;
import static com.application.material.takeacoffee.app.loaders.RestLoaderRetrofit.HTTPActionRequestEnum.*;
import static com.application.material.takeacoffee.app.models.ReviewStatus.parseStatus;


/**
 * Created by davide on 02/10/14.
 */
public class RestResponse {

    private static final String TAG = "RESTResponse";
    private HTTPActionRequestEnum requestType;
    private int resultCode;
    private String data;
    private Object dataObject;
    private boolean userResponse;
    private boolean reviewResponse;
    private ArrayList<User> userListParser;


    public RestResponse(Object data, HTTPActionRequestEnum requestType) {
        this.dataObject = data;
        this.requestType = requestType;
        //TODO need to be implemented
    }

    public RestResponse(String data, int resultCode, HTTPActionRequestEnum requestType) {
        //TODO need to be implemented
        this.data = data;
        this.resultCode = resultCode;
        this.requestType = requestType;
    }

    public RestResponse() {

    }

    public HTTPActionRequestEnum getRequestType() {
        return requestType;
    }

    public int getCode() {
        return resultCode;
    }

    public String getData() {
        return data;
    }

    public int getHttpResponseCode() {
        JSONObject object = null;
        Log.e(TAG, "data result" + data);
        assert data != null;
        try {
            object = new JSONObject(data);
            return object.getJSONObject("result").getInt("code");
        } catch (JSONException e) {
            assert object != null;
            try {
                if(object.getJSONObject("result").keys().hasNext()) {
                    return 200;
                }
            } catch (JSONException e1) {
                try {
                    return object.getInt("code");
                } catch (JSONException e2) {
//                        e2.printStackTrace();
                }
//                    e1.printStackTrace();
            }
        }
        return -1; //NOT AVAILABLE
    }

    /***** DATA PARSER ****/
//    public ReviewCounter parseCountOnReviewsData(String data) {
////            {"result":{"PZrB82ZWVl":{"GOOD":13,"NOTSOBAD":3,"WORST":1}}
//
//        ReviewCounter reviewCounter = null;
//        try {
//            JSONObject objectFirst = new JSONObject(data).getJSONObject("result");
//            Iterator keysIterator = objectFirst.keys();
//
//            while(keysIterator.hasNext()) {
//                String key = keysIterator.next().toString();
//                JSONObject objectTwo = objectFirst.getJSONObject(key);
//
//                Iterator keysIteratorTwo = objectTwo.keys();
//
//                while(keysIteratorTwo.hasNext()) {
//                    String keyTimestamp = keysIteratorTwo.next().toString();
//                    JSONObject objectThree = objectTwo.getJSONObject(keyTimestamp);
//                    //TODO to be replaced
//                    reviewCounter = new ReviewCounter(key,
//                            Long.parseLong(keyTimestamp),
//                            objectThree.getInt("GOOD"),
//                            objectThree.getInt("NOTSOBAD"),
//                            objectThree.getInt("WORST"));
//                }
//            }
//            return reviewCounter;
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private static Review reviewParser(String data) {
        JSONObject reviewJsonObj = null;
        try {
            reviewJsonObj = new JSONObject(data);
            String reviewId = reviewJsonObj.getString("objectId");
            String reviewUserId = reviewJsonObj
                    .getString("user_id_string");
            String reviewCoffeeMachineId = reviewJsonObj
                    .getString("coffee_machine_id_string");
            String reviewComment = reviewJsonObj
                    .getString("comment");
            long timestamp = reviewJsonObj
                    .getLong("timestamp");
            String reviewStatus = reviewJsonObj
                    .getString("status");

            return new Review(reviewId, reviewComment, parseStatus(reviewStatus), timestamp, reviewUserId, reviewCoffeeMachineId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CoffeeMachine> coffeeMachineParser(String data) {
        if(data != null) {
            try {
                JSONArray jsonArray = new JSONObject(data).getJSONArray("results"); //STATIC

                ArrayList<CoffeeMachine> coffeeMachineList = new ArrayList<CoffeeMachine>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject coffeeMachineObj = jsonArray.getJSONObject(i);

                    String name = coffeeMachineObj.getString("name");
                    String address = coffeeMachineObj
                            .getString("address");
                    String coffeeMachineId = coffeeMachineObj
                            .getString("objectId");
                    String iconPath = coffeeMachineObj
                            .getString("icon_path");


                    coffeeMachineList.add(new CoffeeMachine(
                            coffeeMachineId, name, address, iconPath));
                }
                return coffeeMachineList;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean getHasMoreReviews() {
        try {
            if(this.data == null) {
                return false;
            }

            JSONObject dataObject = new JSONObject(this.data);
            return (dataObject.getJSONObject("result")).getBoolean("hasMoreReviews");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Review> getReviewListParser(String data) {
        try {
            JSONObject dataObject = new JSONObject(data);
//                boolean hasMoreReviews = (dataObject.getJSONObject("result")).getBoolean("hasMoreReviews");
            JSONArray reviewJsonArray = (dataObject.getJSONObject("result")).getJSONArray("data");
            ArrayList<Review> reviewsList = new ArrayList<Review>();
            for (int j = 0; j < reviewJsonArray.length(); j ++) {
                JSONObject reviewJsonObj = reviewJsonArray.getJSONObject(j);
                reviewsList.add(reviewParser(reviewJsonObj.toString()));
            }

            return reviewsList;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUserResponse() {
        return requestType != null && requestType.compareTo(USER_REQUEST) == 0;
    }

    public boolean isReviewResponse() {
        return requestType != null && requestType.compareTo(REVIEW_REQUEST) == 0;
    }

    public boolean isMoreReviewResponse() {
        return requestType != null && requestType.compareTo(MORE_REVIEW_REQUEST) == 0;
    }

    public ArrayList<User> getUserListParser() {
        ArrayList<User> userList = new ArrayList<User>();
        try {
            JSONArray jsonArray = new JSONObject(this.data)
                    .getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i ++) {
                User user = userParser(jsonArray.get(i).toString());
                if(user != null) {
                    userList.add(user);
                }
            }
            return userList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User userParser(String data) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
            String userId = jsonObj.getString("objectId");
            String username = jsonObj
                    .getString("username");
            String profilePicturePath = jsonObj
                    .getString("profile_picture_path");

            return new User(userId, profilePicturePath, username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*********BUNDLE**********/

    //TODO MOVE THEM SMWHERE ELSE
    //BUNDLE move out maybe its better
    public static Bundle createBundleUser(ArrayList<String> userIdList) {
        String action = "https://api.parse.com/1/functions/getUserListByUserIdList";
        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("userIdList", new JSONArray(userIdList));
            bundle.putString("params", paramsObj.toString());
            Log.d(TAG, "params" + paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", USER_REQUEST.name());
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle createBundleReview(String coffeeMachineId, long fromTimestamp, long toTimestamp) {
        String action = "https://api.parse.com/1/functions/getReviewByTimestampLimitOnResult";

        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("coffeeMachineId", coffeeMachineId);
            paramsObj.put("toTimestamp", toTimestamp);
            paramsObj.put("fromTimestamp", fromTimestamp);
            bundle.putString("params", paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", REVIEW_REQUEST.name());
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle createBundleMoreReview(String coffeeMachineId, String fromReviewId, long toTimestamp) {
        String action = "https://api.parse.com/1/functions/getMoreReview";
        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("coffeeMachineId", coffeeMachineId);
            paramsObj.put("fromReviewId", fromReviewId);
            paramsObj.put("toTimestamp", toTimestamp);
            bundle.putString("params", paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", MORE_REVIEW_REQUEST.name());
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle createBundleCoffeeMachine() {
        String action = "https://api.parse.com/1/classes/coffee_machines";

        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            bundle.putString("params", paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", REVIEW_REQUEST.name());
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle createBundleReviewDashboard(String coffeeMachineId,
                                                     long fromTimestamp, long toTimestamp) {

        String action = "https://api.parse.com/1/functions/countOnReviewsWithTimestamp";

        Bundle bundle = new Bundle();
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("coffeeMachineId", coffeeMachineId);
            paramsObj.put("toTimestamp", toTimestamp);
            paramsObj.put("fromTimestamp", fromTimestamp);
            bundle.putString("params", paramsObj.toString());
            bundle.putString("action", action);
            bundle.putString("requestType", REVIEW_COUNT_REQUEST.name());
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


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


}
