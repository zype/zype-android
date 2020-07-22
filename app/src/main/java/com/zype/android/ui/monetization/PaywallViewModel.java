package com.zype.android.ui.monetization;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.gson.Gson;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.PurchaseItem;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.v2.base.BaseViewModel;
import com.zype.android.zypeapi.model.MarketplaceIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaywallViewModel extends BaseViewModel {
    private static final String TAG = PaywallViewModel.class.getSimpleName();

    private MutableLiveData<Boolean> isPurchased = new MutableLiveData<>();
    private MutableLiveData<List<PurchaseItem>> purchaseItems = new MutableLiveData<>();
    private MutableLiveData<State> state = new MutableLiveData<>();
    private PurchaseItem selectedItem;

    private String playlistId;
    private PaywallType paywallType;
    private String videoId;

    private BillingManager billingManager;

    public enum State {
        READY_FOR_PURCHASE,
        SIGN_IN_REQUIRED,
        SIGNED_IN
    }

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

    public String getPlaylistId() {
        return playlistId;
    }

    public Playlist getPlaylist() {
        return repo.getPlaylistSync(playlistId);
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public Video getVideo() {
        return repo.getVideoSync(videoId);
    }

    //

    public LiveData<Boolean> isPurchased() {
        return isPurchased;
    }

    public LiveData<List<PurchaseItem>> getPurchaseItems() {
        queryPurchaseItems();
        return purchaseItems;
    }

    public LiveData<State> getState() {
        return state;
    }

    public void setState(State state) {
        this.state.setValue(state);
    }

    public PurchaseItem getSelectedItem() {
        return selectedItem;
    }

    private void queryPurchaseItems() {
        switch (paywallType) {
            case PLAYLIST_TVOD:
                Map<String, Object> itemsToPurchase = new HashMap<>();
                List<String> skuList = new ArrayList<>();
                Playlist playlist = getPlaylist();
                if (playlist != null) {
                    String marketplaceId = getPlaylistMarketplaceId(playlist);
                    itemsToPurchase.put(marketplaceId, playlist);
                    skuList.add(marketplaceId);
                }
                // TODO: We can also add a video to 'itemsToPurchase' and 'skuList', if we will need this option
                billingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList,
                        (responseCode, skuDetailsList) -> {
                            if (responseCode != BillingClient.BillingResponse.OK) {
                                Log.e(TAG, "onSkuDetailsResponse(): Error retrieving sku details from Google Play");
                            }
                            else {
                                if (skuDetailsList != null) {
                                    if (skuDetailsList.size() != skuList.size()) {
                                        Log.e(TAG, "onSkuDetailsResponse(): Unexpected number of items (" +
                                                skuDetailsList.size() + ") in Google Play");
                                    }
                                    else {
                                        List<PurchaseItem> result = new ArrayList<>();
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            PurchaseItem item = new PurchaseItem();
                                            item.product = skuDetails;
                                            if (itemsToPurchase.get(skuDetails.getSku()) instanceof Playlist) {
                                                item.playlist = (Playlist) itemsToPurchase.get(skuDetails.getSku());
                                                result.add(item);
                                            }
                                        }
                                        purchaseItems.setValue(result);
                                    }
                                }
                            }
                        });
                break;
        }
    }

    private BillingManager.BillingUpdatesListener createBillingUpdatesListener() {
        return new BillingManager.BillingUpdatesListener() {
            @Override
            public void onBillingClientSetupFinished() {
                Log.d(TAG, "BillingManager::onBillingClientSetupFinished()");
                if (AuthHelper.isLoggedIn()) {
                    state.setValue(State.SIGNED_IN);
                }
                else {
                    state.setValue(State.SIGN_IN_REQUIRED);
                }
            }

            @Override
            public void onConsumeFinished(String token, int result) {
            }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases) {
                Log.d(TAG, "BillingManager::onPurchasesUpdated(): count=" + purchases.size());
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
                    String marketplaceId = getPlaylistMarketplaceId(playlist);
                    Log.d(TAG, "isItemPurchased(): sku=" + marketplaceId);
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(marketplaceId)) {
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }

    // Actions

    public void makePurchase(Activity activity, PurchaseItem item) {
        selectedItem = item;
        if (isPurchased.getValue()) {
            // Force receipt validation
            isPurchased.setValue(true);
            return;
        }
        if (item.playlist != null) {
            Log.d(TAG, "makePurchase(): playlist, sku=" + item.product.getSku());
            billingManager.initiatePurchaseFlow(activity,
                    item.product.getSku(), BillingClient.SkuType.INAPP);
        }
    }

    public void updateEntitlements(DataRepository.IDataLoading listener) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
                repo.loadVideoEntitlements(listener);
            }, 5000);
    }

    // Util

    private String getPlaylistMarketplaceId(@NonNull Playlist playlist) {
        MarketplaceIds marketplaceIds = new Gson().fromJson(playlist.marketplaceIds, MarketplaceIds.class);
        return marketplaceIds.googleplay;
    }
}
