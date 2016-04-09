package com.application.material.takeacoffee.app.application;
import android.app.Application;
import com.application.material.takeacoffee.app.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by davide on 25/12/14.
 */
public class CoffeePlacesApplication extends Application {
    private static final String FONT_PATH = "fonts/chimphand-regular.ttf";
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig
                .Builder()
                .setDefaultFontPath(FONT_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
