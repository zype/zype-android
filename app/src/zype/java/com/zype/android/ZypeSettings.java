package com.zype.android;

/**
 * Created by Evgeny Cherkasov on 18.03.2017.
 */

public class ZypeSettings {
    // Zype app key
    public static final String APP_KEY = "eIqvZ7K4fJLZgQvWGH8oolqKq5G-J1oUPtY07nTunWlZnuOeahcPhqATaXr3zmi_";
    // OAuth credentials
    public static final String CLIENT_ID = "57cc5ab74d238d644523311362c6ccdb4ab4b163c02d703d9d10df1753195912";

//    public static final String CLIENT_SECRET = "06f45687da00bbe3cf51dddc7dbd7a288d1c852cf0b9a6e76e25bb115dcf872c";
    // Playlist
    public static final String ROOT_PLAYLIST_ID = "5c91b0d2be3b9e14f51c450e";

//    // Zype app key
//    public static final String APP_KEY = "DV8vhDumvxPh2ieCfjOzuQQdn0zSOZ-otnGALBnzFAqtIO-feZbsSwKXy3DA_JhK";
//    // OAuth credentials
//    public static final String CLIENT_ID = "fb40dd8a2bc4ae7c7d66054936f185b04d21b8165990ed27c3d64eaaa9146bb6";
////    public static final String CLIENT_SECRET = "068b42f2487e00dde76707668a9edd9dd2fe16e3b5c34ac450419bf43c61034e";
//    // Playlist
//    public static final String ROOT_PLAYLIST_ID = "5807ccdc849e2d0d11000146";


    // Google Analytics Tracking Id
    public static final String GA_TRACKING_ID = "";

    // Social
    public static final String FACEBOOK_ID = "";
    public static final String INSTAGRAM_ID = "";
    public static final String TWITTER_ID = "";
    public static final String WEB_URL = "";

    // Monetization
    public static final boolean NATIVE_SUBSCRIPTION_ENABLED = false;
    public static final boolean NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED = true;
//    public static final String[] PLAN_IDS = new String[] { "5a26c2445d3c19152e003f70" };
    public static final String[] PLAN_IDS = new String[] { "5b366329849e2d140c00002f", "5b366368849e2d141400002b" };
    public static final boolean SUBSCRIBE_TO_WATCH_AD_FREE_ENABLED = false;
    public static final boolean UNIVERSAL_SUBSCRIPTION_ENABLED = false;
    public static final boolean UNIVERSAL_TVOD = false;

    // Features
    public static final boolean AUTOPLAY = true;
    public static final boolean BACKGROUND_AUDIO_PLAYBACK_ENABLED = true;
    public static final boolean BACKGROUND_PLAYBACK_ENABLED = false;
    public static final boolean DEVICE_LINKING = true;
    public static final String DEVICE_LINKING_URL = "https://www.zype.com";
    public static final boolean DOWNLOADS_ENABLED = true;
    public static final boolean DOWNLOADS_ENABLED_FOR_GUESTS = true;
    public static final boolean PLAYLIST_GALLERY_VIEW = true;
    public static final boolean PLAYLIST_GALLERY_HERO_IMAGES = true;
    public static final boolean PLAYLIST_GALLERY_ITEM_TITLES = true;
    public static final boolean SHARE_VIDEO_ENABLED = false;

    public static final String THEME = ZypeConfiguration.THEME_DARK;

//    /**
//     * Google client id and secret are required for native to universal subscription feature.
//     * They are used in request to Zype Bifrost service for verifying subscription.
//     */
//    public static final String GOOGLE_CLIENT_ID = "818854525960-e4mm2df9tfaqtg2o0li62u0rd80tt5mj.apps.googleusercontent.com";
//    public static final String GOOGLE_CLIENT_SECRET = "OfBs7XR_B_DYR1YRcO-ehVPU";
//    public static final String GOOGLE_REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

}

