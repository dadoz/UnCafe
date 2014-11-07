package com.application.material.takeacoffee.app.loaders;
import android.util.Log;
import com.application.material.takeacoffee.app.models.User;
import java.util.ArrayList;
import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum;

/**
 * Created by davide on 02/10/14.
 */
public class RestResponse {
    private static final String TAG = "RESTResponse";
    private HTTPActionRequestEnum requestType;
    private Object data;
//    private String data;
//    private Object dataObject;
//    private int resultCode;
//    private boolean userResponse;
//    private boolean reviewResponse;
//    private ArrayList<User> userListParser;

    public RestResponse(Object data, HTTPActionRequestEnum requestType) {
        this.data = data;
        this.requestType = requestType;
        //TODO need to be implemented
    }

    public Object getParsedData() {
        if(this.data == null) {
            Log.e(TAG, "error - no data retrieved");
            return null;
        }

        switch (this.requestType) {
            case COFFEE_MACHINE_REQUEST:
                return this.data; //no need to be parsed (GSON makes the trick)
            case REVIEW_REQUEST:
                return this.data;
            case MORE_REVIEW_REQUEST:
                return this.data; //no need to be parsed (GSON makes the trick)
            case REVIEW_COUNT_REQUEST:
                return this.data;
            case ADD_REVIEW_BY_PARAMS:
                return this.data;
            default:
                return this.data;
        }
    }
/*
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
*/


    /*********BUNDLE**********/
/*
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

*/
}
