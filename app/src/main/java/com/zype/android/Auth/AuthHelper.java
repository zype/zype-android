package com.zype.android.Auth;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class AuthHelper {
    private static final String TAG = AuthHelper.class.getSimpleName();

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

    public static boolean isPaywalledVideo(Context context, String videoId, String playlistId) {
        Video video = DataRepository.getInstance((Application) context.getApplicationContext()).getVideoSync(videoId);
        if (video == null) {
            Log.e(TAG, "isPaywalledVideo(): Video not found " + videoId);
            return false;
        }

        Playlist playlist = null;
        if (!TextUtils.isEmpty(playlistId)) {
            playlist = DataRepository.getInstance((Application) context.getApplicationContext())
                    .getPlaylistSync(playlistId);
        }

        if (playlist != null) {
            if (playlist.purchaseRequired == 1) {
                return true;
            }
        }

        if (Integer.valueOf(video.purchaseRequired) == 1) {
            return true;
        }
        if (Integer.valueOf(video.subscriptionRequired) == 1) {
            return true;
        }

        return false;
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

    public static boolean isVideoUnlocked(Context context,
                                          String videoId, String playlistId) {
        boolean result = true;
        DataRepository repo = DataRepository.getInstance((Application) context.getApplicationContext());

        Video video = repo.getVideoSync(videoId);
        if (video == null) {
            return false;
        }

        List<Playlist> playlists = new ArrayList<>();
        if (TextUtils.isEmpty(playlistId)) {
            Type type = new TypeToken<List<String>>(){}.getType();
            List<String> playlistIds = new Gson().fromJson(video.serializedPlaylistIds, type);
            if (playlistIds != null) {
                for (String id : playlistIds) {
                    Playlist playlist = repo.getPlaylistSync(id);
                    if (playlist != null) {
                        playlists.add(playlist);
                    }
                }
            }
        }
        else {
            Playlist playlist = repo.getPlaylistSync(playlistId);
            if (playlist != null) {
                playlists.add(playlist);
            }
        }
        for (Playlist playlist : playlists) {
            if (playlist.purchaseRequired == 1) {
                if (ZypeConfiguration.isUniversalTVODEnabled(context)) {
                    if (video.isEntitled != null && video.isEntitled == 1) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    // Playlist requires purchase, but UTVOD options is turned off in the app configuration
                    Logger.w("Playlist " + playlist.id + " requires purchase, but universal TVOD feature " +
                            "is turned off in the app configuration.");
                    result = false;
                }
            }
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
