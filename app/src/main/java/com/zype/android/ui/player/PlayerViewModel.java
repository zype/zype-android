package com.zype.android.ui.player;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.common.images.WebImage;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.BuildConfig;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.analytics.AnalyticsEvents;
import com.zype.android.analytics.AnalyticsManager;
import com.zype.android.core.provider.helpers.PlaylistHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.AdMacrosHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.zypeapi.IZypeApiListener;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.model.Advertising;
import com.zype.android.zypeapi.model.Analytics;
import com.zype.android.zypeapi.model.PlayerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Evgeny Cherkasov on 23.07.2018
 */
public class PlayerViewModel extends AndroidViewModel implements CustomPlayer.InfoListener {

    private MutableLiveData<List<PlayerMode>> availablePlayerModes;
    private MutableLiveData<PlayerMode> playerMode;
    private MutableLiveData<String> playerUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTrailer = new MutableLiveData<>();
    private MutableLiveData<Integer> playbackState = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private MutableLiveData<Error> error = new MutableLiveData<>();

    private String videoId;
    private Video video;
    private String playlistId;
    private String trailerVideoId;
    private String trailerUrl;

    private List<AdSchedule> adSchedule;
    private AnalyticBeacon analyticBeacon;
    private boolean onAir;

    private long playbackPosition = 0;
    private long previousPlaybackPosition = -1;
    private boolean isPlaybackPositionRestored;
    private boolean isPlaybackStarted = false;
    private boolean isUrlLoaded = false;
    private boolean inBackground = false;

    private static final String APP_BUNDLE = "app_bundle";
    private static final String APP_DOMAIN = "app_domain";
    private static final String APP_ID = "app_id";
    private static final String APP_NAME = "app_name";
    private static final String DEVICE_TYPE = "device_type";
    private static final String DEVICE_IFA = "device_ifa";
    private static final String DEVICE_MAKE = "device_make";
    private static final String DEVICE_MODEL = "device_model";
    private static final String UUID = "uuid";
    private static final String VPI = "vpi";

    public enum PlayerMode {
        AUDIO,
        VIDEO
    }

    public enum ErrorType {
        INVALID_VIDEO_ID,
        LOCKED,
        UNKNOWN
    }

    public static class Error {
        public ErrorType type;
        public String message;

        public Error(ErrorType type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    private DataRepository repo;
    private ZypeApi api;
    WebApiManager oldApi;

    public PlayerViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
        oldApi = WebApiManager.getInstance();
        oldApi.subscribe(this);

        availablePlayerModes = new MutableLiveData<>();
        availablePlayerModes.setValue(new ArrayList<>());
        playerMode = new MutableLiveData<>();

        isTrailer.setValue(false);
    }

    @Override
    protected void onCleared() {
        oldApi.unsubscribe(this);
        super.onCleared();
    }

    public void init(String videoId, String playlistId, PlayerMode mediaType) {
        this.videoId = videoId;
        this.playlistId = playlistId;

        video = repo.getVideoSync(videoId);
        if (video == null) {
            error.setValue(new Error(ErrorType.INVALID_VIDEO_ID, ""));
            return;
        }

        setPlaybackPosition(video.playTime);
        isPlaybackStarted = false;
        isPlaybackPositionRestored = false;
        isPlaying.setValue(false);

        isTrailer.setValue(false);
        trailerVideoId = null;

        updateAvailablePlayerModes();
        if (mediaType != null || isMediaTypeAvailable(mediaType)) {
            playerMode.setValue(mediaType);
        }
        else {
            List<PlayerMode> mediaTypes = availablePlayerModes.getValue();
            if (mediaTypes != null && !mediaTypes.isEmpty()) {
                playerMode.setValue(mediaTypes.get(0));
            }
            else {
                playerMode.setValue(null);
            }
        }
        setPlayerUrl(null);
    }

    // Ad schedule

    private void updateAdSchedule() {
        adSchedule = repo.getAdScheduleSync(videoId);
    }

    public List<AdSchedule> getAdSchedule() {
        return adSchedule;
    }

    // Analytics

    private void updateAnalyticsBeacon() {
        analyticBeacon = repo.getAnalyticsBeaconSync(videoId);
    }

    public AnalyticBeacon getAnalyticBeacon() {
        return analyticBeacon;
    }

    private void analyticsContentCompletion() {
        if (video == null)
            return;

        if (previousPlaybackPosition < 3000 && playbackPosition >= 3000) {
            AnalyticsManager.getInstance()
                    .onPlayerEvent(AnalyticsEvents.EVENT_CONTENT_STARTED, video, playbackPosition);
        }
        else if (previousPlaybackPosition < video.duration * 250 && playbackPosition >= video.duration * 250) {
            AnalyticsManager.getInstance()
                    .onPlayerEvent(AnalyticsEvents.EVENT_CONTENT_COMPLETED_25, video, playbackPosition);
        }
        else if (previousPlaybackPosition < video.duration * 500 && playbackPosition >= video.duration * 500) {
            AnalyticsManager.getInstance()
                    .onPlayerEvent(AnalyticsEvents.EVENT_CONTENT_COMPLETED_50, video, playbackPosition);
        }
        else if (previousPlaybackPosition < video.duration * 750 && playbackPosition >= video.duration * 750) {
            AnalyticsManager.getInstance()
                    .onPlayerEvent(AnalyticsEvents.EVENT_CONTENT_COMPLETED_75, video, playbackPosition);
        }
    }

    // Playback position

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public void setPlaybackPosition(long position) {
        previousPlaybackPosition = playbackPosition;
        playbackPosition = position;
        analyticsContentCompletion();
    }

    public void savePlaybackPosition(long position) {
        setPlaybackPosition(position);

        if(!TextUtils.isEmpty(videoId)) {
            Video video = repo.getVideoSync(videoId);

            if(video != null) {
                video.playTime = position;
                repo.updateVideo(video);
            }
        }

        isPlaybackPositionRestored = false;
    }

    public boolean playbackPositionRestored() {
        return isPlaybackPositionRestored;
    }

    public void onPlaybackPositionRestored() {
        isPlaybackPositionRestored = true;
    }

    public void onPlaybackResumed() {
        if (isTrailer.getValue()) {
            return;
        }
        if(!TextUtils.isEmpty(videoId)) {
            Video video = repo.getVideoSync(videoId);
            if (video != null) {
                video.isPlayStarted = 1;
                repo.updateVideo(video);

                if (!isPlaybackStarted) {
                    isPlaybackStarted = true;
                    AnalyticsManager.getInstance()
                            .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_STARTED, video, playbackPosition);
                }
                else {
                    AnalyticsManager.getInstance()
                            .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_RESUMED, video, playbackPosition);
                }
            }
        }
    }

    public void onPlaybackPaused() {
        AnalyticsManager.getInstance()
                .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_PAUSED, video, playbackPosition);
    }

    public void onPlayback() {
        if (!TextUtils.isEmpty(videoId)) {
            Video video = repo.getVideoSync(videoId);
            if (video != null) {
                AnalyticsManager.getInstance()
                        .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK, video, playbackPosition);
            }
        }
    }

    public void onSeekTo(long position) {
        AnalyticsManager.getInstance()
                .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_SEEK_STARTED, video, position);
        AnalyticsManager.getInstance()
                .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_SEEK_COMPLETED, video, position);
    }

    public void onPlaybackFinished() {
        if (isTrailer.getValue()) {
            return;
        }
        if(!TextUtils.isEmpty(videoId)) {
            Video video = repo.getVideoSync(videoId);
            if (video != null) {
                video.isPlayStarted = 1;
                video.isPlayFinished = 1;
                repo.updateVideo(video);

                AnalyticsManager.getInstance()
                        .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_FINISHED, video, playbackPosition);
                AnalyticsManager.getInstance()
                        .onPlayerEvent(AnalyticsEvents.EVENT_PLAYBACK_COMPLETED, video, playbackPosition);
            }
        }
    }

    public boolean isThereNextVideo() {
        return !(PlaylistHelper.getNextVideoId(videoId, repo.getPlaylistVideosSync(playlistId)) == null);
    }

    public boolean isTherePreviousVideo() {
        return !(PlaylistHelper.getPreviousVideoId(videoId, repo.getPlaylistVideosSync(playlistId)) == null);
    }

    // Playback state

    public MutableLiveData<Integer> getPlaybackState() {
        return playbackState;
    }

    public void setPlaybackState(int state) {
        playbackState.setValue(state);
    }

    public LiveData<Boolean> getIsPlaying() { return isPlaying; }

    public void setIsPlaying(boolean value) {
        isPlaying.setValue(value);
    }

    // On air

    public void setOnAir(boolean onAir) {
        this.onAir = onAir;
    }

    public boolean isOnAir() {
        return this.onAir;
    }

    // Player url

    public LiveData<String> getPlayerUrl() {
        if (playerUrl == null) {
            playerUrl = new MutableLiveData<>();
        }
        if (playerUrl.getValue() == null) {
            if (isTrailer.getValue()) {
                setPlayerUrl(trailerUrl);
            } else {
                Video video = repo.getVideoSync(videoId);
                if (video != null) {
                    video.playerAudioUrl = null;
                    video.playerVideoUrl = null;
                    updatePlayerUrl(video);
                }
                loadPlayer();
            }
        }
        return playerUrl;
    }

    private void setPlayerUrl(String value) {
        Logger.d("setPlayerUrl(): value=" + value);
        playerUrl.setValue(value);
    }

    private void updatePlayerUrl(Video video) {
        String newPlayerUrl = null;
        if (playerMode.getValue() != null) {
            switch (playerMode.getValue()) {
                case AUDIO:
                    if (video.isDownloadedAudio == 1) {
                        newPlayerUrl = video.downloadAudioPath;
                    }
                    else {
                        newPlayerUrl = video.playerAudioUrl;
                    }
                    break;
                case VIDEO:
                    if (video.isDownloadedVideo == 1) {
                        newPlayerUrl = video.downloadVideoPath;
                    }
                    else {
                        newPlayerUrl = video.playerVideoUrl;
                    }
                    break;
            }
        }
        updateAdSchedule();
        updateAnalyticsBeacon();
        if (playerUrl.getValue() == null) {
            if (newPlayerUrl != null) {
                setPlayerUrl(newPlayerUrl);
            }
        }
        else {
            if (!playerUrl.getValue().equals(newPlayerUrl)) {
                setPlayerUrl(newPlayerUrl);
            }
        }
    }

    // Player mode

    public LiveData<PlayerMode> getPlayerMode() {
        return playerMode;
    }

    public void setPlayerMode(PlayerMode mode) {
        Logger.d("setPlayerMode(): mode=" + mode.name());
        Video video = repo.getVideoSync(videoId);
        if (video != null) {
            if (playerMode.getValue() != mode) {
                playerMode.setValue(mode);
                updatePlayerUrl(video);
            }
        }
    }

    public LiveData<List<PlayerMode>> getAvailablePlayerModes() {
        return availablePlayerModes;
    }

    private void updateAvailablePlayerModes() {
        List<PlayerMode> result = new ArrayList<>();

        Video video = repo.getVideoSync(videoId);
        if (video != null) {
            if (ZypeApp.get(getApplication()).getAppConfiguration().audioOnlyPlaybackEnabled) {
                if (!TextUtils.isEmpty(video.playerAudioUrl)
                        || video.isDownloadedAudio == 1) {
                    result.add(PlayerMode.AUDIO);
                }
            }
            if (!TextUtils.isEmpty(video.playerVideoUrl)
                    || video.isDownloadedVideo == 1) {
                result.add(PlayerMode.VIDEO);
            }
        }

        availablePlayerModes.setValue(result);
    }

    public boolean isMediaTypeAvailable(PlayerMode mediaType) {
        return availablePlayerModes.getValue() != null
                && availablePlayerModes.getValue().contains(mediaType);
    }

    public void setMediaTypeAvailable(PlayerMode mediaType, boolean available) {
        Logger.d("setMediaTypeAvailable(): mediaType=" + mediaType + ", available=" + available);
        if (isMediaTypeAvailable(mediaType)) {
            if (!available) {
                List<PlayerMode> currentPlayerModes = availablePlayerModes.getValue();
                currentPlayerModes.remove(mediaType);
                // This is a hack for the case when we disable VIDEO player mode for audio only video.
                // We don't have an audio player url, because the API returns empty media url list
                // when the audio=true parameter is specified.
                // So we just use video player url as an audion one.
                if (mediaType == PlayerMode.VIDEO && currentPlayerModes.isEmpty()) {
                    currentPlayerModes.add(PlayerMode.AUDIO);
                    Video video = repo.getVideoSync(videoId);
                    if (video != null) {
                        video.playerAudioUrl = video.playerVideoUrl;
                        repo.updateVideo(video);
                    }
                }
                availablePlayerModes.setValue(currentPlayerModes);
            }
        }
        else {
            if (available) {
                availablePlayerModes.getValue().add(mediaType);
                availablePlayerModes.setValue(availablePlayerModes.getValue());
            }
        }
    }

    // Downloads

    public boolean isAudioDownloaded() {
        Video video = repo.getVideoSync(videoId);
        if (video != null) {
            return video.isDownloadedAudio == 1;
        }
        else
            return false;
    }

    public boolean isVideoDownloaded() {
        Video video = repo.getVideoSync(videoId);
        if (video != null) {
            return video.isDownloadedVideo == 1;
        }
        else
            return false;
    }

    // Background

    public boolean isBackgroundPlaybackEnabled() {
        if (playerMode.getValue() != null) {
            switch (playerMode.getValue()) {
                case AUDIO:
                    return ZypeConfiguration.isBackgroundAudioPlaybackEnabled(getApplication());
                case VIDEO:
                    return ZypeConfiguration.isBackgroundPlaybackEnabled(getApplication());
                default:
                    return false;
            }
        }
        else {
            return false;
        }
    }

    public void setToBackground(boolean value) {
        inBackground = value;
    }

    public boolean isInBackground() {
        return inBackground;
    }

    // Error

    public LiveData<Error> getPlayerError() {
        if (error == null) {
            error = new MutableLiveData<>();
        }
        return error;
    }

    public void onPlayerError() {
        AnalyticsManager.getInstance()
                .onPlayerEvent(AnalyticsEvents.EVENT_PLAYER_ERROR, video, playbackPosition);
    }

    // Trailer

    public LiveData<Boolean> isTrailer() {
        return isTrailer;
    }

    public void setTrailerVideoId(String trailerVideoId) {
        this.trailerVideoId = trailerVideoId;
        if (TextUtils.isEmpty(trailerVideoId)) {
            isTrailer.setValue(false);
            trailerUrl = null;
            updatePlayerUrl(repo.getVideoSync(videoId));
        }
        else {
            isTrailer.setValue(true);
            setPlayerUrl(trailerUrl);
            loadPlayer();
        }
    }

    //

    public void loadPlayer() {
        Logger.d("loadPlayer(): videoId=" + videoId);
//        String uuid = null;
        AdMacrosHelper.fetchDeviceId(getApplication().getApplicationContext(), deviceId -> {
            Logger.d("onDeviceId(): deviceId=" + deviceId);
            if (AuthHelper.isLoggedIn()) {
                AuthHelper.onLoggedIn(isLoggedIn ->
                        loadVideoPlayer(AuthHelper.getAccessToken(), null));
            }
            else {
                loadVideoPlayer(null, null);
            }
        });
    }

    IZypeApiListener createVideoPlayerListener(String accessToken, String uuid) {
        return response -> {
            PlayerResponse playerResponse = (PlayerResponse) response.data;
            if (response.isSuccessful) {
                List<com.zype.android.zypeapi.model.File> files = playerResponse.playerData.body.files;
                if (files == null || files.isEmpty()) {
                    Logger.w("loadVideoPlayer()::onCompleted(): Video source not found");
                    return;
                }
                // Take first source in the list
                String url = files.get(0).url;
                Logger.d("loadVideoPlayer()::onCompleted(): url=" + url);

                // In play trailer mode just update player url
                if (isTrailer.getValue()) {
                    trailerUrl = url;
                    setPlayerUrl(trailerUrl);
                    return;
                }

                // Ad tags
                repo.deleteAdSchedule(videoId);
                Advertising advertising = playerResponse.playerData.body.advertising;
                if (advertising != null) {
                    List<AdSchedule> schedule = DbHelper.adScheduleApiToEntity(advertising.schedule, videoId);
                    repo.insertAdSchedule(schedule);
                }
                updateAdSchedule();

                // Analytics
                repo.deleteAnalyticsBeacon(videoId);
                Analytics analytics = playerResponse.playerData.body.analytics;
                if (analytics != null &&
                        analytics.beacon != null && analytics.dimensions != null) {
                    repo.insertAnalyticsBeacon(DbHelper.analyticsApiToEntity(analytics));
                }
                updateAnalyticsBeacon();

                // Save video url in the local database if it was changed
                Video video = repo.getVideoSync(videoId);
                if (video.playerVideoUrl == null || !video.playerVideoUrl.equals(url)) {
                    video.playerVideoUrl = url;
                    repo.updateVideo(video);
                }
                updateAvailablePlayerModes();
                if (playerMode.getValue() == null) {
                    setPlayerMode(PlayerMode.VIDEO);
                }
                else if (playerMode.getValue() == PlayerMode.VIDEO) {
                    updatePlayerUrl(video);
                }

                // Load audio
                if (ZypeApp.get(getApplication()).getAppConfiguration().audioOnlyPlaybackEnabled) {
                    loadAudioPlayer(accessToken, uuid);
                }
            }
            else {
                if (response.errorBody != null && response.errorBody.status == 403) {
                    error.setValue(new Error(ErrorType.LOCKED, ""));
                }
                else {
                    if (!isVideoDownloaded()) {
                        if (WebApiManager.isHaveActiveNetworkConnection(getApplication())) {
                            error.setValue(new Error(ErrorType.UNKNOWN,
                                    getApplication().getString(R.string.video_error_bad_request)));
                        } else {
                            error.setValue(new Error(ErrorType.UNKNOWN,
                                    getApplication().getString(R.string.error_internet_connection)));
                        }
                    }
                }
            }
        };
    }

    private HashMap<String, String> getValues(){
        HashMap<String, String> params = new HashMap<>();

        ApplicationInfo appInfo = getApplication().getApplicationInfo();
        // App data
        params.put(APP_BUNDLE, appInfo.packageName);
        params.put(APP_DOMAIN, appInfo.packageName);
        params.put(APP_ID, appInfo.packageName);
        params.put(APP_NAME, (appInfo.labelRes == 0) ? appInfo.nonLocalizedLabel.toString() : getApplication().getString(appInfo.labelRes));
        // Advertizing ID
        String advertisingId = SettingsProvider.getInstance().getString(SettingsProvider.GOOGLE_ADVERTISING_ID);
        params.put(DEVICE_IFA, advertisingId);
        // Device data
        params.put(DEVICE_MAKE, Build.MANUFACTURER);
        params.put(DEVICE_MODEL, Build.MODEL);
        // Default device type is '7' (set top box device)
        params.put(DEVICE_TYPE, "7");
        // Default VPI is 'MP4'
        params.put(VPI, "MP4");
        // UUID us the same as Advertising id
        params.put(UUID, advertisingId);
        return params;
    }

    private void loadVideoPlayer(String accessToken, String uuid) {
        if (isTrailer.getValue()) {
            api.getPlayer(trailerVideoId, false, accessToken, uuid,
                    getApplication().getString(R.string.app_name) + "/" + BuildConfig.VERSION_NAME,
                    createVideoPlayerListener(accessToken, uuid), getValues());
        }
        else {
            api.getPlayer(videoId, false, accessToken, uuid,
                    getApplication().getString(R.string.app_name) + "/" + BuildConfig.VERSION_NAME,
                    createVideoPlayerListener(accessToken, uuid), getValues());
        }
    }

    IZypeApiListener createAudioPlayerListener(String accessToken, String uuid) {
        return response -> {
            PlayerResponse playerResponse = (PlayerResponse) response.data;
            if (response.isSuccessful) {
                List<com.zype.android.zypeapi.model.File> files = playerResponse.playerData.body.files;
                if (files == null || files.isEmpty()) {
                    Logger.w("loadAudioPlayer()::onCompleted(): Audio source not found");
                    return;
                }
                // Take first source in the list
                String url = files.get(0).url;
                Logger.d("loadAudioPlayer()::onCompleted(): url=" + url);

                // Save audio url in the local database if it was changed
                Video video = repo.getVideoSync(videoId);
                if (video.playerAudioUrl == null || !video.playerAudioUrl.equals(url)) {
                    video.playerAudioUrl = url;
                    repo.updateVideo(video);
                }
                updateAvailablePlayerModes();
                if (playerMode.getValue() == null) {
                    setPlayerMode(PlayerMode.AUDIO);
                }
                else if (playerMode.getValue() == PlayerMode.AUDIO) {
                    updatePlayerUrl(video);
                }
            }
            else {
            }
        };
    }

    private void loadAudioPlayer(String accessToken, String uuid) {
        api.getPlayer(videoId, true, accessToken, uuid,
                getApplication().getString(R.string.app_name) + "/" + BuildConfig.VERSION_NAME,
                createAudioPlayerListener(accessToken, uuid), getValues());
    }

    // 'CustomPlayer.InfoListener' implementation

    @Override
    public void onVideoFormatEnabled(Format format, int trigger, long mediaTimeMs) {
        Logger.i("onVideoFormatEnabled()");
        if (getPlayerMode().getValue() == PlayerMode.VIDEO
                && format.codecs != null && format.codecs.equals("mp4a.40.2")) {
            Video video = repo.getVideoSync(videoId);
            video.playerVideoUrl = null;
            repo.updateVideo(video);

            updateAvailablePlayerModes();
            setPlayerMode(PlayerMode.AUDIO);
        }
    }

    @Override
    public void onAudioFormatEnabled(Format format, int trigger, long mediaTimeMs) {
        Logger.i("onAudioFormatEnabled()");
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {
    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
    }

    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
    }

    @Override
    public void onAvailableRangeChanged(TimeRange availableRange) {
    }

    // ExoPlayer 2 related staff
    // TODO: Move this to a helper class

    public MediaSource getMediaSource(Context context, String contentUri) {
        MediaSource result = null;

        String userAgent = Util.getUserAgent(context, WebApiManager.CUSTOM_HEADER_VALUE);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        );
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, null,
                httpDataSourceFactory);

        if (contentUri.contains("http:") || contentUri.contains("https:")) {
            if (contentUri.contains(".mp4")
                    || contentUri.contains(".m4a")
                    || contentUri.contains(".mp3")) {
                result = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(contentUri));
            }
            else {
                result = new HlsMediaSource.Factory(dataSourceFactory)
//                        .setAllowChunklessPreparation(true)
                        .createMediaSource(Uri.parse(contentUri));
            }
        }
        else {
            result = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(contentUri));
        }
        return result;
    }

    public MediaQueueItem buildMediaQueueItem(Video video, String contentUri) {
        Logger.d("buildMediaQueueItem(): contentUri=" + contentUri);
        int mediaType;
        String contentType;
        if (playerMode.getValue() == PlayerMode.AUDIO) {
            mediaType = MediaMetadata.MEDIA_TYPE_MUSIC_TRACK;
            contentType = "audio/mp4";
        }
        else {
            mediaType = MediaMetadata.MEDIA_TYPE_MOVIE;
            if (contentUri.contains("mp4")) {
                contentType = "video/mp4";
            }
            else {
                contentType = "application/x-mpegurl";
            }
        }
        MediaMetadata mediaMetadata = new MediaMetadata(mediaType);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, video.title);
        Uri imageUri = Uri.EMPTY;
        Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
        if (thumbnail != null) {
            imageUri = Uri.parse(thumbnail.getUrl());
        }
        mediaMetadata.addImage(new WebImage(imageUri));
        MediaInfo mediaInfo = new MediaInfo.Builder(contentUri)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(contentType)
                .setMetadata(mediaMetadata)
                .build();
        return new MediaQueueItem.Builder(mediaInfo).build();
    }
}
