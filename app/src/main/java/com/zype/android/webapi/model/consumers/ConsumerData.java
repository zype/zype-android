package com.zype.android.webapi.model.consumers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class ConsumerData {

    @SerializedName("_id")
    @Expose
    private String Id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @Expose
    private String email;
    @Expose
    private String name;
    @SerializedName("password_token")
    @Expose
    private String passwordToken;
    @SerializedName("remember_token")
    @Expose
    private String rememberToken;
    @SerializedName("rss_token")
    @Expose
    private String rssToken;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @SerializedName("subscription_count")
    @Expose
    private int subscriptionCount;
    @SerializedName("linked_devices")
    @Expose
    private List<String> linkedDevices = new ArrayList<>();

    /**
     *
     * @return
     * The Id
     */
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The passwordToken
     */
    public String getPasswordToken() {
        return passwordToken;
    }

    /**
     *
     * @param passwordToken
     * The password_token
     */
    public void setPasswordToken(String passwordToken) {
        this.passwordToken = passwordToken;
    }

    /**
     *
     * @return
     * The rememberToken
     */
    public String getRememberToken() {
        return rememberToken;
    }

    /**
     *
     * @param rememberToken
     * The remember_token
     */
    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    /**
     *
     * @return
     * The rssToken
     */
    public String getRssToken() {
        return rssToken;
    }

    /**
     *
     * @param rssToken
     * The rss_token
     */
    public void setRssToken(String rssToken) {
        this.rssToken = rssToken;
    }

    /**
     *
     * @return
     * The siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     *
     * @param siteId
     * The site_id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     *
     * @return
     * The subscriptionCount
     */
    public int getSubscriptionCount() {
        return subscriptionCount;
    }

    /**
     *
     * @param subscriptionCount
     * The subscription_count
     */
    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    /**
     *
     * @return
     * The linkedDevices
     */
    public List<String> getLinkedDevices() {
        return linkedDevices;
    }

    /**
     *
     * @param linkedDevices
     * The linked_devices
     */
    public void setLinkedDevices(List<String> linkedDevices) {
        this.linkedDevices = linkedDevices;
    }
}
