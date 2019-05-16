package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 14.04.2017.
 */

public class PlayerData {
    @Expose
    public Body body;

    @Expose
    public Device device;

    @SerializedName("revenue_model")
    @Expose
    public RevenueModel revenueModel;

    @Expose
    public Player player;

    @Expose
    public Provider provider;
}
