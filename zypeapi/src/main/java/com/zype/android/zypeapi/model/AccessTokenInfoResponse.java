package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 13.04.2017.
 */

public class AccessTokenInfoResponse {
    @SerializedName("resource_owner_id")
    @Expose
    public String resourceOwnerId;

    @Expose
    public List<String> scopes = new ArrayList<>();

    @SerializedName("expires_in_seconds")
    @Expose
    public long expiresInSeconds;

    @SerializedName("application")
    @Expose
    public ApplicationData applicationData;

    @SerializedName("created_at")
    @Expose
    public long createdAt;
}
