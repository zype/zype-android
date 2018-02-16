package com.zype.android.ui.player;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;

/**
 * @author vasya
 * @version 1
 *          date 10/15/15
 */
public class CustomPlayerControl extends PlayerControl {

    private SeekCallback callback;

    public CustomPlayerControl(ExoPlayer exoPlayer, SeekCallback callback) {
        super(exoPlayer);
        this.callback = callback;
    }

    @Override
    public void seekTo(int timeMillis) {
        AnalyticsManager manager = AnalyticsManager.getInstance();
        manager.seekTo();

        callback.seekEvent(timeMillis);
        super.seekTo(timeMillis);
    }

    public void release() {
        callback = null;
    }
}
