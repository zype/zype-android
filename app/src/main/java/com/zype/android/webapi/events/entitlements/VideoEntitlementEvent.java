package com.zype.android.webapi.events.entitlements;

import android.os.Bundle;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.entitlements.VideoEntitlementResponse;
import com.zype.android.webapi.model.entitlements.VideoEntitlementsResponse;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class VideoEntitlementEvent extends DataEvent<VideoEntitlementResponse> {
    private Bundle options;

    public VideoEntitlementEvent(RequestTicket ticket, Bundle options, VideoEntitlementResponse data) {
        super(ticket, data);
        this.options = options;
    }

    public Bundle getOptions() {
        return options;
    }
}
