package com.zype.android.core.settings;

import com.zype.android.utils.StorageUtils;

import android.content.Context;
import android.text.TextUtils;

public class UserSettings extends CommonPreferences {

    public UserSettings(Context context) {
        super(context);
    }

    public void setUserPreferenceStorage(String userPreferenceStorage) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_STORAGE,
                userPreferenceStorage);
    }

    public int getUserPreferenceStorage() {
        String value = get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_STORAGE,
                "");
        if (TextUtils.isEmpty(value)) {
            return StorageUtils.INTERNAL_SD_CARD;
        }
        return Integer.parseInt(value);
    }

    public void setUserPreferenceMaxSize(String userPreferenceMaxSize) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_MAX_DOWNLOADS_TOTAL_SIZE,
                userPreferenceMaxSize);
    }

    public String getUserPreferenceMaxSize(String defaultValue) {
        String value = get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_MAX_DOWNLOADS_TOTAL_SIZE,
                defaultValue);
        return value;
    }

    public void setUserPreferenceDownloadType(String userPreferenceDownloadType) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_TYPE,
                userPreferenceDownloadType);
    }

    public String getUserPreferenceDownloadType(String defaultValue) {
        return get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_TYPE,
                defaultValue);
    }

    public void setUserPreferenceLoadWiFiOnly(boolean userPreferenceLoadWiFiOnly) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_WIFI,
                userPreferenceLoadWiFiOnly);
    }

    public boolean getUserPreferenceLoadWifiOnly(boolean defaultValue) {
        return get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_WIFI,
                defaultValue);
    }

    public void setUserPreferenceAutoRemoveWatchedContent(boolean value) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_AUTO_REMOVE_WATCHED_CONTENT,
                value);
    }

    public boolean getUserPreferenceAutoRemoveWatchedContent(boolean defaultValue) {
        return get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.PREF_AUTO_REMOVE_WATCHED_CONTENT,
                defaultValue);
    }

    public boolean isDownloadAuto(boolean defaultValue) {
        return get(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_AUTO,
                defaultValue);
    }

    public void setUserPreferenceDownloadAuto(boolean value) {
        set(SettingsProvider.getInstance().getAccessTokenResourceOwnerId() + SettingsProvider.DOWNLOAD_AUTO,
                value);
    }
}
