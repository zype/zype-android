package com.zype.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Consumer.ConsumerActivity;
import com.zype.android.ui.Intro.IntroActivity;
import com.zype.android.ui.Subscription.SubscriptionActivity;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;

/**
 * Created by Evgeny Cherkasov on 11.07.2017.
 */

public class NavigationHelper {
    private static volatile NavigationHelper instance;

    private Context context;

    private NavigationHelper(Context context) {
        if (instance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.context = context.getApplicationContext();
    }

    public static NavigationHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (NavigationHelper.class) {
                if (instance == null) instance = new NavigationHelper(context);
            }
        }
        return instance;
    }

    public void checkSubscription(Activity activity, String videoId, boolean onAir) {
        if (ZypeSettings.NATIVE_SUBSCRIPTION_ENABLED) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                switchToSubscriptionScreen(activity);
            }
            else {
                switchToVideoDetailScreen(activity, videoId);
            }
        }
        else if (ZypeSettings.NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                if (SettingsProvider.getInstance().getSubscriptionCount() > 0
                        || !isLiveStreamLimitExceeded(onAir)) {
                    switchToVideoDetailScreen(activity, videoId);
                }
                else {
                    switchToSubscriptionScreen(activity);
                }
            }
            else {
                switchToSubscriptionScreen(activity);
            }
        }
        else {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                if (SettingsProvider.getInstance().getSubscriptionCount() > 0
                        || !isLiveStreamLimitExceeded(onAir)) {
                    switchToVideoDetailScreen(activity, videoId);
                }
                else {
                    DialogHelper.showSubscriptionAlertIssue(context);
                }
            }
            else {
                switchToLoginScreen(activity);
            }
        }
    }

    private boolean isLiveStreamLimitExceeded(boolean onAir) {
        boolean result = false;
        if (onAir) {
            int liveStreamTime = SettingsProvider.getInstance().getLiveStreamTime();

            Logger.d("isLiveStreamLimitExceeded(): liveStreamLimit=" + liveStreamTime);

            if (liveStreamTime >= SettingsProvider.getInstance().getLiveStreamLimit()
                    && SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                result = true;
            }
        }
        else {
            // For non live stream limit checking doesn't make sense, so just return true;
            result = true;
        }
        return result;
    }

    public void switchToConsumerScreen(Activity activity) {
        Intent intent = new Intent(activity, ConsumerActivity.class);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_CONSUMER);
    }

    public void switchToLoginScreen(Activity activity) {
        switchToLoginScreen(activity, null);
    }

    public void switchToLoginScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, BundleConstants.REQ_LOGIN);
    }

    public void switchToIntroScreen(Activity activity) {
        Intent intent = new Intent(activity, IntroActivity.class);
        activity.startActivity(intent);
    }

    public void switchToSubscriptionScreen(Activity activity) {
        Intent intent = new Intent(activity, SubscriptionActivity.class);
        activity.startActivity(intent);
    }

    public void switchToVideoDetailScreen(Activity activity, String videoId) {
        VideoDetailActivity.startActivity(activity, videoId);
    }

}
