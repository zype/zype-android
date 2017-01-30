package com.zype.android.webapi.events.search;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.search.SearchResponse;

public class SearchEvent extends DataEvent<SearchResponse> {

    public SearchEvent(RequestTicket ticket, SearchResponse data) {
        super(ticket, data);
    }
}
