package com.zype.android.webapi.events;


import android.os.Bundle;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;

import retrofit.RetrofitError;

public class ErrorEvent extends BaseEvent<WebApiManager.Request> {

    private WebApiManager.Request mType;

    private String mErrMessage;

    private RetrofitError error;

    public ErrorEvent(RequestTicket ticket, WebApiManager.Request type, String errMsg, RetrofitError error) {
        super(ticket);
        mType = type;
        mErrMessage = errMsg;
        this.error = error;
    }

    @Override
    public WebApiManager.Request getEventData() {
        return mType;
    }

    public String getErrMessage() {
        return mErrMessage;
    }

    public RetrofitError getError() {
        return error;
    }
}

