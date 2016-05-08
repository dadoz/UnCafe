package com.application.material.takeacoffee.app.presenters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.animator.AnimatorBuilder;
import com.application.material.takeacoffee.app.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 02/05/16.
 */
public class LocationAutocompletePresenter implements AdapterView.OnItemClickListener,
        TextWatcher {
    private static AutoCompleteTextView autoCompleteTextView;
    private static TextInputLayout locationTextInputLayout;
    private static WeakReference<Context> contextWeakRefer;
    private static LocationAutocompletePresenter instance;
    private static String[] countries;
    private static WeakReference<PickLocationInterface> pickLocationListener;
    private static View locationDoneButton;
    private static View errorPickIcon;
    private static View successPickIcon;
    private static AnimatorBuilder animatorBuilder;
    private static View locationPickIcon;
    private static View pickLocationProgress;
    private static View findPositionButton;
    private static View locationSelectedTextview;
    private static final long MIN_DELAY = 500;
    private static final int MIN_OFFSET = 1000;
    private static View locationDoneBorderLayout;

    public LocationAutocompletePresenter() {
    }

    /**
     *
     * @param ctx
     * @param textView
     * @param viewArray
     * @return
     */
    public static LocationAutocompletePresenter getInstance(WeakReference<Context> ctx,
                                                            WeakReference<PickLocationInterface> listener,
                                                            @NonNull AutoCompleteTextView textView,
                                                            @NonNull TextInputLayout textInputLayout,
                                                            @NonNull View[] viewArray) {
        contextWeakRefer = ctx;
        pickLocationListener = listener;
        autoCompleteTextView = textView;
        locationTextInputLayout = textInputLayout;
//        pickDescription = viewArray[0];
        errorPickIcon = viewArray[1];
        successPickIcon = viewArray[2];
        locationPickIcon = viewArray[3];
        locationDoneButton = viewArray[4];
        locationDoneBorderLayout = viewArray[5];
        pickLocationProgress = viewArray[6];
        findPositionButton = viewArray[7];
        locationSelectedTextview = viewArray[8];
//        pickView = viewArray[8];
        countries = contextWeakRefer.get().getResources().getStringArray(R.array.list_of_countries);

        animatorBuilder = AnimatorBuilder.getInstance(ctx);
        return instance == null ?
                instance = new LocationAutocompletePresenter() :
                instance;

    }
    /**
     * init
     */
    public void init() {
        int darkColor = ContextCompat.getColor(contextWeakRefer.get(), R.color.material_brown900);
        autoCompleteTextView.setAdapter(getAutocompleteLocationAdapter());
        autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        autoCompleteTextView
                .setDropDownBackgroundDrawable(new ColorDrawable(darkColor));
        locationTextInputLayout.setY(MIN_OFFSET);
        findPositionButton.getBackground()
                .setColorFilter(darkColor, PorterDuff.Mode.MULTIPLY);
        animateTranslateUpView(locationTextInputLayout, true, true);
    }

    /**
     * TODO refactor
     * @param translateUp
     * @param hasDelay
     */
    private AnimatorSet animateTranslateUpViews(View[] viewArray, boolean translateUp, boolean hasDelay) {
        if (viewArray.length < 2) {
            return null;
        }
        int initY = translateUp ? MIN_OFFSET : 0;
        int finalY = translateUp ? 0 : MIN_OFFSET;
        Animator animator = animatorBuilder.buildTranslationAnimator(viewArray[0], initY, finalY);
        Animator animator2 = animatorBuilder.buildTranslationAnimator(viewArray[1], initY, finalY);
        AnimatorSet animatorSetTmp = new AnimatorSet();
        if (translateUp) {
            animatorSetTmp.play(animator).after(animator2);
        } else {
            animatorSetTmp.play(animator2).after(animator);
        }
        animator.setStartDelay(hasDelay ? MIN_DELAY : 0);
        animatorSetTmp.start();
        return animatorSetTmp;
    }

    /**
     *
     * @param translateUp
     * @param hasDelay
     */
    private AnimatorSet animateTranslateUpView(View view, boolean translateUp, boolean hasDelay) {
        int initY = translateUp ? MIN_OFFSET : 0;
        int finalY = translateUp ? 0 : MIN_OFFSET;
        Animator animator = animatorBuilder.buildTranslationAnimator(view, initY, finalY);
        AnimatorSet animatorSetTmp = new AnimatorSet();
        animatorSetTmp.play(animator);
        animator.setStartDelay(hasDelay ? MIN_DELAY : 0);
        animatorSetTmp.start();
        return animatorSetTmp;
    }

    /**
     * ANIMATE 
     */
    private void animateTransactFromViewToView(View viewFrom, View viewTo) {
        Animator animator = animatorBuilder.buildAlphaAnimator(viewFrom, 1, 0);
        Animator animator2 = animatorBuilder.buildAlphaAnimator(viewTo, 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator, animator2);
        animatorSet.start();
    }

    /**
     *
     * @return
     */
    private ArrayAdapter<String> getAutocompleteLocationAdapter() {
        return new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_list_item_1, countries);
    }

    /**
     *
     * @param showError
     */
    private void setErrorOnAutcompleteTextview(boolean showError) {
        locationTextInputLayout.setErrorEnabled(showError);
        locationTextInputLayout.setError(showError ? Utils.wrapInCustomfont("Cannot find this city! Contact us",
                contextWeakRefer) : null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pickLocationListener.get().pickLocationSuccess(parent.getItemAtPosition(position) + " Italia");
        findPositionButton.setVisibility(View.VISIBLE);
        updateUIOnFilledLocation();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        findPositionButton.setVisibility(View.GONE);
        if (s.length() != 0) {
            updateUIOnFilledLocation();
            return;
        }
        updateUIOnEmptyLocation();
    }

    /*******INTERFACE callback ***/
    /**
     * to interface
     */
    public void updateUIOnEmptyLocation() {
        setErrorOnAutcompleteTextview(false);
    }

    /**
     * to interface
     */
    public void updateUIOnFilledLocation() {
        if (errorPickIcon.getAlpha() != 0) {
            animateTransactFromViewToView(errorPickIcon, locationPickIcon);
            setErrorOnAutcompleteTextview(false);
        }
    }

    /**
     * to interface
     */
    public void updateUIOnFindPosition() {
        AnimatorSet animatorSet = animateTranslateUpViews(new View[]{findPositionButton,
                locationTextInputLayout}, false, false);
        setAnimatorListener(animatorSet);
    }

    /**
     * TODO customize
     * @param animatorSet
     */
    private void setAnimatorListener(AnimatorSet animatorSet) {
        if (animatorSet == null) {
            return;
        }

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pickLocationListener.get().onUpdateUIOnFindPositionCallback();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * to interface
     */
    public void updateUIOnFindPositionError() {
        pickLocationProgress.setVisibility(View.GONE);
        animateTransactFromViewToView(locationPickIcon, errorPickIcon);
        setErrorOnAutcompleteTextview(true);
        animateTranslateUpViews(new View[] {findPositionButton, locationTextInputLayout}, true, false);
    }

    /**
     * to interface
     */
    public void updateUIOnFindPositionSuccess(String location) {
        pickLocationProgress.setVisibility(View.GONE);
        View view = errorPickIcon.getAlpha() == 0 ? locationPickIcon : errorPickIcon;
        animateTransactFromViewToView(view, successPickIcon);
        setDoneButtonVisible(true);
        setLocationSelected(location);
    }

    /**
     *
     */
    private void setLocationSelected(String location) {
        locationSelectedTextview.setVisibility(View.VISIBLE);
        location = location.replace("Italia", "");
        ((TextView) locationSelectedTextview).setText(location);
    }

    /**
     *
     * @param visible
     */
    public void setDoneButtonVisible(boolean visible) {
        locationDoneButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        locationDoneBorderLayout.setVisibility(visible? View.VISIBLE : View.GONE);
    }

    /**
     *
     */
    public interface PickLocationInterface {
        void pickLocationSuccess(String latLng);
        void onUpdateUIOnFindPositionCallback();
    }
}
