package com.zype.android.utils;

import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.settings.SettingsProvider;

import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @author vasya
 * @version 1
 *          date 7/18/15
 */
public class FileUtils {

    public static void deleteVideoFile(String videoId, Context context) {
        Cursor cursor = CursorHelper.getVideoCursor(context.getContentResolver(), videoId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String videoFilePath = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH));
                if (videoFilePath != null) {
                    File file = new File(videoFilePath);
//                    if (file.exists())
                    SettingsProvider.getInstance().deleteFromReserved(file.length());
                    if (!file.delete()) {
                        Logger.e("video file hasn't deleted:" + videoFilePath);
                    }
                }
            } else {
                throw new IllegalStateException("DB does not contains video with id=" + videoId);
            }
            cursor.close();
        }
    }

    public static void deleteAudioFile(String videoId, Context context) {

        Cursor cursor = CursorHelper.getVideoCursor(context.getContentResolver(), videoId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String audioFilePath = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH));
                if (audioFilePath != null) {
                    File file = new File(audioFilePath);
//                    if (file.exists())
                    SettingsProvider.getInstance().deleteFromReserved(file.length());
                    if (!file.delete()) {
                        Logger.e("audio file hasn't deleted:" + audioFilePath);
                    } else {
                        Logger.e("audio file deleted:" + audioFilePath);
                    }
                }
            } else {
                throw new IllegalStateException("DB does not contains video with id=" + videoId);
            }
            cursor.close();
        }
    }

    public static String formatToGb(long size) {
        String hrSize = null;
        double g = (((size / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");
        hrSize = dec.format(g).concat(" GB");
        return hrSize;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }
        return hrSize;
    }

    public static boolean moveFile(File oldFile, String destinationFolder) {
        try {
            if (oldFile.renameTo(new File(destinationFolder + File.separator + oldFile.getName()))) {
                Logger.v("makeStoragePathMigration The file was moved successfully to the new folder" + destinationFolder + File.separator + oldFile.getName());
                return true;
            } else {
                Logger.v("makeStoragePathMigration The File was not moved.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
