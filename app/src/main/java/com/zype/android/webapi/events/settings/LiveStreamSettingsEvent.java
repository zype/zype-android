package com.zype.android.webapi.events.settings;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.settings.LiveStreamSettingsResponse;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class LiveStreamSettingsEvent extends DataEvent<LiveStreamSettingsResponse> {
    public LiveStreamSettingsEvent(RequestTicket ticket, LiveStreamSettingsResponse data) {
        super(ticket, data);
    }
}
