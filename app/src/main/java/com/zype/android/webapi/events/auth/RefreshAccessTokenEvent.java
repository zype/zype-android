package com.zype.android.webapi.events.auth;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.auth.RefreshAccessToken;

public class RefreshAccessTokenEvent extends DataEvent<RefreshAccessToken> {

    public RefreshAccessTokenEvent(RequestTicket ticket, RefreshAccessToken data) {
        super(ticket, data);
    }
}
