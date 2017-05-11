package com.zype.android.service;

import com.zype.android.BuildConfig;
import com.zype.android.core.NetworkStateObserver;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author vasya
 * @version 1
 *          date 7/24/15
 */
public class DownloadHelper {

    public static void checkDownloadTasks(Context context) {
        Cursor cursor = CursorHelper.getVideoForDownloadCursor(context.getContentResolver());
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String fileId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_ID));
                if (cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE)) == 1) {
                    String audioUrl = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL));
                    DownloadHelper.downloadAudio(context, audioUrl, fileId);
                } else if (cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE)) == 1) {
                    String videoUrl = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL));
                    DownloadHelper.downloadVideo(context, videoUrl, fileId);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    static void downloadVideo(@NonNull Context context, @NonNull String url, @NonNull String fileId) {
        DataHelper.addVideoToDownloadList(context.getContentResolver(), fileId, url);
//        if (NetworkStateObserver.isNetworkEnabled() && SettingsProvider.getInstance().isLogined()) {
        if (NetworkStateObserver.isNetworkEnabled()) {
            if (SettingsProvider.getInstance().isUserPreferenceLoadWifiOnlySet()) {
                if (NetworkStateObserver.isWiFiEnable()
                        || BuildConfig.DEBUG) {
                    if (DataHelper.isFileTranscoded(context.getContentResolver(), fileId)) {
                        DownloaderService.downloadVideo(context.getApplicationContext(), url, fileId);
                    } else {
                        Logger.d("ignore file download until it is not transcoded");
                    }
                }
            } else {
                if (DataHelper.isFileTranscoded(context.getContentResolver(), fileId)) {
                    DownloaderService.downloadVideo(context.getApplicationContext(), url, fileId);
                } else {
                    Logger.d("ignore file download until it is not transcoded");
                }
            }
        }
    }

    static void downloadAudio(@NonNull Context context, @NonNull String url, @NonNull String fileId) {
//        if (NetworkStateObserver.isNetworkEnabled() && SettingsProvider.getInstance().isLogined()) {
        if (NetworkStateObserver.isNetworkEnabled()) {
            if (SettingsProvider.getInstance().isUserPreferenceLoadWifiOnlySet()) {
                if (NetworkStateObserver.isWiFiEnable()
                        || BuildConfig.DEBUG) {
                    if (DataHelper.isFileTranscoded(context.getContentResolver(), fileId)) {
                        DownloaderService.downloadAudio(context.getApplicationContext(), url, fileId);
                    } else {
                        Logger.d("ignore file download until it is not transcoded");
                    }
                }
            } else {
                if (DataHelper.isFileTranscoded(context.getContentResolver(), fileId)) {
                    DownloaderService.downloadAudio(context.getApplicationContext(), url, fileId);
                } else {
                    Logger.d("ignore file download until it is not transcoded");
                }
            }
        }
    }

    public static void stopDownload(@NonNull ContentResolver resolver, @NonNull String videoId) {
        DataHelper.deleteFromDownloadList(resolver, videoId);
        DownloaderService.stopDownloadAudio(videoId);
    }

    public static void addVideoToDownloadList(Context context, String url, String fileId) {
        DataHelper.addVideoToDownloadList(context.getContentResolver(), fileId, url);
        checkDownloadTasks(context);
    }

    public static void addAudioToDownloadList(Context context, String url, String fileId) {
        DataHelper.addAudioToDownloadList(context.getContentResolver(), fileId, url);
        checkDownloadTasks(context);
    }

    public static int removeFromNeedToDownload(Context context, String fileId, boolean isVideo) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        if (isVideo) {
            value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO, 0);
            value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
        } else {
            value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO, 0);
            value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
        }
        return context.getContentResolver().update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }
}
