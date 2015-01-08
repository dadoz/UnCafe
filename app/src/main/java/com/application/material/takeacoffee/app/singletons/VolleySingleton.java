package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by davide on 03/01/15.
 */
public class VolleySingleton implements ImageLoader.ImageCache {
    private static final String TAG = "VolleySingleton";
    private static VolleySingleton volleyInstance;
    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);


    private VolleySingleton() {
    }

    public static VolleySingleton getInstance(Context ctx) {
        volleyInstance = volleyInstance == null ?
                new VolleySingleton() :
                volleyInstance;
        initVolley(ctx);
        return volleyInstance;
    }

    private static void initVolley(Context ctx) {
        //VOLLEY stuff
        requestQueue = Volley.newRequestQueue(ctx); //TODO app crash :S out of memory :O
        imageLoader = new ImageLoader(requestQueue, volleyInstance);
    }

    public void imageRequest(String profilePicturePath,
                                   ImageView profilePicImageView, int defaultIconId) {
        if(profilePicturePath == null ||
                profilePicImageView == null) {
            Log.e(TAG, "error: profilePicturePath or profilePictureView are null");
            return;
        }

        imageLoader.get(profilePicturePath, ImageLoader
                .getImageListener(profilePicImageView, defaultIconId, defaultIconId));
    }

    @Override
    public Bitmap getBitmap(String s) {
        return cache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        cache.put(s, bitmap);
    }

}

