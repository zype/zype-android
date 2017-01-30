package com.zype.android.webapi.events.settings;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.settings.ContentSettingsResponse;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class ContentSettingsEvent extends DataEvent<ContentSettingsResponse> {
    public ContentSettingsEvent(RequestTicket ticket, ContentSettingsResponse data) {
        super(ticket, data);
    }
}
