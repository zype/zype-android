package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoResponse;

public class RetrieveHighLightVideoEvent extends DataEvent<VideoResponse> {

    public RetrieveHighLightVideoEvent(RequestTicket ticket, VideoResponse data) {
        super(ticket, data);
    }
}
