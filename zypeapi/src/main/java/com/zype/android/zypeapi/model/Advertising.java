package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 14.04.2017.
 */

public class Advertising {
    @Expose
    public String client;

    @Expose
    public List<AdvertisingSchedule> schedule = new ArrayList<>();
}
