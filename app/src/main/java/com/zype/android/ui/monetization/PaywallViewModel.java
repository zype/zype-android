package com.zype.android.ui.monetization;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.gson.Gson;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.zypeapi.model.MarketplaceIds;

import java.util.List;

public class PaywallViewModel extends BaseViewModel {
    private static final String TAG = PaywallViewModel.class.getSimpleName();

    private MutableLiveData<Boolean> isPurchased = new MutableLiveData<>();

    private String playlistId;
    private PaywallType paywallType;

    private BillingManager billingManager;

    public PaywallViewModel(Application application) {
        super(application);

        isPurchased.setValue(false);

        billingManager = new BillingManager(getApplication(), createBillingUpdatesListener());
    }

    public void setPaywallType(PaywallType paywallType) {
        this.paywallType = paywallType;
    }

    public PaywallType getPaywallType() {
        return paywallType;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public LiveData<Boolean> isPurchased() {
        return isPurchased;
    }

    private BillingManager.BillingUpdatesListener createBillingUpdatesListener() {
        return new BillingManager.BillingUpdatesListener() {
            @Override
            public void onBillingClientSetupFinished() {

            }

            @Override
            public void onConsumeFinished(String token, int result) {

            }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases) {
                if (isItemPurchased(purchases)) {
                    isPurchased.setValue(true);
                }
            }
        };
    }

    private boolean isItemPurchased(List<Purchase> purchases) {
        switch (paywallType) {
            case PLAYLIST_TVOD:
                Playlist playlist = repo.getPlaylistSync(playlistId);
                if (playlist != null) {
                    MarketplaceIds marketplaceIds = new Gson().fromJson(playlist.marketplaceIds, MarketplaceIds.class);
                    Log.d(TAG, "isItemPurchased(): sku=" + marketplaceIds.googleplay);
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(marketplaceIds.googleplay)) {
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }

    // Actions

    public void makePurchase(Activity activity) {
        switch (paywallType) {
            case PLAYLIST_TVOD:
                Playlist playlist = repo.getPlaylistSync(playlistId);
                if (playlist != null) {
                    MarketplaceIds marketplaceIds = new Gson().fromJson(playlist.marketplaceIds, MarketplaceIds.class);
                    Log.d(TAG, "makePurchase(): sku=" + marketplaceIds.googleplay);
                    billingManager.initiatePurchaseFlow(activity, marketplaceIds.googleplay, BillingClient.SkuType.INAPP);
                }
                break;
        }
    }


}
