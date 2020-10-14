package com.zype.android.analytics.mediamelon;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.mediamelon.smartstreaming.MMQBRMode;
import com.mediamelon.smartstreaming.MMSmartStreaming;
import com.mediamelon.smartstreaming.MMSmartStreamingExo2;
import com.mediamelon.smartstreaming.MMSmartStreamingInitializationStatus;
import com.mediamelon.smartstreaming.MMSmartStreamingObserver;
import com.zype.android.ZypeApp;
import com.zype.android.analytics.IAnalytics;

import java.util.Map;

public class MediaMelon implements IAnalytics {
    private static final String TAG = MediaMelon.class.getSimpleName();

    public void init() {
        MMSmartStreamingExo2.enableLogTrace(true);
        if (!MMSmartStreamingExo2.getRegistrationStatus()) {
            MMSmartStreamingExo2.registerMMSmartStreaming(
                    "ExoPlayer_" + ExoPlayerLibraryInfo.VERSION,
                    ZypeApp.getInstance().getAppConfiguration().mediaMelonCustomerId(),
                    null,       // SubscriberId
                    "",         // DomainName
                    "",         // SubscriberType
                    ""          // SubscriberTag
            );
            MMSmartStreamingExo2.reportPlayerInfo("MediaMelon", "ExoPlayer/" + ExoPlayerLibraryInfo.VERSION, "1.0");
            MMSmartStreamingExo2.getInstance().setContext(ZypeApp.getInstance().getApplicationContext());
        }
    }

    @Override
    public void trackPlayerEvent(String event, Map<String, Object> attributes) {
        Log.d(TAG, "trackPlayerEvent(): " + event);
    }
}
