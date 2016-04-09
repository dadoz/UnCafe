package com.application.material.takeacoffee.app.singletons;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by davide on 24/12/14.
 */
public class EventBusSingleton {
    private static EventBus bus = new EventBus();

    private EventBusSingleton() {
    }

    /**
     *
     * @return
     */
    public static EventBus getInstance() {
        return bus;
    }
}
