package com.zype.android.webapi.events.player;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.player.PlayerVideoResponse;

public class PlayerVideoEvent extends DataEvent<PlayerVideoResponse> {

    public PlayerVideoEvent(RequestTicket ticket, PlayerVideoResponse data) {
        super(ticket, data);
    }
}
