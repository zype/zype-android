package com.zype.android.webapi.events.onair;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.onair.OnAirVideoResponse;

public class OnAirVideoChromeCastEvent extends DataEvent<OnAirVideoResponse> {

    public OnAirVideoChromeCastEvent(RequestTicket ticket, OnAirVideoResponse data) {
        super(ticket, data);
    }
}
