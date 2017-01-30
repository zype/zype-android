package com.zype.android.webapi.events.onair;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.onair.OnRequestCookiesResponse;

public class OnRequestCookiesEvent extends DataEvent<OnRequestCookiesResponse> {

    public OnRequestCookiesEvent(RequestTicket ticket, OnRequestCookiesResponse data) {
        super(ticket, data);
    }
}
