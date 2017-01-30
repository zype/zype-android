package com.zype.android.webapi.builder;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public class AuthParamsBuilder extends ParamsBuilder {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String CLIENT_GRAND_TYPE = "grant_type";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String CLIENT_ID = "client_id";

    private static final String CONSTANT_CLIENT_ID = "62f1d247b4c5e77b6111d9a9ed8b3b64bab6be66cc8b7513a928198083cd1c72";
    private static final String CONSTANT_CLIENT_SECRET = "06f45687da00bbe3cf51dddc7dbd7a288d1c852cf0b9a6e76e25bb115dcf872c";
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

    public AuthParamsBuilder addClientId() {
        addPostParam(CLIENT_ID, CONSTANT_CLIENT_ID);
        return this;
    }

    public AuthParamsBuilder addClientSecret() {
        addPostParam(CLIENT_SECRET, CONSTANT_CLIENT_SECRET);
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
