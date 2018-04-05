package com.zype.android.webapi.builder;

import com.zype.android.ZypeSettings;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public class AuthParamsBuilder extends ParamsBuilder {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LINKED_DEVICE_ID = "linked_device_id";
    public static final String PIN = "pin";
    private static final String CLIENT_GRAND_TYPE = "grant_type";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String CLIENT_ID = "client_id";

    public static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    public AuthParamsBuilder addUsername(String email) {
        addPostParam(USERNAME, email);
        return this;
    }

    public AuthParamsBuilder addPassword(String password) {
        addPostParam(PASSWORD, password);
        return this;
    }

    public AuthParamsBuilder addLinkedDeviceId(String deviceId) {
        addPostParam(LINKED_DEVICE_ID, deviceId);
        return this;
    }

    public AuthParamsBuilder addPin(String pin) {
        addPostParam(PIN, pin);
        return this;
    }

    public AuthParamsBuilder addClientId() {
        addPostParam(CLIENT_ID, ZypeSettings.CLIENT_ID);
        return this;
    }

    public AuthParamsBuilder addClientSecret() {
        addPostParam(CLIENT_SECRET, ZypeSettings.CLIENT_SECRET);
        return this;
    }

    public AuthParamsBuilder addGrandType(String grandType) {
        addPostParam(CLIENT_GRAND_TYPE, grandType);
        return this;
    }

    public AuthParamsBuilder addToken(String accessToken) {
        addGetParam(ACCESS_TOKEN, accessToken);
        return this;
    }

    public AuthParamsBuilder addRefreshToken(String refreshToken) {
        addPostParam(REFRESH_TOKEN, refreshToken);
        return this;
    }
}
