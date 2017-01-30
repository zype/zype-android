package com.zype.android.webapi.events.player;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.player.PlayerAudioResponse;

public class PlayerAudioEvent extends DataEvent<PlayerAudioResponse> {

    public PlayerAudioEvent(RequestTicket ticket, PlayerAudioResponse data) {
        super(ticket, data);
    }
}
