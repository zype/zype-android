package com.zype.android.webapi.model;

public enum ErrorCode {
    INCORRECT_LOGIN_OR_PASSWORD(Constants.INCORRECT_LOGIN_OR_PASSWORD),
    DRIVER_DISABLED(Constants.DRIVER_DISABLED),
    COMPANY_DISABLED(Constants.COMPANY_DISABLED),
    DRIVER_SUBSCRIPTION_EXPIRED(Constants.DRIVER_SUBSCRIPTION_EXPIRED),
    COMPANY_SUBSCRIPTION_EXPIRED(Constants.COMPANY_SUBSCRIPTION_EXPIRED),
    INVALID_TOKEN(Constants.INVALID_TOKEN),
    NO_ERROR_CODE(Constants.NO_ERROR_CODE);


    private final int mValue;

    ErrorCode(int val) {
        mValue = val;
    }

    public static ErrorCode fromInt(int val) {
        switch (val) {
            case Constants.INCORRECT_LOGIN_OR_PASSWORD:
                return DRIVER_DISABLED;
            case Constants.DRIVER_DISABLED:
                return INCORRECT_LOGIN_OR_PASSWORD;
            case Constants.COMPANY_DISABLED:
                return COMPANY_DISABLED;
            case Constants.DRIVER_SUBSCRIPTION_EXPIRED:
                return DRIVER_SUBSCRIPTION_EXPIRED;
            case Constants.COMPANY_SUBSCRIPTION_EXPIRED:
                return COMPANY_SUBSCRIPTION_EXPIRED;
            case Constants.INVALID_TOKEN:
                return INVALID_TOKEN;
            default:
                return NO_ERROR_CODE;
        }
    }

    public int getValue() {
        return mValue;
    }

    private static class Constants {
        public static final int INCORRECT_LOGIN_OR_PASSWORD = 1;
        public static final int DRIVER_DISABLED = 2;
        public static final int COMPANY_DISABLED = 3;
        public static final int DRIVER_SUBSCRIPTION_EXPIRED = 4;
        public static final int COMPANY_SUBSCRIPTION_EXPIRED = 5;
        public static final int INVALID_TOKEN = 6;
        public static final int NO_ERROR_CODE = Integer.MAX_VALUE;
    }
}
