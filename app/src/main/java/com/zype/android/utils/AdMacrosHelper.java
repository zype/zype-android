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

public class AdMacrosHelper {
    private static final String APP_BUNDLE = "[app_bundle]";
    private static final String APP_DOMAIN = "[app_domain]";
    private static final String APP_ID = "[app_id]";
    private static final String APP_NAME = "[app_name]";
    private static final String DEVICE_TYPE = "[device_type]";
    private static final String DEVICE_IFA = "[device_ifa]";
    private static final String DEVICE_MAKE = "[device_make]";
    private static final String DEVICE_MODEL = "[device_model]";
    private static final String UUID = "[uuid]";
    private static final String VPI = "[vpi]";

    public static String updateAdTagParameters(Context context, String tag) {
        Map<String, String> values = getValues(context.getApplicationContext());
        String result = tag;
        for (String key : values.keySet()) {
            String value = values.get(key);
            if (value != null) {
                if (result.contains(key)) {
                    result = result.replace(key, Uri.encode(value));
                }
            }
        }
        return result;
    }

    /**
     * Prepare SpotX parameter values
     *
     * @param context
     * @return
     */
    private static Map<String, String> getValues(Context context) {
        Map<String, String> result = new HashMap<>();
        ApplicationInfo appInfo = context.getApplicationContext().getApplicationInfo();
        // App data
//        result.put(APP_BUNDLE, appInfo.packageName);
        result.put(APP_BUNDLE, "com.zype.thisoldhouse");
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
        result.put(DEVICE_TYPE, "7");
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

    public interface IDeviceIdListener {
        void onDeviceId(String deviceId);
    }

    public static void fetchDeviceId(final Context context, final IDeviceIdListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("getDeviceId(): 'listener' must be not null");
        }
        // If we have device id stored in preferences already, just return it
        String deviceId = SettingsProvider.getInstance().getString(SettingsProvider.GOOGLE_ADVERTISING_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            listener.onDeviceId(deviceId);
        }
        else {
            // Try to retrieve Google advertising id as device id
            GoogleAdvertisingIdTask task = new GoogleAdvertisingIdTask(context,
                    new GoogleAdvertisingIdTask.IGoogleAdvertisingIdListener() {
                        @Override
                        public void onSuccess(String advertId) {
                            // Save retrieved advertising id in preferences and return it
                            if (!TextUtils.isEmpty(advertId)) {
                                SettingsProvider.getInstance().setString(SettingsProvider.GOOGLE_ADVERTISING_ID, advertId);
                                listener.onDeviceId(advertId);
                            }
                            // Generate UUID as device id
                            else {
                                listener.onDeviceId(createDeviceIdAsUUID());
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            listener.onDeviceId(createDeviceIdAsUUID());
                        }
                    });
            task.execute();
        }

    }

    private static String createDeviceIdAsUUID() {
        return java.util.UUID.randomUUID().toString();
    }

//    /**
//     * Retrieve Advertising Id provided by Google Play Services and store it in app preferences
//     * Call this method before {@link #getValues(Context)}
//     *
//     * @param context
//     */
//    public static void fetchGoogleAdvertisingId(final Context context, final IDeviceIdListener listener) {
//        GoogleAdvertisingIdTask task = new GoogleAdvertisingIdTask(context)
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                AdvertisingIdClient.Info idInfo = null;
//                try {
//                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
//                }
//                catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                    if (listener != null) {
//                        listener.onError(e);
//                    }
//                }
//                catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                    if (listener != null) {
//                        listener.onError(e);
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                    if (listener != null) {
//                        listener.onError(e);
//                    }
//                }
//                String advertId = null;
//                try{
//                    advertId = idInfo.getId();
//                }
//                catch (NullPointerException e){
//                    e.printStackTrace();
//                    if (listener != null) {
//                        listener.onError(e);
//                    }
//                }
//                return advertId;
//            }
//
//            @Override
//            protected void onPostExecute(String advertId) {
//                SettingsProvider.getInstance().setString(SettingsProvider.GOOGLE_ADVERTISING_ID, advertId);
//                if (listener != null) {
//                    listener.onDeviceId(advertId);
//                }
//            }
//
//        }.execute();
//    }

    /**
     * Retrieve Advertising Id provided by Google Play Services
     */
    private static class GoogleAdvertisingIdTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private IGoogleAdvertisingIdListener listener;

        public interface IGoogleAdvertisingIdListener {
            void onSuccess(String advertId);
            void onError(Exception e);
        }

        public GoogleAdvertisingIdTask(Context context, IGoogleAdvertisingIdListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... params) {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
            }
            catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
                listener.onError(e);
            }
            catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                listener.onError(e);
            }
            catch (IOException e) {
                e.printStackTrace();
                listener.onError(e);
            }
            String advertId = null;
            try{
                advertId = idInfo.getId();
            }
            catch (NullPointerException e){
                e.printStackTrace();
                listener.onError(e);
            }
            return advertId;
        }

        @Override
        protected void onPostExecute(String advertId) {
            listener.onSuccess(advertId);
        }
    }
}
