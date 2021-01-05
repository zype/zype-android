package com.zype.android.analytics;

import java.util.Map;

public interface IAnalytics {
    void trackPlayerEvent(String event, Map<String, Object> attributes);
}
