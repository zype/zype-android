package com.zype.android.webapi;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zype.android.core.settings.SettingsProvider;

import java.io.IOException;
import java.util.HashSet;

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences MAY VARY.
 * <p/>
 * Created by tsuharesu on 4/1/15. https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b
 */
public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = SettingsProvider.getInstance().getCookies();
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v("OkHttp", "Adding Header: " + cookie);
        }

        return chain.proceed(builder.build());
    }
}
