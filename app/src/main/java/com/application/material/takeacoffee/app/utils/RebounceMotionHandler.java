package com.application.material.takeacoffee.app.utils;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 01/04/16.
 */
public class RebounceMotionHandler implements SpringListener {
    //TODO move out
    private final static double TENSION = 800;
    private final static double DAMPER = 20; //friction
    private float FINAL_TRANSLATION_Y = 200f;
    private static WeakReference<OnSpringMotionHandler> listener;
    private boolean movedUp = false;
    private float initialY;
    private Spring spring;
    private static RebounceMotionHandler instance;

    /**
     * constructor
     */
    private RebounceMotionHandler() {
        init();
    }

    /**
     *
     * @param onMotionCallbackWeakReference
     * @return
     */
    public static RebounceMotionHandler getInstance(WeakReference<OnSpringMotionHandler>
                                                            onMotionCallbackWeakReference) {
        listener = onMotionCallbackWeakReference;
        return instance == null ?
                instance = new RebounceMotionHandler() : instance;
    }

    /**
     *
     * @param value
     */
    public void setFinalTranslationY(float value) {
        this.FINAL_TRANSLATION_Y = value;
    }

    /**
     * handle motion updating spring value
     */
    public void translateViewOnY(float viewY) {
        //TODO refactor
        initialY = movedUp ? initialY : viewY;
        spring.setEndValue(initialY + (movedUp ? 0 : -FINAL_TRANSLATION_Y));
        movedUp = !movedUp;
    }

    /**
     * init spring sistem
     */
    private void init() {
        spring = SpringSystem.create()
                .createSpring()
                .setSpringConfig(new SpringConfig(TENSION, DAMPER))
                .addListener(this);
    }

    @Override
    public void onSpringUpdate(Spring spring) {
        listener.get().handleSpringUpdate((float) spring.getCurrentValue());
    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }

    public boolean isDown() {
        return movedUp;
    }

    public boolean isMovedUp() {
        return movedUp;
    }

    /**
     * onMotionCallback interface
     */
    public interface OnSpringMotionHandler {
        void handleSpringUpdate(float value);
    }
}
