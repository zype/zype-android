package com.zype.android.webapi.events.consumer;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.consumers.ConsumerResponse;

public class ConsumerEvent extends DataEvent<ConsumerResponse> {

    private WebApiManager.Request request;

    public ConsumerEvent(RequestTicket ticket, WebApiManager.Request request, ConsumerResponse data) {
        super(ticket, data);
        this.request = request;
    }

    public WebApiManager.Request getRequest() {
        return request;
    }
}
