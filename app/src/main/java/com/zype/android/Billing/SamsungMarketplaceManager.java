package com.zype.android.Billing;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.samsung.android.sdk.iap.lib.BuildConfig;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnGetOwnedListListener;
import com.samsung.android.sdk.iap.lib.listener.OnGetProductsDetailsListener;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.OwnedProductVo;
import com.samsung.android.sdk.iap.lib.vo.ProductVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Evgeny Cherkasov on 12.11.2018
 */

public class SamsungMarketplaceManager extends MarketplaceManager {

    private IapHelper iapHelper  = null;

    public SamsungMarketplaceManager(Context context, IMarketplaceManager listener) {
        super(context, listener);

        iapHelper = IapHelper.getInstance(context);

        if (!BuildConfig.DEBUG)
            iapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_PRODUCTION);
        else
            iapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_TEST);


    }

    @Override
    public void getPurchases(int productType) {
        String samsungProductType = null;
        switch (productType) {
            case PRODUCT_TYPE_ALL:
                samsungProductType = IapHelper.PRODUCT_TYPE_ALL;
                break;
            case PRODUCT_TYPE_ENTITLEMENT:
                samsungProductType = IapHelper.PRODUCT_TYPE_ITEM;
                break;
            case PRODUCT_TYPE_SUBSCRIPTION:
                samsungProductType = IapHelper.PRODUCT_TYPE_SUBSCRIPTION;
                break;
        }
        if (samsungProductType == null) {
            Logger.e("getPurchases(): Invalid product type: " + productType);
            throw new IllegalArgumentException("Invalid product type");
        }

        iapHelper.getOwnedList(samsungProductType, new OnGetOwnedListListener() {
            @Override
            public void onGetOwnedProducts(ErrorVo _errorVO, ArrayList<OwnedProductVo> _ownedList) {
                boolean isSuccessful;
                List<Bundle> purchases = new ArrayList<>();
                String errorMessage = null;

                if (_errorVO != null) {
                    if (_errorVO.getErrorCode() == IapHelper.IAP_ERROR_NONE) {
                        isSuccessful = true;
                        if (_ownedList != null && !_ownedList.isEmpty()) {
                            for (OwnedProductVo item : _ownedList) {
                                Bundle purchase = new Bundle();
                                purchase.putString(PURCHASE_DEESCRIPTION, item.getItemDesc());
                                purchase.putString(PURCHASE_ORIGINAL_DATA, item.getJsonString());
                                purchase.putString(PURCHASE_PRICE, item.getItemPriceString());
                                purchase.putString(PURCHASE_SKU, item.getItemId());
                                purchase.putString(PURCHASE_TITLE, item.getItemName());
                                if (item.getType().equals("Item")) {
                                    purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_ENTITLEMENT);
                                }
                                else if (item.getType().equals("Subscription")) {
                                    purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_SUBSCRIPTION);
                                }
                                else {
                                    purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_ALL);
                                }

                                purchases.add(purchase);
                            }
                        }
                    }
                    else {
                        isSuccessful = false;
                        errorMessage = _errorVO.getErrorString();
                    }
                }
                else {
                    isSuccessful = false;
                    errorMessage = "ErrorVo is null";
                }

                listener.onPurchasesUpdated(new Response(isSuccessful, purchases, errorMessage));
            }
        });
    }

    @Override
    public void getProductDetails(final Object zypeProduct) {
        String sku = null;
        if (zypeProduct instanceof Subscription) {
            Subscription subscription = (Subscription) zypeProduct;
            if (subscription.getZypePlan().marketplaceIds == null) {
                Logger.e("getProductDetails(): marketplaceIds is empty.");
                return;
            }
            sku = subscription.getZypePlan().marketplaceIds.samsung;
        }
        else if (zypeProduct instanceof Video) {
            Video video = (Video) zypeProduct;
            sku = video.id;
        }
        else {
            Logger.e("getProductDetails(): Invalid type of 'productObhject' argument. It must be 'Subscription' or 'Video'");
            throw new IllegalArgumentException("Invalid 'productObject' type");
        }
        if (!TextUtils.isEmpty(sku)) {
            iapHelper.getProductsDetails(sku, new OnGetProductsDetailsListener() {
                @Override
                public void onGetProducts(ErrorVo _errorVO, ArrayList<ProductVo> _productList) {
                    boolean isSuccessful;
                    ProductDetails productDetails = null;
                    String errorMessage;

                    if (_errorVO != null) {
                        if (_errorVO.getErrorCode() == IapHelper.IAP_ERROR_NONE) {
                            isSuccessful = true;
                            for (ProductVo vo : _productList) {
                                productDetails = new ProductDetails();
                                productDetails.description = vo.getItemDesc();
                                productDetails.price = vo.getItemPriceString();
                                productDetails.sku = vo.getItemId();
                                productDetails.title = vo.getItemName();
                                productDetails.originalObject = vo.dump();
                                break;
                            }
                            errorMessage = null;
                        }
                        else {
                            isSuccessful = false;
                            errorMessage = _errorVO.getErrorString();
                        }
                    }
                    else {
                        isSuccessful = false;
                        errorMessage = "ErrorVo is null";
                    }

                    listener.onProductDetails(zypeProduct, new Response(isSuccessful, productDetails, errorMessage));
                }
            });
        }
        else {
            Logger.e("getProductDetails(): Product sku is empty");
        }
    }

    @Override
    public void makePurchase(String sku) {
        // Transaction id
        String passThroughParam = UUID.randomUUID().toString();

        iapHelper.startPayment(sku, passThroughParam, true, new OnPaymentListener() {
            @Override
            public void onPayment(ErrorVo _errorVO, PurchaseVo _purchaseVO) {
                boolean isSuccessful;
                Bundle purchase = null;
                String errorMessage = null;

                if (_errorVO != null) {
                    // Purchase successful
                    if (_errorVO.getErrorCode() == IapHelper.IAP_ERROR_NONE) {
                        isSuccessful = true;
                        purchase = new Bundle();
                        purchase.putString(PURCHASE_DEESCRIPTION, _purchaseVO.getItemDesc());
                        purchase.putString(PURCHASE_ORIGINAL_DATA, _purchaseVO.getJsonString());
                        purchase.putString(PURCHASE_PRICE, _purchaseVO.getItemPriceString());
                        purchase.putString(PURCHASE_SKU, _purchaseVO.getItemId());
                        purchase.putString(PURCHASE_TITLE, _purchaseVO.getItemName());
                        if (_purchaseVO.getType().equals("Item")) {
                            purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_ENTITLEMENT);
                        }
                        else if (_purchaseVO.getType().equals("Subscription")) {
                            purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_SUBSCRIPTION);
                        }
                        else {
                            purchase.putInt(PURCHASE_TYPE, PRODUCT_TYPE_ALL);
                        }
                    }
                    else {
                        isSuccessful = false;
                        errorMessage = _errorVO.getErrorString();
                    }
                }
                else {
                    isSuccessful = false;
                    errorMessage = "ErrorVo is null";
                }

                listener.onPurchase(new Response(isSuccessful, purchase, errorMessage));
            }
        });
    }
}
