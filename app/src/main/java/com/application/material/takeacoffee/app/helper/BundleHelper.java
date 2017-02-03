package com.application.material.takeacoffee.app.helper;

import android.os.Bundle;

import com.application.material.takeacoffee.app.models.CoffeePlace;

/**
 * Created by davide on 01/02/2017.
 */

public class BundleHelper {

    /**
     *
     * @param place
     */
    public static Bundle createBundleByPlacePosition(CoffeePlace place) {
        Bundle bundle = new Bundle();
        bundle.putString(CoffeePlace.COFFEE_PLACE_ID_KEY, place.getId());
        bundle.putString(CoffeePlace.COFFEE_PLACE_NAME_KEY, place.getName());
        bundle.putString(CoffeePlace.COFFEE_PLACE_PHOTO_REFERENCE_KEY, place.getPhotoReference());
        bundle.putString(CoffeePlace.COFFEE_PLACE_LATLNG_REFERENCE_KEY,
                place.getGeometry().getLocation().getLat() + "," +
                        place.getGeometry().getLocation().getLng());
        return bundle;
    }
}
