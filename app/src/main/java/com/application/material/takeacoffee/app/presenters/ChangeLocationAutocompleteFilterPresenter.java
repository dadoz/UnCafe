package com.application.material.takeacoffee.app.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.application.material.takeacoffee.app.models.City;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by davide on 23/06/16.
 */
public class ChangeLocationAutocompleteFilterPresenter implements TextWatcher, AdapterView.OnItemClickListener {

    private static WeakReference<Context> contextWeakRefer;
    private static ChangeLocationAutocompleteFilterPresenter instance;
    private static AutoCompleteTextView autoCompleteTextView;
    private static TextInputLayout autoCompleteTextInputView;
    private PlaceApiManager placeApiManager;
    private static WeakReference<PlaceApiManager.OnHandlePlaceApiResult> listener;
    private boolean dropdownForceToBeShown;

    public static ChangeLocationAutocompleteFilterPresenter getInstance(WeakReference<Context> ctx,
                                                                        WeakReference<PlaceApiManager.OnHandlePlaceApiResult> lst,
                                                                        @NonNull View[] viewArray) {
        contextWeakRefer = ctx;
        listener = lst;
        autoCompleteTextView = (AutoCompleteTextView) viewArray[0];
        autoCompleteTextInputView = (TextInputLayout) viewArray[1];
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
        autoCompleteTextView.setOnItemClickListener(this);
    }

    /**
     *
     * @param list
     * @param type
     */
    public void onCitiesRetrieveSuccess(Object list, PlaceApiManager.RequestType type) {
        ArrayList<City> parsedList = (ArrayList<City>) list;
//        Log.e("TAG", "tag - " + parsedList.get(0).getDescription() + parsedList.get(0).getTypes().toString());
//        Log.e("TAG", "size - " + parsedList.size());
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(contextWeakRefer.get(),
                android.R.layout.simple_list_item_1, City.getArrayFromList(parsedList)));
        if (!autoCompleteTextView.isPopupShowing() &&
                dropdownForceToBeShown) {
            autoCompleteTextView.showDropDown();
        }
    }

    /**
     *
     * @param type
     */
    public void onCitiesRetrieveError(PlaceApiManager.RequestType type) {
        //TODO implement it
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        autoCompleteTextInputView.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
//        Log.e("TAG CHANGE", s.toString());
        dropdownForceToBeShown = s.length() == 2;
        if (s.length() > 1) {
            setAutocompleteLocationAdapterAsync(s.toString());
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}