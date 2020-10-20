package com.zype.android;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 10.11.2018.
 */

public class AppConfiguration {
    @SerializedName("audioOnlyPlayback")
    public Boolean audioOnlyPlaybackEnabled;

    public Boolean hideFavoritesActionWhenSignedOut;

    public String marketplace;

    // Analytics
    public boolean mediaMelon() {
        return ZypeSettings.ADVANCED_ANALYTICS_ENABLED;
    }

    public String mediaMelonCustomerId() {
        return ZypeSettings.ADVANCED_ANALYTICS_CUSTOMER_ID;
    }

    public boolean segmentAnalytics() {
        return ZypeSettings.SEGMENT_ANALYTICS;
    }

    public String segmentAnalyticsWriteKey() {
        return ZypeSettings.SEGMENT_ANALYTICS_WRITE_KEY;
    }
  
//    /* Setting to "true" enables the new paywall flow, that allows user to open the paywalled
//        video detail screen even the user is not signed in / entitled.
//     */
//    public Boolean updatedPaywalls;
}
