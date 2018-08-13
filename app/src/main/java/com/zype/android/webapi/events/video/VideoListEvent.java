package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoListResponse;

public class VideoListEvent extends DataEvent<VideoListResponse> {
    private final String playlistId;

    public VideoListEvent(RequestTicket ticket, VideoListResponse data, String playlistId) {
        super(ticket, data);
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }
}
