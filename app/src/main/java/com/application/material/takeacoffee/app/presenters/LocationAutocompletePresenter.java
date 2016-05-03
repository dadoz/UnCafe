package com.application.material.takeacoffee.app.presenters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.application.material.takeacoffee.app.R;
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

    public LocationAutocompletePresenter() {
    }

    /**
     *
     * @param ctx
     * @param textView
     * @return
     */
    public static LocationAutocompletePresenter getInstance(WeakReference<Context> ctx,
                                                            WeakReference<PickLocationInterface> listener,
                                                            AutoCompleteTextView textView, View button) {
        autoCompleteTextView = textView;
        contextWeakRefer = ctx;
        pickLocationListener = listener;
        locationDoneButton = button;
        countries = contextWeakRefer.get().getResources().getStringArray(R.array.list_of_countries);
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


//    @Override
//    public void onGeocoderSuccess(LatLng latLng) {
//        //notify user location has been found
//        //enable continue button
////        pickLocationListener.get().pickLocationSuccess(latLng);
//    }
//
//    @Override
//    public void onGeocoderErrorResult() {
//        //notify error on found location
////        locationDoneButton.setVisibility(View.GONE);
////        pickLocationListener.get().pickLocationError();
//    }

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
    public interface PickLocationInterface {
        void pickLocationSuccess(String latLng);
        void pickLocationError();
    }
}
