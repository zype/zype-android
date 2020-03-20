package com.zype.android.Auth;

import com.zype.android.zypeapi.model.ConsumerData;

public class AuthState {
    public AuthState(boolean isAuthenticated, ConsumerData consumer) {
        this.isAuthenticated = isAuthenticated;
        this.consumer = consumer;
    }

    boolean isAuthenticated;

    ConsumerData consumer;
}
