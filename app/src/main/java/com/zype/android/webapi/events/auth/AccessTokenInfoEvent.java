package com.zype.android.webapi.events.auth;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.auth.AccessTokenInfoResponse;

public class AccessTokenInfoEvent extends DataEvent<AccessTokenInfoResponse> {

    public AccessTokenInfoEvent(RequestTicket ticket, AccessTokenInfoResponse data) {
        super(ticket, data);
    }
}
