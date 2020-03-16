package com.zype.android.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.billingclient.api.Purchase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.Consumer.UnAuthorizedUserActivity;
import com.zype.android.ui.Subscription.SubscribeOrLoginActivity;
import com.zype.android.ui.Consumer.ConsumerActivity;
import com.zype.android.ui.Gallery.GalleryActivity;
import com.zype.android.ui.Intro.IntroActivity;
import com.zype.android.ui.Subscription.SubscriptionActivity;
import com.zype.android.ui.main.fragments.playlist.PlaylistActivity;
import com.zype.android.ui.monetization.PaywallActivity;
import com.zype.android.ui.monetization.PaywallType;
import com.zype.android.ui.v2.search.SearchActivity;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static com.zype.android.ui.monetization.PaywallActivity.EXTRA_PAYWALL_TYPE;

/**
 * Created by Evgeny Cherkasov on 11.07.2017.
 */

public class NavigationHelper {
    private static volatile NavigationHelper instance;

    private Context context;
    private DataRepository repo;

    private NavigationHelper(Context context) {
        if (instance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.context = context.getApplicationContext();
        this.repo = DataRepository.getInstance(ZypeApp.get(this.context));
    }

    public static NavigationHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (NavigationHelper.class) {
                if (instance == null) instance = new NavigationHelper(context);
            }
        }
        return instance;
    }

//    public void checkSubscription(Activity activity, String videoId, String playlistId, boolean onAir) {
//        Bundle extras = new Bundle();
//        extras.putString(BundleConstants.VIDEO_ID, videoId);
//        extras.putString(BundleConstants.PLAYLIST_ID, playlistId);
//        if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
//            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
//                switchToSubscriptionScreen(activity, extras);
//            }
//            else {
//                switchToVideoDetailsScreen(activity, videoId, playlistId, false);
//            }
//        }
//        else if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(activity)) {
//            if (SettingsProvider.getInstance().isLoggedIn()) {
//                if (SettingsProvider.getInstance().getSubscriptionCount() > 0
//                        || !isLiveStreamLimitExceeded(onAir)) {
//                    switchToVideoDetailsScreen(activity, videoId, playlistId, false);
//                }
//                else {
//                    switchToSubscriptionScreen(activity, extras);
//                }
//            }
//            else {
//                switchToSubscriptionScreen(activity, extras);
//            }
//        }
//        else {
//            if (SettingsProvider.getInstance().isLoggedIn()) {
//                if (SettingsProvider.getInstance().getSubscriptionCount() > 0
//                        || !isLiveStreamLimitExceeded(onAir)) {
//                    switchToVideoDetailsScreen(activity, videoId, playlistId, false);
//                }
//                else {
//                    DialogHelper.showSubscriptionAlertIssue(context);
//                }
//            }
//            else {
//                switchToLoginScreen(activity);
//            }
//        }
//    }

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
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_CONSUMER);
    }

    public void switchToLoginScreen(Activity activity) {
        switchToLoginScreen(activity, null);
    }

    public void handleUnAuthorizedVideo(Activity activity) {
        Intent intent = new Intent(activity, UnAuthorizedUserActivity.class);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_USER);
    }

    public void switchToLoginScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
    }

    public void switchToUnauthorizedUserScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(activity, UnAuthorizedUserActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, BundleConstants.REQUEST_USER);
    }

    public void switchToIntroScreen(Activity activity) {
        Intent intent = new Intent(activity, IntroActivity.class);
        activity.startActivity(intent);
    }

    public void switchToSearchScreen(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    public void switchToSubscriptionScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(activity, SubscriptionActivity.class);
        if (extras !=null){
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, BundleConstants.REQUEST_SUBSCRIPTION);
    }

    public void switchToVideoDetailsScreen(Activity activity,
                                           String videoId, String playlistId, boolean autoplay) {
        Intent intent = new Intent(activity, com.zype.android.ui.video_details.v2.VideoDetailActivity.class);
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
        Intent intent = new Intent(activity, com.zype.android.ui.v2.videos.PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void switchToGalleryScreen(Activity activity, String playlistId) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void switchToSubscribeOrLoginScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(activity, SubscribeOrLoginActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_SUBSCRIBE_OR_LOGIN);
    }

    public void switchToPaywallScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(activity, PaywallActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_PAYWALL);
    }

    public void switchToPurchaseScreen(Activity activity, Bundle extras) {
        Intent intent = new Intent(activity, SubscriptionActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, BundleConstants.REQUEST_PURCHASE);
    }

    // Video

    // TODO: REFACTORING - Remove 'autoplay' parameter
    public void handleVideoClick(Activity activity, @NonNull Video video, String playlistId, boolean autoplay) {
        if (ZypeConfiguration.playerPaywall()
                || ZypeConfiguration.trailers() && VideoHelper.hasTrailer(video)) {
            // If the 'Player paywall' feature is enabled,
            // or the 'Trailers' feature is enabled and the video has a trailer,
            // open the video detail screen, where user can play the trailer or purchase the video.
            switchToVideoDetailsScreen(activity, video.id, playlistId, autoplay);
        }
        else {
            Playlist playlist = null;
            if (!TextUtils.isEmpty(playlistId)) {
                playlist = DataRepository.getInstance(activity.getApplication())
                        .getPlaylistSync(playlistId);
            }

            if (AuthHelper.isVideoUnlocked(activity, video.id, playlistId)) {
                switchToVideoDetailsScreen(activity, video.id, playlistId, autoplay);
            }
            else {
                handleLockedVideo(activity, video, playlist);
            }
        }
    }

    public void handleLockedVideo(Activity activity, Video video, Playlist playlist) {
        Bundle extras = new Bundle();
        extras.putString(BundleConstants.VIDEO_ID, video.id);
        if (playlist == null) {
            playlist = findPurchaseRequiredPlaylist(video);
        }
        if (playlist != null) {
            extras.putString(BundleConstants.PLAYLIST_ID, playlist.id);

            if (playlist.purchaseRequired == 1) {
                if (ZypeConfiguration.isNativeTvodEnabled(activity)) {
                    extras.putSerializable(EXTRA_PAYWALL_TYPE, PaywallType.PLAYLIST_TVOD);
                    switchToPaywallScreen(activity, extras);
                    return;
                }
            }
        }

        if (video.registrationRequired == 1) {
            if (!AuthHelper.isLoggedIn()) {
                switchToUnauthorizedUserScreen(activity, extras);
            }
        }
        else if (Integer.valueOf(video.subscriptionRequired) == 1) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                switchToSubscriptionScreen(activity, extras);
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
                if (AuthHelper.isLoggedIn()) {
                    // TODO: REFACTORING - Move subcription paywall logic to 'PaywallActivity'
                    List<Purchase> purchases = ZypeApp.marketplaceGateway.getBillingManager().getPurchases();
                    if (purchases != null && purchases.size() > 0) {
                        switchToSubscribeOrLoginScreen(activity, extras);
                    }
                    else {
                        switchToSubscriptionScreen(activity, extras);
                    }
                }
                else {
                    switchToSubscribeOrLoginScreen(activity, extras);
                }
            }
            else {
                DialogHelper.showAlert(activity,
                        context.getString(R.string.dialog_update_app_title),
                        context.getString(R.string.dialog_update_app_message));
            }
        }
    }

    private Playlist findPurchaseRequiredPlaylist(Video video) {
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> playlistIds = new Gson().fromJson(video.serializedPlaylistIds, type);
        if (playlistIds != null) {
            for (String id : playlistIds) {
                Playlist playlist = repo.getPlaylistSync(id);
                if (playlist != null && playlist.purchaseRequired == 1) {
                    return playlist;
                }
            }
        }
        return null;
    }

    public void handleNotAuthorizedVideo(Activity activity, String videoId, String playlistId) {
        Video video = DataRepository.getInstance((Application) context.getApplicationContext()).getVideoSync(videoId);
        Bundle extras = new Bundle();
        extras.putString(BundleConstants.VIDEO_ID, videoId);
        extras.putString(BundleConstants.PLAYLIST_ID, playlistId);
        if (video == null) {
            Logger.e("handleNotAuthorizedVideo(): Error get video, videoId=" + videoId);
            return;
        }
        if (Integer.valueOf(video.subscriptionRequired) == 1) {
            if (ZypeConfiguration.isNativeSubscriptionEnabled(activity)) {
                switchToSubscriptionScreen(activity, extras);
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
                if (AuthHelper.isLoggedIn()) {
                    List<Purchase> purchases = ZypeApp.marketplaceGateway.getBillingManager().getPurchases();
                    if (purchases != null && purchases.size() > 0) {
                        switchToSubscribeOrLoginScreen(activity, extras);
                    }
                    else {
                        switchToSubscriptionScreen(activity, extras);
                    }
                }
                else {
                    switchToSubscribeOrLoginScreen(activity, extras);
                }
            }
            else {
                DialogHelper.showAlert(activity,
                        context.getString(R.string.dialog_update_app_title),
                        context.getString(R.string.dialog_update_app_message));
            }
        }
    }

    // Playlist

    public void handlePlaylistClick(Activity activity, Playlist playlist) {
        if (playlist.playlistItemCount > 0) {
            switchToPlaylistVideosScreen(activity, playlist.id);
        }
        else {
            if (ZypeConfiguration.playlistGalleryView(activity)) {
                switchToGalleryScreen(activity, playlist.id);
            }
            else {
                switchToPlaylistScreen(activity, playlist.id);
            }
        }
    }
}
