package com.zype.android.Billing;

/**
 * Created by Evgeny Cherkasov on 08.11.2018
 */

public interface IMarketplaceManager {
    void onPurchasesUpdated(MarketplaceManager.Response response);
    void onProductDetails(MarketplaceManager.Response response);
    void onPurchase(MarketplaceManager.Response response);
}
