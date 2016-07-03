package com.application.material.takeacoffee.app.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

/**
 * Created by davide on 30/12/14.
 */
public class Utils {
    private Utils() {
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

    /**
     *
     * @param myText
     * @param contextWeakRefer
     * @return
     */
    public static Spannable wrapInCustomfont(String myText, WeakReference<Context> contextWeakRefer) {
        Typeface typeface = Typeface.createFromAsset(contextWeakRefer.get().getAssets(), "fonts/chimphand-regular.ttf");
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(typeface);
        SpannableString spannable = new SpannableString(myText);
        spannable.setSpan(typefaceSpan, 0, myText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     *
     * @param contextWeakRefer
     * @return
     */
    public static void hideKeyboard(WeakReference<Context> contextWeakRefer, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager)contextWeakRefer.get()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     *
     * @param view
     * @param messageResourceId
     * @return
     */
    public static void showSnackbar(WeakReference<Context> contextWeakRefer, View view, int messageResourceId) {
        if (contextWeakRefer.get() == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(view, messageResourceId, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(contextWeakRefer.get(),
                R.color.material_red400));
        snackbar.show();
    }

    /**
     *
     * @param timestampStr
     * @return
     */
    public static String convertLastUpdateFromTimestamp(String timestampStr) {
        if (timestampStr == null) {
            return " -";
        }

        return new DateTime(Long.parseLong(timestampStr))
                .toString(DateTimeFormat.forPattern("dd MMM YYYY"));
    }

    /**
     *
     * @param color
     * @param window
     */
    public static void setStatusBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT >= 22) {
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(color);
        }
    }


    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
