package com.zype.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * @author vasya
 * @version 1
 *          date 7/10/15
 */
public class GoogleKeyUtils {
    private static final String META_DATA_NAME = "com.zype.android.TouTube.ApiKey";

    public static String getGoogleApiKey(Context context) {
        String googleApiKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            googleApiKey = bundle.getString(META_DATA_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return googleApiKey;
    }
}
