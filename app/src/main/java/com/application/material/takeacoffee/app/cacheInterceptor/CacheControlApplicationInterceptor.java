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
        String headerValue = ConnectivityUtils.checkConnectivity(contextWeakRef) ?
                "no-cache" :
                "public, max-stale=2419200";

//            String cacheControl = originalResponse.header("Cache-Control");
//            if (cacheControl == null ||
//                    cacheControl.contains("no-store") ||
//                    cacheControl.contains("no-cache") ||
//                    cacheControl.contains("must-revalidate") ||
//                    cacheControl.contains("max-age=0")) {
//
//                Log.e("TAG", originalResponse.headers().toString() + " - " + originalResponse.code());
        Response newResponse = originalResponse.newBuilder()
                .header("Cache-Control", headerValue)//"public, max-age=" + 5000)
                .build();

//                Log.e("TAG", newResponse.headers().toString() + " - " + newResponse.code());
        return newResponse;
//            }

//            Log.e("TAG-original", originalResponse.headers().toString() + " - " + originalResponse.code());
//            return originalResponse;



//            Request request = chain.request();
//            String headerValue = ConnectivityUtils.checkConnectivity(contextWeakRef) ?
//                    "only-if-cached" :
//                    "public, max-stale=2419200";
//
//            Request cachedRequest = request.newBuilder()
//                    .header("Cache-Control", headerValue)
//                    .build();
//            Log.e("TAG", cachedRequest.headers().toString() + " - " + cachedRequest.url().toString());
//            Response response = chain.proceed(cachedRequest);
//            Log.e("TAG", response.headers().toString() + " - " + response.code());
////            Log.e("TAG", oldResponse.headers().toString() + " - " + oldResponse.code());
//
//            return response;
    }
}

