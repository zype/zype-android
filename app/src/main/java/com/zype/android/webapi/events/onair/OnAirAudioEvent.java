package com.zype.android.webapi.events.onair;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.onair.OnAirAudioResponse;

public class OnAirAudioEvent extends DataEvent<OnAirAudioResponse> {

    public OnAirAudioEvent(RequestTicket ticket, OnAirAudioResponse data) {
        super(ticket, data);
    }
}
