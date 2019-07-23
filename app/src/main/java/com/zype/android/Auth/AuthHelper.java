package com.zype.android.Auth;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

import java.util.Date;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class AuthHelper {

    public static void onLoggedIn(Observer<Boolean> observer) {
        AuthLiveData.getInstance().observeForever(observer);
        AuthLiveData.getInstance().updateLoginState();
    }

    public static void onLoginStateChanged() {
        AuthLiveData.getInstance().updateLoginState();
    }

    public static boolean isLoggedIn() {
        return SettingsProvider.getInstance().isLoggedIn();
    }

    public static String getAccessToken() {
        return SettingsProvider.getInstance().getAccessToken();
    }

    public static boolean isAccessTokenExpired() {
        long currentTimeInSeconds = new Date().getTime() / 1000L;
        long expirationDateInSeconds = SettingsProvider.getInstance().getAccessTokenExpirationDate();
        long acceptableBuffer = 60; // 1 minute
        long interval = expirationDateInSeconds - currentTimeInSeconds;

        if (interval < acceptableBuffer) {
            return true;
        }

        return false;
    }

    public static boolean isVideoRequiredAuthorization(Context context, String videoId) {
        boolean result = false;

        Video video = DataRepository.getInstance((Application) context.getApplicationContext()).getVideoSync(videoId);
        if (video == null) {
            return false;
        }
        if (Integer.valueOf(video.purchaseRequired) == 1) {
            return true;
        }
        if (Integer.valueOf(video.subscriptionRequired) == 1) {
            return true;
        }

        return result;
    }

    public static boolean isRegistrationRequired(Context context, String videoId) {
        VideoData videoData = VideoHelper.getFullData(context.getContentResolver(), videoId);

        if (videoData != null) {
            if (videoData.isRegistrationRequired()) {
                if (!SettingsProvider.getInstance().isLoggedIn()) {
                    return true;
                }
            }
        }

        return false;

    }

    public static boolean isVideoAuthorized(Context context, String videoId) {
        boolean result = true;

        Video video = DataRepository.getInstance((Application) context.getApplicationContext()).getVideoSync(videoId);
        if (video == null) {
            return false;
        }
        if (video.registrationRequired == 1) {
            if (!isLoggedIn()) {
                return false;
            }
        }
        if (Integer.valueOf(video.purchaseRequired) == 1) {
            if (ZypeConfiguration.isUniversalTVODEnabled(context)) {
                if (video.isEntitled != null && Integer.valueOf(video.isEntitled) == 1) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                // Video requires purchase, but UTVOD options is turned off in the app configuration
                Logger.w("Video " + videoId + " requires purchase, but universal TVOD feature " +
                        "is turned off in the app configuration.");
                result = false;
            }
        }
        if (Integer.valueOf(video.subscriptionRequired) == 1) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(context)) {
                if (SubscriptionHelper.hasSubscription()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(context)) {
                if (SubscriptionHelper.hasSubscription()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (ZypeConfiguration.isUniversalSubscriptionEnabled(context)) {
                if (isLoggedIn()) {
                    if (SubscriptionHelper.hasSubscription()) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            else {
                // Video requires subscription, but NSVOD and USVOD options are turned off
                // in the app configuration
                Logger.w("Video " + videoId + " requires subscription, but subscription features " +
                        "are turned off the app configuration.");
                result = false;
            }
        }

        return result;

    }
}
