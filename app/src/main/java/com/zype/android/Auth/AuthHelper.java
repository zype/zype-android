package com.zype.android.Auth;

import android.content.Context;

import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class AuthHelper {

    public static boolean isLoggedIn() {
        return SettingsProvider.getInstance().isLoggedIn();
    }

    public static boolean isVideoAuthorized(Context context, String videoId) {
        boolean result = true;

        VideoData videoData = VideoHelper.getFullData(context.getContentResolver(), videoId);
        if (videoData == null) {
            return false;
        }
        if (videoData.isSubscriptionRequired()) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(context)) {
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
                // Video is subscription required, but NSVOD and USVOD options are turned off
                // in the app configuration
                Logger.w("Video " + videoId + " is subscription required, but subscription features are turned off the app configuration.");
                result = false;
            }
        }

        return result;

    }
}
