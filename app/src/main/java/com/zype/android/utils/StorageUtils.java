package com.zype.android.utils;

import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.webapi.model.video.VideoData;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

public class StorageUtils {

    public static final int INTERNAL_SD_CARD = 0;
    public static final int EXTERNAL_SD_CARD = 1;

    public static String getAppCacheFolderPath(Context applicationContext) {
        if (isSdCardAvailableToUse(applicationContext)) {
            if (SettingsProvider.getInstance().getUserPreferenceStorage() == EXTERNAL_SD_CARD) {
                return getSDcardDownloadsPath(applicationContext);
            } else {
                return getInternalDownloadsPath(applicationContext);
            }
        } else {
            return getInternalDownloadsPath(applicationContext);
        }
    }

    private static String getInternalDownloadsPath(Context applicationContext) {
        String path = getInternalDirectoryPath(applicationContext);
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        return path;
    }

    private static String getSDcardDownloadsPath(Context context) {
        String path = getSDcardDirectoryPath() + File.separator + "Android"
                + File.separator + "data" + File.separator + context.getPackageName()
                + File.separator + SettingsProvider.getInstance().getAccessTokenResourceOwnerId();
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        return path;
    }

    public static long getAppCacheStorageFreeSpace(Context applicationContext) {
        long freeSpace = 0;
        if (SettingsProvider.getInstance().getUserPreferenceStorage() == EXTERNAL_SD_CARD) {
            freeSpace = getFreeSpaceExternal(applicationContext);
        } else {
            freeSpace = getFreeSpaceInternal(applicationContext);
        }
        return freeSpace;
    }

    public static long getFreeSpaceInternal(Context applicationContext) {
        return new File(getInternalDirectoryPath(applicationContext)).getFreeSpace();
    }

    public static long getFreeSpaceExternal(Context applicationContext) {
        if (isSdCardAvailableToUse(applicationContext)) {
            File externalStorageDir1 = new File(getSDcardDirectoryPath());
            long free = externalStorageDir1.getFreeSpace();
            return free;
        } else
            return -1;
    }

    /**
     * Returns the path to internal storage
     */
    private static String getInternalDirectoryPath(Context applicationContext) {
        return applicationContext.getFilesDir().getPath();
    }

    /**
     * Returns the SDcard storage path
     */
    private static String getSDcardDirectoryPath() {
        String strSDCardPath = System.getenv("SECONDARY_STORAGE");
        if ((null == strSDCardPath) || (strSDCardPath.length() == 0)) {
            strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        return strSDCardPath;
    }

    public static boolean isSdCardAvailableToUse(Context context) {
        ContextCompat.getExternalFilesDirs(context, null);
        String secondarySd = getSDcardDirectoryPath();
        File secondaryStorage = new File(secondarySd + File.separator + "Android" +
                File.separator + "data" + File.separator + context.getPackageName());

        boolean exist = secondaryStorage.exists();
        boolean makeDir = secondaryStorage.mkdirs();
        boolean canWrite = secondaryStorage.canWrite();
        if ((exist || makeDir) && canWrite) {
            return true;
        } else
            return false;
    }

    public static void initStorage(Context context) {
        // this calls make sure that the folder per app will be created inside external SD card Android/data/
        ContextCompat.getExternalFilesDirs(context, null);
        StorageUtils.isSdCardAvailableToUse(context);
    }

    public static long getSizeOfDownloadsFolder(Context context) {
        File internalAudioUserPath = new File(getInternalDownloadsPath(context) + "/audio");
        File internalVideoPerUserPath = new File(getInternalDownloadsPath(context) + "/video");
        File extAudio = new File(getSDcardDownloadsPath(context) + "/audio");
        File extVideo = new File(getSDcardDownloadsPath(context) + "/video");
        long sum = sumFolderLength(internalAudioUserPath) +
                sumFolderLength(internalVideoPerUserPath) +
                sumFolderLength(extAudio) + sumFolderLength(extVideo);
        return sum;
    }

    private static long sumFolderLength(File folderWithFiles) {
        long totalDownloadsSize = 0;
        if (folderWithFiles.exists() && folderWithFiles.listFiles() != null) {
            File[] files = folderWithFiles.listFiles();
            for (int i = 0; i < files.length; i++) {
                totalDownloadsSize += files[i].length();
            }
        }
        return totalDownloadsSize;
    }

    public static void checkSizeOfDownloads(MainActivity context) {

        List<VideoData> videos = VideoHelper.getAllDownloads(context.getContentResolver());
        if (videos != null) {
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getDownloadAudioPath() != null && !TextUtils.isEmpty(videos.get(i).getDownloadAudioPath())) {
                    File file = new File(videos.get(i).getDownloadAudioPath());
                    SettingsProvider.getInstance().addToReserved(file.length());
                }
                if (videos.get(i).getDownloadVideoPath() != null && !TextUtils.isEmpty(videos.get(i).getDownloadVideoPath())) {
                    File file = new File(videos.get(i).getDownloadVideoPath());
                    SettingsProvider.getInstance().addToReserved(file.length());
                }
            }
        }
    }
}
