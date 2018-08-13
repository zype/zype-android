package com.zype.android.ui.video_details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ui.video_details.Model.VideoLiveData;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.VideoParamsBuilder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
public class VideoDetailViewModel extends AndroidViewModel {
    VideoLiveData video;
    VideoLiveData videoCheckOnAir;

    private Timer timer;
    private TimerTask timerTask;
    private long TIMER_PERIOD = 60000;

    private DataRepository repo;

    public VideoDetailViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
    }

    public VideoLiveData getVideo(String videoId) {
        if (video == null) {
            video = new VideoLiveData();
            video.setCheckOnAir(false);
            loadVideo(videoId);
        }
        return video;
    }

    @Override
    protected void onCleared() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onCleared();
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
        timer.schedule(timerTask, TIMER_PERIOD, TIMER_PERIOD);

        return videoCheckOnAir;
    }

    private void loadVideo(String videoId) {
        Logger.d("loadVideo(): videoId=" + videoId);
        VideoParamsBuilder builder = new VideoParamsBuilder()
                .addVideoId(videoId);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO, builder.build());
    }

    public void updateVideoOnAir(Video video) {
        Video dbVideo = repo.getVideoSync(video.id);
        dbVideo.onAir = video.onAir;
        repo.updateVideo(dbVideo);
    }
}
