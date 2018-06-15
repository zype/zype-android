package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoResponse;

public class RetrieveVideoEvent extends DataEvent<VideoResponse> {
    private final String playlistId;

    public RetrieveVideoEvent(RequestTicket ticket, VideoResponse data, String playlistId) {
        super(ticket, data);
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }
}
