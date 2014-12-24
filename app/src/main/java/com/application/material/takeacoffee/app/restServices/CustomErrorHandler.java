package com.application.material.takeacoffee.app.restServices;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by davide on 24/12/14.
 */
public class CustomErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        //get code from cause and return what you need
        return null;
    }
}
