package com.application.material.takeacoffee.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 02/05/16.
 */
public class SharedPrefManager {
    private static SharedPrefManager instance;
    public static final String LOCATION_NAME_SHAREDPREF_KEY = "LOCATION_NAME_SHAREDPREF_KEY";
    private static final String PREFS_NAME = "UNCOFFEE_SHAREDPREF_NAME";
    private String DEFAULT_VALUE = null;
    private static SharedPreferences sharedPref;
    public final static String LATLNG_SHAREDPREF_KEY = "LATLNG_SHAREDPREF_KEY";

    /**
     *
     * @param ctx
     * @return
     */
    public static  SharedPrefManager getInstance(WeakReference<Context> ctx) {
        sharedPref = ctx.get().getSharedPreferences(PREFS_NAME, 0);
        return instance == null ? instance = new SharedPrefManager() :
                instance;
    }

    /**
     *
     */
    public void setValueByKey(String key, String value) {
        sharedPref
                .edit()
                .putString(key, value)
                .apply();
    }

    /**
     *
     */
    public String getValueByKey(String key) {
        return sharedPref.getString(key, DEFAULT_VALUE);
    }
}
