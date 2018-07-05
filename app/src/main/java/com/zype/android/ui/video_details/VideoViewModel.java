package com.zype.android.ui.video_details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.Gallery.Model.HeroImageLiveData;
import com.zype.android.ui.video_details.Model.VideoLiveData;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
public class VideoViewModel extends ViewModel {
    VideoLiveData video;
    VideoLiveData videoCheckOnAir;

    private Timer timer;
    private TimerTask timerTask;
    private long TIMER_PERIOD = 6000;

    public VideoLiveData getVideo(String videoId) {
        if (video == null) {
            video = new VideoLiveData();
            video.setCheckOnAir(false);
            loadVideo(videoId);
        }
        return video;
    }

    public VideoLiveData checkOnAir(final String videoId) {
        if (videoCheckOnAir == null) {
            videoCheckOnAir = new VideoLiveData();
            videoCheckOnAir.setCheckOnAir(true);
        }
        if (timer == null) {
            timer = new Timer();
        }
        else {
            timer.cancel();
            timer.purge();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                loadVideo(videoId);
            }
        };
        timer.schedule(timerTask, 10000, TIMER_PERIOD);

        return videoCheckOnAir;
    }

    private void loadVideo(String videoId) {
        VideoParamsBuilder builder = new VideoParamsBuilder()
                .addVideoId(videoId);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO, builder.build());
    }

}
