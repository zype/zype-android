package com.zype.android.Billing;

import com.android.billingclient.api.SkuDetails;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;

public class PurchaseItem {
    public Playlist playlist;
    public SkuDetails product;
    public Video video;
    public boolean verified = false;
}
