package com.zype.android.analytics.mediamelon;

import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mediamelon.smartstreaming.MMAnalyticsBridge;
import com.mediamelon.smartstreaming.MMQBRMode;
import com.mediamelon.smartstreaming.MMSmartStreamingExo2;
import com.mediamelon.smartstreaming.MMSmartStreamingInitializationStatus;
import com.mediamelon.smartstreaming.MMSmartStreamingObserver;
import com.zype.android.ZypeApp;
import com.zype.android.analytics.IAnalytics;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Subscription.SubscriptionHelper;

import java.util.Map;

import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_STARTED;
import static com.zype.android.analytics.AnalyticsTags.CONSUMER_ID;
import static com.zype.android.analytics.AnalyticsTags.SUBSCRIPTION_ID;
import static com.zype.android.analytics.AnalyticsTags.VIDEO_ID;
import static com.zype.android.analytics.AnalyticsTags.VIDEO_SITE_ID;
import static com.zype.android.analytics.AnalyticsTags.VIDEO_TITLE;
import static com.zype.android.analytics.AnalyticsTags.VIDEO_URL;

public class MediaMelon implements IAnalytics, MMSmartStreamingObserver {
    private static final String TAG = MediaMelon.class.getSimpleName();

    public void init(Map<String, Object> attributes) {
        MMSmartStreamingExo2.enableLogTrace(true);
        if (!MMSmartStreamingExo2.getRegistrationStatus()) {
            ApplicationInfo appInfo = ZypeApp.getInstance().getApplicationContext().getApplicationInfo();
            String subscriptionId = SubscriptionHelper.getSubscriptionId();
            MMSmartStreamingExo2.registerMMSmartStreaming(
                    "ExoPlayer_" + ExoPlayerLibraryInfo.VERSION,
                    ZypeApp.getInstance().getAppConfiguration().mediaMelonCustomerId(),
                    (String) attributes.get(CONSUMER_ID),       // SubscriberId
                    appInfo.packageName,         // DomainName
                    TextUtils.isEmpty(subscriptionId) ? "" : "subscription",         // SubscriberType
                    ""          // SubscriberTag
            );
            MMSmartStreamingExo2.reportPlayerInfo("MediaMelon", "ExoPlayer/" + ExoPlayerLibraryInfo.VERSION, "1.0");
            MMSmartStreamingExo2.getInstance().setContext(ZypeApp.getInstance().getApplicationContext());
        }
    }

    @Override
    public void onStartVideoSession(Player player, Map<String, Object> attributes) {
        Log.d(TAG, "onStartVideoSession(): ");
        MMSmartStreamingExo2.getInstance().initializeSession(
                (SimpleExoPlayer) player,
                MMQBRMode.QBRModeDisabled,
                (String) attributes.get(VIDEO_URL),
                null,
                "",     // AssetId
                (String) attributes.get(VIDEO_TITLE),     // AssetName
                (String) attributes.get(VIDEO_ID),
                this
        );
        MMSmartStreamingExo2.getInstance().reportCustomMetadata("siteId", (String) attributes.get(VIDEO_SITE_ID));
        MMSmartStreamingExo2.getInstance().reportCustomMetadata("subscriptionId", (String) attributes.get(SUBSCRIPTION_ID));
    }

    @Override
    public void onEndVideoSession() {
        Log.d(TAG, "onEndVideoSession(): ");
        MMSmartStreamingExo2.getInstance().reportPlayerState(false, Player.STATE_ENDED);
    }

    @Override
    public void trackPlayerEvent(String event, Map<String, Object> attributes) {
        Log.d(TAG, "trackPlayerEvent(): " + event);
        switch (event) {
            case EVENT_PLAYBACK_STARTED:
                MMSmartStreamingExo2.getInstance().reportUserInitiatedPlayback();
                break;
        }
    }

    @Override
    public void sessionInitializationCompleted(Integer initCmdId, MMSmartStreamingInitializationStatus status, String description) {
        Log.d(TAG,"sessionInitializationCompleted(): Init Cmd Id " + initCmdId + " completed with the status "+ status + description);
    }
}
