package com.zype.android.webapi.events.onair;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.onair.OnAirResponse;

public class OnAirEvent extends DataEvent<OnAirResponse> {

    public OnAirEvent(RequestTicket ticket, OnAirResponse data) {
        super(ticket, data);
    }
}
