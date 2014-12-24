package com.application.material.takeacoffee.app.singletons;

import com.squareup.otto.Bus;
import com.application.material.takeacoffee.app.restServices.bus.*;

/**
 * Created by davide on 24/12/14.
 */
public class BusSingleton {
    private static Bus bus = new BusWrapper();

    public static Bus getInstance() {
        return bus;
    }

    private BusSingleton() {

    }

}
