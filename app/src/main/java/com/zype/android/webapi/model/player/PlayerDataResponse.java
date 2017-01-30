package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class PlayerDataResponse {

    @Expose
    private Body body;
//    @Expose
//    private String body;
    @Expose
    private Device device;
    @SerializedName("revenue_model")
    @Expose
    private RevenueModel revenueModel;
    @Expose
    private Player player;
    @Expose
    private Provider provider;

//    /**
//     *
//     * @return
//     * The body
//     */
//    public String getBody() {
//        return body;
//    }
//
//    /**
//     *
//     * @param body
//     * The body
//     */
//    public void setBody(String body) {
//        this.body = body;
//    }

    /**
     *
     * @return
     * The body
     */
    public Body getBody() {
        return body;
    }

    /**
     *
     * @param body
     * The body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     *
     * @return
     * The device
     */
    public Device getDevice() {
        return device;
    }

    /**
     *
     * @param device
     * The device
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     *
     * @return
     * The revenueModel
     */
    public RevenueModel getRevenueModel() {
        return revenueModel;
    }

    /**
     *
     * @param revenueModel
     * The revenue_model
     */
    public void setRevenueModel(RevenueModel revenueModel) {
        this.revenueModel = revenueModel;
    }

    /**
     *
     * @return
     * The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @param player
     * The player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *
     * @return
     * The provider
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     *
     * @param provider
     * The provider
     */
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
