package com.zype.android.webapi.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class TokenInfo {

    @SerializedName("resource_owner_id")
    @Expose
    private String resourceOwnerId;
    @Expose
    private List<String> scopes = new ArrayList<>();
    @SerializedName("expires_in_seconds")
    @Expose
    private long expiresInSeconds;
    @SerializedName("application")
    @Expose
    private ApplicationData applicationData;
    @SerializedName("created_at")
    @Expose
    private long createdAt;

    /**
     *
     * @return
     * The resourceOwnerId
     */
    public String getResourceOwnerId() {
        return resourceOwnerId;
    }

    /**
     *
     * @param resourceOwnerId
     * The resource_owner_id
     */
    public void setResourceOwnerId(String resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }

    /**
     *
     * @return
     * The scopes
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     *
     * @param scopes
     * The scopes
     */
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    /**
     *
     * @return
     * The expiresInSeconds
     */
    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    /**
     *
     * @param expiresInSeconds
     * The expires_in_seconds
     */
    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    /**
     *
     * @return
     * The application
     */
    public ApplicationData getApplicationData() {
        return applicationData;
    }

    /**
     *
     * @param applicationData
     * The application
     */
    public void setApplicationData(ApplicationData applicationData) {
        this.applicationData = applicationData;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
