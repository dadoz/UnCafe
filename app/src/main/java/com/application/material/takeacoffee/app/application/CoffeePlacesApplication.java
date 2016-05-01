package com.application.material.takeacoffee.app.application;
import android.app.Application;
import android.net.http.HttpResponseCache;

import com.application.material.takeacoffee.app.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by davide on 25/12/14.
 */
public class CoffeePlacesApplication extends Application {
    public static final String FONT_PATH = "fonts/chimphand-regular.ttf";
    public static final String FONT_BOLD_PATH = "fonts/chimphand-bold.ttf";
    private File httpCacheDir;
    long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
    private Cache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        initCalligraph();
        initCacheFile();
    }

    /**
     *
     */
    private void initCalligraph() {
        CalligraphyConfig.initDefault(new CalligraphyConfig
                .Builder()
                .setDefaultFontPath(FONT_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    /**
     *
     */
    private void initCacheFile() {
        try {
            httpCacheDir = new File(getCacheDir(), "http");
            httpCacheDir.setReadable(true);
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
            cache = new Cache(httpCacheDir, httpCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public Cache getCache() {
        return cache;
    }

    /**
     *
     * @return
     */
    public boolean isCacheValid() {
        try {
            return cache.size() != 0 &&
                    cache.urls().hasNext();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
