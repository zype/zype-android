package com.zype.android.webapi.model.bifrost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 10.11.2017.
 */

public class BifrostData {
    @Expose
    public boolean success;

    @SerializedName("is_valid")
    @Expose
    public boolean isValid;

    @Expose
    public boolean expired;
}
