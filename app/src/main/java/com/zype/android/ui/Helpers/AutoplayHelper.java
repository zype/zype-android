package com.zype.android.ui.Helpers;

import android.app.Activity;

import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.helpers.PlaylistHelper;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.ui.NavigationHelper;

/**
 * Created by Evgeny Cherkasov on 14.05.2018.
 */
public class AutoplayHelper {

    public static void playNextVideo(Activity activity, String currentVideoId, String playlistId) {
//        String nextVideoId = PlaylistHelper.getNextVideoId(currentVideoId,
//                CursorHelper.getPlaylistVideosCursor(activity.getContentResolver(), playlistId));
        String nextVideoId = PlaylistHelper.getNextVideoId(currentVideoId, playlistId, activity.getApplication());
        NavigationHelper.getInstance(activity).switchToVideoDetailsScreen(activity, nextVideoId, playlistId, true);
    }

    public static void playPreviousVideo(Activity activity, String currentVideoId, String playlistId) {
//        String nextVideoId = PlaylistHelper.getPreviousVideoId(currentVideoId,
//                CursorHelper.getPlaylistVideosCursor(activity.getContentResolver(), playlistId));
//        VideoDetailActivity.startActivity(activity, nextVideoId, playlistId);
        String previousVideoId = PlaylistHelper.getPreviousVideoId(currentVideoId, playlistId, activity.getApplication());
        NavigationHelper.getInstance(activity).switchToVideoDetailsScreen(activity, previousVideoId, playlistId, true);
    }

}
