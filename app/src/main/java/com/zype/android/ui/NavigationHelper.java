package com.zype.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Intro.IntroActivity;
import com.zype.android.ui.Subscription.SubscriptionActivity;
import com.zype.android.ui.main.fragments.playlist.PlaylistActivity;
import com.zype.android.ui.main.fragments.videos.VideosActivity;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

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

    public void checkSubscription(Activity activity, String videoId, String playlistId, boolean onAir) {
        if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                switchToSubscriptionScreen(activity);
            }
            else {
                VideoDetailActivity.startActivity(activity, videoId, playlistId);
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
                    VideoDetailActivity.startActivity(activity, videoId, playlistId);
                }
            }
            else {
                switchToLoginScreen(activity);
            }
        }
    }

    public void switchToLoginScreen(Activity activity) {
        Intent intent = new Intent(context, LoginActivity.class);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
    }

    public void switchToIntroScreen(Activity activity) {
        Intent intent = new Intent(activity, IntroActivity.class);
        activity.startActivity(intent);
    }

    public void switchToSubscriptionScreen(Activity activity) {
        Intent intent = new Intent(activity, SubscriptionActivity.class);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_SUBSCRIPTION);
    }

    public void switchToVideoDetailsScreen(Activity activity, String videoId, String playlistId, boolean autoplay) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        bundle.putBoolean(VideoDetailActivity.EXTRA_AUTOPLAY, autoplay);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void switchToPlaylistScreen(Activity activity, String playlistId) {
        Intent intent = new Intent(activity, PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PARENT_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void switchToPlaylistVideosScreen(Activity activity, String playlistId) {
        Intent intent = new Intent(activity, VideosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PARENT_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    //
    public void handleNotAuthorizedVideo(Activity activity, String videoId) {
        VideoData videoData = VideoHelper.getFullData(activity.getContentResolver(), videoId);
        if (videoData == null) {
            Logger.e("Error get video data, videoId=" + videoId);
            return;
        }
        if (videoData.isSubscriptionRequired()) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                NavigationHelper.getInstance(activity).switchToSubscriptionScreen(activity);
            }
            else if (ZypeConfiguration.isUniversalSubscriptionEnabled(activity)) {
                if (AuthHelper.isLoggedIn()) {
                    DialogHelper.showSubscriptionAlertIssue(activity);
                }
                else {
                    DialogHelper.showLoginAlert(activity);
                }
            }
            else {

            }
        }

    }
}
