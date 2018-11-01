package com.zype.android.ui.player.v2;

import android.content.Context;

import com.akamai.android.exoplayer2loader.AkamaiExoPlayerLoader;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.Map;

/**
 * Created by Evgeny Cherkasov 29/10/2018
 */

public class AnalyticsManager {
    private static final AnalyticsManager sInstance = new AnalyticsManager();
    private AkamaiExoPlayerLoader exoPlayerLoader = null;

    public static AnalyticsManager getInstance() {
        return sInstance;
    }

    private AnalyticsManager() {
    }

    public void trackPlay(Context context, ExoPlayer player, String configPath, String url, Map<String, String> dimensions) {
        exoPlayerLoader = new AkamaiExoPlayerLoader(context, configPath, true);

        for (Map.Entry<String, String> dimension: dimensions.entrySet() ) {
            String key = dimension.getKey();
            String value = dimension.getValue();

             exoPlayerLoader.setData(key, value);
        }

        exoPlayerLoader.initializeLoader(player, url);
    }

    public void trackStop() {
        if (exoPlayerLoader != null) {
            exoPlayerLoader.releaseLoader();
            exoPlayerLoader = null;
        }
    }
}
