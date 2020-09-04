package com.zype.android.analytics;

import android.util.Log;

import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
import com.zype.android.analytics.segment.SegmentAnalytics;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.webapi.model.video.Thumbnail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        result.put(AnalyticsTags.VIDEO_SERIES_ID, video.seriesId);
        result.put(AnalyticsTags.VIDEO_TITLE, video.title);
        result.put(AnalyticsTags.VIDEO_UPDATED_AT, video.updatedAt);

        Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
        if (thumbnail != null) {
            result.put(AnalyticsTags.VIDEO_THUMBNAIL, thumbnail.getUrl());
        }

        return result;
    }
}
