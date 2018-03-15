package com.zype.android.webapi.events.entitlements;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.entitlements.VideoEntitlementsResponse;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class VideoEntitlementsEvent extends DataEvent<VideoEntitlementsResponse> {
    public VideoEntitlementsEvent(RequestTicket ticket, VideoEntitlementsResponse data) {
        super(ticket, data);
    }
}
