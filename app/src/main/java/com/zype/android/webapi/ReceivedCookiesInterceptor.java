package com.zype.android.webapi;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import com.zype.android.core.settings.SettingsProvider;

import java.io.IOException;
import java.util.HashSet;

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 * <p/>
 * Created by tsuharesu on 4/1/15. https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SettingsProvider.getInstance().saveCookies(cookies);
        }

        return originalResponse;
    }
}