package com.zype.android.core.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class CursorHelper {
    @Nullable
    public static Cursor getVideoCursor(@NonNull ContentResolver contentResolver, @NonNull final String videoId) {
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Video.COLUMN_ID + "=?", new String[]{videoId}, null);
    }

    public static Cursor getVideoForDownloadCursor(@NonNull ContentResolver contentResolver) {
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE + "=? OR " +
                Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE + "=?", new String[]{String.valueOf(1), String.valueOf(1)}, null);
    }

    static boolean isVideoExist(@NonNull ContentResolver contentResolver, @NonNull String fileId) {
        boolean result = false;
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO)) == 1;
            }
            cursor.close();
        }
        return result;
    }

    static boolean isAudioExist(@NonNull ContentResolver contentResolver, @NonNull String fileId) {
        boolean result = false;
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO)) == 1;
            }
            cursor.close();
        }
        return result;
    }

    static int isFileTranscoded(@NonNull ContentResolver contentResolver, @NonNull String fileId) {
        int result = -1;
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_TRANSCODED));
        }
        cursor.close();
        return result;
    }

    public static Cursor getLatestVideo(@NonNull ContentResolver contentResolver) {
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.query(uri, null, null, null, Contract.Video.COLUMN_CREATED_AT + " DESC");
    }

    public static Cursor getAllDownloadsCursor(@NonNull ContentResolver contentResolver) {
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + "=? OR " +
                Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + "=?", new String[]{String.valueOf(1), String.valueOf(1)}, null);
    }


    public static Cursor getAllFavoritesVideoCursor(@NonNull ContentResolver contentResolver) {
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Video.COLUMN_IS_FAVORITE + "=?", new String[]{String.valueOf(1)}, null);
    }

    // Ads
    public static Cursor getAdScheduleCursorByVideoId(@NonNull ContentResolver contentResolver, @NonNull final String videoId) {
        Uri uri = Contract.AdSchedule.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.AdSchedule.VIDEO_ID + "=?", new String[] { videoId }, null);
    }

    // Favorite
    public static Cursor getFavoriteCursorByVideoId(@NonNull ContentResolver contentResolver, @NonNull final String videoId) {
        Uri uri = Contract.Favorite.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Favorite.COLUMN_VIDEO_ID + "=?", new String[] { videoId }, null);
    }

    // Playlist
    @Nullable
    public static Cursor getPlaylistCursor(@NonNull ContentResolver contentResolver, @NonNull final String playlistId) {
        Uri uri = Contract.Playlist.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.Playlist.COLUMN_ID + "=?", new String[] { playlistId }, null);
    }

    // Beacons
    public static Cursor getAnalyticsByVideoId(@NonNull ContentResolver contentResolver, @NonNull final String videoId) {
        Uri uri = Contract.AnalyticBeacon.CONTENT_URI;
        return contentResolver.query(uri, null, Contract.AnalyticBeacon.VIDEO_ID + "=?", new String[] { videoId }, null);
    }
}
