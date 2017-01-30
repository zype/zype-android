package com.zype.android.webapi.events.zobject;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.zobjects.ZObjectResponse;

public class ZObjectEvent extends DataEvent<ZObjectResponse> {

    public ZObjectEvent(RequestTicket ticket, ZObjectResponse data) {
        super(ticket, data);
    }
}
