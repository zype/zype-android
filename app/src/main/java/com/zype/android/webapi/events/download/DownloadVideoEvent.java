package com.zype.android.webapi.events.download;

import com.zype.android.webapi.RequestTicket;
import com.zype.android.webapi.events.DataEvent;
import com.zype.android.webapi.model.download.DownloadVideoResponse;

import androidx.annotation.NonNull;

public class DownloadVideoEvent extends DataEvent<DownloadVideoResponse> {

//    public DownloadResultReceiver resultReceiver;
    public String mFileId;

    public DownloadVideoEvent(RequestTicket ticket, DownloadVideoResponse data, @NonNull String fileId) {
        super(ticket, data);
        mFileId = fileId;
//        resultReceiver = (DownloadResultReceiver) receiver;
    }
}
