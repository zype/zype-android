package com.zype.android.webapi.model.download;

import com.zype.android.webapi.model.DataModel;
import com.zype.android.webapi.model.player.PlayerAudioData;

/**
 * @author vasya
 * @version 1
 *          date 6/29/15
 */
public class DownloadAudioResponse extends DataModel<PlayerAudioData> {
    public DownloadAudioResponse(PlayerAudioData data) {
        super(data);
    }
}
