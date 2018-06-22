package com.zype.android.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Auth.SubscribeOrLoginActivity;
import com.zype.android.ui.Consumer.ConsumerActivity;
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
                switchToVideoDetailsScreen(activity, videoId, playlistId, false);
            }
        }
        else if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(activity)) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                if (SettingsProvider.getInstance().getSubscriptionCount() > 0
                        || !isLiveStreamLimitExceeded(onAir)) {
                    switchToVideoDetailsScreen(activity, videoId, playlistId, false);
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
                    switchToVideoDetailsScreen(activity, videoId, playlistId, false);
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

    public void switchToSubscribeOrLoginScreen(Activity activity) {
        Intent intent = new Intent(activity, SubscribeOrLoginActivity.class);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_SUBSCRIBE_OR_LOGIN);
    }

    //
    public void handleNotAuthorizedVideo(Activity activity, String videoId) {
        Video video = DataRepository.getInstance((Application) context.getApplicationContext()).getVideoSync(videoId);
        if (video == null) {
            Logger.e("handleNotAuthorizedVideo(): Error get video, videoId=" + videoId);
            return;
        }
        if (Integer.valueOf(video.subscriptionRequired) == 1) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                switchToSubscriptionScreen(activity);
            }
            else if (ZypeConfiguration.isUniversalSubscriptionEnabled(activity)) {
                if (AuthHelper.isLoggedIn()) {
                    DialogHelper.showSubscriptionAlertIssue(activity);
                }
                else {
                    DialogHelper.showLoginAlert(activity);
                }
            }
            else if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(activity)) {
                switchToSubscribeOrLoginScreen(activity);
            }
            else {
                Logger.e("handleNotAuthorizedVideo(): Failed to handle not authorized video, videoId=" + videoId);
            }
        }
    }
}
