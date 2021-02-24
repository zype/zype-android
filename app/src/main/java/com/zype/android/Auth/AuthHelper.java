package com.zype.android.Auth;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.Observer;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class AuthHelper {
    private static final String TAG = AuthHelper.class.getSimpleName();

    public static void onLoggedIn(Observer<Boolean> observer) {
        AuthLiveData.getInstance().observeForever(observer);
        AuthLiveData.getInstance().updateLoginState();
    }

    public static void onLoginStateChanged() {
        AuthLiveData.getInstance().updateLoginState();
    }

    public static boolean isLoggedIn() {
        return SettingsProvider.getInstance().isLoggedIn();
    }

    public static String getAccessToken() {
        return SettingsProvider.getInstance().getAccessToken();
    }

    public static boolean isAccessTokenExpired() {
        long currentTimeInSeconds = new Date().getTime() / 1000L;
        long expirationDateInSeconds = SettingsProvider.getInstance().getAccessTokenExpirationDate();
        long acceptableBuffer = 60; // 1 minute
        long interval = expirationDateInSeconds - currentTimeInSeconds;

        return interval < acceptableBuffer;
    }
}
