package com.application.material.takeacoffee.app.singletons;

import android.util.Log;

import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by davide on 24/03/16.
 */
public class FirebaseManager {
    //TODO move out
    private static final String FIREBASE_URL = "https://torrid-heat-5131.firebaseio.com/";
    private static final String REVIEW_CLASS = "reviews";
    private static final String TAG = "FirebaseManager";
    private Firebase firebaseRef;
    private static FirebaseManager instance;

    public FirebaseManager() {
        firebaseRef = new Firebase(FIREBASE_URL);
    }

    /**
     *
     * @return
     */
    public static FirebaseManager getIstance() {
        return instance == null ?
                instance = new FirebaseManager() : instance;
    }

    /**
     *
     * @param listener
     */
    public void getReviewListAsync(final OnRetrieveFirebaseDataInterface listener) {
        firebaseRef.child(REVIEW_CLASS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
//                    ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) snapshot.getValue();
                    //get GSON data
//                    Gson gson = new Gson();
//                    ArrayList<Review> listTemp = new ArrayList<>();
//                    for (HashMap<String, String> temp: list) {
//                        JSONObject jsonObject = new JSONObject(temp);
//                        Review reviewTemp = gson.fromJson(jsonObject.toString(), Review.class);
//                        listTemp.add(reviewTemp);
//                    }

                    //TODO refactor it
                    ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) snapshot.getValue();
                    Type reviewType = new TypeToken<ArrayList<Review>>(){}.getType();
                    ReviewDeserializer deserializer = new ReviewDeserializer(User.class, new UserDeserializer());
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(reviewType, deserializer);
                    Gson gson = builder.create();
                    ArrayList<Review> listTemp = gson.fromJson(new JSONArray(list).toString(), reviewType);

                    String type = REVIEW_CLASS;
                    listener.retrieveFirebaseDataSuccessCallback(type, listTemp);
                } catch (Exception e) {
                    listener.retrieveFirebaseDataErrorCallback(new FirebaseError(0, e.getMessage()));
                }
            }

            @Override public void onCancelled(FirebaseError error) {
                Log.e(TAG, error.getMessage());
                listener.retrieveFirebaseDataErrorCallback(error);
            }

        });

    }

    /**
     *
     * @param <T>
     */
    public class ReviewDeserializer<T> implements JsonDeserializer<T> {
        private final Class mNestedClazz;
        private final Object mNestedDeserializer;

        public ReviewDeserializer(Class nestedClazz, Object nestedDeserializer) {
            mNestedClazz = nestedClazz;
            mNestedDeserializer = nestedDeserializer;
        }

        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            GsonBuilder builder = new GsonBuilder();
            if (mNestedClazz != null &&
                    mNestedDeserializer != null) {
                builder.registerTypeAdapter(mNestedClazz, mNestedDeserializer);
            }
            return builder.create().fromJson(je.getAsJsonArray(), type);

        }
    }

    /**
     *
     * @param <T>
     */
    public class UserDeserializer<T> implements JsonDeserializer<T> {

        public UserDeserializer() {
        }

        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            GsonBuilder builder = new GsonBuilder();
            return builder.create().fromJson(je, type);

        }
    }

    /**
     * interface callback
     */
    public interface OnRetrieveFirebaseDataInterface {
        void retrieveFirebaseDataSuccessCallback(String type, ArrayList<Review> list);
        void retrieveFirebaseDataErrorCallback(FirebaseError error);
    }
}
