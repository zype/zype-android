package com.zype.android.Billing;

import android.content.Context;

import java.util.List;

/**
 * Created by Evgeny Cherkasov on 08.11.2018
 */

public abstract class MarketplaceManager {
    private static final String TAG = MarketplaceManager.class.getSimpleName();

    public static final int PRODUCT_TYPE_ALL = 0;
    public static final int PRODUCT_TYPE_ENTITLEMENT = 1;
    public static final int PRODUCT_TYPE_SUBSCRIPTION = 2;

    public interface PurchasesUpdatedListener {
        void onPurchasesUpdated(MarketplaceManager.PurchasesUpdatedResponse response);
    }

    public interface ProductDetailsListener {
        void onProductDetails(MarketplaceManager.ProductDetailsResponse response);
    }

    public interface PurchaseListener {
        void onPurchase(MarketplaceManager.PurchaseResponse response);
    }

    public MarketplaceManager() {
    }

    /**
     * Request purchases that user has made.
     * 'onPurchasesUpdated()' method of the listener will be called on response from the marketplace.
     *
     * @param productType Product type value, must be one of 'PRODUCT_TYPE_xxx' constants.
     *
     */
    public abstract void getPurchases(int productType, PurchasesUpdatedListener listener);

    /**
     * Request product details from the marketplace by product id.
     * 'onProductDetails()' method of the listener will be called on response from the marketplace.
     *
     * @param sku Product id in the marketplace
     *
     */
    public abstract void getProductDetails(String sku, ProductDetailsListener listener);

    /**
     * Start purchasing process for scecified sku
     *
     * @param sku Marketplace product id
     */
    public abstract void makePurchase(String sku, int productType, PurchaseListener listener);


    public class Response {
        private boolean isSuccessful;
        private String errorMessage;

        public Response(boolean isSuccessful, String errorMessage) {
            this.isSuccessful = isSuccessful;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public class PurchasesUpdatedResponse extends Response {
        private List<PurchaseDetails> purchases;

        public PurchasesUpdatedResponse(boolean isSuccessful, String errorMessage, List<PurchaseDetails> purchases) {
            super(isSuccessful, errorMessage);
            this.purchases = purchases;
        }

        public List<PurchaseDetails> getPurchases() {
            return purchases;
        }
    }

    public class ProductDetailsResponse extends Response {
        private ProductDetails productDetails;

        public ProductDetailsResponse(boolean isSuccessful, String errorMessage, ProductDetails productDetails) {
            super(isSuccessful, errorMessage);
            this.productDetails = productDetails;
        }

        public ProductDetails getProductDetails() {
            return productDetails;
        }
    }

    public class PurchaseResponse extends Response {
        private PurchaseDetails purchaseDetails;

        public PurchaseResponse(boolean isSuccessful, String errorMessage, PurchaseDetails purchaseDetails) {
            super(isSuccessful, errorMessage);
            this.purchaseDetails = purchaseDetails;
        }

        public PurchaseDetails getPurchaseDetails() {
            return purchaseDetails;
        }
    }
}
