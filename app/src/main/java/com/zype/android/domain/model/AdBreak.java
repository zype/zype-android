package com.zype.android.domain.model;

import androidx.annotation.NonNull;

/*
 * Class to hold ad break data
 */
public class AdBreak {
    public AdBreak(String adTag, float offset) {
        this(adTag, offset, 0, false);
    }

    public AdBreak(String adTag, float offset, float duration, boolean isSsai) {
        this.adTag = adTag;
        this.duration = duration;
        this.isSsai = isSsai;
        this.offset = offset;
    }

    public String adTag;

    public float duration;

    public boolean isSsai;

    public float offset;

    @NonNull
    @Override
    public String toString() {
        return AdBreak.class.getSimpleName() + " offset=" + offset + ", duration=" + duration;
    }
}
