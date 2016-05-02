package com.application.material.takeacoffee.app.presenters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.singletons.GeocoderManager;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 02/05/16.
 */
public class LocationAutocompletePresenter implements AdapterView.OnItemClickListener, GeocoderManager.OnHandleGeocoderResult, TextWatcher {
    private static AutoCompleteTextView autoCompleteTextView;
    private static WeakReference<Context> contextWeakRefer;
    private static LocationAutocompletePresenter instance;
    private static String[] countries;
    private static Button doneButton;
    private LatLng selectLatLng;

    public LocationAutocompletePresenter() {
    }

    /**
     *
     * @param textView
     * @param ctx
     * @return
     */
    public static LocationAutocompletePresenter getInstance(WeakReference<Context> ctx,
                                                            AutoCompleteTextView textView,
                                                            Button btn) {
        autoCompleteTextView = textView;
        contextWeakRefer = ctx;
        doneButton = btn;
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
        Log.e("TAG", "trying to get position from place");
        GeocoderManager geocoder = GeocoderManager.getInstance(new WeakReference<GeocoderManager.OnHandleGeocoderResult>(this),
                contextWeakRefer);
        geocoder.getLatLongByLocationName(countries[position]);
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


    @Override
    public void onGeocoderSuccess(LatLng latLng) {
        Log.e("LAT LONG", latLng.toString());
        selectLatLng = latLng;
        //notify user location has been found
        //enable continue button
    }

    @Override
    public void onGeocoderErrorResult() {
        //notify error on found location
    }

    /**
     *
     * @return
     */
    public LatLng getSelectedLocation() {
        return selectLatLng;
    }

    /**
     *
     */
    public void setEmptyLocation() {
        //show find current location button
        selectLatLng = null;
        doneButton.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    public void setFilledLocation() {
        //hide find current location button
        doneButton.setVisibility(View.GONE);
    }
}
