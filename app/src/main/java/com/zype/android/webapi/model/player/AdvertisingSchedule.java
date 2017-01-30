package com.zype.android.webapi.model.player;

import com.google.gson.annotations.Expose;

/**
 * Created by Evgeny Cherkasov on 18.11.2016.
 */

public class AdvertisingSchedule {
    @Expose
    private int offset;

    @Expose
    private String tag;

    /**
     *
     * @return
     * The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     * The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     *
     * @return
     * The tag
     */
    public String getTag() {
        return tag;
    }

    /**
     *
     * @param tag
     * The tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}
