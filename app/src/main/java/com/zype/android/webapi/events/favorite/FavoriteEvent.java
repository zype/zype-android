package com.zype.android.webapi.events.favorite;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.favorite.FavoriteResponse;

/**
 * @author vasya
 * @version 1
 *          date 7/15/15
 */
public class FavoriteEvent extends DataEvent<FavoriteResponse> {
    public FavoriteEvent(RequestTicket ticket, FavoriteResponse data) {
        super(ticket, data);
    }
}
