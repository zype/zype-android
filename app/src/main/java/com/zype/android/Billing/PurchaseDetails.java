package com.zype.android.Billing;

import android.os.Bundle;

public class PurchaseDetails {
    private static final String DESCRIPTION = "Description";
    private static final String ORIGINAL_DATA = "OriginalData";
    private static final String PRICE = "Price";
    private static final String SKU = "Sku";
    private static final String TITLE = "Title";
    private static final String TYPE = "Type";

    private Bundle data;

    public PurchaseDetails() {
        data = new Bundle();
    }

    public String getString(String key) {
        return data.getString(key);
    }

    public void setString(String key, String value) {
        data.putString(key, value);
    }

    public int getInt(String key) {
        return data.getInt(key);
    }

    public void setInt(String key, int value) {
        data.putInt(key, value);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setDescription(String value) {
        setString(DESCRIPTION, value);
    }

    public String getOriginalData() {
        return getString(ORIGINAL_DATA);
    }

    public void setOriginalData(String value) {
        setString(ORIGINAL_DATA, value);
    }

    public String getPrice() {
        return getString(PRICE);
    }

    public void setPrice(String value) {
        setString(PRICE, value);
    }

    public String getSku() {
        return getString(SKU);
    }

    public void setSku(String value) {
        setString(SKU, value);
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String value) {
        setString(TITLE, value);
    }

    public int getType() {
        return getInt(TYPE);
    }

    public void setType(int value) {
        setInt(TYPE, value);
    }
}
