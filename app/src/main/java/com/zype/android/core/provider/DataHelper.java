package com.zype.android.core.provider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zype.android.core.provider.helpers.FavoriteHelper;
import com.zype.android.core.provider.helpers.PlaylistHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.AdvertisingSchedule;
import com.zype.android.webapi.model.player.AnalyticsDimensions;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class DataHelper {
    public static int insertVideos(@NonNull ContentResolver contentResolver, @NonNull List<VideoData> videoList) {
        ContentValues[] values = new ContentValues[videoList.size()];
        for (int i = 0; i < videoList.size(); i++) {
            ContentValues value = VideoHelper.objectToContentValues(videoList.get(i));
            values[i] = value;
        }
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static int insertFavorites(@NonNull ContentResolver contentResolver, @NonNull List<ConsumerFavoriteVideoData> favoriteList) {
        ContentValues[] values = new ContentValues[favoriteList.size()];
        for (int i = 0; i < favoriteList.size(); i++) {
            ContentValues value = FavoriteHelper.objectToContentValues(favoriteList.get(i));
            values[i] = value;
        }
        Uri uri = Contract.Favorite.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    // //////////
    // Playlist
    //
    public static int insertPlaylists(@NonNull ContentResolver contentResolver, @NonNull List<PlaylistData> playlistList) {
        ContentValues[] values = new ContentValues[playlistList.size()];
        for (int i = 0; i < playlistList.size(); i++) {
            ContentValues value = PlaylistHelper.objectToContentValues(playlistList.get(i));
            values[i] = value;
        }
        Uri uri = Contract.Playlist.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static int deletePlaylistsByParentId(@NonNull ContentResolver contentResolver, @NonNull String parentId) {
        String selection = Contract.Playlist.COLUMN_PARENT_ID + " = ?";
        String[] selectionArgs = new String[] { parentId };
        Uri uri = Contract.Playlist.CONTENT_URI;
        return contentResolver.delete(uri, selection, selectionArgs);
    }

    public static int insertHighlightVideos(@NonNull ContentResolver contentResolver, @NonNull List<VideoData> videoList) {
        ContentValues[] values = new ContentValues[videoList.size()];
        for (int i = 0; i < videoList.size(); i++) {
            ContentValues value = VideoHelper.objectToContentValues(videoList.get(i));
            value.put(Contract.Video.COLUMN_IS_HIGHLIGHT, 1);
            values[i] = value;
        }
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static int setFavoriteVideo(@NonNull ContentResolver contentResolver, @NonNull String videoId, boolean isFavorite) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{videoId});
    }

    public static int setVideoDownloaded(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String filePath, @NonNull String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO, 1);
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH, filePath);
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int updateVideoDownloadedPath(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String filePath) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO, 1);
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH, filePath);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int setAudioDownloaded(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String filePath, @NonNull String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO, 1);
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH, filePath);
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int updateAudioDownloadedPath(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String filePath) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO, 1);
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH, filePath);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int insertFavoriteVideos(@NonNull ContentResolver contentResolver, @NonNull List<VideoData> videoList) {
        ContentValues[] values = new ContentValues[videoList.size()];
        for (int i = 0; i < videoList.size(); i++) {
            ContentValues value = VideoHelper.objectToContentValues(videoList.get(i));
            value.put(Contract.Video.COLUMN_IS_FAVORITE, 1);
            values[i] = value;
        }
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static int addVideosToPlaylist(@NonNull ContentResolver contentResolver, @NonNull List<VideoData> videoList, String parentId) {
        ContentValues[] values = new ContentValues[videoList.size()];
        for (int i = 0; i < videoList.size(); i++) {
            ContentValues value = VideoHelper.objectToContentValues(videoList.get(i));
            List<String> temp = (List<String>) value.get(Contract.Video.COLUMN_PLAYLISTS);
            if (temp == null) {
                temp = new ArrayList<>();
            }
            temp.add(parentId);
            value.put(Contract.Video.COLUMN_PLAYLISTS, new Gson().toJson(temp));
            values[i] = value;
        }
        Uri uri = Contract.Video.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static int setVideoPlaying(@NonNull ContentResolver contentResolver, @NonNull String videoId) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_PLAY_STARTED, 1);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{videoId});
    }

    public static int setVideoPlayed(@NonNull ContentResolver contentResolver, @NonNull String videoId) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_PLAY_STARTED, 1);
        value.put(Contract.Video.COLUMN_IS_PLAY_FINISHED, 1);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{videoId});
    }

    public static int setVideoDeleted(@NonNull ContentResolver contentResolver, @NonNull String videoId) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO, 0);
//        value.put(Contract.Video.COLUMN_IS_PLAY_STARTED, 0);
//        value.put(Contract.Video.COLUMN_IS_PLAY_FINISHED, 0);
//        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
//        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH, "");
//        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL, "");
//        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH, "");
//        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL, "");
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{videoId});
    }

    public static int setAudioDeleted(@NonNull ContentResolver contentResolver, @NonNull String videoId) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO, 0);
//        value.put(Contract.Video.COLUMN_IS_PLAY_STARTED, 0);
//        value.put(Contract.Video.COLUMN_IS_PLAY_FINISHED, 0);
//        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
//        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
//        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH, "");
//        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL, "");
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH, "");
//        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL, "");
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{videoId});
    }

    public static int addVideoToDownloadList(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 1);
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int addAudioToDownloadList(@NonNull ContentResolver contentResolver, @NonNull String fileId, @NonNull String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 1);
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int deleteFromDownloadList(@NonNull ContentResolver resolver, @NonNull String fileId) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE, 0);
        value.put(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE, 0);
        return resolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }


    public static boolean isFileTranscoded(@NonNull ContentResolver resolver, @NonNull String fileId) {
        int isTranscoded = CursorHelper.isFileTranscoded(resolver, fileId);
        return isTranscoded == 1;
    }

    @Nullable
    public static String getLatestVideoTimeStamp(@NonNull ContentResolver contentResolver) {
        Cursor cursor = CursorHelper.getLatestVideo(contentResolver);
        String out = null;
        if (cursor.moveToFirst()) {
            out = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_CREATED_AT));
        }
        cursor.close();
        return out;
    }

    public static boolean isVideoExist(ContentResolver contentResolver, String fileId) {
        return CursorHelper.isVideoExist(contentResolver, fileId);
    }

    public static boolean isAudioExist(ContentResolver contentResolver, String fileId) {
        return CursorHelper.isAudioExist(contentResolver, fileId);
    }

    public static int setPlayTime(ContentResolver contentResolver, String fileId, long position) {
        Logger.i("TIME: save playing time:" + position + " ID:" + fileId);
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_PLAY_TIME, position);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static long getPlayTime(ContentResolver contentResolver, String fileId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        int position = -1;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                position = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_PLAY_TIME));
            }
            cursor.close();
        }
        Logger.i("TIME: get playing time:" + position + " ID:" + fileId);
        return position;
    }

    public static int saveVideoPlayerLink(ContentResolver contentResolver, String fileId, String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_PLAYER_VIDEO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int saveAudioPlayerLink(ContentResolver contentResolver, String fileId, String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_PLAYER_AUDIO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static List<Thumbnail> getThumbnailList(ContentResolver contentResolver, String fileId) {
        List<Thumbnail> thumbnailList = null;
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String thumbnailJson = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_THUMBNAILS));
                thumbnailList = (new Gson().fromJson(thumbnailJson, new TypeToken<List<Thumbnail>>() {
                }.getType()));
            }
            cursor.close();
        }
        return thumbnailList;
    }

    public static String getDownloadAudioPath(ContentResolver contentResolver, String mVideoId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, mVideoId);
        String filePath = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH));
            }
            cursor.close();
        }
        return filePath;
    }

    public static String getDownloadVideoPath(ContentResolver contentResolver, String mVideoId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, mVideoId);
        String filePath = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH));
            }
            cursor.close();
        }
        return filePath;
    }

    public static String getYoutubeLink(ContentResolver contentResolver, String fileId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        String youtubeId = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                youtubeId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_YOUTUBE_ID));
            }
            cursor.close();
        }
        return youtubeId;
    }

    public static String getVideoUrl(ContentResolver contentResolver, String episodeId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, episodeId);
        String url = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                url = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL));
            }
            cursor.close();
        }
        return url;
    }

    public static String getAudioUrl(ContentResolver contentResolver, String episodeId) {
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, episodeId);
        String url = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                url = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL));
            }
            cursor.close();
        }
        return url;
    }

    public static int saveVideoUrl(ContentResolver contentResolver, String episodeId, String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{episodeId});
    }

    public static int saveAudioUrl(ContentResolver contentResolver, String episodeId, String url) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL, url);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{episodeId});
    }

    public static int saveGuests(ContentResolver contentResolver, String episodeId, List<ZobjectData> zObjectList) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_GUESTS, new Gson().toJson(zObjectList));
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{episodeId});
    }

    public static List<ZobjectData> getGuestList(ContentResolver contentResolver, String fileId) {
        List<ZobjectData> guestList = null;
        Cursor cursor = CursorHelper.getVideoCursor(contentResolver, fileId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String guestJson = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_GUESTS));
                guestList = (new Gson().fromJson(guestJson, new TypeToken<List<ZobjectData>>() {
                }.getType()));
            }
            cursor.close();
        }
        return guestList;
    }

    public static void clearAllVideos(@NonNull ContentResolver contentResolver) {
        Uri uri = Contract.Video.CONTENT_URI;
        contentResolver.delete(uri, null, null);
    }

    // Ads
    public static int saveAdVideoTag(ContentResolver contentResolver, String fileId, String tag) {
        Uri uri = Contract.Video.CONTENT_URI;
        ContentValues value = new ContentValues();
        value.put(Contract.Video.COLUMN_AD_VIDEO_TAG, tag);
        return contentResolver.update(uri, value, Contract.Video.COLUMN_ID + " =?", new String[]{fileId});
    }

    public static int updateAdSchedule(ContentResolver contentResolver, String videoId, List<AdvertisingSchedule> adSchedule) {
        Uri uri = Contract.AdSchedule.CONTENT_URI;
        // Delete current schedule
        int resultDelete = contentResolver.delete(uri, Contract.AdSchedule.VIDEO_ID + "=?", new String[] { videoId });
        if (resultDelete == -1) {
            return resultDelete;
        }
        // Insert new records if schedule is not empty
        if (adSchedule != null && adSchedule.size() > 0) {
            ContentValues[] values = new ContentValues[adSchedule.size()];
            for (int i = 0; i < adSchedule.size(); i++) {
                ContentValues value = new ContentValues();
                AdvertisingSchedule item = adSchedule.get(i);
                value.put(Contract.AdSchedule.OFFSET, item.getOffset());
                value.put(Contract.AdSchedule.TAG, item.getTag());
                value.put(Contract.PlaylistVideo.VIDEO_ID, videoId);
                values[i] = value;
            }
            return contentResolver.bulkInsert(uri, values);
        }
        else {
            return 0;
        }
    }

    // Beacons
    public static Uri updateAnalytics(ContentResolver contentResolver, String beacon, AnalyticsDimensions dimensions){
        String videoId = dimensions.getVideoId();

        if (videoId != null) {
            Uri uri = Contract.AnalyticBeacon.CONTENT_URI;
            ContentValues value = new ContentValues();

            // delete current analytics
            int resultDelete = contentResolver.delete(uri, Contract.AnalyticBeacon.VIDEO_ID + "=?", new String[] { videoId });

            String siteId = dimensions.getSiteId();
            String playerId = dimensions.getPlayerId();
            String device = dimensions.getDevice();

            value.put(Contract.AnalyticBeacon.BEACON, beacon);
            value.put(Contract.AnalyticBeacon.VIDEO_ID, videoId);

            if (siteId != null) { value.put(Contract.AnalyticBeacon.SITE_ID, siteId); }
            if (playerId != null) { value.put(Contract.AnalyticBeacon.PLAYER_ID, playerId); }
            if (device != null) { value.put(Contract.AnalyticBeacon.DEVICE, device); }

            return contentResolver.insert(uri, value);
        } else {
            return null;
        }
    }


    // Favorites
    public static String getFavoriteId(ContentResolver contentResolver, String videoId) {
        Cursor cursor = CursorHelper.getFavoriteCursorByVideoId(contentResolver, videoId);
        String favoriteId = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                favoriteId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Favorite.COLUMN_ID));
            }
            cursor.close();
        }
        return favoriteId;
    }

    public static int deleteFavorite(ContentResolver contentResolver, String videoId) {
        Uri uri = Contract.Favorite.CONTENT_URI;
        return contentResolver.delete(uri, Contract.Favorite.COLUMN_VIDEO_ID + "=?", new String[] { videoId});
    }

    // Playlist video
    public static int insertPlaylistVideo(@NonNull ContentResolver contentResolver, @NonNull List<VideoData> videoList, String parentId, int baseNumber) {
        ContentValues[] values = new ContentValues[videoList.size()];
        for (int i = 0; i < videoList.size(); i++) {
            ContentValues value = new ContentValues();
            value.put(Contract.PlaylistVideo.NUMBER, baseNumber + i);
            value.put(Contract.PlaylistVideo.PLAYLIST_ID, parentId);
            value.put(Contract.PlaylistVideo.VIDEO_ID, videoList.get(i).getId());
            values[i] = value;
        }
        Uri uri = Contract.PlaylistVideo.CONTENT_URI;
        return contentResolver.bulkInsert(uri, values);
    }

    public static void clearPlaylistVideo(@NonNull ContentResolver contentResolver, String parentId) {
        Uri uri = Contract.PlaylistVideo.CONTENT_URI;
        String selection = Contract.PlaylistVideo.PLAYLIST_ID + "=?";
        String[] selectionArgs = new String[] { parentId };
        contentResolver.delete(uri, selection, selectionArgs);
    }

}
