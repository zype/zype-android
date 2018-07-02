package com.zype.android.ui.video_details.fragments.video;

/**
 * @author vasya
 * @version 1
 *          date 8/12/15
 */
public interface OnVideoAudioListener {

    void onFullscreenChanged(boolean isFullscreen);

    void videoFinished();

    void audioFinished();

    void videoStarted();

    void audioStarted();

    int getCurrentTimeStamp();

    void saveCurrentTimeStamp(long currentPosition);

    void onSeekToMillis(int ms);
}
