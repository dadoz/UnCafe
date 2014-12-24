package com.application.material.takeacoffee.app.restServices.bus;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;

/**
 * Created by davide on 24/12/14.
 */
public class BusWrapper extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}
