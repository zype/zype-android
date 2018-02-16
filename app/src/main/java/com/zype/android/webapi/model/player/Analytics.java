package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;

/**
 * Created by Andy Zheng on 2/13/18.
 */

public class Analytics {
    @Expose
    private String beacon;

    @Expose
    private AnalyticsDimensions dimensions = new AnalyticsDimensions();

    /**
     * @return
     * The beacon
     */
    public String getBeacon() { return beacon; }

    /**
     * @param beacon
     * The beacon
     */
    public void setBeacon(String beacon) { this.beacon = beacon; }

    /**
     * @return
     * The dimensions
     */
    public AnalyticsDimensions getDimensions() { return dimensions; }

    /**
     * @param dimensions
     * The dimensions
     */
    public void setDimensions(AnalyticsDimensions dimensions) { this.dimensions = dimensions; }

}
