package com.application.material.takeacoffee.app.presenters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.application.material.takeacoffee.app.animator.AnimatorBuilder;
import java.lang.ref.WeakReference;

public class PlaceFilterPresenter implements Animator.AnimatorListener {
    private static final long MIN_DELAY = 100;
    private static final int MIN_OFFSET = 100;
    private static PlaceFilterPresenter instance;
    private static WeakReference<Context> context;
    private static AnimatorBuilder animatorBuilder;
    private static View layout;
    private static View cardview;
    private static View swipeRefreshLayout;
    private static View changePlaceCardview;
    private static View backgroundFrameLayout;

    private enum  PlaceFilterEnum {COLLAPSED, EDIT, LOADING_EDIT, IDLE};
    private PlaceFilterEnum state;

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
        changePlaceCardview = viewArray[1];
        layout = viewArray[2];
        swipeRefreshLayout = viewArray[3];
        backgroundFrameLayout = viewArray[4];
        animatorBuilder = AnimatorBuilder.getInstance(ctx);
        return instance == null ? instance = new PlaceFilterPresenter() : instance;
    }

    /**
     *
     */
    public void init() {
        changePlaceCardview.setY(-1000);
        onExpand();
    }

    /**
     *
     */
    public void onExpand() {
        state = PlaceFilterEnum.IDLE;
        expand(cardview);
    }

    /**
     *
     */
    public void onCollapse() {
        state = PlaceFilterEnum.COLLAPSED;
        collapse(cardview);
    }

    /**
     *
     */
    public void onShowEditPlace() {
        state = PlaceFilterEnum.EDIT;
        backgroundFrameLayout.setVisibility(View.VISIBLE);
        expandEdit(cardview, changePlaceCardview, backgroundFrameLayout);
    }

    /**
     *
     */
    public void onHideEditPlace() {
        state = PlaceFilterEnum.IDLE;
//        backgroundFrameLayout.setVisibility(View.GONE);
        collapseEdit(cardview, changePlaceCardview, backgroundFrameLayout);
    }

    public void onLoadEditPlace() {
        state = PlaceFilterEnum.LOADING_EDIT;
        onOnlyHideEditPlace();
    }

    /**
     *
     */
    public void onOnlyHideEditPlace() {
        collapseEdit(null, changePlaceCardview, null);
    }
    /**
     *
     */
    public void onOnlyShowEditPlace() {
        state = PlaceFilterEnum.EDIT;
        expandEdit(null, changePlaceCardview, null);
    }

    /**
     *
     * @param view
     */
    private void expand(View view) {
        //TODO calculate height
        int MIN_TRANSLATION_Y = -(view.getHeight() + MIN_OFFSET);
        Animator anim1 = animatorBuilder.buildTranslationAnimator(view, MIN_TRANSLATION_Y, 0);
        anim1.setStartDelay(MIN_DELAY);
        initAndStartAnimatorSet(new Animator[] {anim1});
//        layout.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param view
     */
    private void collapse(View view) {
        //TODO calculate height
        int MIN_TRANSLATION_Y = -(view.getHeight() + MIN_OFFSET);
        Animator anim1 = animatorBuilder.buildTranslationAnimator(view, 0, MIN_TRANSLATION_Y);
        initAndStartAnimatorSet(new Animator[] {anim1});
    }

    /**
     *
     * @param mainView
     * @param editView
     */
    private void collapseEdit(View mainView, View editView, View backgroundView) {
        //TODO calculate height
        int MIN_TRANSLATION_EDIT_Y = -(editView.getHeight() + MIN_OFFSET);
        Animator anim1 = animatorBuilder.buildTranslationAnimator(editView, 0, MIN_TRANSLATION_EDIT_Y);
        if (mainView != null) {
            int MIN_TRANSLATION_Y = -(mainView.getHeight() + MIN_OFFSET);
            Animator anim2 = animatorBuilder.buildTranslationAnimator(mainView, MIN_TRANSLATION_Y, 0);
            Animator anim3 = animatorBuilder.buildAlphaAnimator(backgroundView, 1, 0);
            initAndStartAnimatorSet(new Animator[] {anim3, anim1, anim2});
            return;
        }

        initAndStartAnimatorSet(new Animator[] {anim1});
    }

    /**
     *
     * @param mainView
     * @param editView
     */
    private void expandEdit(View mainView, View editView, View backgroundView) {
        //TODO calculate height
        int MIN_TRANSLATION_EDIT_Y = -(editView.getHeight() + MIN_OFFSET);
        Animator anim2 = animatorBuilder.buildTranslationAnimator(editView, MIN_TRANSLATION_EDIT_Y, 0);
        if (mainView != null) {
            int MIN_TRANSLATION_Y = -(mainView.getHeight() + MIN_OFFSET);
            Animator anim1 = animatorBuilder.buildTranslationAnimator(mainView, 0, MIN_TRANSLATION_Y);
            Animator anim3 = animatorBuilder.buildAlphaAnimator(backgroundView, 0, 1);
            initAndStartAnimatorSet(new Animator[] {anim3, anim1, anim2});
            return;
        }
        initAndStartAnimatorSet(new Animator[] {anim2});
    }

    /**
     *
     * @param animatorArray
     */
    private void initAndStartAnimatorSet(Animator[] animatorArray) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(this);
        animatorSet.playSequentially(animatorArray);
        animatorSet.start();
    }

    /**
     *
     * @return
     */
    public boolean isCollapsed() {
        return state == PlaceFilterEnum.COLLAPSED;
    }

    /**
     *
     * @return
     */
    public boolean isEdit() {
        return state == PlaceFilterEnum.EDIT;
    }

    public void setEditMode() {
        state = PlaceFilterEnum.EDIT;
    }

    /**
     *
     * @return
     */
    public boolean isLoadingEdit() {
        return state == PlaceFilterEnum.LOADING_EDIT;
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
