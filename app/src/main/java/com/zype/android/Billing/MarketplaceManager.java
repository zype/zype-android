package com.zype.android.Billing;

import android.content.Context;

/**
 * Created by Evgeny Cherkasov on 08.11.2018
 */

public abstract class MarketplaceManager {
    private static final String TAG = MarketplaceManager.class.getSimpleName();

    public static final int PRODUCT_TYPE_ALL = 0;
    public static final int PRODUCT_TYPE_ENTITLEMENT = 1;
    public static final int PRODUCT_TYPE_SUBSCRIPTION = 2;

    public static final String PURCHASE_DEESCRIPTION = "Description";
    public static final String PURCHASE_ORIGINAL_DATA = "OriginalData";
    public static final String PURCHASE_PRICE = "Price";
    public static final String PURCHASE_SKU = "Sku";
    public static final String PURCHASE_TITLE = "Title";
    public static final String PURCHASE_TYPE = "Type";

    protected IMarketplaceManager listener;

    public MarketplaceManager(Context context, IMarketplaceManager listener) {
        this.listener = listener;
    }

    /**
     * Request purchases that user has made.
     * 'onPurchasesUpdated()' method of the listener will be called on response from the marketplace.
     *
     * @param productType Product type value, must be one of 'PRODUCT_TYPE_xxx' constants.
     *
     */
    public abstract void getPurchases(int productType);

    /**
     * Request product details from the marketplace for specified object (subscription or video).
     * 'onProductDetails()' method of the listener will be called on response from the marketplace.
     *
     * @param zypeProduct must be Subscription or Video
     *
     */
    public abstract void getProductDetails(Object zypeProduct);

    /**
     * Start purchasing process for scecified sku
     *
     * @param sku Marketplace product id
     */
    public abstract void makePurchase(String sku);


    public class Response {
        private boolean isSuccessful;
        private Object responseData;
        private String errorMessage;

        public Response(boolean isSuccessful, Object responseData, String errorMessage) {
            this.isSuccessful = isSuccessful;
            this.responseData = responseData;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public Object getResponseData() {
            return responseData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
