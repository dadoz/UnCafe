package com.application.material.takeacoffee.app.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 23/06/16.
 */
public class ChangeLocationAutocompleteFilterPresenter implements PlaceApiManager.OnHandlePlaceApiResult, TextWatcher {

    private static WeakReference<Context> contextWeakRefer;
    private static ChangeLocationAutocompleteFilterPresenter instance;
    private static AutoCompleteTextView autoCompleteTextView;
    private PlaceApiManager placeApiManager;

    public static ChangeLocationAutocompleteFilterPresenter getInstance(WeakReference<Context> ctx,
                                                                        @NonNull AutoCompleteTextView autocompleteTv) {
        contextWeakRefer = ctx;
        autoCompleteTextView = autocompleteTv;
        return instance == null ?
                instance = new ChangeLocationAutocompleteFilterPresenter() :
                instance;

    }

    /**
     *
     */
    private void setAutocompleteLocationAdapterAsync(String find) {
        placeApiManager.retrieveCitiesAsync(find);
    }

    /**
     * init
     */
    public void init() {
        //TODO due to context
        placeApiManager = PlaceApiManager.getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
                contextWeakRefer);
        autoCompleteTextView.addTextChangedListener(this);
    }

    @Override
    public void onPlaceApiSuccess(Object list, PlaceApiManager.RequestType type) {
        ArrayList<City> parsedList = (ArrayList<City>) list;
        Log.e("TAG", "tag - " + parsedList.get(0).getDescription() + parsedList.get(0).getTypes().toString());
        Log.e("TAG", "size - " + parsedList.size());
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_list_item_1, City.getArrayFromList(parsedList)));

    }

    @Override
    public void onEmptyResult() {

    }

    @Override
    public void onErrorResult(PlaceApiManager.RequestType type) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                changePlaceTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() != 0) {
            setAutocompleteLocationAdapterAsync(s.toString());
        }
    }


}