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
public class ChangeLocationAutocompleteFilterPresenter implements TextWatcher {

    private static WeakReference<Context> contextWeakRefer;
    private static ChangeLocationAutocompleteFilterPresenter instance;
    private static AutoCompleteTextView autoCompleteTextView;
    private PlaceApiManager placeApiManager;
    private static WeakReference<PlaceApiManager.OnHandlePlaceApiResult> listener;

    public static ChangeLocationAutocompleteFilterPresenter getInstance(WeakReference<Context> ctx,
                                                                        WeakReference<PlaceApiManager.OnHandlePlaceApiResult> lst,
                                                                        @NonNull AutoCompleteTextView autocompleteTv) {
        contextWeakRefer = ctx;
        listener = lst;
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
        placeApiManager = PlaceApiManager.getInstance(listener, contextWeakRefer);
        autoCompleteTextView.addTextChangedListener(this);
    }

    /**
     *
     * @param list
     * @param type
     */
    public void onCitiesRetrieveSuccess(Object list, PlaceApiManager.RequestType type) {
        ArrayList<City> parsedList = (ArrayList<City>) list;
        Log.e("TAG", "tag - " + parsedList.get(0).getDescription() + parsedList.get(0).getTypes().toString());
        Log.e("TAG", "size - " + parsedList.size());
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_dropdown_item_1line, City.getArrayFromList(parsedList)));
    }

    /**
     *
     * @param type
     */
    public void onCitiesRetrieveError(PlaceApiManager.RequestType type) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        autoCompleteTextView.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() != 0) {
            setAutocompleteLocationAdapterAsync(s.toString());
        }
    }


}