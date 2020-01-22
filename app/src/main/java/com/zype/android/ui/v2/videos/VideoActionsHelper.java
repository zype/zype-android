package com.zype.android.ui.v2.videos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.FavoriteVideo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideoFavoriteResponse;

import java.util.HashMap;

public class VideoActionsHelper {

    public static final int ACTION_FAVORITE = 1;
    public static final int ACTION_UNFAVORITE = 2;

    public interface IVideoActionCallback {
        void onActionCompleted(boolean success);
    }

    public static void onFavorite(final Video video, final Application application, IVideoActionCallback listener) {
        if (AuthHelper.isLoggedIn()) {
            String accessToken = AuthHelper.getAccessToken();
            String consumerId = SettingsProvider.getInstance().getConsumerId();
            ZypeApi.getInstance().addVideoFavorite(accessToken, consumerId, video.id,
                    new IZypeApiListener<VideoFavoriteResponse>() {
                        @Override
                        public void onCompleted(ZypeApiResponse<VideoFavoriteResponse> response) {
                            if (response.isSuccessful) {
                                updateVideoFavorite(application, video, response.data.data.id, true, listener);
                            }
                        }
                    });
        }
        else {
            if (ZypeApp.get(application).getAppConfiguration().hideFavoritesActionWhenSignedOut) {
                if (listener != null) {
                    listener.onActionCompleted(false);
                }
//                NavigationHelper.getInstance(activity).switchToLoginScreen(activity);
            }
            else {
                updateVideoFavorite(application, video, null, true, listener);
            }
        }
    }

    public static void onUnfavorite(Video video, Application application, IVideoActionCallback listener) {
        if (AuthHelper.isLoggedIn()) {
            String accessToken = AuthHelper.getAccessToken();
            String consumerId = SettingsProvider.getInstance().getConsumerId();
            FavoriteVideo favoriteVideo = DataRepository.getInstance(application).getVideoFavoriteByVideoId(video.id);
            if (favoriteVideo != null) {
                ZypeApi.getInstance().removeVideoFavorite(accessToken, consumerId, favoriteVideo.id,
                        new IZypeApiListener() {
                            @Override
                            public void onCompleted(ZypeApiResponse response) {
                                if (response.isSuccessful) {
                                    updateVideoFavorite(application, video, favoriteVideo.id, false, listener);
                                }
                            }
                        });
            }
            else {
                updateVideoFavorite(application, video, null, false, listener);
            }
        }
        else {
            if (ZypeApp.get(application).getAppConfiguration().hideFavoritesActionWhenSignedOut) {
                if (listener != null) {
                    listener.onActionCompleted(false);
                }
//                NavigationHelper.getInstance(activity).switchToLoginScreen(activity);
            }
            else {
                updateVideoFavorite(application, video, null, false, listener);
            }
        }
    }

    private static void updateVideoFavorite(Application application,
                                            Video video, String videoFavoriteId, boolean isFavorite,
                                            IVideoActionCallback listener) {
        DataRepository repo = DataRepository.getInstance(application);

        video.isFavorite = (isFavorite) ? 1 : 0;
        repo.updateVideo(video);

        if (isFavorite) {
            if (videoFavoriteId != null) {
                FavoriteVideo favoriteVideo = new FavoriteVideo();
                favoriteVideo.id = videoFavoriteId;
                favoriteVideo.videoId = video.id;
                repo.addVideoFavorite(favoriteVideo);
            }
        }
        else {
            repo.deleteVideoFavoriteByVideoId(video.id);
        }

        if (listener != null) {
            listener.onActionCompleted(true);
        }
    }
}
