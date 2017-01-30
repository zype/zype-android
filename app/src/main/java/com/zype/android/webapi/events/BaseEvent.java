package com.zype.android.webapi.events;

import com.zype.android.webapi.RequestTicket;

public abstract class BaseEvent<T> {

    private RequestTicket mTicket;

    public BaseEvent(RequestTicket ticket) {
        mTicket = ticket;
    }

    public RequestTicket getTicket() {
        return mTicket;
    }

    public abstract T getEventData();

}

