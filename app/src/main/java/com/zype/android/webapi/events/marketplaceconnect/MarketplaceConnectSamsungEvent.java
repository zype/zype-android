package com.zype.android.webapi.events.marketplaceconnect;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.marketplaceconnect.MarketplaceConnectResponse;
import com.zype.android.webapi.model.marketplaceconnect.MarketplaceConnectSamsungResponse;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class MarketplaceConnectSamsungEvent extends DataEvent<MarketplaceConnectSamsungResponse> {

    public MarketplaceConnectSamsungEvent(RequestTicket ticket, MarketplaceConnectSamsungResponse data) {
        super(ticket, data);
    }
}
