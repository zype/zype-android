package com.zype.android.webapi.events.consumer;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.consumers.ConsumerResponse;

public class ConsumerEvent extends DataEvent<ConsumerResponse> {

    public ConsumerEvent(RequestTicket ticket, ConsumerResponse data) {
        super(ticket, data);
    }
}
