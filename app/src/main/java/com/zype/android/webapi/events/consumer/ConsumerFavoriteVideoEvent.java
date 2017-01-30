package com.zype.android.webapi.events.consumer;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoResponse;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class ConsumerFavoriteVideoEvent extends DataEvent<ConsumerFavoriteVideoResponse> {
    public ConsumerFavoriteVideoEvent(RequestTicket ticket, ConsumerFavoriteVideoResponse data) {
        super(ticket, data);
    }
}
