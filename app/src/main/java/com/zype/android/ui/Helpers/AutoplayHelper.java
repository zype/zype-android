package com.zype.android.ui.Helpers;

import android.app.Activity;

import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.helpers.PlaylistHelper;
import com.zype.android.ui.video_details.VideoDetailActivity;

public class AutoplayHelper {
    public static void playNextVideo(Activity activity, String currentVideoId, String playlistId) {
        String nextVideoId = PlaylistHelper.getNextVideoId(currentVideoId,
                CursorHelper.getPlaylistVideosCursor(activity.getContentResolver(), playlistId));
        VideoDetailActivity.startActivity(activity, nextVideoId, playlistId);
    }
}
