package com.zype.android.ui.player;

import java.util.Map;
import android.content.Context;
import com.akamai.android.exoplayerloader.AkamaiExoPlayerLoader;
import com.google.android.exoplayer.ExoPlayer;
import com.zype.android.webapi.model.player.AnalyticsDimensions;

import java.util.Dictionary;

/**
 * Created by Andy Zheng on 2/12/18.
 */

public class AnalyticsManager {
    private static final AnalyticsManager sInstance = new AnalyticsManager();
    private AkamaiExoPlayerLoader exoPlayerLoader = null;

    public static AnalyticsManager getInstance() {
        return sInstance;
    }

    private AnalyticsManager() {
    }

    public void trackPlay(Context context, CustomPlayer player, String configPath, String url, Map<String, String> dimensions) {
        exoPlayerLoader = new AkamaiExoPlayerLoader(context, configPath, true);

        for (Map.Entry<String, String> dimension: dimensions.entrySet() ) {
            String key = dimension.getKey();
            String value = dimension.getValue();

            exoPlayerLoader.setData(key, value);
        }

        exoPlayerLoader.initializeLoader(player.getPlayer(), url);
    }

    public void seekTo() {
        if (exoPlayerLoader != null) {
            exoPlayerLoader.seekTo();
        }
    }

    public void trackStop() {
        if (exoPlayerLoader != null) {
            exoPlayerLoader.releaseLoader();
            exoPlayerLoader = null;
        }
    }
}
