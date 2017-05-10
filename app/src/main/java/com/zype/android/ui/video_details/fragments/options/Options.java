package com.zype.android.ui.video_details.fragments.options;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public class Options {

    final static String TITLE_VIDEO = "Video";
    final static String TITLE_AUDIO = "Audio";
    String title;
    int id;
    int drawableId;
    String secondText;
    public int progress = -1;

    public Options(int id, String title, boolean isVideo) {
        this.id = id;
        this.title = title;
        if (isVideo) {
            this.secondText = TITLE_VIDEO;
        } else {
            this.secondText = TITLE_AUDIO;
        }
    }

    public Options(int id, String title, int drawableId) {
        this.id = id;
        this.title = title;
        this.drawableId = drawableId;
    }

    private Options() {
    }
}
