package com.zype.android.webapi.events.bifrost;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.bifrost.BifrostResponse;
import com.zype.android.webapi.model.consumers.ConsumerResponse;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class BifrostEvent extends DataEvent<BifrostResponse> {

    public BifrostEvent(RequestTicket ticket, BifrostResponse data) {
        super(ticket, data);
    }
}
