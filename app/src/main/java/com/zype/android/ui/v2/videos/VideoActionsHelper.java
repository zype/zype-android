package com.zype.android.ui.v2.videos;

import android.app.Activity;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;

import java.util.HashMap;

public class VideoActionsHelper {
    public static void onFavorite(final Video video, final Activity activity) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(activity)) {
            if (AuthHelper.isLoggedIn()) {
                String accessToken = AuthHelper.getAccessToken();
                String consumerId = SettingsProvider.getInstance().getConsumerId();
                ZypeApi.getInstance().addVideoFavorite(accessToken, consumerId, video.id,
                        new IZypeApiListener() {
                            @Override
                            public void onCompleted(ZypeApiResponse response) {
                                if (response.isSuccessful) {
                                    updateVideoFavorite(activity, video, true);
                                }
                            }
                        });
            }
            else {
                NavigationHelper.getInstance(activity).switchToLoginScreen(activity);
            }
        }
        else {
            updateVideoFavorite(activity, video, true);
        }
    }

    public static void onUnfavorite(Video video, Activity activity) {

    }

    private static void updateVideoFavorite(Activity activity, Video video, boolean isFavorite) {
        video.isFavorite = (isFavorite) ? 1 : 0;
        DataRepository.getInstance(activity.getApplication()).updateVideo(video);
    }
}
