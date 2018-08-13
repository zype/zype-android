package com.zype.android.webapi.events.player;

import android.os.Bundle;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.player.PlayerAudioResponse;

public class PlayerAudioEvent extends DataEvent<PlayerAudioResponse> {

    public PlayerAudioEvent(RequestTicket ticket, Bundle options, PlayerAudioResponse data) {
        super(ticket, options, data);
    }
}
