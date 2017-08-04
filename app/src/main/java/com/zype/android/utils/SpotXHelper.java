package com.zype.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.zype.android.core.settings.SettingsProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 04.08.2017.
 */

public class SpotXHelper {
    private static final String APP_BUNDLE = "app[bundle]";
    private static final String APP_DOMAIN = "app[domain]";
    private static final String APP_ID = "app[id]";
    private static final String APP_NAME = "app[name]";
    private static final String DEVICE_DEVICE_TYPE = "device[devicetype]";
    private static final String DEVICE_IFA = "device[ifa]";
    private static final String DEVICE_MAKE = "device[make]";
    private static final String DEVICE_MODEL = "device[model]";
    private static final String UUID = "uuid";
    private static final String VPI = "VPI";

    private static final String REPLACE_VALUE = "REPLACE_ME";

    public static String addSpotXParameters(Context context, String tag) {
        Map<String, String> spotXParameters = getSpotXParameters(context.getApplicationContext());
        Map<String, String> queryParameters;
        try {
            queryParameters = getUrlQueryParameters(tag);
        }
        catch (MalformedURLException e) {
            queryParameters = new HashMap<>();
            e.printStackTrace();
        }
        final Uri uri = Uri.parse(tag);
        final Uri.Builder resultUri = uri.buildUpon().clearQuery();
        for (String paramName : queryParameters.keySet()) {
            String value = queryParameters.get(paramName);
            if ((value == null || value.equals(REPLACE_VALUE))
                    && spotXParameters.containsKey(paramName)) {
                value = Uri.encode(spotXParameters.get(paramName));
            }
            resultUri.appendQueryParameter(paramName, value);
        }
        return resultUri.build().toString();
    }

    /**
     * Prepare SpotX parameter values
     *
     * @param context
     * @return
     */
    private static Map<String, String> getSpotXParameters(Context context) {
        Map<String, String> result = new HashMap<>();
        ApplicationInfo appInfo = context.getApplicationContext().getApplicationInfo();
        // App data
        result.put(APP_BUNDLE, appInfo.packageName);
        result.put(APP_DOMAIN, appInfo.packageName);
        result.put(APP_ID, appInfo.packageName);
        result.put(APP_NAME, (appInfo.labelRes == 0) ? appInfo.nonLocalizedLabel.toString() : context.getString(appInfo.labelRes));
        // Advertizing ID
        String advertisingId = SettingsProvider.getInstance().getString(SettingsProvider.GOOGLE_ADVERTISING_ID);
        result.put(DEVICE_IFA, advertisingId);
        // Device data
        result.put(DEVICE_MAKE, Build.MANUFACTURER);
        result.put(DEVICE_MODEL, Build.MODEL);
        // Default device type is '7' (set top box device)
        // TODO: What is the code for mobile?
        result.put(DEVICE_DEVICE_TYPE, "7");
        // Default VPI is 'MP4'
        result.put(VPI, "MP4");
        // UUID us the same as Advertising id
        result.put(UUID, advertisingId);
        return result;
    }

    private static Map<String, String> getUrlQueryParameters(String urlString) throws MalformedURLException {
        Map<String, String> queryParams = new HashMap<>();
        URL url = new URL(urlString);
        String query = url.getQuery();
        String[] strParams = query.split("&");

        for (String param : strParams) {
            String[] split = param.split("=");
            // Get the parameter name.
            if (split.length > 0) {
                String name = split[0];
                // Get the parameter value.
                if (split.length > 1) {
                    String value = split[1];
                    queryParams.put(name, value);
                }
                // If there is no value just put an empty string as placeholder.
                else {
                    queryParams.put(name, "");
                }
            }
        }
        return queryParams;
    }

    /**
     * Retrieve Advertising Id provided by Google Play Services and store it in app preferences
     * Call this method before {@link #getSpotXParameters(Context)}
     *
     * @param context
     */
    public static void fetchGoogleAdvertisingId(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
                }
                catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String advertId = null;
                try{
                    advertId = idInfo.getId();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
                SettingsProvider.getInstance().setString(SettingsProvider.GOOGLE_ADVERTISING_ID, advertId);
            }

        }.execute();
    }
}
