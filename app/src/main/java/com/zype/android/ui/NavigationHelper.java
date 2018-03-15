package com.zype.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
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
        if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                switchToSubscriptionScreen(activity);
            }
            else {
                VideoDetailActivity.startActivity(activity, videoId);
            }
        }
        else {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                boolean liveStreamLimitExceeded = false;
                if (onAir) {
                    // Check total played live stream time
                    Logger.d(String.format("onItemClick(): liveStreamLimit=%1$s", SettingsProvider.getInstance().getLiveStreamLimit()));
                    int liveStreamTime = SettingsProvider.getInstance().getLiveStreamTime();
                    if (liveStreamTime >= SettingsProvider.getInstance().getLiveStreamLimit()
                            && SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                        liveStreamLimitExceeded = true;
                    }
                }
                if (SettingsProvider.getInstance().getSubscriptionCount() <= 0 || liveStreamLimitExceeded) {
                    if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                        switchToSubscriptionScreen(activity);
                    }
                    else {
                        DialogHelper.showSubscriptionAlertIssue(activity);
                    }
                }
                else {
                    VideoDetailActivity.startActivity(activity, videoId);
                }
            }
            else {
                if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                    switchToIntroScreen(activity);
                }
                else {
                    switchToLoginScreen(activity);
                }
            }
        }
    }

    public void switchToLoginScreen(Activity activity) {
        Intent intent = new Intent(context, LoginActivity.class);
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

}
