package com.zype.android.analytics;

import android.content.Context;

import com.google.android.exoplayer2.Player;

import java.util.Map;

public interface IAnalytics {

    void onStartVideoSession(Player player, Map<String, Object> attributes);

    void onEndVideoSession();

    void trackPlayerEvent(String event, Map<String, Object> attributes);

}
