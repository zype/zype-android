package com.zype.android.webapi.events.auth;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.auth.RetrieveAccessToken;

public class RetrieveAccessTokenEvent extends DataEvent<RetrieveAccessToken> {

    public RetrieveAccessTokenEvent(RequestTicket ticket, RetrieveAccessToken data) {
        super(ticket, data);
    }
}
