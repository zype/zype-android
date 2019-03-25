package com.zype.android;

/**
 * Created by Evgeny Cherkasov on 18.03.2017.
 */

public class ZypeSettings {
    // Zype app key
    public static final String APP_KEY = "eIqvZ7K4fJLZgQvWGH8oolqKq5G-J1oUPtY07nTunWlZnuOeahcPhqATaXr3zmi_";
    // OAuth credentials
    public static final String CLIENT_ID = "57cc5ab74d238d644523311362c6ccdb4ab4b163c02d703d9d10df1753195912";
//    public static final String CLIENT_SECRET = "<CLIENT_SECRET>";
    // Playlist
    public static final String ROOT_PLAYLIST_ID = "5c91b0d2be3b9e14f51c450e";

    // Google Analytics Tracking Id
    public static final String GA_TRACKING_ID = "";

    // Social
    public static final String FACEBOOK_ID = "";
    public static final String INSTAGRAM_ID = "";
    public static final String TWITTER_ID = "";
    public static final String WEB_URL = "";

    // Monetization
    public static final boolean NATIVE_SUBSCRIPTION_ENABLED = Boolean.valueOf("false");
    // NOTE: This is a gated feature that REQUIRES Zype to configure. Please reach out to Zype Support for help on setting up this feature.
    public static final boolean NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED = Boolean.valueOf("false");
    public static final String[] PLAN_IDS = new String[] { "" };
    public static final boolean SUBSCRIBE_TO_WATCH_AD_FREE_ENABLED = Boolean.valueOf("false");
    public static final boolean UNIVERSAL_SUBSCRIPTION_ENABLED = Boolean.valueOf("false");
    public static final boolean UNIVERSAL_TVOD = Boolean.valueOf("false");

    // Features
    public static final boolean AUTOPLAY = Boolean.valueOf("true");
    public static final boolean BACKGROUND_AUDIO_PLAYBACK_ENABLED = Boolean.valueOf("true");
    public static final boolean BACKGROUND_PLAYBACK_ENABLED = Boolean.valueOf("true");
    public static final boolean DEVICE_LINKING = Boolean.valueOf("false");
    public static final String DEVICE_LINKING_URL = "";
    public static final boolean DOWNLOADS_ENABLED = Boolean.valueOf("false");
    public static final boolean DOWNLOADS_ENABLED_FOR_GUESTS = Boolean.valueOf("false");

    // Following options are not supported by platform app builder.
    public static final boolean PLAYLIST_GALLERY_VIEW = false;
    public static final boolean PLAYLIST_GALLERY_HERO_IMAGES = false;
    public static final boolean PLAYLIST_GALLERY_ITEM_TITLES = false;
    public static final boolean SHARE_VIDEO_ENABLED = false;

    /**
     * Theme of the app.
     * Use following constants for the theme:
     * @see ZypeConfiguration#THEME_LIGHT
     * @see ZypeConfiguration#THEME_DARK
     */
    public static final String THEME = "THEME_LIGHT";

}

