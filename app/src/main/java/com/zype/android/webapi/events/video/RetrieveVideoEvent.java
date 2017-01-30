package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoResponse;

public class RetrieveVideoEvent extends DataEvent<VideoResponse> {

    public RetrieveVideoEvent(RequestTicket ticket, VideoResponse data) {
        super(ticket, data);
    }
}
