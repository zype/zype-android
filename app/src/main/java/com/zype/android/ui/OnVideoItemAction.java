package com.zype.android.ui;

/**
 * @author vasya
 * @version 1
 *          date 8/17/15
 */
public interface OnVideoItemAction {

    void onFavoriteVideo(String videoId);

    void onUnFavoriteVideo(String videoId);

    void onShareVideo(String videoId);

    void onDownloadVideo(String videoId);

    void onDownloadAudio(String videoId);
}
