package com.application.material.takeacoffee.app.singletons;

import android.util.Log;

import com.application.material.takeacoffee.app.models.Review;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by davide on 24/03/16.
 */
public class FirebaseManager {
    //TODO move out
    private static final String FIREBASE_URL = "https://torrid-heat-5131.firebaseio.com/";
    private static final String REVIEW_CLASS = "review";
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
                //TODO refactor it
                try {
                    String jsonString = snapshot.getValue().toString();
                    Gson gson = new Gson();
                    String type = REVIEW_CLASS;
                    List<Review> result = Arrays.asList(gson.fromJson(jsonString,
                            Review.class));
                    ArrayList<Review> list = (result.size() != 0) ?
                            new ArrayList<>(result) : null;
                    listener.retrieveFirebaseDataSuccessCallback(type, list);
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
     * interface callback
     */
    public interface OnRetrieveFirebaseDataInterface {
        void retrieveFirebaseDataSuccessCallback(String type, ArrayList<Review> list);
        void retrieveFirebaseDataErrorCallback(FirebaseError error);
    }
}
