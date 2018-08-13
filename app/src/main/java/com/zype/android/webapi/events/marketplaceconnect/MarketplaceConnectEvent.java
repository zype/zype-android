package com.zype.android.webapi.events.marketplaceconnect;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.marketplaceconnect.MarketplaceConnectResponse;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class MarketplaceConnectEvent extends DataEvent<MarketplaceConnectResponse> {

    public MarketplaceConnectEvent(RequestTicket ticket, MarketplaceConnectResponse data) {
        super(ticket, data);
    }
}
