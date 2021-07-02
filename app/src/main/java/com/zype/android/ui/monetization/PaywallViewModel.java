package com.zype.android.ui.monetization;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.gson.Gson;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.MarketplaceGateway;
import com.zype.android.Billing.PurchaseItem;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeApp;
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
//    private MutableLiveData<PurchaseItem> purchaseItem = new MutableLiveData<>();
    private MutableLiveData<State> state = new MutableLiveData<>();
    private PurchaseItem selectedItem;

    private PaywallType paywallType;
    private String playlistId;
    private String videoId;

    private BillingManager billingManager;

    public PaywallViewModel(Application application) {
        super(application);

        isPurchased.setValue(false);
        billingManager = new BillingManager(getApplication(), createBillingUpdatesListener());
    }

    public PaywallType getPaywallType() {
        return paywallType;
    }

    public void setPaywallType(PaywallType paywallType) {
        this.paywallType = paywallType;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public Playlist getPlaylist() {
        return repo.getPlaylistSync(playlistId);
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Video getVideo() {
        return repo.getVideoSync(videoId);
    }

    public LiveData<Boolean> isPurchased() {
        return isPurchased;
    }

    //

    public LiveData<List<PurchaseItem>> getPurchaseItems() {
        queryPurchaseItems();
        return purchaseItems;
    }

//    public LiveData<PurchaseItem> getPurchaseItem() {
//        return purchaseItem;
//    }
//
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
            case PLAYLIST_TVOD: {
                Map<String, Object> itemsToPurchase = new HashMap<>();
                List<String> skuList = new ArrayList<>();
                Playlist playlist = getPlaylist();
                if (playlist != null) {
                    String marketplaceId = getPlaylistMarketplaceId(playlist);
                    itemsToPurchase.put(marketplaceId, playlist);
                    skuList.add(marketplaceId);
                }
                billingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList,
                        (responseCode, skuDetailsList) -> {
                            if (responseCode != BillingClient.BillingResponse.OK) {
                                Log.e(TAG, "onSkuDetailsResponse(): Error retrieving sku details from Google Play");
                            } else {
                                if (skuDetailsList != null) {
                                    if (skuDetailsList.size() != skuList.size()) {
                                        Log.e(TAG, "onSkuDetailsResponse(): Unexpected number of items (" +
                                                skuDetailsList.size() + ") in Google Play");
                                    } else {
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
            case VIDEO_TVOD: {
                List<String> skuList = new ArrayList<>();
                Video video = getVideo();
                if (video != null) {
                    String marketplaceId = getVideoMarketplaceId(video);
                    skuList.add(marketplaceId);
                }
                billingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList,
                        (responseCode, skuDetailsList) -> {
                            if (responseCode != BillingClient.BillingResponse.OK) {
                                Log.e(TAG, "onSkuDetailsResponse(): Error retrieving sku details from Google Play");
                            } else {
                                if (skuDetailsList != null) {
                                    if (skuDetailsList.size() != skuList.size()) {
                                        Log.e(TAG, "onSkuDetailsResponse(): Unexpected number of items (" +
                                                skuDetailsList.size() + ") in Google Play");
                                    } else {
                                        PurchaseItem item = new PurchaseItem();
                                        item.product = skuDetailsList.get(0);
                                        item.video = video;
                                        selectedItem = item;
                                        setState(State.READY_FOR_PURCHASE);
                                    }
                                }
                            }
                        });
                break;
            }
        }
    }

    private BillingManager.BillingUpdatesListener createBillingUpdatesListener() {
        return new BillingManager.BillingUpdatesListener() {
            @Override
            public void onBillingClientSetupFinished() {
                Log.d(TAG, "BillingManager::onBillingClientSetupFinished()");
                if (AuthHelper.isLoggedIn()) {
                    setState(State.SIGNED_IN);
                } else {
                    setState(State.SIGN_IN_REQUIRED);
                }
            }

            @Override
            public void onConsumeFinished(String token, int result) {
            }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases) {
                Log.d(TAG, "BillingManager::onPurchasesUpdated(): count=" + purchases.size());
                if (!purchases.isEmpty()) {
                    if (isItemPurchased(purchases)) {
                        if (state.getValue() == State.PURCHASE_IN_PROGRESS) {
                            verifyVideoPurchase();
                        }
                        isPurchased.setValue(true);
                    }
                }
            }

            @Override
            public void onPurchaseCancelled() {
                Log.d(TAG, "BillingManager::onPurchaseCancelled():");
                if (state.getValue() == State.PURCHASE_IN_PROGRESS) {
                    setState(State.READY_FOR_PURCHASE);
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
            case VIDEO_TVOD: {
                Video video = getVideo();
                if (video != null) {
                    String marketplaceId = getVideoMarketplaceId(video);
                    Log.d(TAG, "isItemPurchased(): sku=" + marketplaceId);
                    for (Purchase purchase : purchases) {
                        if (purchase.getSku().equals(marketplaceId)) {
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return false;
    }

    // Actions

    public void makePurchase(Activity activity, PurchaseItem item) {
        selectedItem = item;
        if (isPurchased.getValue()) {
            setState(State.PURCHASE_IN_PROGRESS);
            verifyVideoPurchase();
            return;
        }
        if (item.playlist != null) {
            Log.d(TAG, "makePurchase(): playlist, sku=" + item.product.getSku());
            billingManager.initiatePurchaseFlow(activity,
                    item.product.getSku(), BillingClient.SkuType.INAPP);
        }
        else if (item.video != null) {
            Log.d(TAG, "makePurchase(): video, sku=" + item.product.getSku());
            setState(State.PURCHASE_IN_PROGRESS);
            if (isItemPurchased(billingManager.getPurchases())) {
                verifyVideoPurchase();
            }
            else {
                billingManager.initiatePurchaseFlow(activity,
                        item.product.getSku(), BillingClient.SkuType.INAPP);
            }
        }
        else {
            Log.d(TAG, "makePurchase(): Either playlist or video must be specified");
        }
    }

    private void verifyVideoPurchase() {
        setState(State.PURCHASE_VERIFYING);
        ZypeApp.marketplaceGateway.verifyVideoPurchase(getVideo(), selectedItem, success -> {
            if (success) {
//                billingManager.consumePurchase(billingManager.getPurchase(selectedItem.product.getSku()));
                updateEntitlements(success1 -> {
                    if (success1) {
                        setState(State.PURCHASE_COMPLETED);
                    }
                    else {
                        // TODO: Maybe add a separate error for failed loading entitlements
                        setState(State.PURCHASE_ERROR_VERIFYING);
                    }
                });
            }
            else {
                setState(State.PURCHASE_ERROR_VERIFYING);
            }
        });
    }

    public void updateEntitlements(DataRepository.IDataLoading listener) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            repo.loadVideoEntitlements(listener);
        }, 5000);
    }

    private String getPlaylistMarketplaceId(@NonNull Playlist playlist) {
        MarketplaceIds marketplaceIds = new Gson().fromJson(playlist.marketplaceIds, MarketplaceIds.class);
        return marketplaceIds.googleplay;
    }

    private String getVideoMarketplaceId(@NonNull Video video) {
        MarketplaceIds marketplaceIds = new Gson().fromJson(video.marketplaceIds, MarketplaceIds.class);
        return marketplaceIds.googleplay;
    }

    // Util

    public enum State {
        ERROR_PRODUCT_NOT_FOUND,
        PURCHASE_COMPLETED,
        PURCHASE_ERROR_VERIFYING,
        PURCHASE_IN_PROGRESS,
        PURCHASE_VERIFYING,
        READY_FOR_PURCHASE,
        SIGN_IN_REQUIRED,
        SIGNED_IN
    }
}