package com.zype.android;

import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 18.03.2017.
 */

// TODO: Provide valid app settings
//
public class ZypeSettings {
    // Zype app key
    // TODO: Provide your Zype app key
    public static final String APP_KEY = "iBjj-jnjT12tQGmyeOR9op8_RjsGmT1Nq5OEETkbxOXnNmDE1m9MrB0wpti0__9l";
    // OAuth credentials
    // TODO: Provide your Zype authentication credentials
    public static final String CLIENT_ID = "62f1d247b4c5e77b6111d9a9ed8b3b64bab6be66cc8b7513a928198083cd1c72";
    public static final String CLIENT_SECRET = "06f45687da00bbe3cf51dddc7dbd7a288d1c852cf0b9a6e76e25bb115dcf872c";
    // Playlist
    // TODO: Provide your Zype root playlist id
    public static final String ROOT_PLAYLIST_ID = "577e65c85577de0d1000c1ee";
    // Google Analytica Tracking Id
    // TODO: To use Google Analytics provide your GA tracking id
    public static final String GA_TRACKING_ID = "";
    // Social
    // TODO: Add your real social network ids and web url
    public static final String FACEBOOK_ID = "";
    public static final String INSTAGRAM_ID = "";
    public static final String TWITTER_ID = "";
    public static final String WEB_URL = "";

    // App features
    // TODO: Update app feature settings
    public static final boolean BACKGROUND_PLAYBACK_ENABLED = false;
    public static final boolean DOWNLOADS_ENABLED = true;
    public static final boolean DOWNLOADS_ENABLED_FOR_GUESTS = true;
    public static final boolean SHARE_VIDEO_ENABLED = false;
    public static final boolean THEME_LIGHT = true;
    public static final boolean UNIVERSAL_SUBSCRIPTION_ENABLED = true;
    public static final boolean NATIVE_SUBSCRIPTION_ENABLED = true;

    public static boolean isDownloadsEnabled() {
        return SettingsProvider.getInstance().getBoolean(SettingsProvider.DOWNLOADS_ENABLED);
    }

    public static boolean isDownloadsEnabledForGuests() {
        return SettingsProvider.getInstance().getBoolean(SettingsProvider.DOWNLOADS_ENABLED_FOR_GUESTS);
    }

    public static boolean isThemeLight() {
        return SettingsProvider.getInstance().getBoolean(SettingsProvider.THEME_LIGHT);
    }
}

