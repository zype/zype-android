package com.zype.android.webapi.model.app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zype.android.webapi.model.consumers.ConsumerData;

/**
 * Created by Evgeny Cherkasov on 07.12.2018.
 */

public class App {

    @SerializedName("response")
    @Expose
    private AppData appData;

    /**
     *
     * @return
     * The response
     */
    public AppData getAppData() {
        return appData;
    }

    /**
     *
     * @param appData
     * The response
     */
    public void setAppData(AppData appData) {
        this.appData = appData;
    }

}