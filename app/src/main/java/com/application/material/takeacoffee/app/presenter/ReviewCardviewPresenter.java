package com.application.material.takeacoffee.app.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 06/04/16.
 */
public class ReviewCardviewPresenter {
    public static final int REVIEW_VIEW_MODE = 0;
    private static ReviewCardviewPresenter instance;
    private static WeakReference<Context> context;
    private final ReviewCardviewPresenter.ReviewAnimatorBuilder animatorBuilder;

    private ReviewCardviewPresenter() {
        animatorBuilder = new ReviewCardviewPresenter.ReviewAnimatorBuilder(context);
    }

    /**
     *
     * @return
     */
    public static ReviewCardviewPresenter getInstance(WeakReference<Context> ctx) {
        context = ctx;
        return instance == null ? instance = new ReviewCardviewPresenter() : instance;
    }

    /**
     *
     */
    public void init(View view , int type) {
        view.setBackgroundColor(ContextCompat.getColor(context.get(), R.color.material_brown400));
        if (type == REVIEW_VIEW_MODE) {
        }
    }

    public class ReviewAnimatorBuilder {
        private final WeakReference<Context> context;

        public ReviewAnimatorBuilder(WeakReference<Context> context) {
            this.context = context;
        }
    }
}
