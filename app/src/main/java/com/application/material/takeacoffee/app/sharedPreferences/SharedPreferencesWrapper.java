package com.application.material.takeacoffee.app.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by davide on 28/12/14.
 */
public class SharedPreferencesWrapper {
    private static SharedPreferences sharedPreferences;
    private static String SHARED_PREF = "TAKEACOFFEE_SHARED_PREF";
    public static String LOGGED_USER_ID = "LOGGED_USER_ID";

    private SharedPreferencesWrapper() {
    }

    public static SharedPreferences initSharedPreferences(Activity activityRef) {
        return sharedPreferences = activityRef.getApplicationContext()
                .getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static void putString(Activity activityRef, String key, String value) {
        initSharedPreferences(activityRef);
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getValue(Activity activityRef, String key) {
        initSharedPreferences(activityRef);
        return sharedPreferences.getString(key, null);
    }

}
