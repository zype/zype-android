package com.zype.android.ui.video_details.fragments;

/**
 * @author vasya
 * @version 1
 *          date 7/10/15
 */
public interface OnDetailActivityFragmentListener {

    void onShareVideo(String videoId);

//    List<ZobjectData> getZObjectList();

    void onDownloadVideo(String videoId);

    void onDownloadAudio(String videoId);

    void onShowAudio();

    void onShowVideo();

    void onFavorite(String videoId);

    void onUnFavorite(String videoId);

    int getCurrentTimeStamp();

    int getCurrentFragment();

//    void setCurrentTimeStamp(int currentPosition);
}
