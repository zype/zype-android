package com.zype.android.webapi.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.DataModel;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public class RetrieveAccessToken extends DataModel<RetrieveAccessToken.RetrieveAccessTokenData> {
    public RetrieveAccessToken(RetrieveAccessTokenData data) {
        super(data);
    }

    public class RetrieveAccessTokenData {
        @SerializedName("access_token")
        @Expose
        private String accessToken;
        @SerializedName("token_type")
        @Expose
        private String tokenType;
        @SerializedName("expires_in")
        @Expose
        private long expiresIn;
        @SerializedName("refresh_token")
        @Expose
        private String refreshToken;
        @Expose
        private String scope;

        /**
         * @return The accessToken
         */
        public String getAccessToken() {
            return accessToken;
        }

        /**
         * @param accessToken The access_token
         */
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        /**
         * @return The tokenType
         */
        public String getTokenType() {
            return tokenType;
        }

        /**
         * @param tokenType The token_type
         */
        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        /**
         * @return The expiresIn
         */
        public long getExpiresIn() {
            return expiresIn;
        }

        /**
         * @param expiresIn The expires_in
         */
        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        /**
         * @return The refreshToken
         */
        public String getRefreshToken() {
            return refreshToken;
        }

        /**
         * @param refreshToken The refresh_token
         */
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        /**
         * @return The scope
         */
        public String getScope() {
            return scope;
        }

        /**
         * @param scope The scope
         */
        public void setScope(String scope) {
            this.scope = scope;
        }
    }
}
