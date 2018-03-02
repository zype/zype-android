package com.zype.android.core.settings;

import android.content.Context;
import android.text.TextUtils;

import com.zype.android.ZypeSettings;
import com.zype.android.utils.Logger;
import com.zype.android.utils.StorageUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.AuthParamsBuilder;
import com.zype.android.webapi.model.auth.ApplicationData;
import com.zype.android.webapi.model.settings.LiveStreamSettings;
import com.zype.android.webapi.model.settings.LiveStreamSettingsData;
import com.zype.android.webapi.model.settings.Settings;
import com.zype.android.webapi.model.settings.SettingsData;
import com.zype.android.webapi.model.zobjects.Picture;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SettingsProvider extends CommonPreferences {

    public static final String DOWNLOADS_ENABLED = "DownloadsEnabled";
    public static final String DOWNLOADS_ENABLED_FOR_GUESTS = "DownloadsEnabledForGuests";
    public static final String GOOGLE_ADVERTISING_ID = "GoogleAdvertisingId";
    public static final String THEME_LIGHT = "ThemeLight";

    public static final String IS_FIRST_LAUNCH = "IsFirstLaunch";

    // Closed captions
    public static final String CLOSED_CAPTIONS_ENABLED = "ClosedCaptionsEnabled";
    public static final String SELECTED_CLOSED_CAPTIONS_TRACK = "SelectedClosedCaptionsTrack";

    private static final HashMap<String, Object> defaultValues = new HashMap<>();

    private static final String DEFAULT_STRING = "";
    private static final long DEFAULT_LONG = -1L;
    private static final String LOCAL_TOKEN_TYPE = "TOKEN_TYPE";
    private static final String LOCAL_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String LOCAL_EXPIRES_IN = "EXPIRES_IN";
    private static final String LOCAL_REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String LOCAL_SCOPE = "SCOPE";
    private static final String ACCESS_TOKEN_EXPIRES_IN_SECONDS = "ACCESS_TOKEN_EXPIRES_IN_SECONDS";
    private static final String ACCESS_TOKEN_EXPIRATION_DATE = "ACCESS_TOKEN_EXPIRATION_DATE";
    private static final String ACCESS_TOKEN_CREATED_AT = "ACCESS_TOKEN_CREATED_AT";
    private static final String ACCESS_TOKEN_RESOURCE_OWNER_ID = "ACCESS_TOKEN_RESOURCE_OWNER_ID";
    private static final String ACCESS_TOKEN_APPLICATION_UID = "ACCESS_TOKEN_APPLICATION_UID";
    private static final String ACCESS_TOKEN_SCOPES = "ACCESS_TOKEN_SCOPES";
    private static final String ON_AIR_PICTURE_URL = "ON_AIR_PICTURE_URL";
    private static final String OFF_AIR_PICTURE_URL = "OFF_AIR_PICTURE_URL";
    private static final String NOT_SUBSCRIBED_PICTURE_URL = "NOT_SUBSCRIBED_PICTURE_URL";
    private static final String NO_FAVORITES_MESSAGE = "NO_FAVORITES_MESSAGE";
    private static final String NO_FAVORITES_MESSAGE_NOT_LOGGED_IN = "NO_FAVORITES_MESSAGE_NOT_LOGGED_IN";
    private static final String NO_DOWNLOADS_MESSAGE = "NO_DOWNLOADS_MESSAGE";
    private static final String SHARE_MESSAGE = "SHARE_MESSAGE";
    private static final String SHARE_SUBJECT = "SHARE_SUBJECT";

    public static final String DOWNLOAD_WIFI = "DOWNLOAD_WIFI";
    public static final String DOWNLOAD_TYPE = "DOWNLOAD_TYPE";
    public static final String DOWNLOAD_AUTO = "DOWNLOAD_AUTO";

    private static final String IS_SHOW_LIVE = "IS_SHOW_LIVE";
    private static final String NOTIFICATION_LIVE = "NOTIFICATION_LIVE";
    private static final String PREF_COOKIES = "PREF_COOKIES";
    private static final String PREF_DOWNLOAD_LATEST_ONE = "PREF_DOWNLOAD_LATEST_ONE";
    public static final String SETTINGS_MIGRATION = "SETTINGS_MIGRATION";
    public static final String RESERVED = "RESERVED";

    private static final String CONSUMER_SUBSCRIPTION_COUNT = "CONSUMER_SUBSCRIPTION_COUNT";
    private static final String CONSUMER_ID = "ConsumerId";
    private static final String SUBSCRIBE_URL = "SUBSCRIBE_URL";
    public static final String FILE_LENGTH = "FILE_";
    private static SettingsProvider sInstance;
    private UserSettings mUserSettings;


    public static final String PREF_MAX_DOWNLOADS_TOTAL_SIZE = "PREF_MAX_DOWNLOAD_TOTAL_SIZE";
    public static final String PREF_AUTO_REMOVE_WATCHED_CONTENT = "PREF_AUTO_REMOVE_WATCHED_CONTENT";
    public static final String PREF_STORAGE = "PREF_STORAGE";
    public static String PREF_ALL_DOWNLOADS_SIZE = "PREF_ALL_DOWNLOADS_SIZE";
    private long ONE_GIGABYTE = 1 * 1024 * 1024 * 1024;

    // Live stream
    private static final String LIVE_STREAM_LIMIT = "LiveStreamLimit";
    private static final String LIVE_STREAM_MESSAGE = "LiveStreamMessage";
    private static final String LIVE_STREAM_REFRESH_RATE = "LiveStreamRefreshRate";
    private static final String LIVE_STREAM_TIME = "LiveStreamTime";

    // default values
    public static final String DEFAULT = "DEFAULT";
    public static final String DEFAULT_MAX_UNLIMITED = "11";
    public static final String DEFAULT_TYPE_AUDIO = "0";
    public static final boolean DEFAULT_LOAD_WIFI_ONLY = true;
    public static final boolean DEFAULT_AUTO_REMOVE_WATCHED_CONTENT = false;

//    // svetliy addition
//    public static final boolean DEFAULT_DOWNLOADS_ENABLED = true;

    public SettingsProvider(Context context) {
        super(context);
        mUserSettings = new UserSettings(context);
    }

    public static void create(Context c) {
        if (sInstance != null) {
            throw new IllegalStateException("Already created!");
        }
        sInstance = new SettingsProvider(c);
        initDefaultValues();
    }

    public static synchronized SettingsProvider getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call create() first");
        }
        return sInstance;
    }

    private static void initDefaultValues() {
        defaultValues.put(DOWNLOADS_ENABLED, ZypeSettings.DOWNLOADS_ENABLED);
        defaultValues.put(DOWNLOADS_ENABLED_FOR_GUESTS, ZypeSettings.DOWNLOADS_ENABLED_FOR_GUESTS);
//        defaultValues.put(THEME_LIGHT, ZypeSettings.THEME_LIGHT);

        defaultValues.put(IS_FIRST_LAUNCH, true);

        defaultValues.put(CLOSED_CAPTIONS_ENABLED, false);
    }

    public void logout() {
        saveAccessToken(DEFAULT_STRING);
        saveExpiresIn(DEFAULT_LONG);
        saveRefreshToken(DEFAULT_STRING);
        saveScope(DEFAULT_STRING);
        saveTokenType(DEFAULT_STRING);
        saveAccessToken(DEFAULT_STRING);
        saveAccessTokenApplicationUid(DEFAULT_STRING);
        saveAccessTokenCreatedAt(DEFAULT_LONG);
        saveAccessTokenExpiration(DEFAULT_LONG);
        saveAccessTokenResourceOwnerId(DEFAULT_STRING);
        saveSubscriptionCount(0);
        saveConsumerId(DEFAULT_STRING);
    }

    public void saveAccessToken(String accessToken) {
        set(LOCAL_ACCESS_TOKEN, accessToken);
    }

    public void saveExpiresIn(long expiresIn) {
        set(LOCAL_EXPIRES_IN, expiresIn);
    }

    public void saveRefreshToken(String refreshToken) {
        set(LOCAL_REFRESH_TOKEN, refreshToken);
    }

    public void saveScope(String scope) {
        set(LOCAL_SCOPE, scope);
    }

    public void saveTokenType(String tokenType) {
        set(LOCAL_TOKEN_TYPE, tokenType);
    }

    public String getAccessToken() {
        return get(LOCAL_ACCESS_TOKEN, DEFAULT_STRING);
    }

    private long getExpiresIn() {
        try {
            return get(LOCAL_EXPIRES_IN, DEFAULT_LONG);
        } catch (ClassCastException e) {
            return DEFAULT_LONG;
        }
    }

    private CharSequence getTokenType() {
        return get(LOCAL_TOKEN_TYPE, DEFAULT_STRING);
    }

    private CharSequence getScope() {
        return get(LOCAL_SCOPE, DEFAULT_STRING);
    }

    public String getRefreshToken() {
        return get(LOCAL_REFRESH_TOKEN, DEFAULT_STRING);
    }

    public boolean isLogined() {

        // Check if Access Token needs to be refreshed
        if (isAccessTokenExpiring() == true) {
            WebApiManager webApiManager = WebApiManager.getInstance();
            AuthParamsBuilder builder = new AuthParamsBuilder();
            builder.addClientId();
            builder.addClientSecret();
            builder.addRefreshToken(SettingsProvider.getInstance().getRefreshToken());
            builder.addGrandType("refresh_token");
            webApiManager.executeRequest(WebApiManager.Request.AUTH_REFRESH_ACCESS_TOKEN, builder.build());
        }

        return !TextUtils.isEmpty(getAccessToken())
                && !TextUtils.isEmpty(getRefreshToken())
                && !TextUtils.isEmpty(getScope())
                && !TextUtils.isEmpty(getTokenType())
                && getExpiresIn() != -1L;
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken())
                && !TextUtils.isEmpty(getRefreshToken())
                && !TextUtils.isEmpty(getScope())
                && !TextUtils.isEmpty(getTokenType())
                && getExpiresIn() != -1L;
    }

    public void saveAccessTokenApplication(ApplicationData applicationData) {
        saveAccessTokenApplicationUid(applicationData.getUid());
    }

    private void saveAccessTokenApplicationUid(String uid) {
        set(ACCESS_TOKEN_APPLICATION_UID, uid);
    }

    private void saveShareSubject(String shareSubject) {
        set(SHARE_SUBJECT, shareSubject);
    }

    public String getShareSubject() {
        return get(SHARE_SUBJECT, DEFAULT_STRING);
    }

    private void saveShareMessage(String shareMessage) {
        set(SHARE_MESSAGE, shareMessage);
    }

    public String getShareMessage() {
        return get(SHARE_MESSAGE, DEFAULT_STRING);
    }

    private void saveNoDownloadsMessage(String noDownloadsMessage) {
        set(NO_DOWNLOADS_MESSAGE, noDownloadsMessage);
    }

    public String getNoDownloadsMessage() {
        return get(NO_DOWNLOADS_MESSAGE, DEFAULT_STRING);
    }

    private void saveNoFavoritesMessage(String noFavoritesMessage) {
        set(NO_FAVORITES_MESSAGE, noFavoritesMessage);
    }

    public String getNoFavoritesMessage() {
        return get(NO_FAVORITES_MESSAGE, DEFAULT_STRING);
    }

    private void saveNoFavoritesMessageNotLoggedIn(String noFavoritesMessageNotLoggedIn) {
        set(NO_FAVORITES_MESSAGE_NOT_LOGGED_IN, noFavoritesMessageNotLoggedIn);
    }

    public String getNoFavoritesMessageNotLoggedIn() {
        return get(NO_FAVORITES_MESSAGE_NOT_LOGGED_IN, DEFAULT_STRING);
    }

    private void saveNotSubscribedPicture(String notSubscribedPictureUrl) {
        set(NOT_SUBSCRIBED_PICTURE_URL, notSubscribedPictureUrl);
    }

    public String getNotSubscribedPictureUrl() {
        return get(NOT_SUBSCRIBED_PICTURE_URL, DEFAULT_STRING);
    }

    private void saveOffAirPictureUrl(String offAirPictureUrl) {
        set(OFF_AIR_PICTURE_URL, offAirPictureUrl);
    }

    public String getOffAirPictureUrl() {
        return get(OFF_AIR_PICTURE_URL, DEFAULT_STRING);
    }

    private void saveOnAirPictureUrl(String onAirPictureUrl) {
        set(ON_AIR_PICTURE_URL, onAirPictureUrl);
    }

    public String getOnAirPictureUrl() {
        return get(ON_AIR_PICTURE_URL, DEFAULT_STRING);
    }

    public boolean isShowLive() {
        return get(IS_SHOW_LIVE, false);
    }

    public void saveShowLive(boolean isShowLive) {
        set(IS_SHOW_LIVE, isShowLive);
    }

    public void saveAccessTokenCreatedAt(long createdAt) {
        set(ACCESS_TOKEN_CREATED_AT, createdAt);
    }

    public void saveAccessTokenExpiration(long expiresInSeconds) {
        saveAccessTokenExpiresInSeconds(expiresInSeconds);
        saveAccessTokenExpirationDateInSeconds(expiresInSeconds);
    }

    public void saveAccessTokenExpiresInSeconds(long expiresInSeconds) {
        set(ACCESS_TOKEN_EXPIRES_IN_SECONDS, expiresInSeconds);
    }

    private void saveAccessTokenExpirationDateInSeconds(long expiresInSeconds) {
        long createdAt = get(ACCESS_TOKEN_CREATED_AT, DEFAULT_LONG);
        set(ACCESS_TOKEN_EXPIRATION_DATE, (Long)(createdAt + expiresInSeconds));
    }

    private boolean isAccessTokenExpiring() {
        long currentTimeInSeconds = new Date().getTime()/1000L;
        long expirationDateInSeconds = get(ACCESS_TOKEN_EXPIRATION_DATE, DEFAULT_LONG);
        long acceptableBuffer = 60; // 1 minute
        long interval = expirationDateInSeconds - currentTimeInSeconds;

        if (interval < acceptableBuffer) {
            return true;
        }

        return false;
    }

    public void saveAccessTokenResourceOwnerId(String resourceOwnerId) {
        set(ACCESS_TOKEN_RESOURCE_OWNER_ID, resourceOwnerId);
    }

    public String getAccessTokenResourceOwnerId() {
        return get(ACCESS_TOKEN_RESOURCE_OWNER_ID, "USER");
    }

    public void saveAccessTokenScopes(List<String> scopes) {
        HashSet<String> set = new HashSet<>();
        set.addAll(scopes);
        set(ACCESS_TOKEN_SCOPES, set);
    }

    public void saveSettingsFromServer(Settings settings) {
        SettingsData firstSettings = settings.getSettingsData().get(0);
        saveShareSubject(firstSettings.getShareSubject());
        saveShareMessage(firstSettings.getShareMessage());
        saveNoDownloadsMessage(firstSettings.getNoDownloadsMessage());
        saveNoFavoritesMessage(firstSettings.getNoFavoritesMessage());
        saveNoFavoritesMessageNotLoggedIn(firstSettings.getNoFavoritesMessageNotLoggedIn());
        saveSubscribeUrl(firstSettings.getSubscribeUrl());
        List<Picture> pictureList = firstSettings.getPictures();
        for (Picture picture : pictureList) {
            switch (picture.getTitle()) {
                case "not-subscribed":
                    saveNotSubscribedPicture(picture.getUrl());
                    break;
                case "off-air":
                    saveOffAirPictureUrl(picture.getUrl());
                    break;
                case "on-air":
                    saveOnAirPictureUrl(picture.getUrl());
                    break;
                default:
                    throw new RuntimeException("Unknown data from server:" + picture.getTitle());

            }
        }
    }

    private void saveSubscribeUrl(String subscribeUrl) {
        set(SUBSCRIBE_URL, subscribeUrl);
    }

    public String getSubscribeUrl() {
        return get(SUBSCRIBE_URL, DEFAULT_STRING);
    }

    public boolean isDownloadVideo() {
        String value = getUserPreferenceDownloadType();
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return Integer.valueOf(value) == 1;
    }

    public boolean showPushNotification() {
        return get(NOTIFICATION_LIVE, true);
    }

    public void saveCookies(HashSet<String> cookies) {
        set(PREF_COOKIES, cookies);
    }

    public HashSet<String> getCookies() {
        return get(PREF_COOKIES, new HashSet<String>());
    }

    public void setDownloadLatestOne(boolean b) {
        set(PREF_DOWNLOAD_LATEST_ONE, b);
    }

    public boolean isDownloadLatestOne() {
        return get(PREF_DOWNLOAD_LATEST_ONE, false);
    }

    public void saveSubscriptionCount(int subscriptionCount) {
        set(CONSUMER_SUBSCRIPTION_COUNT, subscriptionCount);
    }

    public int getSubscriptionCount() {
        return get(CONSUMER_SUBSCRIPTION_COUNT, 0);
    }

    public String getConsumerId() {
        return get(CONSUMER_ID, "");
    }

    public void saveConsumerId(String id) {
        set(CONSUMER_ID, id);
    }

    public boolean isDownloadAuto() {
        boolean defaultValue = get(DEFAULT + DOWNLOAD_AUTO, false);
        if (isLogined()) {
            return mUserSettings.isDownloadAuto(defaultValue);
        } else {
            return defaultValue;
        }
    }

    // Preference auto download
    public void setUserPreferenceDownloadAuto(boolean userPreferenceStorage) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceDownloadAuto(userPreferenceStorage);
        } else {
            set(DEFAULT + DOWNLOAD_AUTO, userPreferenceStorage);
        }
    }

    /* return in Gigabytes max size to store data */
    public long getDownloadsLimitInBytes() {
        String value = getUserPreferenceMaxSize();
        if (TextUtils.isEmpty(value) || value.equals(DEFAULT_MAX_UNLIMITED)) {
            return Long.MAX_VALUE;
        }
        float gigabyteValue = Float.parseFloat(value);
        return (long) (gigabyteValue * ONE_GIGABYTE);
    }

    // Preference storage
    public void setUserPreferenceStorage(String userPreferenceStorage) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceStorage(userPreferenceStorage);
        } else {
            set(DEFAULT + PREF_STORAGE, userPreferenceStorage);
        }
    }

    public int getUserPreferenceStorage() {
        if (isLoggedIn()) {
            return mUserSettings.getUserPreferenceStorage();
        } else {
            String value = get(DEFAULT + PREF_STORAGE, "");
            if (TextUtils.isEmpty(value)) {
                return StorageUtils.INTERNAL_SD_CARD;
            }
            return Integer.parseInt(value);
        }
    }

    // Preference max downloads size
    public void setUserPreferenceMaxSize(String userPreferenceMaxSize) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceMaxSize(userPreferenceMaxSize);
        } else {
            set(DEFAULT + PREF_MAX_DOWNLOADS_TOTAL_SIZE, userPreferenceMaxSize);
        }
    }

    public String getUserPreferenceMaxSize() {
        String defaultValue = get(DEFAULT + PREF_MAX_DOWNLOADS_TOTAL_SIZE, DEFAULT_MAX_UNLIMITED);
        if (isLoggedIn()) {
            return mUserSettings.getUserPreferenceMaxSize(defaultValue);
        } else {
            return defaultValue;
        }
    }

    // Preference downloads type
    public void setUserPreferenceDownloadType(String userPreferenceDownloadType) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceDownloadType(userPreferenceDownloadType);
        } else {
            set(DEFAULT + DOWNLOAD_TYPE, userPreferenceDownloadType);
        }
    }

    public String getUserPreferenceDownloadType() {
        String defaultValue = get(DEFAULT + DOWNLOAD_TYPE, DEFAULT_TYPE_AUDIO);
        if (isLogined()) {
            return mUserSettings.getUserPreferenceDownloadType(defaultValue);
        } else {
            return defaultValue;
        }
    }

    // Preference max wifi only
    public void setUserPreferenceLoadWiFiOnly(boolean userPreferenceLoadWiFiOnly) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceLoadWiFiOnly(userPreferenceLoadWiFiOnly);
        } else {
            set(DEFAULT + DOWNLOAD_WIFI, userPreferenceLoadWiFiOnly);
        }
    }

    public boolean isUserPreferenceLoadWifiOnlySet() {
        boolean defaultValue = get(DEFAULT + DOWNLOAD_WIFI, DEFAULT_LOAD_WIFI_ONLY);
        if (isLoggedIn()) {
            return mUserSettings.getUserPreferenceLoadWifiOnly(defaultValue);
        } else {
            return defaultValue;
        }
    }

    // Preference AutoRemove content
    public boolean isUserPreferenceAutoRemoveWatchedContentSet() {
        boolean defaultValue = get(DEFAULT + PREF_AUTO_REMOVE_WATCHED_CONTENT, DEFAULT_AUTO_REMOVE_WATCHED_CONTENT);
        if (isLogined()) {
            return mUserSettings.getUserPreferenceAutoRemoveWatchedContent(defaultValue);
        } else {
            return defaultValue;
        }
    }

    public void setUserPreferenceAutoRemoveWatchedContent(boolean userPreferenceAutoRemoveWatchedContent) {
        if (isLogined()) {
            mUserSettings.setUserPreferenceAutoRemoveWatchedContent(userPreferenceAutoRemoveWatchedContent);
        } else {
            set(DEFAULT + PREF_AUTO_REMOVE_WATCHED_CONTENT, userPreferenceAutoRemoveWatchedContent);
        }
    }

    public void makeSettingsMigration() {
        String userBeforeMigrationDownloadType = get(DOWNLOAD_TYPE, DEFAULT_TYPE_AUDIO);
        setUserPreferenceDownloadType(userBeforeMigrationDownloadType);
        boolean userBeforeMigrationWifiOnlyFlag = get(DOWNLOAD_WIFI, true);
        setUserPreferenceLoadWiFiOnly(userBeforeMigrationWifiOnlyFlag);
        boolean userBeforeMigrationAutoDownloadContent = get(DOWNLOAD_AUTO, false);
        setUserPreferenceDownloadAuto(userBeforeMigrationAutoDownloadContent);
    }

    public void addToReserved(long fileLength) {
        long beforeReserved = get(DEFAULT + RESERVED, (long) 0);
        set(DEFAULT + RESERVED, fileLength + beforeReserved);
    }

    public void saveToReserved(long fileLength) {
        set(DEFAULT + RESERVED, fileLength);
    }

    public long getReserved() {
        return get(DEFAULT + RESERVED, (long) 0);
    }

    public void saveFileLength(String fileId, long fileLength) {
        set(FILE_LENGTH + fileId, fileLength);
    }

    public long getFileLength(String fileId) {
        return get(FILE_LENGTH + fileId, (long) 0);
    }

    public void deleteFromReserved(long length) {
        long beforeReserved = get(DEFAULT + RESERVED, (long) 0);
        set(DEFAULT + RESERVED, beforeReserved - length);
        Logger.v("after deletion = " + get(DEFAULT + RESERVED, (long) 0));
    }

    public boolean isMigrated() {
        return get(DEFAULT + SETTINGS_MIGRATION, false);
    }

    public void setMigrated(boolean migrated) {
        set(DEFAULT + SETTINGS_MIGRATION, migrated);
    }

//    public boolean isDownloadsEnabled() {
//        return DEFAULT_DOWNLOADS_ENABLED;
//    }

    // Live stream settings
    //
    public void saveLiveStreamSettings(LiveStreamSettings settings) {
        LiveStreamSettingsData data = settings.getData().get(0);
        saveLiveStreamLimit(data.getLimit());
        saveLiveStreamMessage(data.getMessage());
        saveLiveStreamRefreshRate(data.getRefreshRate());
    }

    public void saveLiveStreamLimit(int limit) {
        set(LIVE_STREAM_LIMIT, limit);
    }

    public int getLiveStreamLimit() {
        return get(LIVE_STREAM_LIMIT, 600);
    }

    public void saveLiveStreamMessage(String message) {
        set(LIVE_STREAM_MESSAGE, message);
    }

    public String getLiveStreamMessage() {
        return get(LIVE_STREAM_MESSAGE, "");
    }

    public void saveLiveStreamRefreshRate(String refreshRate) {
        set(LIVE_STREAM_REFRESH_RATE, refreshRate);
    }

    public String getLiveStreamRefreshRate() {
        return get(LIVE_STREAM_REFRESH_RATE, "");
    }

    public void saveLiveStreamTime(int minutes) {
        set(LIVE_STREAM_TIME, minutes);
    }

    public int getLiveStreamTime() {
        return get(LIVE_STREAM_TIME, 0);
    }

    // //////////
    // General get/set methods
    //
    public boolean getBoolean(String key) {
        return get(key, (Boolean) defaultValues.get(key));
    }

    public void setBoolean(String key, boolean value) {
        set(key, value);
    }

    public String getString(String key) {
        return get(key, (String) defaultValues.get(key));
    }

    public void setString(String key, String value) {
        set(key, value);
    }

}
