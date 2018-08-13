package com.zype.android.webapi.events.video;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.video.VideoListResponse;
import com.zype.android.webapi.model.video.VideoResponse;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
public class VideoEvent extends DataEvent<VideoResponse> {
    public VideoEvent(RequestTicket ticket, VideoResponse data) {
        super(ticket, data);
    }
}
