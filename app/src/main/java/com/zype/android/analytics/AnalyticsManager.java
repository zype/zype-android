package com.zype.android.analytics;

import android.util.Log;

import com.google.android.exoplayer2.Player;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.analytics.mediamelon.MediaMelon;
import com.zype.android.analytics.segment.SegmentAnalytics;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.webapi.model.video.Thumbnail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zype.android.analytics.AnalyticsTags.CONSUMER_ID;
import static com.zype.android.analytics.AnalyticsTags.SUBSCRIPTION_ID;

public class AnalyticsManager {
    private static final String TAG = AnalyticsManager.class.getSimpleName();

    private static AnalyticsManager instance;

    private List<IAnalytics> analyticsImpls = new ArrayList<>();

    private AnalyticsManager() {}

    public static synchronized AnalyticsManager getInstance() {
        if (instance == null) {
            instance = new AnalyticsManager();
        }
        return instance;
    }

    public void init() {
        analyticsImpls.clear();
        // Segment Analytics
        if (ZypeApp.getInstance().getAppConfiguration().segmentAnalytics()) {
            SegmentAnalytics segmentAnalytics = new SegmentAnalytics();
            segmentAnalytics.init();
            analyticsImpls.add(segmentAnalytics);
            Log.d(TAG, "init(): Segment Analytics is added");
        }
        // MediaMelon
        if (ZypeApp.getInstance().getAppConfiguration().mediaMelon()) {
            MediaMelon mediaMelon = new MediaMelon();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(CONSUMER_ID, SettingsProvider.getInstance().getConsumerId());
            mediaMelon.init(attributes);
            analyticsImpls.add(mediaMelon);
            Log.d(TAG, "init(): MediaMelon is added");
        }
    }

    public void onStartVideoSession(Player player, String url, Video video) {
        Log.d(TAG, "onStartVideoSession(): ");

        Map<String, Object> attributes = new HashMap<>();
        attributes.putAll(getVideoAttributes(video));
        attributes.putAll(getConsumerAttributes());
        attributes.put(AnalyticsTags.VIDEO_URL, url);

        for (IAnalytics analyticsImpl : analyticsImpls) {
            analyticsImpl.onStartVideoSession(player, attributes);
        }
    }

    public void onEndVideoSession() {
        for (IAnalytics analyticsImpl : analyticsImpls) {
            analyticsImpl.onEndVideoSession();
        }
    }

    public void onPlayerEvent(String event, Video video, long position) {
        Log.d(TAG, "onPlayerEvent(): " + event);

        Map<String, Object> attributes = new HashMap<>();
        attributes.putAll(getVideoAttributes(video));
        attributes.put(AnalyticsTags.VIDEO_CURRENT_POSITION, position);

        for (IAnalytics analyticsImpl : analyticsImpls) {
            analyticsImpl.trackPlayerEvent(event, attributes);
        }
    }

    private Map<String, Object> getVideoAttributes(Video video) {
        Map<String, Object> result = new HashMap<>();
        result.put(AnalyticsTags.VIDEO_CREATED_AT, video.createdAt);
        result.put(AnalyticsTags.VIDEO_DURATION, (long) video.duration);
        result.put(AnalyticsTags.VIDEO_ID, video.id);
        result.put(AnalyticsTags.VIDEO_PUBLISHED_AT, video.publishedAt);
        result.put(AnalyticsTags.VIDEO_SITE_ID, video.siteId);
        result.put(AnalyticsTags.VIDEO_TITLE, video.title);
        result.put(AnalyticsTags.VIDEO_UPDATED_AT, video.updatedAt);

        Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
        if (thumbnail != null) {
            result.put(AnalyticsTags.VIDEO_THUMBNAIL, thumbnail.getUrl());
        }

        return result;
    }

    private Map<String, Object> getConsumerAttributes() {
        Map<String, Object> result = new HashMap<>();
        result.put(CONSUMER_ID, SettingsProvider.getInstance().getConsumerId());
        result.put(SUBSCRIPTION_ID, SubscriptionHelper.getSubscriptionId());
        return result;
    }
}
