package com.application.material.takeacoffee.app.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.application.material.takeacoffee.app.R;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by davide on 30/12/14.
 */
public class Utils {

    private Utils() {
    }

    /**
     *
     * @param activityRef
     * @param editTextView
     */
    public static void hideKeyboard(Activity activityRef, EditText editTextView) {
        ((InputMethodManager) activityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editTextView.getWindowToken(), 0);

    }

    /**
     *
     * @param view
     * @param message
     */
    public static void showSnackbar(View view , String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * format date
     * @return
     */
    public static String getFormattedTimestamp(long timestamp) {
        return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ITALY)
                .format(new Date(timestamp * 1000));
    }

    /**
     * TODO move out
     * @return
     */
    public static Drawable getColoredDrawable(Drawable defaultIcon, int color) {
        defaultIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return defaultIcon;
    }

    /**
     *
     * @param latLng
     * @return
     */
    public static String getLatLngString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }


//    public static SpannableString getSpannableFromString(Activity activity, String text) {
//        //TODO move on Utils
//        Typeface font = Typeface.createFromAsset(activity.getAssets(), "chimphand-regular.ttf");
//        SpannableString spannableString = new SpannableString(text);
//        spannableString.setSpan(new TypefaceSpan("", font), 0,
//                spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }
}
