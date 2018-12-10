package com.zype.android;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeny Cherkasov on 10.11.2018.
 */

public class AppConfiguration {
    @SerializedName("audioOnlyPlayback")
    public Boolean audioOnlyPlaybackEnabled;

    public String marketplace;

}
