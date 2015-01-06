package com.application.material.takeacoffee.app.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by davide on 30/12/14.
 */
public class Utils {

    private Utils() {
    }

    public static void hideKeyboard(Activity activityRef, EditText editTextView) {
        ((InputMethodManager) activityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editTextView.getWindowToken(), 0);

    }

}
