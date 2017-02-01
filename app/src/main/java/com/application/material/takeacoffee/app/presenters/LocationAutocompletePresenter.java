package com.application.material.takeacoffee.app.presenters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.animator.AnimatorBuilder;
import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.utils.ConnectivityUtils;
import com.application.material.takeacoffee.app.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LocationAutocompletePresenter implements AdapterView.OnItemClickListener,
        TextWatcher, PlaceApiManager.OnHandlePlaceApiResult {
    private static AutoCompleteTextView autoCompleteTextView;
    private static TextInputLayout locationTextInputLayout;
    private static WeakReference<Context> contextWeakRefer;
    private static LocationAutocompletePresenter instance;
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
    private static PlaceApiManager placeApiManager;
    private boolean dropdownForceToBeShown;
    private static final int MIN_DEFAULT_HEIGHT = 2000; //TODO patch

    /**
     *
     */
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
        errorPickIcon = viewArray[1];
        successPickIcon = viewArray[2];
        locationPickIcon = viewArray[3];
        locationDoneButton = viewArray[4];
//        locationDoneBorderLayout = viewArray[5];
        pickLocationProgress = viewArray[6];
        findPositionButton = viewArray[7];
        locationSelectedTextview = viewArray[8];
        animatorBuilder = AnimatorBuilder.getInstance(ctx);
        return instance == null ?
                instance = new LocationAutocompletePresenter() :
                instance;

    }

    /**
     * init
     */
    public void init() {
        int minHeight = getMinViewHeight();
        //TODO due to context
        placeApiManager = PlaceApiManager.getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
                contextWeakRefer);

        int darkColor = ContextCompat.getColor(contextWeakRefer.get(), R.color.material_brown900);
        autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        autoCompleteTextView
                .setDropDownBackgroundDrawable(new ColorDrawable(darkColor));
        locationTextInputLayout.setY(minHeight);
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
        int minHeight = getMinViewHeight();
        int initY = translateUp ? minHeight : 0;
        int finalY = translateUp ? 0 : minHeight;
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
        int minHeight = getMinViewHeight();
        int initY = translateUp ? minHeight : 0;
        int finalY = translateUp ? 0 : minHeight ;
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
     * @param showError
     */
    private void setErrorOnAutcompleteTextview(boolean showError) {
        locationTextInputLayout.setErrorEnabled(showError);
        locationTextInputLayout.setError(showError ? Utils.wrapInCustomfont("Cannot find this city! Contact us",
                contextWeakRefer) : null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Utils.hideKeyboard(contextWeakRefer, autoCompleteTextView);
//        String selectedPlace = parent.getItemAtPosition(position).toString();
//        setLocationBySelectedPlace(selectedPlace);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        dropdownForceToBeShown = s.length() == 2;
        findPositionButton.setVisibility(View.GONE);
        if (s.length() != 0) {
            findPositionButton.setVisibility(View.VISIBLE);
            setAutocompleteLocationAdapterAsync(s.toString());
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
//        location = location.replace("Italia", "");
        ((TextView) locationSelectedTextview).setText(location);
    }

    /**
     *
     * @param visible
     */
    public void setDoneButtonVisible(boolean visible) {
        locationDoneButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     *
     */
    private void setAutocompleteLocationAdapterAsync(String find) {
        if (ConnectivityUtils.isConnected(contextWeakRefer)) {
            placeApiManager.retrieveCitiesAsync(find);
        }
    }

    /**
     *
     * @param place
     */
    private void setLocationBySelectedPlace(String place) {
        pickLocationListener.get().pickLocationSuccess(place);
        findPositionButton.setVisibility(View.VISIBLE);
        updateUIOnFilledLocation();
    }

    /**
     *
     */
    public void updatePositionNotSelected() {
        Utils.hideKeyboard(contextWeakRefer, autoCompleteTextView);
        String selectedPlace = autoCompleteTextView.getText().toString();
        setLocationBySelectedPlace(selectedPlace);
    }

    /**
     *
     * @return
     */
    private int getMinViewHeight() {
        int temp = ((Activity) contextWeakRefer.get())
                .getWindow().getDecorView().getRootView().getMeasuredHeight();
        return temp == 0 ? MIN_DEFAULT_HEIGHT : temp;
    }
    @Override
    public void onPlaceApiSuccess(Object list, PlaceApiManager.RequestType type) {
        ArrayList<City> parsedList = (ArrayList<City>) list;
//        Log.e("TAG", "tag - " + parsedList.get(0).getDescription() + parsedList.get(0).getTypes().toString());
//        Log.e("TAG", "size - " +  parsedList.size());
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_list_item_1,  City.getArrayFromList(parsedList)));
        if (!autoCompleteTextView.isPopupShowing() &&
                dropdownForceToBeShown) {
            autoCompleteTextView.showDropDown();
        }

    }

    @Override
    public void onPlaceApiEmptyResult() {
    }

    @Override
    public void onPlaceApiError(PlaceApiManager.RequestType type) {
    }

    /**
     *
     */
    public interface PickLocationInterface {
        void pickLocationSuccess(String latLng);
        void onUpdateUIOnFindPositionCallback();
    }
}
