package com.application.material.takeacoffee.app.cacheInterceptor;

import android.content.Context;
import com.application.material.takeacoffee.app.utils.ConnectivityUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * interceptor
 */
public class CacheControlApplicationInterceptor implements Interceptor {
    private final WeakReference<Context> contextWeakRef;

    public CacheControlApplicationInterceptor(WeakReference<Context> ctx) {
        this.contextWeakRef = ctx;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String headerValue = ConnectivityUtils.isConnected(contextWeakRef) ?
                "no-cache" :
                "public, max-stale=2419200";

        return originalResponse.newBuilder()
                .header("Cache-Control", headerValue)//"public, max-age=" + 5000)
                .build();
    }
}

