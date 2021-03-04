package com.zype.android.analytics.segment;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.zype.android.AppConfiguration;
import com.zype.android.ZypeApp;
import com.zype.android.analytics.AnalyticsTags;
import com.zype.android.analytics.IAnalytics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.zype.android.analytics.AnalyticsEvents.EVENT_CONTENT_COMPLETED_25;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_CONTENT_COMPLETED_50;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_CONTENT_COMPLETED_75;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_CONTENT_STARTED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_COMPLETED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_FINISHED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_PAUSED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_RESUMED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_SEEK_COMPLETED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_SEEK_STARTED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYBACK_STARTED;
import static com.zype.android.analytics.AnalyticsEvents.EVENT_PLAYER_ERROR;

public class SegmentAnalytics implements IAnalytics {
    private static final String TAG = SegmentAnalytics.class.getSimpleName();

    public void init() {
        AppConfiguration appConfig = ZypeApp.getInstance().getAppConfiguration();
        // Create an analytics client with the given context and Segment write key.
        Analytics analytics = new Analytics.Builder(ZypeApp.getInstance().getApplicationContext(),
                appConfig.segmentAnalyticsWriteKey())
//                .logLevel(Analytics.LogLevel.DEBUG)
                .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically!
//                .recordScreenViews() // Enable this to record screen views automatically!
                .build();

        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(analytics);
   }

    @Override
    public void trackPlayerEvent(String event, Map<String, Object> attributes) {
        Log.d(TAG, "trackPlayerEvent(): " + event);
        Properties properties = attributesToProperties(attributes);
        trackEvent(event, properties);
    }

    private void trackEvent(String event, Properties properties) {
        Context context = ZypeApp.getInstance().getApplicationContext();
        switch (event) {
            case EVENT_PLAYBACK_STARTED:
                Analytics.with(context).track("Video Content Started", properties);
                Analytics.with(context).track("Video Playback Started", properties);
                break;
            case EVENT_PLAYBACK:
                Analytics.with(context).track("Video Content Playing", properties);
                break;
            case EVENT_PLAYBACK_FINISHED:
                Analytics.with(context).track("Video Content Completed", properties);
                break;
            case EVENT_CONTENT_STARTED:
                Analytics.with(context).track("Video Content Started (after 3 seconds)", properties);
                break;
            case EVENT_CONTENT_COMPLETED_25:
                Analytics.with(context).track("Video Content Completed 25 percent", properties);
                break;
            case EVENT_CONTENT_COMPLETED_50:
                Analytics.with(context).track("Video Content Completed 50 percent", properties);
                break;
            case EVENT_CONTENT_COMPLETED_75:
                Analytics.with(context).track("Video Content Completed 75 percent", properties);
                break;
//            case EVENT_PLAYBACK_STARTED:
//                Analytics.with(context).track("Video Playback Started", properties);
//                break;
            case EVENT_PLAYBACK_COMPLETED:
                Analytics.with(context).track("Video Playback Completed", properties);
                break;
            case EVENT_PLAYBACK_PAUSED:
                Analytics.with(context).track("Video Playback Paused", properties);
                break;
            case EVENT_PLAYBACK_RESUMED:
                Analytics.with(context).track("Video Playback Resumed", properties);
                break;
            case EVENT_PLAYBACK_SEEK_STARTED:
                Analytics.with(context).track("Video Playback Seek Started", properties);
                break;
            case EVENT_PLAYBACK_SEEK_COMPLETED:
                Analytics.with(context).track("Video Playback Seek Completed", properties);
                break;
            case EVENT_PLAYER_ERROR:
                Analytics.with(context).track("Video Player Error", properties);
                break;
        }
    }

    private Properties attributesToProperties(Map<String, Object> attributes) {
        Properties properties = new Properties();

        String contentCmsCategory = null;
        properties.putValue("contentCmsCategory", contentCmsCategory);

        String adType = null;
        properties.putValue("Ad Type", adType);

        String contentShownOnPlatform = "";
        properties.putValue("contentShownOnPlatform", contentShownOnPlatform);

//        String streamingDevice = (String) attributes.get(AnalyticsTags.ATTRIBUTE_CONTENT_ANALYTICS_DEVICE);
        String streamingDevice = Build.MANUFACTURER + " " + Build.MODEL;
        properties.putValue("streaming_device", streamingDevice);

        String videoAccountId = "";
        properties.putValue("videoAccountId", videoAccountId);

        String videoAccountName = "";
        properties.putValue("videoAccountName", videoAccountName);

        String videoAdDuration = null;
        properties.putValue("videoAdDuration", videoAdDuration);

        String videoAdVolume = null;
        properties.putValue("videoAdVolume", videoAdVolume);

        // total_length
        long duration = (Long) attributes.get(AnalyticsTags.VIDEO_DURATION);
        properties.putValue("videoContentDuration", duration);

        // position
        long position = (Long) attributes.get(AnalyticsTags.VIDEO_CURRENT_POSITION) / 1000;
        properties.putValue("videoContentPosition", position);

        long percent = (duration != 0) ? position * 100 / duration : 0;
        properties.putValue("videoContentPercentComplete", percent);

        String videoCreatedAt = (String) attributes.get(AnalyticsTags.VIDEO_CREATED_AT);
        properties.putValue("videoCreatedAt", videoCreatedAt);

        String videoFranchise = (String) attributes.get(AnalyticsTags.VIDEO_SERIES_ID);
        if (TextUtils.isEmpty(videoFranchise)) {
            videoFranchise = "null";
        }
        properties.putValue("videoFranchise", videoFranchise);

        // asset_id
        String videoId = (String) attributes.get(AnalyticsTags.VIDEO_ID);
        properties.putValue("videoId", videoId);

        // title
        String title = (String) attributes.get(AnalyticsTags.VIDEO_TITLE);
        properties.putValue("videoName", title);

        // airdate
        String airdate = (String) attributes.get(AnalyticsTags.VIDEO_PUBLISHED_AT);
        if (TextUtils.isEmpty(airdate)) {
            airdate = null;
        }
        properties.putValue("videoPublishedAt", airdate);

        String videoSyndicate = null;
        properties.putValue("videoSyndicate", videoSyndicate);

        String videoTags = null;
        properties.putValue("videoTags", videoTags);

        String videoThumbnail = (String) attributes.get(AnalyticsTags.VIDEO_THUMBNAIL);
        properties.putValue("videoThumbnail", videoThumbnail);

        String videoUpdatedAt = (String) attributes.get(AnalyticsTags.VIDEO_UPDATED_AT);
        properties.putValue("videoUpdatedAt", videoUpdatedAt);

        Log.d(TAG, "attributesToProperties(): " + properties.toString());
        return properties;
    }
}
