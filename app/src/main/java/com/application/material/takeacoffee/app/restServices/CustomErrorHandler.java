package com.application.material.takeacoffee.app.restServices;

import android.util.Log;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.singletons.BusSingleton;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Created by davide on 24/12/14.
 */
public class CustomErrorHandler implements ErrorHandler {
    private String TAG = "CustomErrorHandler ";

//    @Override
//    public Throwable handleError(RetrofitE cause) {
//        //get code from cause and return what you need
//        Log.e(TAG, "retrofit request error" + cause.toString());
//        if(cause.getResponse() == null) {
//            Throwable error = new Throwable("generic error - handleError",
//                    new Throwable("500"));
//
//            BusSingleton.getInstance().post(error);
//            return error;
//        }
//
//        Throwable error = new Throwable(cause.getResponse().getReason(),
//                new Throwable("" + cause.getResponse().getStatus()));
//        BusSingleton.getInstance().post(error);
//        return error;
//    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {

    }

    @Override
    public void error(SAXParseException exception) throws SAXException {

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {

    }
}
