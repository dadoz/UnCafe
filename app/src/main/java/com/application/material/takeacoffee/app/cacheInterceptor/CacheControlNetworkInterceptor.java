package com.application.material.takeacoffee.app.cacheInterceptor;

import android.content.Context;
import com.application.material.takeacoffee.app.utils.ConnectivityUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * interceptor
 */
public class CacheControlNetworkInterceptor implements Interceptor {
    private final WeakReference<Context> contextWeakRef;

    public CacheControlNetworkInterceptor(WeakReference<Context> ctx) {
        this.contextWeakRef = ctx;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!ConnectivityUtils.isConnected(contextWeakRef)) {
            request = request.newBuilder()
                    .header("Cache-Control", "public, max-stale=2419200")
                    .build();
        }
        Response response = chain.proceed(request);
//            Log.e("TAG-network", response.headers().toString() + " - " + response.code());
        return response;

    }
}