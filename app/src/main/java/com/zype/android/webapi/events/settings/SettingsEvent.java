package com.zype.android.webapi.events.settings;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.settings.SettingsResponse;

public class SettingsEvent extends DataEvent<SettingsResponse> {

    public SettingsEvent(RequestTicket ticket, SettingsResponse data) {
        super(ticket, data);
    }
}
