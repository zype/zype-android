package com.zype.android.webapi.events.playlist;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.playlist.PlaylistResponse;

public class PlaylistEvent extends DataEvent<PlaylistResponse> {

    public String parentId;

    public PlaylistEvent(RequestTicket ticket, PlaylistResponse data, String parentId) {
        super(ticket, data);
        this.parentId = parentId;
    }
}
