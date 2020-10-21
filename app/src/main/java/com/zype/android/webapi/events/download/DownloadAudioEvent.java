package com.zype.android.webapi.events.download;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.download.DownloadAudioResponse;

import androidx.annotation.NonNull;

public class DownloadAudioEvent extends DataEvent<DownloadAudioResponse> {

//    public DownloadResultReceiver resultReceiver;
    public String mFileId;

    public DownloadAudioEvent(RequestTicket ticket, DownloadAudioResponse data, @NonNull String fileId) {
        super(ticket, data);
        mFileId = fileId;
//        resultReceiver = (DownloadResultReceiver) receiver;
    }
}
