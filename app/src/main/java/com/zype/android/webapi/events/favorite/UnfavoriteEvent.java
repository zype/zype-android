package com.zype.android.webapi.events.favorite;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.favorite.UnfavoriteResponse;

/**
 * @author vasya
 * @version 1
 *          date 7/15  /15
 */
public class UnfavoriteEvent extends DataEvent<UnfavoriteResponse> {
    private final String videoId;

    public UnfavoriteEvent(RequestTicket ticket, UnfavoriteResponse data, String videoId) {
        super(ticket, data);
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }
}
