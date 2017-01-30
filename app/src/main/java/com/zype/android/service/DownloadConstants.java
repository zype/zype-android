package com.zype.android.service;

/**
 * @author vasya
 * @version 1
 *          date 7/24/15
 */
public class DownloadConstants {
    public static final int PROGRESS_START_AUDIO = 820;
    public static final int PROGRESS_START_VIDEO = 821;
    public static final int PROGRESS_UPDATE_AUDIO = 830;
    public static final int PROGRESS_UPDATE_VIDEO = 831;
    public static final int PROGRESS_END_AUDIO = 840;
    public static final int PROGRESS_END_VIDEO = 841;
    public static final int PROGRESS_FAIL_AUDIO = 850;
    public static final int PROGRESS_FAIL_VIDEO = 851;
    public static final int PROGRESS_CANCELED_AUDIO = 860;
    public static final int PROGRESS_CANCELED_VIDEO = 861;
    public static final int PROGRESS_FREE_SPACE = 826;

    public static final String ACTION = "com.zype.android.service.action";
    public static final String ACTION_TYPE = "com.zype.android.service.action.ACTION_TYPE";
    public static final String EXTRA_FILE_ID = "com.zype.android.service.extra.FILE_ID";
}
