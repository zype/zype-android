package com.zype.android.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zype.android.domain.model.AdBreak;
import com.zype.android.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class AdBreakManager {
    private final static String TAG = AdBreakManager.class.getSimpleName();

    private List<AdBreak> adBreaks = new ArrayList<>();
    private List<AdBreak> shownAdBreaks = new ArrayList<>();

    private MutableLiveData<AdBreak> currentAdBreak = new MutableLiveData<>();

    private float lastPlaybackPosition = -1f;

    public void init(List<AdBreak> adBreaks) {
        this.adBreaks.clear();
        this.adBreaks.addAll(adBreaks);
        shownAdBreaks.clear();
        currentAdBreak.setValue(null);
        lastPlaybackPosition = -1f;
    }

    public LiveData<AdBreak> getAdBreak() {
        return currentAdBreak;
    }

    public boolean hasPreroll() {
        return adBreaks.size() > 0 && adBreaks.get(0).offset == 0;
    }

    private AdBreak getNextAdBreak(float position) {
        for (AdBreak adBreak : adBreaks) {
            if (position >= adBreak.offset
                    && !shownAdBreaks.contains(adBreak)) {
                shownAdBreaks.add(adBreak);
                if (lastPlaybackPosition != -1f) {
                    return adBreak;
                }
            }
        }
        return null;
    }

    public void onPositionChanged(float position) {
        if (lastPlaybackPosition == position)
            return;
        AdBreak nextAdBreak = getNextAdBreak(position);
        lastPlaybackPosition = position;
        if (currentAdBreak.getValue() != nextAdBreak) {
            Logger.d("onPositionChanged(): position=" + position + ", adBreak=" + (nextAdBreak == null ? null : nextAdBreak.toString()));
            currentAdBreak.setValue(nextAdBreak);
        }
    }
}
