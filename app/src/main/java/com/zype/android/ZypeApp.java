package com.zype.android;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.onesignal.OneSignal;
import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.MarketplaceGateway;
import com.zype.android.Db.Entity.Video;
import com.zype.android.analytics.AnalyticsManager;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.utils.SharedPref;
import com.zype.android.utils.StorageUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.AppParamsBuilder;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.app.AppEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.model.app.AppData;
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.zypeapi.ZypeApi;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */


public class ZypeApp extends MultiDexApplication {
    private static ZypeApp INSTANCE;

    public static final double VOLUME_INCREMENT = 0.05;
    public static final int NOTIFICATION_ID = 100;
    public static final String NOTIFICATION_CHANNEL_ID = "ZypeChannel";

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static boolean isLiveTest = false;
    // Monitors Memory Leaks
//    private RefWatcher refWatcher;

    public static boolean needToLoadData = true;
    public static AppData appData;
    private AppConfiguration appConfiguration;
    public static MarketplaceGateway marketplaceGateway;

    // AWS
    private static PinpointManager pinpointManager;

    @NonNull
    public static ZypeApp get(@NonNull Context context) {
        return (ZypeApp) context.getApplicationContext();
    }

    public static ZypeApp getInstance() {
        return ZypeApp.INSTANCE;
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
        INSTANCE = this;
        createNotificationChannel();

        StorageUtils.initStorage(this);
        SettingsProvider.create(this);
        WebApiManager.create(this);

        WebApiManager.getInstance().subscribe(this);

        initApp();
        // ThreeTenABP library is used for parsing durations
        AndroidThreeTen.init(this);

        // Analytics
        if (appConfiguration.appsflyerAnalytics()) {
            initAppsflyer();
        }
        AnalyticsManager.getInstance().init();

        // Fabric
        // TODO: Uncomment following line to use Fabric
//        initFabric();

        // OneSignal
        if (BuildConfig.ONESIGNAL) {
            initializeOneSignal();
        }

        int nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        if (ZypeConfiguration.getTheme(this).equals(ZypeConfiguration.THEME_DARK)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        if (nightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            Logger.d("Manually instantiating WebView to avoid night mode issue.");
            try {
                new WebView(getApplicationContext());
            }
            catch (Exception e) {
                Logger.e("Got exception while trying to instantiate WebView to avoid night mode issue. Ignoring problem.", e);
            }
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);

        // This is used to create database via Room, but not Content provider. Need to be removed
        // after refactoring to use Room for working with database.
        initDataRepository();

        // Google Analytics
        // TODO: Uncomment following line to use Google Analytics
//        initGoogleAnalytics();

//        initVideoCastManager();
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

        // Setup marketplace connect
        if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(this)
            || (ZypeConfiguration.isNativeTvodEnabled(this))) {
            marketplaceGateway = new MarketplaceGateway(this, ZypeConfiguration.getAppKey(),
                    ZypeConfiguration.getPlanIds());
            marketplaceGateway.setup();
        }

        AuthHelper.onLoggedIn(isLoggedIn -> {
            if (isLoggedIn) {
                loadConsumer();
            }
        });
        SharedPref.init(this);
    }

    @Override
    public void onTerminate() {
        WebApiManager.getInstance().unsubscribe(this);

        super.onTerminate();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initApp() {
        appConfiguration = readAppConfiguration();

        ZypeApi.getInstance().init(ZypeConfiguration.getAppKey());

        appData = null;
        AppParamsBuilder builder = new AppParamsBuilder();
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.APP, builder.build());
    }

    private AppConfiguration readAppConfiguration() {
        AppConfiguration result = null;

        String jsonAppConfiguration = readRawFile(R.raw.zype_app_configuration);
        if (!TextUtils.isEmpty(jsonAppConfiguration)) {
            Gson gson = new Gson();
            result = gson.fromJson(jsonAppConfiguration, AppConfiguration.class);
        }

        return result;
    }

    public AppConfiguration getAppConfiguration() {
        return appConfiguration;
    }

    private void loadConsumer() {
        ConsumerParamsBuilder builder = new ConsumerParamsBuilder()
                .addAccessToken();
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.CONSUMER_GET, builder.build());
    }

    @Subscribe
    public void handleApp(AppEvent event) {
        appData = event.getEventData().getModelData().getAppData();
        Logger.i("handleApp(): App data successfully loaded");
    }

    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        Logger.d("handleConsumer()");
        if (event.getRequest() == WebApiManager.Request.CONSUMER_FORGOT_PASSWORD) {
            return;
        }
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
        String consumerId = data.getConsumerData().getId();
        SettingsProvider.getInstance().saveConsumerId(consumerId);
        SettingsProvider.getInstance().setString(SettingsProvider.CONSUMER_EMAIL, data.getConsumerData().getEmail());
    }

    @Subscribe
    public void handleError(ErrorEvent event) {
        if (event.getEventData() == WebApiManager.Request.APP) {
            Logger.e("handleError(): Retrieving app data failed");
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

    private void initAppsflyer() {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Logger.d("AppsFlyerConversionListener(): attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Logger.d("AppsFlyerConversionListener(): error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    Logger.d("AppsFlyerConversionListener(): attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Logger.d("AppsFlyerConversionListener(): error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().init(appConfiguration.appsflyerAnalyticsDevKey(),
                conversionListener, this);
        AppsFlyerLib.getInstance().startTracking(this);
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

    private void initDataRepository() {
        DataRepository.getInstance(this).getPlaylistsSync(ZypeConfiguration.getRootPlaylistId(this));
    }

    // OneSignal

    private void initializeOneSignal() {
        if (BuildConfig.DEBUG)
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true).setNotificationOpenedHandler(result -> {

            JSONObject jsonObject = result.toJSONObject();

            if (jsonObject.has("notification")) {
                try {

                    JSONObject additionalDataObject = jsonObject.getJSONObject("notification").getJSONObject("payload").getJSONObject("additionalData");

                    if (additionalDataObject.has("videoID")) {
                        //need to check for the id
                        final String videoId = additionalDataObject.optString("videoID");
                        loadPush(videoId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).init();
    }

    private void loadPush(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }

        String playlistId = "";

        Video video = DataRepository.getInstance(this).getVideoSync(videoId);

        if (video != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (!TextUtils.isEmpty(video.serializedPlaylistIds)) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> playlistIds = new Gson().fromJson(video.serializedPlaylistIds, type);

                if (playlistIds.size() > 0)
                    playlistId = playlistIds.get(0);
            }

            intent.putExtra(BundleConstants.VIDEO_ID, videoId);
            intent.putExtra(BundleConstants.PLAYLIST_ID, playlistId);
            startActivity(intent);
        }
    }

    // AWS

    /**
     * Connect to AWS and initialize Pinpoint push notifications service.
     *
     * Called from the `LaunchActivity` because it is required activity context parameter
     *
     * @param context
     */
    public void initAWSPinPoint(Context context) {
        // Initialize the AWS Mobile Client
        AWSMobileClient.getInstance().initialize(context, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Logger.d("AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        // Initialize PinpointManager
        pinpointManager = getPinpointManager(context);
    }

    public static PinpointManager getPinpointManager(Context context) {
        if (pinpointManager == null
                || TextUtils.isEmpty(pinpointManager.getNotificationClient().getDeviceToken())) {
            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    context.getApplicationContext(),
                    AWSMobileClient.getInstance().getCredentialsProvider(),
                    AWSMobileClient.getInstance().getConfiguration());

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (task.isSuccessful()) {
                                final String token = task.getResult().getToken();
                                Logger.d("Registering push notifications token: " + token);
                                pinpointManager.getNotificationClient().registerDeviceToken(token);
                            }
                            else {
                                Logger.e("onComplete(): Failed: " + task.getException().getMessage());
                            }
                        }
                    });
        }
        return pinpointManager;
    }


    // Util

    private String readRawFile(int fileResId) {
        String result;

        AssetManager am = getAssets();
        byte[] buffer = null;
        InputStream is;
        try {
            is = getResources().openRawResource(fileResId);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        result = new String(buffer);
        return result;
    }
}
