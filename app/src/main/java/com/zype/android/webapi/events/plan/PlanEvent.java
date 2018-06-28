package com.zype.android.webapi.events.plan;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.plan.PlanResponse;

/**
 * Created by Evgeny Cherkasov on 22.06.2018
 */
public class PlanEvent extends DataEvent<PlanResponse> {

    public PlanEvent(RequestTicket ticket, PlanResponse data) {
        super(ticket, data);
    }
}

