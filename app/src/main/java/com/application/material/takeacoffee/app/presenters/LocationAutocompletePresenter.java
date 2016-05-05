package com.application.material.takeacoffee.app.presenters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;


/**
 * Created by davide on 02/05/16.
 */
public class LocationAutocompletePresenter implements AdapterView.OnItemClickListener,
        TextWatcher {
    private static AutoCompleteTextView autoCompleteTextView;
    private static WeakReference<Context> contextWeakRefer;
    private static LocationAutocompletePresenter instance;
    private static String[] countries;
    private static WeakReference<PickLocationInterface> pickLocationListener;
    private static View locationDoneButton;
    private static View pickDescription;
    private static View errorPickIcon;
    private static View successPickIcon;
    private static AnimatorBuilder animatorBuilder;

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
                                                            @NonNull AutoCompleteTextView textView, @NonNull View[] viewArray) {
        contextWeakRefer = ctx;
        pickLocationListener = listener;
        autoCompleteTextView = textView;
        pickDescription = viewArray[0];
        errorPickIcon = viewArray[1];
        successPickIcon = viewArray[2];
        locationDoneButton = viewArray[3];
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
        autoCompleteTextView.setAdapter(getAutocompleteLocationAdapter());
        autoCompleteTextView.setOnItemClickListener(this);
        autoCompleteTextView.addTextChangedListener(this);

        Animator animator = animatorBuilder.buildTranslationAnimator(autoCompleteTextView, 0, 300);
        Animator animator2 = animatorBuilder.buildTranslationAnimator(pickDescription, 0, 300);
        new AnimatorSet().playSequentially(animator, animator2);
    }

    /**
     *
     * @return
     */
    private ArrayAdapter<String> getAutocompleteLocationAdapter() {
        return new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_list_item_1, countries);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("TAG", "get Place -> " + parent.getItemAtPosition(position));
        pickLocationListener.get().pickLocationSuccess(parent.getItemAtPosition(position) + " Italia");

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            setEmptyLocation();
            return;
        }
        setFilledLocation();
    }

    /**
     *
     */
    public void setEmptyLocation() {
        //show find current location button
        //TODO animate
        locationDoneButton.setVisibility(View.GONE);
    }

    /**
     *
     */
    public void setFilledLocation() {
        //hide find current location button
        //TODO animate
        locationDoneButton.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    static class BuilderAnimator {
        public Animator getTranslationAnimator() {
            return new ObjectAnimator();
        }
    }

    /**
     *
     */
    public interface PickLocationInterface {
        void pickLocationSuccess(String latLng);
        void pickLocationError();
    }
}
