package com.zype.android.webapi.events.app;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.app.AppResponse;
import com.zype.android.webapi.model.consumers.ConsumerResponse;

/**
 * Created by Evgeny Cherkasov on 07.12.2018.
 */

public class AppEvent extends DataEvent<AppResponse> {

    private WebApiManager.Request request;

    public AppEvent(RequestTicket ticket, WebApiManager.Request request, AppResponse data) {
        super(ticket, data);
        this.request = request;
    }

    public WebApiManager.Request getRequest() {
        return request;
    }
}
