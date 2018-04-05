package com.zype.android.webapi.events.linking;

import android.os.Bundle;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.entitlements.VideoEntitlementResponse;
import com.zype.android.webapi.model.linking.DevicePinResponse;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class DevicePinEvent extends DataEvent<DevicePinResponse> {
    private Bundle options;

    public DevicePinEvent(RequestTicket ticket, Bundle options, DevicePinResponse data) {
        super(ticket, data);
        this.options = options;
    }

    public Bundle getOptions() {
        return options;
    }
}
