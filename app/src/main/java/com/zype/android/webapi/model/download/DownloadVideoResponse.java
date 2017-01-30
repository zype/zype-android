package com.zype.android.webapi.model.download;

import com.zype.android.webapi.model.DataModel;
import com.zype.android.webapi.model.player.PlayerVideoData;

/**
 * @author vasya
 * @version 1
 *          date 6/29/15
 */
public class DownloadVideoResponse extends DataModel<PlayerVideoData> {
    public DownloadVideoResponse(PlayerVideoData data) {
        super(data);
    }
}
