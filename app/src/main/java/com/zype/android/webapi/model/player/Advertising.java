package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 18.11.2016.
 */

public class Advertising {
    @Expose
    private String client;

    @Expose
    private List<AdvertisingSchedule> schedule = new ArrayList<>();

    /**
     *
     * @return
     * The client
     */
    public String getClient() {
        return client;
    }

    /**
     *
     * @param client
     * The client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     *
     * @return
     * The schedule
     */
    public List<AdvertisingSchedule> getSchedule() {
        return schedule;
    }

    /**
     *
     * @param schedule
     * The schedule
     */
    public void setSchedule(List<AdvertisingSchedule> schedule) {
        this.schedule = schedule;
    }
}
