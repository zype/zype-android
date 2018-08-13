package com.zype.android.webapi.events;


import android.os.Bundle;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.model.BaseModel;

public abstract class DataEvent<T extends BaseModel> extends BaseEvent {

    private final T mData;
    private Bundle options;

    public DataEvent(RequestTicket ticket, T data) {
        super(ticket);
        mData = data;
    }

    public DataEvent(RequestTicket ticket, Bundle options, T data) {
        super(ticket);
        this.options = options;
        mData = data;
    }

    @Override
    public T getEventData() {
        return mData;
    }

    public boolean isSuccess() {
//        return (mData != null) ? mData.isSuccess() : false;
        return (mData != null);
    }

    public Bundle getOptions() {
        return options;
    }
}
