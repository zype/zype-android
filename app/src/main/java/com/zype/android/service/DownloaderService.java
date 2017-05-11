package com.zype.android.service;

import com.zype.android.R;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.utils.StorageUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DownloaderService extends IntentService {

    private static final String ACTION_VIDEO_START = "com.zype.android.service.action.VIDEO_START";
    private static final String ACTION_AUDIO_START = "com.zype.android.service.action.AUDIO_START";
    private static final String ACTION_VIDEO_STOP = "com.zype.android.service.action.VIDEO_STOP";
    private static final String ACTION_AUDIO_STOP = "com.zype.android.service.action.AUDIO_STOP";

    private static final String EXTRA_URL = "com.zype.android.service.extra.URL";

    private static final HashMap<String, Integer> progressMap = new HashMap<>();
    private static final Set<String> cancelSet = new HashSet<>();

    public DownloaderService() {
        super("DownloaderService");
    }

    protected static void stopDownloadAudio(@NonNull String videoId) {
        cancelSet.add(videoId);
    }

    protected static void downloadVideo(@NonNull Context context, @NonNull String url, @NonNull String videoId) {
        if (!progressMap.containsKey(videoId)) {
            Intent intent = new Intent(context, DownloaderService.class);
            intent.setAction(ACTION_VIDEO_START);
            intent.putExtra(EXTRA_URL, url);
            intent.putExtra(DownloadConstants.EXTRA_FILE_ID, videoId);

            context.startService(intent);
        } else {
            Logger.w("This video file is downloading. FileId=" + videoId);
        }
    }

    protected static void downloadAudio(@NonNull Context context, @NonNull String url, @NonNull String audioId) {
        if (!progressMap.containsKey(audioId)) {
            Intent intent = new Intent(context, DownloaderService.class);

            intent.setAction(ACTION_AUDIO_START);
            intent.putExtra(EXTRA_URL, url);
            intent.putExtra(DownloadConstants.EXTRA_FILE_ID, audioId);

            context.startService(intent);
        } else {
            Logger.w("This audio file is downloading. FileId=" + audioId);
        }
    }

    public static int currentProgress(@NonNull String fileId) {
        if (progressMap.containsKey(fileId)) {
            return progressMap.get(fileId);
        } else {
            return -1;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            final String url = intent.getStringExtra(EXTRA_URL);
            final String fileId = intent.getStringExtra(DownloadConstants.EXTRA_FILE_ID);
            switch (action) {
                case ACTION_VIDEO_START:
                    handleActionDownloadVideo(fileId, url);
                    break;
                case ACTION_AUDIO_START:
                    handleActionDownloadAudio(fileId, url);
                    break;
                case ACTION_AUDIO_STOP:
                    cancelSet.add(fileId);
                    break;
                case ACTION_VIDEO_STOP:
                    cancelSet.add(fileId);
                    break;
                default:
                    throw new IllegalArgumentException("unknown action:" + action);
            }
        }
    }

    private void handleActionDownloadAudio(@NonNull final String fileId, @NonNull final String fileUrl) {
        Thread thread = new Thread("download_audio_" + fileId) {
            @Override
            public void run() {
                progressMap.put(fileId, 0);
                String folderPath = StorageUtils.getAppCacheFolderPath(getApplicationContext()) + "/audio";
                File folderCheck = new File(folderPath);
                if (!folderCheck.exists()) {
                    folderCheck.mkdir();
                }
                String filePath = folderPath + "/" + fileId;
                boolean result = false;
                try {
                    result = download(filePath, fileId, new URL(fileUrl), false);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (result) {
                    sendAudioEnded(fileId);
                    DataHelper.setAudioDownloaded(getContentResolver(), fileId, filePath, fileUrl);
                    Logger.d("FINISH audio download correctly");
                } else {
                    Logger.e("FINISH audio download with error");
                }
            }
        };
        thread.start();
    }

    private void handleActionDownloadVideo(@NonNull final String fileId, @NonNull final String fileUrl) {
        Thread thread = new Thread("download_video_" + fileId) {
            @Override
            public void run() {
                progressMap.put(fileId, 0);
                String folderPath = StorageUtils.getAppCacheFolderPath(getApplicationContext()) + "/video";
                File folderCheck = new File(folderPath);
                if (!folderCheck.exists()) {
                    folderCheck.mkdir();
                }
                String filePath = folderPath + "/" + fileId;
                boolean result = false;
                try {
                    result = download(filePath, fileId, new URL(fileUrl), true);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (result) {
                    sendVideoEnded(fileId);
                    DataHelper.setVideoDownloaded(getContentResolver(), fileId, filePath, fileUrl);
                    Logger.d("FINISH video download correctly");
                } else {
                    Logger.e("FINISH video download with error");
                }
            }
        };
        thread.start();
    }

    boolean download(@NonNull String filePath, @NonNull String fileId, URL url, boolean isVideo) {
        long downloaded = 0;
        boolean isCanceled = false;
        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        BufferedOutputStream bout = null;
        boolean isDownloadFinishedCorrectly = false;
        boolean isNewFile = false;
        try {
            connection = (HttpURLConnection) url.openConnection();
            File file = new File(filePath);
            if (file.exists()) {
                downloaded = file.length();
                connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-");
            } else {
                isNewFile = true;
                connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            }
            connection.connect();

            if (connection.getResponseCode() != 416) {
                int fileLength = connection.getContentLength();

                long freeBytesInternal = StorageUtils.getAppCacheStorageFreeSpace(getApplicationContext());
                long reserved = SettingsProvider.getInstance().getReserved();
                long limit = SettingsProvider.getInstance().getDownloadsLimitInBytes();

                if (fileLength >= freeBytesInternal) {
                    sendFreeSpaceProblem(fileId, R.string.alert_dialog_message_free_space);
                    connection.disconnect();
                    progressMap.remove(fileId);
                    return false;
                } else if (isNewFile) {
                    if (reserved + fileLength > limit) {
                        progressMap.remove(fileId);
                        DownloadHelper.removeFromNeedToDownload(getApplicationContext(), fileId, isVideo);
                        sendFreeSpaceProblem(fileId, R.string.download_maxsize_exceeded);
                        connection.disconnect();
                        return false;
                    } else {
                        SettingsProvider.getInstance().saveFileLength(fileId, fileLength);
                        SettingsProvider.getInstance().addToReserved(fileLength);
                    }
                }
                sendStart(fileId, isVideo);

                in = new BufferedInputStream(connection.getInputStream());
                FileOutputStream fos = (downloaded == 0) ? new FileOutputStream(filePath) : new FileOutputStream(filePath, true);
                bout = new BufferedOutputStream(fos, 1024);
                byte[] data = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) >= 0) {
                    if (cancelSet.contains(fileId)) {
                        cancelSet.remove(fileId);
                        progressMap.remove(fileId);
                        DownloadHelper.removeFromNeedToDownload(getApplicationContext(), fileId, isVideo);
                        isCanceled = true;
                        long newReserved = SettingsProvider.getInstance().getReserved() - SettingsProvider.getInstance().getFileLength(fileId);
                        SettingsProvider.getInstance().saveToReserved(newReserved);
//                    Logger.v("download cancelled= " + "newres= " + newReserved + " fl= " +
//                            SettingsProvider.getInstance().getFileLength(fileId) + " reserved =" + SettingsProvider.getInstance().getReserved());
                        break;
                    }
                    bout.write(data, 0, count);
                    downloaded += count;
                    sendProgress(fileId, downloaded, fileLength, isVideo);
                }
                bout.flush();
            }
            progressMap.remove(fileId);
            isDownloadFinishedCorrectly = true;
        } catch (SocketException e) {
            Logger.e("SocketException", e);
            sendFail(fileId, e.getMessage(), isVideo);
        } catch (IOException e) {
            Logger.e("IOException", e);
            sendFail(fileId, e.getMessage(), isVideo);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isCanceled) {
                isDownloadFinishedCorrectly = false;
                deleteDownloadedFile(filePath);
                sendCancel(fileId, isVideo);
            }
        }
        progressMap.remove(fileId);
        return isDownloadFinishedCorrectly;
    }

//    private String createFolder(String folderName) {
//        String folderPath = StorageUtils.getAppCacheFolderPath() + "/" + folderName;
//        File folderCheck = new File(folderPath);
//        if (!folderCheck.exists()) {
//            folderCheck.mkdir();
//        }
//        return folderPath;
//    }

    private void sendFreeSpaceProblem(String fileId, int message) {
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_FREE_SPACE);
        intent.putExtra(BundleConstants.PROGRESS_ERROR_MESSAGE, message);
        sendLocalBroadcast(intent);
    }

    private void sendVideoEnded(String fileId) {
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_END_VIDEO);
        sendLocalBroadcast(intent);
    }

    private void sendAudioEnded(String fileId) {
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_END_AUDIO);
        sendLocalBroadcast(intent);
    }

    private void sendCancel(String fileId, boolean isVideo) {
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        if (isVideo) {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_CANCELED_VIDEO);
        } else {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_CANCELED_AUDIO);
        }
        sendLocalBroadcast(intent);
    }

    private void deleteDownloadedFile(String filePath) {
        File file = new File(filePath);
        if (!file.delete()) {
            Logger.e("file wasn't deleted:" + filePath);
        }
    }

    private void sendStart(String fileId, boolean isVideo) {
        Logger.d("sendStart");
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        if (isVideo) {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_START_VIDEO);
        } else {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_START_AUDIO);
        }
        sendLocalBroadcast(intent);
    }

    private void sendFail(String fileId, String message, boolean isVideo) {
        Logger.d("sendFail");
        Intent intent = new Intent(DownloadConstants.ACTION);
        intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
        if (isVideo) {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_FAIL_VIDEO);
        } else {
            intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_FAIL_AUDIO);
        }
        intent.putExtra(BundleConstants.PROGRESS_ERROR_MESSAGE, message);
        sendLocalBroadcast(intent);

    }

    private void sendProgress(String fileId, long total, int fileLength, boolean isVideo) {
        int progress = (int) (total * 100 / fileLength);

        if (currentProgress(fileId) != progress) {
            progressMap.put(fileId, progress);
            Intent intent = new Intent(DownloadConstants.ACTION);
            intent.putExtra(DownloadConstants.EXTRA_FILE_ID, fileId);
            if (isVideo) {
                intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_UPDATE_VIDEO);
            } else {
                intent.putExtra(DownloadConstants.ACTION_TYPE, DownloadConstants.PROGRESS_UPDATE_AUDIO);
            }
            intent.putExtra(BundleConstants.PROGRESS, progress);
            sendLocalBroadcast(intent);

        }
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //        sendBroadcast(intent);
    }

    public static void cancelAllDownloads() {
        if (progressMap != null && !progressMap.isEmpty()) {
            Set<String> keys = progressMap.keySet();
            for (String key : keys) {
                stopDownloadAudio(key);
            }
        }
    }
}
