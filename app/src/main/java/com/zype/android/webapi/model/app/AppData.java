package com.zype.android.webapi.model.app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 23.01.2018.
 */

public class AppData {
    @Expose
    public String backgroundPlayback;

    @Expose
    public String deviceLinking;

    @Expose
    public String deviceLinkingUrl;

    @Expose
    public String downloads;

    @Expose
    public String downloadsForGuests;

    @SerializedName("_id")
    @Expose
    public String Id;

    @SerializedName("featured_playlist_id")
    @Expose
    public String featuredPlaylistId;

    @SerializedName("native_subscription")
    @Expose
    public String nativeSubscription;

    @SerializedName("native_to_universal_subscription")
    @Expose
    public String nativeToUniversalSubscription;

    @SerializedName("per_page")
    @Expose
    public String perPage;

    @SerializedName("subscribe_to_watch_ad_free")
    @Expose
    public String subscribeToWatchAdFree;

    @Expose
    public String theme;

    @SerializedName("universal_tvod")
    @Expose
    public String universalTVOD;

    @SerializedName("device_linking")
    @Expose
    public String universalSubscription;
}
