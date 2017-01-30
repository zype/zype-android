package com.zype.android.webapi.events;


import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;

public class ErrorEvent extends BaseEvent<WebApiManager.Request> {

    private WebApiManager.Request mType;

    private String mErrMessage;

    public ErrorEvent(RequestTicket ticket, WebApiManager.Request type, String errMsg) {
        super(ticket);
        mType = type;
        mErrMessage = errMsg;
    }

    @Override
    public WebApiManager.Request getEventData() {
        return mType;
    }

    public String getErrMessage() {
        return mErrMessage;
    }
}

