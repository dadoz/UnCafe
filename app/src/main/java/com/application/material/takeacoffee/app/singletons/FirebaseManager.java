package com.application.material.takeacoffee.app.singletons;

import android.os.AsyncTask;
import android.util.Log;

import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by davide on 24/03/16.
 */
public class FirebaseManager implements ChildEventListener, ValueEventListener {
    //TODO move out
    private static final String REVIEW_CLASS = "reviews";
    private static final String FIREBASE_URL = "https://torrid-heat-5131.firebaseio.com/";
    private static final String TAG = "FirebaseManager";
    private Firebase firebaseRef;
    private static FirebaseManager instance;
    private WeakReference<OnRetrieveFirebaseDataInterface> listener;
    private ArrayList<Review> list = new ArrayList<>();

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
     * @param placeId
     */
    public void getReviewListAsync(final WeakReference<OnRetrieveFirebaseDataInterface> listener,
                                   final String placeId) {
        this.listener = listener;
        final FirebaseManager ref = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("RUN", "EUN");
                firebaseRef.child(REVIEW_CLASS)
                        .orderByChild("placeId")
                        .equalTo(placeId)
                        .addValueEventListener(ref);
            }
        }).run();
    }

    /**
     *
     * @param json
     * @return
     */
    public Review getObjectFromJSON(String json) {
        ReviewDeserializer deserializer = new ReviewDeserializer(User.class, new UserDeserializer());
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Review.class, deserializer);
        Gson gson = builder.create();
        return gson.fromJson(json, Review.class);
    }

    /**
     *
     * @param iterator
     * @return
     */
    public ArrayList<Review> getListFromIterable(Iterator<DataSnapshot> iterator) {
        ArrayList<Review> listTmp = new ArrayList<>();
        //TODO add observable
        while (iterator.hasNext()) {
            String jsonString = new JSONObject((HashMap<String, String>) iterator.next().getValue()).toString();
            listTmp.add(getObjectFromJSON(jsonString));
        }
        return listTmp;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            String type = REVIEW_CLASS;
            list = getListFromIterable(dataSnapshot.getChildren().iterator());
            listener.get().retrieveFirebaseDataSuccessCallback(type, list);
        } catch (Exception e) {
            listener.get().retrieveFirebaseDataErrorCallback(new FirebaseError(0, e.getMessage()));
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            if (!dataSnapshot.exists()) {
                listener.get().emptyFirebaseDataCallback();
                return;
            }
            String type = REVIEW_CLASS;
            list = getListFromIterable(dataSnapshot.getChildren().iterator());
            listener.get().retrieveFirebaseDataSuccessCallback(type, list);
        } catch (Exception e) {
            listener.get().retrieveFirebaseDataErrorCallback(new FirebaseError(0, e.getMessage()));
        }

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, firebaseError.getMessage());
        listener.get().retrieveFirebaseDataErrorCallback(firebaseError);
    }

    /**
     * GSON
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
            return builder.create().fromJson(je.getAsJsonObject(), type);

        }
    }

    /**
     * GSON
     * @param <T>
     */
    public class UserDeserializer<T> implements JsonDeserializer<T> {
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
        void emptyFirebaseDataCallback();
    }
}
