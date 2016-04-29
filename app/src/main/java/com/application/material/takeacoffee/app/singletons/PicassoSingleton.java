package com.application.material.takeacoffee.app.singletons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 29/04/16.
 */
public class PicassoSingleton implements Callback {

    private static PicassoSingleton instance;
    private static WeakReference<Context> contextWeakRef;
    private static WeakReference<PicassoCallbacksInterface> listenerWeakRef;

    public static PicassoSingleton getInstance(WeakReference<Context> context,
                                               WeakReference<PicassoCallbacksInterface> listener) {
        contextWeakRef = context;
        listenerWeakRef = listener;
        return instance == null ? instance = new PicassoSingleton() : instance;
    }

    private PicassoSingleton() {
    }

    /**
     *
     * @param coffeePlaceImageView
     * @param photoReference
     */
    public void setPhotoAsync(final ImageView coffeePlaceImageView, String photoReference, Drawable defaultIcon) {
        Picasso
                .with(contextWeakRef.get())
                .load(RetrofitManager.getInstance()
                        .getPlacePhotoUrlByReference(photoReference))
                .placeholder(defaultIcon)
                .fit()
                .centerCrop()
                .into(coffeePlaceImageView, this);
    }

    @Override
    public void onSuccess() {
        if (listenerWeakRef != null &&
            listenerWeakRef.get() != null) {
            listenerWeakRef.get().onPicassoSuccessCallback();
        }
    }

    @Override
    public void onError() {
        if (listenerWeakRef != null &&
                listenerWeakRef.get() != null) {
            listenerWeakRef.get().onPicassoErrorCallback();
        }

    }

    /**
     * picasso callbacks interface
     */
    public interface PicassoCallbacksInterface {
        void onPicassoSuccessCallback();
        void onPicassoErrorCallback();
    }
}
