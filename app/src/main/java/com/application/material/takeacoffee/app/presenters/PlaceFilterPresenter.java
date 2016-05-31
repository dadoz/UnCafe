package com.application.material.takeacoffee.app.presenters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import com.application.material.takeacoffee.app.animator.AnimatorBuilder;
import java.lang.ref.WeakReference;

public class PlaceFilterPresenter implements Animator.AnimatorListener {
    private static final long MIN_DELAY = 100;
    private static PlaceFilterPresenter instance;
    private static WeakReference<Context> context;
    private static AnimatorBuilder animatorBuilder;
    private static View layout;
    private static View cardview;
    private static View swipeRefreshLayout;
    private boolean collapsed;
    private float MIN_TRANSLATION_Y = -200;
    private int MIN_HEIGHT = 200;

    /**
     *
     * @param ctx
     * @param viewArray
     * @return
     */
    public static PlaceFilterPresenter getInstance(WeakReference<Context> ctx,
                                                   @NonNull View[] viewArray) {
        context = ctx;
        cardview = viewArray[0];
        layout = viewArray[1];
        swipeRefreshLayout = viewArray[2];
        animatorBuilder = AnimatorBuilder.getInstance(ctx);
        return instance == null ? instance = new PlaceFilterPresenter() : instance;
    }

    /**
     *
     */
    public void onExpand() {
        collapsed = false;
        Animator anim1 = animatorBuilder.buildTranslationAnimator(cardview, MIN_TRANSLATION_Y, 0);
        anim1.setStartDelay(MIN_DELAY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(this);
        animatorSet.playSequentially(anim1);
        animatorSet.start();
        layout.setVisibility(View.VISIBLE);

    }

    /**
     *
     */
    public void onCollapse() {
        collapsed = true;
        animatorBuilder.buildTranslationAnimator(cardview, 0, MIN_TRANSLATION_Y).start();
        Animator anim1 = animatorBuilder.buildTranslationAnimator(cardview, 0, MIN_TRANSLATION_Y);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(this);
        animatorSet.playSequentially(anim1);
        animatorSet.start();
//        layout.setVisibility(View.GONE);
    }

    /**
     *
     * @return
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     *
     * @return
     */
    private Animator getSwipeRefreshLayoutAnimation() {
            return collapsed ?
                    animatorBuilder.buildTranslationAnimator(swipeRefreshLayout, MIN_HEIGHT, 0) :
                    animatorBuilder.buildTranslationAnimator(swipeRefreshLayout, 0, MIN_HEIGHT);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
