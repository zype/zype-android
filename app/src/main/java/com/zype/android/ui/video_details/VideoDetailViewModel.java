package com.zype.android.ui.video_details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.helpers.PlaylistHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.video_details.Model.VideoLiveData;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.DownloadAudioParamsBuilder;
import com.zype.android.webapi.builder.DownloadVideoParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.video.VideoEvent;
import com.zype.android.webapi.model.player.File;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.ZypeApiResponse;
import com.zype.android.zypeapi.model.VideoResponse;

import java.util.Timer;
import java.util.TimerTask;

import static com.zype.android.webapi.WebApiManager.WorkerHandler.BAD_REQUEST;

/**
 * Created by Evgeny Cherkasov on 05.07.2018
 */
public class VideoDetailViewModel extends AndroidViewModel {
    VideoLiveData video;
    VideoLiveData videoCheckOnAir;

    private MutableLiveData<Video> videoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> onAir = new MutableLiveData<>();
    private MutableLiveData<Boolean> fullscreen = new MutableLiveData<>();

    private String videoId;
    private String playlistId;

    private Timer timer;
    private TimerTask timerTask;
    private long TIMER_PERIOD = 60000;

    private DataRepository repo;
    private ZypeApi api;
    // TODO: REFACTORING - Replace any usage of 'WebApiManager' to 'ZypeApi'
    private WebApiManager oldApi;

    public VideoDetailViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
        oldApi = WebApiManager.getInstance();
        oldApi.subscribe(this);
    }

    @Override
    protected void onCleared() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        oldApi.unsubscribe(this);
        super.onCleared();
    }


    public VideoDetailViewModel setVideoId(String videoId) {
        this.videoId = videoId;
        initVideo();
        return this;
    }

    public String getVideoId() {
        return videoId;
    }

    public VideoDetailViewModel setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
        return this;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public Playlist getPlaylistSync() {
        if (!TextUtils.isEmpty(playlistId)) {
            return repo.getPlaylistSync(playlistId);
        }
        else {
            return null;
        }
    }

    private void initVideo() {
        Video video = repo.getVideoSync(videoId);
        videoLiveData.setValue(video);

        loadVideo(videoId);

        if (video.onAir != 1) {
            if (ZypeConfiguration.isDownloadsEnabled(getApplication())
                    && (ZypeConfiguration.isDownloadsForGuestsEnabled(getApplication())
                    || SettingsProvider.getInstance().isLoggedIn())) {
                if (TextUtils.isEmpty(video.downloadVideoUrl)
                    || video.isDownloadedVideo == 0) {
                    loadVideoDownloadUrl(videoId);
                }
                if (TextUtils.isEmpty(video.downloadAudioUrl)
                    || video.isDownloadedAudio == 0) {
                    loadAudioDownloadUrl(videoId);
                }
            }
        }
    }

    // Video

    public MutableLiveData<Video> getVideo() {
        return videoLiveData;
    }

    public void nextVideo() {
        setVideoId(PlaylistHelper.getNextVideoId(videoId, playlistId, getApplication()));
    }

    public void previousVideo() {
        setVideoId(PlaylistHelper.getPreviousVideoId(videoId, playlistId, getApplication()));
    }

    public Video getVideoSync() {
        if (videoLiveData != null) {
            return videoLiveData.getValue();
        }
        else {
            return null;
        }
    }

    public void onVideoFinished(boolean isTrailer) {
        if (isTrailer) {
            // When trailer playback is finished just fire video detail event with existing data
            videoLiveData.setValue(videoLiveData.getValue());
        }
    }

    // On air

    public MutableLiveData<Boolean> getOnAir() {
        return onAir;
    }


    // Fullscreen

    public void setFullscreen(boolean value) {
        fullscreen.setValue(value);
    }

    public MutableLiveData<Boolean> isFullscreen() {
        return fullscreen;
    }


    // Favorites

    public void onFavorite() {

    }

    public void onUnFavorite() {

    }


    // Deprecated

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
        timer.schedule(timerTask, TIMER_PERIOD, TIMER_PERIOD);

        return videoCheckOnAir;
    }


    // Zype API

    /**
     * Make API request for video
     *
     * @param videoId Video id
     */
    private void loadVideo(String videoId) {
        Logger.d("loadVideo(): videoId=" + videoId);

        final IZypeApiListener listener = new IZypeApiListener() {
            @Override
            public void onCompleted(ZypeApiResponse response) {
                VideoResponse videoResponse = (VideoResponse) response.data;
                if (response.isSuccessful) {
                    Video video = repo.getVideoSync(videoId);
                    if (video != null) {
                        video = DbHelper.videoUpdateEntityByApi(video, videoResponse.videoData);
                    }
                    else {
                        video = DbHelper.videoApiToEntity(videoResponse.videoData);
                    }
                    repo.updateVideo(video);
                }
                else {
                    // TODO: Add error handling
                }
            }
        };
        api.getVideo(videoId, false, listener);

//        VideoParamsBuilder builder = new VideoParamsBuilder()
//                .addVideoId(videoId);
//        oldApi.executeRequest(WebApiManager.Request.VIDEO, builder.build());
    }

    public boolean updateVideoOnAir(Video video) {
        Video dbVideo = repo.getVideoSync(video.id);
        if (dbVideo.onAir != video.onAir) {
            dbVideo.onAir = video.onAir;
            repo.updateVideo(dbVideo);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Handles API request for video
     *
     * @param event Response event
     */
    @Subscribe
    public void handleVideo(VideoEvent event) {
        Logger.d("handleVideo()");
        VideoData data = event.getEventData().getModelData().getVideoData();
        Video video = DbHelper.videoDataToVideoEntity(data);
        Video dbVideo = repo.getVideoSync(videoId);
        if (dbVideo == null) {
            dbVideo = video;
            onAir.setValue(video.onAir == 1);
        }
        else {
            if (dbVideo.onAir != video.onAir) {
                dbVideo.onAir = video.onAir;
                onAir.setValue(video.onAir == 1);
            }
        }
        repo.updateVideo(dbVideo);
        videoLiveData.setValue(dbVideo);
    }

    /**
     * Make API request for player url with 'download' parameter turned on
     *
     * @param videoId Video id
     */
    private void loadAudioDownloadUrl(final String videoId) {
        Logger.d("loadAudioDownloadUrl(): videoId=" + videoId);

        if (AuthHelper.isLoggedIn()) {
            AuthHelper.onLoggedIn(new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isLoggedIn) {
                    DownloadAudioParamsBuilder builder = new DownloadAudioParamsBuilder();
                    if (SettingsProvider.getInstance().isLoggedIn()) {
                        builder.addAccessToken();
                    }
                    else {
                        builder.addAppKey();
                    }
                    builder.addAudioId(videoId);
                    oldApi.executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO, builder.build());
                }
            });
        }
        else {
            DownloadAudioParamsBuilder builder = new DownloadAudioParamsBuilder();
            builder.addAppKey();
            builder.addAudioId(videoId);
            oldApi.executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO, builder.build());
        }
    }

    /**
     * Handles API request for player audio download url
     *
     * @param event Response event
     */
    @Subscribe
    public void handleDownloadAudio(DownloadAudioEvent event) {
        File fileM4A = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "m4a");
        File fileMP3 = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp3");
        File file = null;
        if (fileM4A != null)
            file = fileM4A;
        else if (fileMP3 != null)
            file = fileMP3;

        String url;
        if (file != null) {
            url = file.getUrl();
            String videoId = event.mFileId;

            // Save download url in the local database if it was changed
            Video video = repo.getVideoSync(videoId);
            video.downloadAudioUrl = url;
            repo.updateVideo(video);
        }
        else {
            Logger.e("handleDownloadVideo(): m4a or mp3 source not found");
        }
    }

    /**
     * Make API request for player url with 'download' parameter turned on
     *
     * @param videoId Video id
     */
    private void loadVideoDownloadUrl(final String videoId) {
        Logger.d("loadVideoDownloadUrl(): videoId=" + videoId);

        if (AuthHelper.isLoggedIn()) {
            AuthHelper.onLoggedIn(new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isLoggedIn) {
                    DownloadVideoParamsBuilder builder = new DownloadVideoParamsBuilder();
                    if (SettingsProvider.getInstance().isLoggedIn()) {
                        builder.addAccessToken();
                    }
                    else {
                        builder.addAppKey();
                    }
                    builder.addVideoId(videoId);
                    oldApi.executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO, builder.build());
                }
            });
        }
        else {
            DownloadVideoParamsBuilder builder = new DownloadVideoParamsBuilder();
            builder.addAppKey();
            builder.addVideoId(videoId);
            oldApi.executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO, builder.build());
        }
    }

    /**
     * Handles API request for player video download url
     *
     * @param event Response event
     */
    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
        Logger.d("handleDownloadVideo()");

        File file = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
        String url;
        if (file != null) {
            url = file.getUrl();
            String videoId = event.mFileId;

            // Save download url in the local database if it was changed
            Video video = repo.getVideoSync(videoId);
            video.downloadVideoUrl = url;
            repo.updateVideo(video);
        }
        else {
            Logger.e("handleDownloadVideo(): mp4 source not found");
        }
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        if (err.getError() == null) {
            if (err.getEventData() != WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO
                    && err.getEventData() != WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO) {
//                onError();
            }
            return;
        }
        if (err.getError().getResponse().getStatus() == BAD_REQUEST) {
            if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
//                onError();
            }
        }
        else {
            if (err instanceof ForbiddenErrorEvent) {
                if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
//                    hideProgress();
//                    showVideoThumbnail();
//                    UiUtils.showErrorIndefiniteSnackbar(findViewById(R.id.root_view), err.getErrMessage());
                }
            }
            else if (err instanceof AuthorizationErrorEvent) {
                // TODO: Handle 401 error
            }
            else {
                if (err.getEventData() != WebApiManager.Request.UN_FAVORITE) {
//                    UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
                }
            }
        }
    }

}
