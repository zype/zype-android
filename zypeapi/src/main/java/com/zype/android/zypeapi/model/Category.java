package com.zype.android.zypeapi.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 12.02.2019.
 */

public class Category {
    @Expose
    public String title;
    @Expose
    public List<String> value = new ArrayList<>();

}
