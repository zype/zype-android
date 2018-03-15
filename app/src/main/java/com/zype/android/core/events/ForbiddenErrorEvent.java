package com.zype.android.core.events;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.ErrorEvent;

public class ForbiddenErrorEvent extends ErrorEvent {
    public ForbiddenErrorEvent(RequestTicket ticket, WebApiManager.Request type, String errMsg) {
        super(ticket, type, errMsg, null);
    }
}
