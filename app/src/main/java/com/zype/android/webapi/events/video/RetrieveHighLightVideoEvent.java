package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoListResponse;

public class RetrieveHighLightVideoEvent extends DataEvent<VideoListResponse> {

    public RetrieveHighLightVideoEvent(RequestTicket ticket, VideoListResponse data) {
        super(ticket, data);
    }
}
