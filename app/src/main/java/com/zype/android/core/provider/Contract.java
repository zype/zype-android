package com.zype.android.core.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.zype.android.BuildConfig;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final String TABLE_NAME_VIDEO = "video";
    public static final String TABLE_NAME_FAVORITE = "favorite";
    public static final String TABLE_NAME_PLAYLIST = "playlist";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private Contract() {
    }

    public static class Video implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_VIDEO).build();
        public static final String TABLE = TABLE_NAME_VIDEO;
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE;

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_AD_VIDEO_TAG = "ad_video_tag";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_DATA_SOURCES = "data_source";
        public static final String COLUMN_DOWNLOAD_AUDIO_PATH = "download_audio_path";
        public static final String COLUMN_DOWNLOAD_AUDIO_URL = "download_audio_url";
        public static final String COLUMN_DOWNLOAD_VIDEO_PATH = "download_video_path";
        public static final String COLUMN_DOWNLOAD_VIDEO_URL = "download_video_url";
        public static final String COLUMN_CREATED_AT = "createdAt";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DISCOVERY_URL = "discovery_url";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_GUESTS = "guest";
        public static final String COLUMN_EPISODE = "episode";
        public static final String COLUMN_EXPIRE_AT = "expire_at";
        public static final String COLUMN_FEATURED = "featured";
        public static final String COLUMN_FOREIGN_ID = "foreign_id";
        public static final String COLUMN_IS_DOWNLOADED_VIDEO = "is_downloaded_video";
        public static final String COLUMN_IS_DOWNLOADED_AUDIO = "is_downloaded_audio";
        public static final String COLUMN_IS_DOWNLOADED_VIDEO_SHOULD_BE = "is_downloaded_video_should_be";
        public static final String COLUMN_IS_DOWNLOADED_AUDIO_SHOULD_BE = "is_downloaded_audio_should_be";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_IS_HIGHLIGHT = "is_highlight";
        public static final String COLUMN_IS_PLAY_STARTED = "is_play_started";
        public static final String COLUMN_IS_PLAY_FINISHED = "is_play_finished";
//        public static final String COLUMN_IS_PLAYING = "is_playing";
        public static final String COLUMN_KEYWORDS = "keywords";
        public static final String COLUMN_MATURE_CONTENT = "mature_content";
        public static final String COLUMN_ON_AIR = "on_air";
        public static final String COLUMN_PLAY_TIME = "play_time";
        public static final String COLUMN_PLAYER_AUDIO_URL = "player_audio_url";
        public static final String COLUMN_PLAYER_VIDEO_URL = "player_video_url";
        public static final String COLUMN_PLAYLISTS = "playlists";
        public static final String COLUMN_PUBLISHED_AT = "published_at";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELATED_PLAYLIST_IDS = "related_playlist_ids";
        public static final String COLUMN_REQUEST_COUNT = "request_count";
        public static final String COLUMN_SEASON = "season";
        public static final String COLUMN_SEGMENT = "segment";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_SITE_ID = "site_id";
        public static final String COLUMN_START_AT = "start_at";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_SUBSCRIPTION_REQUIRED = "subscription_required";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_THUMBNAILS = "thumbnails";
        public static final String COLUMN_IMAGES = "images";
        public static final String COLUMN_TRANSCODED = "transcoded";
        public static final String COLUMN_HULU_ID = "hulu_id";
        public static final String COLUMN_YOUTUBE_ID = "youtube_id";
        public static final String COLUMN_CRUNCHYROLL_ID = "crunchyroll_id";
        public static final String COLUMN_VIDEO_ZOBJECTS = "video_zobject";
        public static final String COLUMN_ZOBJECT_IDS = "zobjectIds";
        public static final String COLUMN_SEGMENTS = "segments";

        public static final String ENTITLEMENT_UPDATED_AT = "entitlement_updated_at";
        public static final String IS_ENTITLED = "is_entitled";
        public static final String PREVIEW_IDS = "preview_ids";
        public static final String PURCHASE_REQUIRED = "PurchaseRequired";
        public static final String COLUMN_REGISTRATION_REQUIRED = "registration_required";
    }

    public static class Favorite implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_FAVORITE).build();
        public static final String TABLE = TABLE_NAME_FAVORITE;
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CONSUMER_ID = "consumer_id";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_DELETED_AT = "deleted_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_VIDEO_ID = "video_id";
    }

    public static class Playlist implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_PLAYLIST).build();
        public static final String TABLE = TABLE_NAME_PLAYLIST;
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_THUMBNAILS = "thumbnails";
        public static final String COLUMN_IMAGES = "images";
        public static final String COLUMN_PARENT_ID = "parent_id";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_DELETED_AT = "deleted_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_PLAYLIST_ITEM_COUNT = "playlist_item_count";
        public static final String COLUMN_THUMBNAIL_LAYOUT = "thumbnail_layout";

    }

    public static class PlaylistVideo implements BaseColumns {
        public static final String TABLE_NAME = "playlist_video";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String ID = "playlist_video_id";
        public static final String NUMBER = "number";
        public static final String PLAYLIST_ID = "playlist_id";
        public static final String VIDEO_ID = "video_id";
    }

    public static class AdSchedule implements BaseColumns {
        public static final String TABLE_NAME = "ad_schedule";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String ID = "ad_schedule_id";
        public static final String OFFSET = "offset";
        public static final String TAG = "tag";
        public static final String VIDEO_ID = "video_id";
    }

    public static class AnalyticBeacon implements BaseColumns {
        public static final String TABLE_NAME = "analytic_beacon";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String ID = "beacon_id";
        public static final String BEACON = "beacon";

        public static final String VIDEO_ID = "video_id";
        public static final String SITE_ID = "site_id";
        public static final String PLAYER_ID = "player_id";
        public static final String DEVICE = "device";
    }
}
