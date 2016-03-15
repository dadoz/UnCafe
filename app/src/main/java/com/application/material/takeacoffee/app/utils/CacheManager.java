package com.application.material.takeacoffee.app.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by davide on 15/03/16.
 */
public class CacheManager {
    private static CacheManager instance;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 8;
    private static LruCache<String, Bitmap> mMemoryCache;

    /**
     *
     * @return
     */
    public static CacheManager getInstance() {
        instance = instance == null ?
                instance = new CacheManager() :
                instance;
        if (mMemoryCache == null) {
            instance.initCache();
        }
        return instance;
    }

    /**
     * init cache
     */
    private void initCache() {
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
