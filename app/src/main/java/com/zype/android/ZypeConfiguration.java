package com.zype.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.webapi.model.app.AppData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 23.01.2018.
 */

public class ZypeConfiguration {
    private static final String PREFERENCE_AUTOPLAY = "ZypeAutoplay";
    private static final String PREFERENCE_BACKGROUND_AUDIO_PLAYBACK = "ZypeBackgroundAudioPlayback";
    private static final String PREFERENCE_BACKGROUND_PLAYBACK = "ZypeBackgroundPlayback";
    private static final String PREFERENCE_DEVICE_LINKING = "ZypeDeviceLinking";
    private static final String PREFERENCE_DEVICE_LINKING_URL = "ZypeDeviceLinkingUrl";
    private static final String PREFERENCE_DOWNLOADS = "ZypeDownloads";
    private static final String PREFERENCE_DOWNLOADS_FOR_GUESTS = "ZypeDownloadsForGuests";
    private static final String PREFERENCE_NATIVE_SUBSCRIPTION = "ZypeNativeSubscription";
    private static final String PREFERENCE_NATIVE_TVOD = "ZypeNativeTvod";
    private static final String PREFERENCE_NATIVE_TO_UNIVERSAL_SUBSCRIPTION = "ZypeNativeToUniversalSubscription";
    private static final String PREFERENCE_PLAYLIST_GALLERY_VIEW = "ZypePlaylistGalleryView";
    private static final String PREFERENCE_PLAYLIST_GALLERY_HERO_IMAGES = "ZypePlaylistGalleryHeroImages";
    private static final String PREFERENCE_PLAYLIST_GALLERY_ITEM_TITLES = "ZypePlaylistGalleryItemTitles";
    private static final String PREFERENCE_ROOT_PLAYLIST_ID = "ZypeRootPlaylistId";
    private static final String PREFERENCE_SUBSCRIBE_TO_WATCH_AD_FREE = "ZypeSubscribeToWatchAdFree";
    private static final String PREFERENCE_THEME = "ZypeTheme";
    private static final String PREFERENCE_UNIVERSAL_SUBSCRIPTION = "ZypeUniversalSubscription";
    private static final String PREFERENCE_UNIVERSAL_TVOD = "ZypeUniversalTVOD";

    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void update(AppData appData, Context context) {
        clear(context);

        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (!TextUtils.isEmpty(appData.nativeSubscription)) {
            editor.putBoolean(PREFERENCE_NATIVE_SUBSCRIPTION, Boolean.valueOf(appData.nativeSubscription));
        }
        if (!TextUtils.isEmpty(appData.nativeToUniversalSubscription)) {
            editor.putBoolean(PREFERENCE_NATIVE_TO_UNIVERSAL_SUBSCRIPTION, Boolean.valueOf(appData.nativeToUniversalSubscription));
        }
        if (!TextUtils.isEmpty(appData.featuredPlaylistId)) {
            editor.putString(PREFERENCE_ROOT_PLAYLIST_ID, appData.featuredPlaylistId);
        }
        if (!TextUtils.isEmpty(appData.subscribeToWatchAdFree)) {
            editor.putBoolean(PREFERENCE_SUBSCRIBE_TO_WATCH_AD_FREE, Boolean.valueOf(appData.subscribeToWatchAdFree));
        }
        if (!TextUtils.isEmpty(appData.theme)) {
            editor.putString(PREFERENCE_THEME, appData.theme);
        }
        if (!TextUtils.isEmpty(appData.universalSubscription)) {
            editor.putBoolean(PREFERENCE_UNIVERSAL_SUBSCRIPTION, Boolean.valueOf(appData.universalSubscription));
        }
        if (!TextUtils.isEmpty(appData.universalTVOD)) {
            editor.putBoolean(PREFERENCE_UNIVERSAL_TVOD, Boolean.valueOf(appData.universalTVOD));
        }
        // Features
        if (!TextUtils.isEmpty(appData.autoplay)) {
            editor.putBoolean(PREFERENCE_AUTOPLAY, Boolean.valueOf(appData.autoplay));
        }
        if (!TextUtils.isEmpty(appData.backgroundPlayback)) {
            editor.putBoolean(PREFERENCE_BACKGROUND_PLAYBACK, Boolean.valueOf(appData.backgroundPlayback));
        }
        if (!TextUtils.isEmpty(appData.downloads)) {
            editor.putBoolean(PREFERENCE_DOWNLOADS, Boolean.valueOf(appData.downloads));
        }
        if (!TextUtils.isEmpty(appData.downloadsForGuests)) {
            editor.putBoolean(PREFERENCE_DOWNLOADS_FOR_GUESTS, Boolean.valueOf(appData.downloadsForGuests));
        }

        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
//        editor.remove(PREFERENCE_BACKGROUND_PLAYBACK);
//        editor.remove(PREFERENCE_DEVICE_LINKING);
//        editor.remove(PREFERENCE_DEVICE_LINKING_URL);
//        editor.remove(PREFERENCE_DOWNLOADS);
//        editor.remove(PREFERENCE_DOWNLOADS_FOR_GUESTS);
//        editor.remove(PREFERENCE_NATIVE_SUBSCRIPTION);
//        editor.remove(PREFERENCE_NATIVE_TO_UNIVERSAL_SUBSCRIPTION);
//        editor.remove(PREFERENCE_ROOT_PLAYLIST_ID);
//        editor.remove(PREFERENCE_SUBSCRIBE_TO_WATCH_AD_FREE);
//        editor.remove(PREFERENCE_THEME);
//        editor.remove(PREFERENCE_UNIVERSAL_SUBSCRIPTION);
//        editor.remove(PREFERENCE_UNIVERSAL_TVOD);
        editor.apply();
    }

    private static boolean getBooleanPreference(String key, boolean defaultValue, Context context) {
        SharedPreferences prefs = getPreferences(context);
        if (prefs.contains(key)) {
            return prefs.getBoolean(key, defaultValue);
        }
        else {
            return defaultValue;
        }
    }

    private static String getStringPreference(String key, String defaultValue, Context context) {
        SharedPreferences prefs = getPreferences(context);
        if (prefs.contains(key)) {
            return prefs.getString(key, defaultValue);
        }
        else {
            return defaultValue;
        }
    }

    public static String getAppKey() {
        return ZypeSettings.APP_KEY;
    }

    public static String getTheme(Context context) {
        return getStringPreference(PREFERENCE_THEME, ZypeSettings.THEME, context);
    }

    public static String getRootPlaylistId(Context context) {
        return getStringPreference(PREFERENCE_ROOT_PLAYLIST_ID, ZypeSettings.ROOT_PLAYLIST_ID, context);
    }

    // Monetization
    //
    public static boolean isNativeSubscriptionEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_NATIVE_SUBSCRIPTION, ZypeSettings.NATIVE_SUBSCRIPTION_ENABLED, context);
    }

    public static boolean isNativeToUniversalSubscriptionEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_NATIVE_TO_UNIVERSAL_SUBSCRIPTION, ZypeSettings.NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED, context);
    }

    public static List<String> getPlanIds() {
        return Arrays.asList(ZypeSettings.PLAN_IDS);
    }

    public static boolean isSubscribeToWatchAdFreeEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_SUBSCRIBE_TO_WATCH_AD_FREE, ZypeSettings.SUBSCRIBE_TO_WATCH_AD_FREE_ENABLED, context);
    }

    public static boolean isUniversalSubscriptionEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_UNIVERSAL_SUBSCRIPTION, ZypeSettings.UNIVERSAL_SUBSCRIPTION_ENABLED, context);
    }

    public static boolean isUniversalTVODEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_UNIVERSAL_TVOD, ZypeSettings.UNIVERSAL_TVOD, context);
    }

    public static boolean isNativeTvodEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_NATIVE_TVOD, ZypeSettings.NATIVE_TVOD, context);
    }

    // Features
    //
    public static boolean autoplayEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_AUTOPLAY, ZypeSettings.AUTOPLAY, context);
    }

    public static boolean isBackgroundPlaybackEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_BACKGROUND_PLAYBACK, ZypeSettings.BACKGROUND_PLAYBACK_ENABLED, context);
    }

    public static boolean isBackgroundAudioPlaybackEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_BACKGROUND_AUDIO_PLAYBACK, ZypeSettings.BACKGROUND_AUDIO_PLAYBACK_ENABLED, context);
    }


    public static boolean isDeviceLinkingEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_DEVICE_LINKING, ZypeSettings.DEVICE_LINKING, context);
    }

    public static String getDeviceLinkingUrl(Context context) {
        return getStringPreference(PREFERENCE_DEVICE_LINKING_URL, ZypeSettings.DEVICE_LINKING_URL, context);
    }

    public static boolean isDownloadsEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_DOWNLOADS, ZypeSettings.DOWNLOADS_ENABLED, context);
    }

    public static boolean isDownloadsForGuestsEnabled(Context context) {
        return getBooleanPreference(PREFERENCE_DOWNLOADS_FOR_GUESTS, ZypeSettings.DOWNLOADS_ENABLED_FOR_GUESTS, context);
    }

    public static boolean playlistGalleryView(Context context) {
        return getBooleanPreference(PREFERENCE_PLAYLIST_GALLERY_VIEW, ZypeSettings.PLAYLIST_GALLERY_VIEW, context);
    }

    public static boolean playlistGalleryHeroImages(Context context) {
        return getBooleanPreference(PREFERENCE_PLAYLIST_GALLERY_HERO_IMAGES, ZypeSettings.PLAYLIST_GALLERY_HERO_IMAGES, context);
    }

    public static boolean playlistGalleryItemTitles(Context context) {
        return getBooleanPreference(PREFERENCE_PLAYLIST_GALLERY_ITEM_TITLES, ZypeSettings.PLAYLIST_GALLERY_ITEM_TITLES, context);
    }

    public static boolean playlistGalleryItemInlineTitles() {
        return ZypeSettings.PLAYLIST_GALLERY_ITEM_INLINE_TITLES;
    }

    public static boolean playerPaywall() {
        return ZypeSettings.PLAYER_PAYWALL_ENABLED;
    }

    public static boolean trailers() {
        return ZypeSettings.TRAILERS_ENABLED;
    }
}
