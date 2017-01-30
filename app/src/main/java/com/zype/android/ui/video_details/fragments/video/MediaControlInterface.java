package com.zype.android.ui.video_details.fragments.video;


public interface MediaControlInterface {

    void seekToMillis(int ms);

    int getCurrentTimeStamp();

    void play();

    void stop();
}
