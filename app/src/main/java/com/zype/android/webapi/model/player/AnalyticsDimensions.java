package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Andy Zheng on 2/13/18.
 */

public class AnalyticsDimensions {
    @SerializedName("video_id")
    @Expose
    private String videoId;

    @SerializedName("site_id")
    @Expose
    private String siteId;

    @SerializedName("player_id")
    @Expose
    private String playerId;

    @Expose
    private String device;

    /**
     * @return
     * The video id
     */
    public String getVideoId() { return videoId; }

    /**
     * @param videoId
     * The video id
     */
    public void setVideoId(String videoId) { this.videoId = videoId; }

    /**
     * @return
     * The site id
     */
    public String getSiteId() { return siteId; }

    /**
     * @param siteId
     * The site id
     */
    public void setSiteId(String siteId) { this.siteId = siteId; }

    /**
     * @return
     * The player id
     */
    public String getPlayerId() { return playerId; }

    /**
     * @param playerId
     * The player id
     */
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    /**
     * @return
     * The device
     */
    public String getDevice() { return device; }

    /**
     * @param device
     * The device
     */
    public void setDevice(String device) { this.device = device; }
}
