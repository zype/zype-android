package com.zype.android;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.StorageUtils;
import com.zype.android.webapi.WebApiManager;

import io.fabric.sdk.android.Fabric;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */


public class ZypeApp extends Application {
    public static final double VOLUME_INCREMENT = 0.05;
    public static final int NOTIFICATION_ID = 13254;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static boolean isLiveTest = false;
    // Monitors Memory Leaks
//    private RefWatcher refWatcher;

    @NonNull
    public static ZypeApp get(@NonNull Context context) {
        return (ZypeApp) context.getApplicationContext();
    }

    @NonNull
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    public static Tracker getTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Fabric
        // TODO: Uncomment following line to use Fabric
//        initFabric();

        // OneSignal
        // TODO: Uncomment following line to use OneSignal
//        OneSignal.startInit(this).init();

        //init external folders storage
        StorageUtils.initStorage(this);
        SettingsProvider.create(this);
        WebApiManager.create(this);

//        AppCompatDelegate.setDefaultNightMode(ZypeSettings.isThemeLight() ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        if (ZypeConfiguration.getTheme(this).equals(ZypeConfiguration.THEME_LIGHT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Google Analytics
        // TODO: Uncomment following line to use Google Analytics
//        initGoogleAnalytics();

        initVideoCastManager();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(ZypeApp.NOTIFICATION_ID);
            }
        });
    }

    private void initFabric() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        else {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(true)
                    .build();
            Fabric.with(fabric);
        }
    }

    private void initVideoCastManager() {
        String applicationId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;

        // initialize VideoCastManager
        VideoCastManager.
                initialize(this, applicationId, null, null).
                setVolumeStep(VOLUME_INCREMENT).
                enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
                        VideoCastManager.FEATURE_LOCKSCREEN |
                        VideoCastManager.FEATURE_WIFI_RECONNECT |
                        VideoCastManager.FEATURE_CAPTIONS_PREFERENCE |
                        VideoCastManager.FEATURE_DEBUGGING);
    }

    private void initGoogleAnalytics() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
//        analytics.enableAutoActivityReports(this);

        tracker = analytics.newTracker(ZypeSettings.GA_TRACKING_ID);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(1, null).build());
    }

//    @NonNull
//    public RefWatcher refWatcher() {
//        return refWatcher;
//    }

//    private void initVideoCastManager() {
//        String applicationId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
//
//        // initialize VideoCastManager
//        VideoCastManager.
//                initialize(this, applicationId, BaseVideoActivity.class, null).
//                setVolumeStep(VOLUME_INCREMENT).
//                enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
//                        VideoCastManager.FEATURE_LOCKSCREEN |
//                        VideoCastManager.FEATURE_WIFI_RECONNECT |
//                        VideoCastManager.FEATURE_CAPTIONS_PREFERENCE |
//                        VideoCastManager.FEATURE_DEBUGGING);
//
//        // this is the default behavior but is mentioned to make it clear that it is configurable.
//        VideoCastManager.getInstance().setNextPreviousVisibilityPolicy(
//                VideoCastController.NEXT_PREV_VISIBILITY_POLICY_DISABLED);
//
//        // this is the default behavior but is mentioned to make it clear that it is configurable.
//        VideoCastManager.getInstance().setCastControllerImmersive(true);
//    }
}
