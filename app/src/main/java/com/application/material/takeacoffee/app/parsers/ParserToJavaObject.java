package com.application.material.takeacoffee.app.parsers;

import com.application.material.takeacoffee.app.models.*;
import com.google.gson.JsonParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.application.material.takeacoffee.app.models.ReviewStatus.parseStatus;

/**
 * Created by davide on 05/11/14.
 */
public class ParserToJavaObject {

    /***** DATA PARSER ****/
    public static ReviewCounter parseCountOnReviewsData(String data) {
//            {"result":{"PZrB82ZWVl":{"GOOD":13,"NOTSOBAD":3,"WORST":1}}

        ReviewCounter reviewCounter = null;
        try {
            JSONObject objectFirst = new JSONObject(data).getJSONObject("result");
            Iterator keysIterator = objectFirst.keys();

            while(keysIterator.hasNext()) {
                String key = keysIterator.next().toString();
                JSONObject objectTwo = objectFirst.getJSONObject(key);

                Iterator keysIteratorTwo = objectTwo.keys();

                while(keysIteratorTwo.hasNext()) {
                    String keyTimestamp = keysIteratorTwo.next().toString();
                    JSONObject objectThree = objectTwo.getJSONObject(keyTimestamp);
                    //TODO to be replaced
                    reviewCounter = new ReviewCounter(key,
                            Long.parseLong(keyTimestamp),
                            objectThree.getInt("GOOD"),
                            objectThree.getInt("NOTSOBAD"),
                            objectThree.getInt("WORST"));
                }
            }
            return reviewCounter;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

            return new Review(reviewId, reviewComment,
                    parseStatus(reviewStatus), timestamp,
                    reviewUserId, reviewCoffeeMachineId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ReviewStatus reviewStatusParser(String data) {
        JSONObject reviewJsonObj = null;
        try {
            reviewJsonObj = new JSONObject(data).getJSONObject("result");
            String status = reviewJsonObj
                    .getString("status");
            String name = reviewJsonObj
                    .getString("name");
            int weeklyReviewCnt = reviewJsonObj
                    .getInt("weekly_review_cnt");
            boolean reviewCnt = reviewJsonObj
                    .getBoolean("has_at_least_one_review");

            return new ReviewStatus(status, name, weeklyReviewCnt, reviewCnt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<CoffeeMachine> coffeeMachineParser(String data) {
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

    public static boolean getHasMoreReviews(String data) {
        try {
            return data != null &&
                    (new JSONObject(data).getJSONObject("result"))
                            .getBoolean("hasMoreReviews");
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

//    public boolean isUserResponse() {
//        return requestType != null && requestType.compareTo(USER_REQUEST) == 0;
//    }
//
//    public boolean isReviewResponse() {
//        return requestType != null && requestType.compareTo(REVIEW_REQUEST) == 0;
//    }
//
//    public boolean isMoreReviewResponse() {
//        return requestType != null && requestType.compareTo(MORE_REVIEW_REQUEST) == 0;
//    }

    public static ArrayList<User> getUserListParser(String data) {
        ArrayList<User> userList = new ArrayList<User>();
        try {
            JSONArray jsonArray = new JSONObject(data)
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

    private static User userParser(String data) {
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

}
