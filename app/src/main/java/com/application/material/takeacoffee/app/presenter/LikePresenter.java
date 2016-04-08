package com.application.material.takeacoffee.app.presenter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.application.material.takeacoffee.app.R;
import com.firebase.client.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 04/04/16.
 */
public class LikePresenter {
    private static final float SCALE_EXPAND = 1.3f;
    private static final float SCALE_SHRINK = 1.0f;
    private static LikePresenter instance;
    private static WeakReference<Context> context;
    private boolean isLike;
    private LikeAnimatorBuilder animatorBuilder;

    private LikePresenter() {
        animatorBuilder = new LikeAnimatorBuilder(context);
    }

    /**
     *
     * @return
     */
    public static LikePresenter getInstance(WeakReference<Context> ctx) {
        context = ctx;
        return instance == null ? instance = new LikePresenter() : instance;
    }

    /**
     *
     * @param isLike
     */
    public void initPresenter(boolean isLike) {
        this.isLike = isLike;
    }

    /**
     *
     */
    public void toggleLikeStatus(View view) {
        isLike = !isLike;
    }

    /**
     *
     */
    public void initLikeStatus(View view) {
        setColorToDrawable((ImageView) view, isLike ?
                R.color.material_red700 : R.color.material_grey400);
    }

    /**
     *
     * @return
     */
    public boolean isLike() {
        return isLike;
    }

    /**
     *
     * @param view
     * @param colorRes
     */
    public void setColorToDrawable(@NotNull ImageView view, int colorRes) {
        //TODO
        view.getDrawable().setTint(ContextCompat
                .getColor(context.get(),colorRes));

    }
    /**
     * animatorBuilder
     */
    public static class LikeAnimatorBuilder implements ValueAnimator.AnimatorUpdateListener {
        private static final String ALPHA = "alpha";
        private final int duration;
        private Drawable drawable;

        public LikeAnimatorBuilder(WeakReference<Context> context) {
            duration = context.get().getResources()
                    .getInteger(android.R.integer.config_mediumAnimTime);
        }

        /**
         *
         * @param view
         * @param startAlpha
         * @param endAlpha
         * @return
         */
        public Animator getAlphaAnimator(View view, float startAlpha, float endAlpha) {
            ObjectAnimator obj = ObjectAnimator.ofFloat(view, ALPHA, startAlpha, endAlpha);
            obj.setDuration(duration);
            return obj;
        }


        /**
         *
         * @param view
         * @param scaleValue
         * @return
         */
        public Animator getScaleYAnimator(View view, float scaleValue) {
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", scaleValue);
            scaleDownY.setDuration(duration);
            return scaleDownY;
        }

        /**
         *
         * @param view
         * @param scaleValue
         * @return
         */
        public Animator getScaleXAnimator(View view, float scaleValue) {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", scaleValue);
            scaleDownX.setDuration(duration);
            return scaleDownX;
        }

        /**
         *
         * @param colorFrom
         * @param colorTo
         * @return
         */
        public ValueAnimator getColorAnimator(@NotNull int colorFrom, @NotNull int colorTo) {
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            animator.setDuration(duration);
            return animator;
        }

        /**
         *
         * @param drawable
         * @param isLike
         * @return
         */
        public Animator initColorAnimator(final Drawable drawable, boolean isLike) {
            this.drawable = drawable;
            int greyColor = ContextCompat.getColor(context.get(), R.color.material_grey400);
            int redColor = ContextCompat.getColor(context.get(), R.color.material_red700);
            ValueAnimator colorAnimator = isLike ? getColorAnimator(greyColor, redColor) :
                    getColorAnimator(redColor, greyColor);
            colorAnimator.addUpdateListener(this);
            return colorAnimator;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            drawable.setTint((int) animation.getAnimatedValue());
        }
    }
}
