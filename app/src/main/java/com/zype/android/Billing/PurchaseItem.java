package com.zype.android.Billing;

import com.android.billingclient.api.SkuDetails;
import com.zype.android.Db.Entity.Playlist;

public class PurchaseItem {
    public Playlist playlist;
    public SkuDetails product;
    public boolean verified = false;
}
