package com.zype.android.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.zype.android.core.provider.Contract;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class ZypeDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "zype.db";

    private static final String TYPE_TEXT = " TEXT ";
    private static final String TYPE_INTEGER = " INTEGER ";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL ";

    public ZypeDatabase(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @NonNull
    private static String getCreateVideoTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.Video.TABLE + "("
                + Contract.Video.COLUMN_ID + TYPE_TEXT + NOT_NULL + " PRIMARY KEY " + COMMA_SEP
                + Contract.Video.COLUMN_ACTIVE + TYPE_INTEGER + NOT_NULL + COMMA_SEP
                + Contract.Video.COLUMN_AD_VIDEO_TAG + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_CATEGORY + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_COUNTRY + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_CREATED_AT + TYPE_TEXT + NOT_NULL + COMMA_SEP
                + Contract.Video.COLUMN_DATA_SOURCES + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DOWNLOAD_AUDIO_URL + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DOWNLOAD_VIDEO_URL + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DESCRIPTION + TYPE_TEXT + NOT_NULL + COMMA_SEP
                + Contract.Video.COLUMN_DISCOVERY_URL + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_DURATION + TYPE_INTEGER + NOT_NULL + COMMA_SEP
                + Contract.Video.COLUMN_GUESTS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_EPISODE + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_EXPIRE_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_FEATURED + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_FOREIGN_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_FAVORITE + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_HIGHLIGHT + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_PLAY_STARTED + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_IS_PLAY_FINISHED + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_KEYWORDS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_MATURE_CONTENT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_ON_AIR + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_PLAY_TIME + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_PLAYER_AUDIO_URL + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_PLAYER_VIDEO_URL + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_PLAYLISTS + TYPE_TEXT + COMMA_SEP
//                + Contract.Video.COLUMN_PLAYING_POSITION + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_PUBLISHED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_RATING + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_RELATED_PLAYLIST_IDS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_REQUEST_COUNT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_SEASON + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_SEGMENT + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_SHORT_DESCRIPTION + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_SITE_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_START_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_STATUS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_SUBSCRIPTION_REQUIRED + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_TITLE + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_UPDATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_TRANSCODED + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.COLUMN_THUMBNAILS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_IMAGES + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_HULU_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_YOUTUBE_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_CRUNCHYROLL_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_VIDEO_ZOBJECTS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_ZOBJECT_IDS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_SEGMENTS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.ENTITLEMENT_UPDATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Video.IS_ENTITLED + TYPE_INTEGER + COMMA_SEP
                + Contract.Video.PREVIEW_IDS + TYPE_TEXT + COMMA_SEP
                + Contract.Video.PURCHASE_REQUIRED + TYPE_TEXT + COMMA_SEP
                + Contract.Video.COLUMN_REGISTRATION_REQUIRED + TYPE_TEXT
                + ");";
    }

    private static String getCreateFavoriteTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.Favorite.TABLE + "("
                + Contract.Favorite.COLUMN_ID + TYPE_TEXT + NOT_NULL + " PRIMARY KEY " + COMMA_SEP
                + Contract.Favorite.COLUMN_CONSUMER_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Favorite.COLUMN_CREATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Favorite.COLUMN_DELETED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Favorite.COLUMN_UPDATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Favorite.COLUMN_VIDEO_ID + TYPE_TEXT
                + ");";
    }

    private static String getCreatePlaylistTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.Playlist.TABLE + "("
                + Contract.Playlist.COLUMN_ID + TYPE_TEXT + NOT_NULL + " PRIMARY KEY " + COMMA_SEP
                + Contract.Playlist.COLUMN_TITLE + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_PARENT_ID + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_CREATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_DELETED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_UPDATED_AT + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_PRIORITY + TYPE_INTEGER + COMMA_SEP
                + Contract.Playlist.COLUMN_PLAYLIST_ITEM_COUNT + TYPE_INTEGER + COMMA_SEP
                + Contract.Playlist.COLUMN_THUMBNAILS + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_THUMBNAIL_LAYOUT + TYPE_TEXT + COMMA_SEP
                + Contract.Playlist.COLUMN_IMAGES + TYPE_TEXT
                + ");";
    }

    private static String getCreatePlaylistVideoTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.PlaylistVideo.TABLE_NAME + "("
                + Contract.PlaylistVideo.ID + TYPE_INTEGER + " PRIMARY KEY " + COMMA_SEP
                + Contract.PlaylistVideo.NUMBER + TYPE_INTEGER + COMMA_SEP
                + Contract.PlaylistVideo.PLAYLIST_ID + TYPE_TEXT + COMMA_SEP
                + Contract.PlaylistVideo.VIDEO_ID + TYPE_TEXT
                + ");";
    }

    private static String getCreateAdScheduleTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.AdSchedule.TABLE_NAME + "("
                + Contract.AdSchedule.ID + TYPE_INTEGER + " PRIMARY KEY " + COMMA_SEP
                + Contract.AdSchedule.OFFSET + TYPE_INTEGER + COMMA_SEP
                + Contract.AdSchedule.TAG + TYPE_TEXT + COMMA_SEP
                + Contract.AdSchedule.VIDEO_ID + TYPE_TEXT
                + ");";
    }

    private static String getCreateAnalyticsBeaconTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + Contract.AnalyticBeacon.TABLE_NAME + "("
                + Contract.AnalyticBeacon.ID + TYPE_INTEGER + " PRIMARY KEY " + COMMA_SEP
                + Contract.AnalyticBeacon.BEACON + TYPE_TEXT + COMMA_SEP
                + Contract.AnalyticBeacon.VIDEO_ID + TYPE_TEXT + COMMA_SEP
                + Contract.AnalyticBeacon.SITE_ID + TYPE_TEXT + COMMA_SEP
                + Contract.AnalyticBeacon.PLAYER_ID + TYPE_TEXT + COMMA_SEP
                + Contract.AnalyticBeacon.DEVICE + TYPE_TEXT
                + ");";
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(getCreateVideoTableQuery());
        db.execSQL(getCreateFavoriteTableQuery());
        db.execSQL(getCreatePlaylistTableQuery());
        db.execSQL(getCreatePlaylistVideoTableQuery());
        db.execSQL(getCreateAdScheduleTableQuery());
        db.execSQL(getCreateAnalyticsBeaconTableQuery());
    }


    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.Video.DELETE_TABLE);
        db.execSQL(Contract.AdSchedule.DELETE_TABLE);
        db.execSQL(Contract.AnalyticBeacon.DELETE_TABLE);
        // TODO: After implementing delete all tables call 'onCreate' to upgrade all tables without duplicate the code
        db.execSQL(getCreateVideoTableQuery());
        db.execSQL(getCreateAdScheduleTableQuery());
        db.execSQL(getCreateAnalyticsBeaconTableQuery());
    }
}
