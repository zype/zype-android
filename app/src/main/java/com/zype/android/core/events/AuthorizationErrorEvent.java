package com.zype.android.core.events;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.ErrorEvent;

public class AuthorizationErrorEvent extends ErrorEvent {

//    private final ErrorCode mCode;

    public AuthorizationErrorEvent(RequestTicket ticket, WebApiManager.Request type, String errMsg) {
        super(ticket, type, errMsg, null);
//        mCode = code;
    }

//    public ErrorCode getErrorCode() {
//        return mCode;
//    }
}
