package com.zype.android.ui.video_details.Model;

import android.arch.lifecycle.LiveData;

import com.squareup.otto.Subscribe;
import com.zype.android.DataHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.video.VideoEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
public class VideoLiveData extends LiveData<Video> {
    private boolean checkOnAir = false;

    public void setCheckOnAir(boolean checkOnAir) {
        this.checkOnAir = checkOnAir;
    }

    @Override
    protected void onActive() {
        WebApiManager.getInstance().subscribe(this);
    }

    @Override
    protected void onInactive() {
        WebApiManager.getInstance().unsubscribe(this);
    }

    @Subscribe
    public void handleVideo(VideoEvent event) {
        Logger.d("handleVideo()");
        VideoData data = event.getEventData().getModelData().getVideoData();
        Video video = DataHelper.videoDataToVideoEntity(data);
        if (checkOnAir) {
            if (getValue().onAir == 0 && video.onAir == 1) {
                setValue(video);
            }
        }
        else {
            setValue(video);
        }
    }

}
