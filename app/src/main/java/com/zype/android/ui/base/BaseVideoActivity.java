package com.zype.android.ui.base;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastController;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.ui.Helpers.AutoplayHelper;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.chromecast.ChromecastCheckStatusFragment;
import com.zype.android.ui.chromecast.ChromecastFragment;
import com.zype.android.ui.player.ThumbnailFragment;
import com.zype.android.ui.video_details.fragments.OnDetailActivityFragmentListener;
import com.zype.android.ui.video_details.fragments.video.MediaControlInterface;
import com.zype.android.ui.video_details.fragments.video.OnVideoAudioListener;
import com.zype.android.ui.video_details.fragments.video.YouTubeFragment;
import com.zype.android.ui.player.PlayerFragment;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.DownloadAudioParamsBuilder;
import com.zype.android.webapi.builder.DownloadVideoParamsBuilder;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.builder.PlayerParamsBuilder;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.webapi.model.video.VideoData;

/**
 * @author vasya
 * @version 1
 *          date 8/3/15
 */
public abstract class BaseVideoActivity extends BaseActivity implements OnDetailActivityFragmentListener, OnVideoAudioListener {

    public final static int TYPE_YOUTUBE = 30;
    public final static int TYPE_WEB = 40;
    public final static int TYPE_UNKNOWN = -101;
    private static final String FRAGMENT_TAG_PLAYER = "FRAGMENT_TAG_PLAYER";
    protected static String mVideoId;
    protected String epgAppendUrl;
    protected int mType = TYPE_UNKNOWN;
    protected MediaControlInterface mInterface;
    protected ActionBar mActionBar;
    private VideoCastManager mCastManager;
    private VideoCastConsumerImpl mCastConsumer;

    protected String playlistId;
    protected static boolean autoplay = false;

    View baseView;
    private ProgressBar progressBar;

    // LIVE URLS
    protected String liveVideoUrlToPlay = "";
    protected String liveAudioUrlToPlay = "";
    protected String epgVideoUrlToPlay = "";

    protected View getBaseView() {
        return (ViewGroup) ((ViewGroup) this
                .findViewById(R.id.root_view)).getChildAt(0);
    }

    protected abstract int getLayoutId();

    protected static int getMediaType(VideoData mVideoData) {
        int type;
        if (mVideoData == null) {
            return TYPE_WEB;
        }
        if (mVideoData.isVideoDownloaded()) {
            type = PlayerFragment.TYPE_VIDEO_LOCAL;
        } else if (mVideoData.isAudioDownloaded()) {
            type = PlayerFragment.TYPE_AUDIO_LOCAL;
        } else if (!TextUtils.isEmpty(mVideoData.getPlayerVideoUrl())) {
            type = PlayerFragment.TYPE_VIDEO_WEB;
        } else if (!TextUtils.isEmpty(mVideoData.getPlayerAudioUrl())) {
            type = PlayerFragment.TYPE_AUDIO_WEB;
//        } else if (!TextUtils.isEmpty(mVideoData.getYoutubeId())) {
//            type = TYPE_YOUTUBE;
        } else {
            type = TYPE_WEB;
        }
        return type;
    }

    private static Fragment getVideoFragment(String filePath, String adTag, boolean onAir, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_VIDEO_WEB, filePath, adTag, onAir, fileId, autoplay);
    }

    private static Fragment getVideoLocalFragment(String filePath, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_VIDEO_LOCAL, filePath, fileId);
    }

    private Fragment getLiveVideoFragment(String liveUrl, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_VIDEO_LIVE, liveUrl, fileId);
    }

    private static Fragment getAudioFragment(String filePath, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_AUDIO_WEB, filePath, fileId);
    }

    private static Fragment getLiveAudioFragment(String liveUrl, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_AUDIO_LIVE, liveUrl, fileId);
    }

    private static Fragment getAudioLocalFragment(String filePath, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_AUDIO_LOCAL, filePath, fileId);
    }

    private static Fragment getEpgVideoFragment(String filePath, String fileId) {
        return PlayerFragment.newInstance(PlayerFragment.TYPE_VIDEO_EPG, filePath, fileId);
    }

    private static Fragment getYoutubeFragment(String videoId) {
        return YouTubeFragment.newInstance(videoId);
    }

    private static MediaInfo getRemoteMediaInfo(VideoCastManager castManager) {
        MediaInfo mediaInfo = null;
        try {
            mediaInfo = castManager.getRemoteMediaInformation();
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            Logger.e("castManager.getRemoteMediaInformation() ", e);
        }
        return mediaInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        baseView = getBaseView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) baseView.findViewById(R.id.progress);

        initVideo();
        //Block ChromeCast
        if (isShowChromeCastMenu()) {
            initVideoCastManager();
        }
        if (mType != TYPE_WEB && mType != PlayerFragment.TYPE_VIDEO_LIVE && mType != PlayerFragment.TYPE_AUDIO_LIVE) {
            changeFragment(isChromecastConntected());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        autoplay = true;
        initVideo();
        if (mType != TYPE_WEB && mType != PlayerFragment.TYPE_VIDEO_LIVE && mType != PlayerFragment.TYPE_AUDIO_LIVE) {
            changeFragment(isChromecastConntected());
        }
    }

    private void requestLiveAudioUrl(String videoId) {
        PlayerParamsBuilder playerParamsBuilder = new PlayerParamsBuilder()
                .addVideoId(videoId)
                .addAccessToken()
                .addAudio();
        getApi().executeRequest(WebApiManager.Request.PLAYER_ON_AIR_AUDIO, playerParamsBuilder.build());
    }

    private void requestLiveVideoUrl(String videoId) {
        PlayerParamsBuilder playerParamsBuilder = new PlayerParamsBuilder()
                .addAccessToken()
                .addVideoId(videoId);
        getApi().executeRequest(WebApiManager.Request.PLAYER_ON_AIR_VIDEO, playerParamsBuilder.build());
    }

    protected abstract Class<?> getActivityClass();

    public void getDownloadUrls(String videoId) {
        if (mType != TYPE_YOUTUBE && mType != PlayerFragment.TYPE_VIDEO_LIVE && mType != PlayerFragment.TYPE_AUDIO_LIVE) {
            String videoUrl = DataHelper.getVideoUrl(getContentResolver(), videoId);
            String audioUrl = DataHelper.getAudioUrl(getContentResolver(), videoId);
            if (videoUrl == null) {
                getVideoDownloadUrl(videoId);
            }
            if (audioUrl == null) {
                getAudioDownloadUrl(videoId);
            }
        }
    }

    private void getAudioDownloadUrl(String videoId) {
        DownloadAudioParamsBuilder downloadAudioParamsBuilder = new DownloadAudioParamsBuilder();
        if (SettingsProvider.getInstance().isLoggedIn()) {
            downloadAudioParamsBuilder.addAccessToken();
        }
        else {
            downloadAudioParamsBuilder.addAppKey();
        }
        downloadAudioParamsBuilder.addAudioId(videoId);
        getApi().executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO, downloadAudioParamsBuilder.build());
    }

    private void getVideoDownloadUrl(String videoId) {
        DownloadVideoParamsBuilder downloadVideoParamsBuilder = new DownloadVideoParamsBuilder();
        if (SettingsProvider.getInstance().isLoggedIn()) {
            downloadVideoParamsBuilder.addAccessToken();
        }
        else {
            downloadVideoParamsBuilder.addAppKey();
        }
        downloadVideoParamsBuilder.addVideoId(videoId);
        getApi().executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO, downloadVideoParamsBuilder.build());
    }

    @Nullable
    protected Fragment getFragment(boolean isChromeCastConnected, String mVideoId) {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);

        Fragment fragment;
        if (isChromeCastConnected && mType != TYPE_YOUTUBE) {
            MediaInfo localMediaInfo = getLocalMediaInfo(mType, videoData);
            MediaInfo remoteMediaInfo = getRemoteMediaInfo(mCastManager);
            if (localMediaInfo != null && remoteMediaInfo != null
                    && localMediaInfo.getContentId().equalsIgnoreCase(remoteMediaInfo.getContentId())) {
                fragment = ChromecastFragment.newInstance(getLocalMediaInfo(mType, videoData), mVideoId);
            } else {
                fragment = ChromecastCheckStatusFragment.newInstance(localMediaInfo, mVideoId);
            }
        } else {
            switch (mType) {
                case PlayerFragment.TYPE_AUDIO_LOCAL:
                    fragment = getAudioLocalFragment(videoData.getDownloadAudioPath(), mVideoId);
                    break;
                case PlayerFragment.TYPE_AUDIO_WEB:
                    fragment = getAudioFragment(videoData.getPlayerAudioUrl(), mVideoId);
                    break;
                case PlayerFragment.TYPE_VIDEO_LOCAL:
                    fragment = getVideoLocalFragment(videoData.getDownloadVideoPath(), mVideoId);
                    break;
                case PlayerFragment.TYPE_VIDEO_WEB:
                    fragment = getVideoFragment(videoData.getPlayerVideoUrl(), videoData.getAdVideoTag(), videoData.isOnAir(), mVideoId);
                    break;
                case PlayerFragment.TYPE_AUDIO_LIVE:
                    fragment = getLiveAudioFragment(liveAudioUrlToPlay, mVideoId);
                    break;
                case PlayerFragment.TYPE_VIDEO_LIVE:
                    fragment = getLiveVideoFragment(liveVideoUrlToPlay, mVideoId);
                    break;
                case TYPE_YOUTUBE:
                    fragment = getYoutubeFragment(videoData.getId());
                    break;
                case PlayerFragment.TYPE_VIDEO_EPG:
                    fragment = getEpgVideoFragment(epgVideoUrlToPlay, mVideoId);
                    break;
                case TYPE_WEB:
                    fragment = null;
                    break;
                default:
                    throw new IllegalStateException("unknown fragment type " + mType + " mVideoId:" + mVideoId);
            }
        }
        return fragment;
    }

    protected void changeFragment(boolean isChromeCastConnected) {
        Logger.d("changeFragment(): videoId=" + mVideoId);
        Video video = DataRepository.getInstance(getApplication()).getVideoSync(mVideoId);
        Fragment fragment;
        if (epgAppendUrl != null || video.isZypeLive == 0 || VideoHelper.isLiveEventOnAir(video)) {
            fragment = getFragment(isChromeCastConnected, mVideoId);
        }
        else {
            fragment = ThumbnailFragment.newInstance();
//            fragment = ThumbnailFragment.newInstance(mVideoId);
        }
        if (fragment != null) {
            showFragment(fragment);
        }
    }

    private void showFragment(Fragment fragment) {
        Logger.d("showFragment()");
        hideProgress();
        if (mInterface != null) {
            mInterface.stop();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment instanceof MediaControlInterface) {
            mInterface = (MediaControlInterface) fragment;
        }
        fragmentTransaction.replace(R.id.video_container, fragment, FRAGMENT_TAG_PLAYER);
        fragmentTransaction.commit();
    }

    protected void requestVideoUrl(final String videoId) {
        showProgress();
        if (AuthHelper.isLoggedIn()) {
            AuthHelper.onLoggedIn(new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isLoggedIn) {
                    Logger.d("onLoggedIn(): " + isLoggedIn);
                    PlayerParamsBuilder playerParamsBuilder = new PlayerParamsBuilder();
                    if (isLoggedIn) {
                        playerParamsBuilder.addAccessToken();
                    }
                    else {
                        playerParamsBuilder.addAppKey();
                    }
                    playerParamsBuilder.addVideoId(videoId);
                    getApi().executeRequest(WebApiManager.Request.PLAYER_VIDEO, playerParamsBuilder.build());
                }
            });

        }
        else {
            PlayerParamsBuilder playerParamsBuilder = new PlayerParamsBuilder();
            playerParamsBuilder.addAppKey();
            playerParamsBuilder.addVideoId(videoId);
            getApi().executeRequest(WebApiManager.Request.PLAYER_VIDEO, playerParamsBuilder.build());
        }
    }

    protected void requestAudioUrl(String audioId) {
        PlayerParamsBuilder playerParamsBuilder = new PlayerParamsBuilder()
                .addVideoId(audioId)
                .addAppKey()
                .addAudio();
        getApi().executeRequest(WebApiManager.Request.PLAYER_AUDIO, playerParamsBuilder.build());
    }

    @Override
    public void onSeekToMillis(int ms) {
        if (mInterface != null) {
            mInterface.seekToMillis(ms);
            mInterface.play();
        }
    }

//    @Override
//    public void onShowVideo() {
//        if (mInterface != null) {
//            mInterface.stop();
//        }
//        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
//        if (!TextUtils.isEmpty(videoData.getDownloadVideoPath())) {
//            mType = PlayerFragment.TYPE_VIDEO_LOCAL;
//        } else if (!TextUtils.isEmpty(videoData.getPlayerVideoUrl())) {
//            mType = PlayerFragment.TYPE_VIDEO_WEB;
//        } else {
//            requestVideoUrl(mVideoId);
//        }
//        changeFragment(false);
//    }
//
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void onShowAudio() {
//        if (mInterface != null) {
//            mInterface.stop();
//        }
//        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
//        if (!TextUtils.isEmpty(videoData.getDownloadAudioPath())) {
//            mType = PlayerFragment.TYPE_AUDIO_LOCAL;
//        } else if (!TextUtils.isEmpty(videoData.getPlayerAudioUrl())) {
//            mType = PlayerFragment.TYPE_AUDIO_WEB;
//        } else {
//            requestAudioUrl(mVideoId);
//        }
//        changeFragment(false);
//    }

    @Override
    public void videoStarted() {
        hideProgress();
        DataHelper.setVideoPlaying(getContentResolver(), mVideoId);
    }

    @Override
    public void videoFinished() {
        DataHelper.setVideoPlayed(getContentResolver(), mVideoId);
        DataHelper.setPlayTime(getContentResolver(), mVideoId, 0);

        // Jump to next video when Autoplay feature is enabled
        if (ZypeConfiguration.autoplayEnabled(this)
                && SettingsProvider.getInstance().getBoolean(SettingsProvider.AUTOPLAY)) {
            Logger.d("videoFinished(): Autoplay next video");
            hideVideoLayout();
            showProgress();
            autoplay = true;
            AutoplayHelper.playNextVideo(this, mVideoId, playlistId);
        }
    }

    @Override
    public void audioFinished() {
        DataHelper.setVideoPlayed(getContentResolver(), mVideoId);
        DataHelper.setPlayTime(getContentResolver(), mVideoId, 0);
    }

    @Override
    public void audioStarted() {
        hideProgress();
        DataHelper.setVideoPlaying(getContentResolver(), mVideoId);
    }

    @Override
    public void onShareVideo(String videoId) {
        UiUtils.shareVideo(this, videoId);
    }

    @Override
    public void onFavorite(String videoId) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(this)) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                FavoriteParamsBuilder builder = new FavoriteParamsBuilder()
                        .addVideoId(videoId)
                        .addAccessToken();
                getApi().executeRequest(WebApiManager.Request.FAVORITE, builder.build());
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
            }
        }
        else {
            DataHelper.setFavoriteVideo(getContentResolver(), videoId, true);
        }
    }

    @Override
    public void onUnFavorite(String videoId) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(this)) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                FavoriteParamsBuilder builder = new FavoriteParamsBuilder()
                        .addPathFavoriteId(DataHelper.getFavoriteId(getContentResolver(), videoId))
                        .addPathVideoId(videoId)
                        .addAccessToken();
                getApi().executeRequest(WebApiManager.Request.UN_FAVORITE, builder.build());
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
            }
        }
        else {
            DataHelper.setFavoriteVideo(getContentResolver(), videoId, false);
        }
    }

    @Override
    public int getCurrentTimeStamp() {
        if (mInterface == null) {
            return -1;
        }
        return mInterface.getCurrentTimeStamp();
    }

    @Override
    public void saveCurrentTimeStamp(long currentPosition) {
        DataHelper.setPlayTime(getContentResolver(), mVideoId, currentPosition);
    }

    @Override
    public void onDownloadVideo(String episodeId) {
        String videoUrl = DataHelper.getVideoUrl(getContentResolver(), episodeId);
        if (TextUtils.isEmpty(videoUrl)) {
            throw new IllegalStateException("Empty url for episodeId=" + episodeId);
        }
        DownloadHelper.addVideoToDownloadList(getApplicationContext(), videoUrl, episodeId);
    }

    @Override
    public void onDownloadAudio(String episodeId) {
        String audioUrl = DataHelper.getAudioUrl(getContentResolver(), episodeId);
        if (TextUtils.isEmpty(audioUrl)) {
            throw new IllegalStateException("Empty url for episodeId=" + episodeId);
        }
        DownloadHelper.addAudioToDownloadList(getApplicationContext(), audioUrl, episodeId);
    }


    public boolean isChromecastConntected() {
        if (!isShowChromeCastMenu()) {
            return false;
        }
        VideoCastManager castManager = VideoCastManager.getInstance();
        return castManager != null && (castManager.isConnected() || castManager.isConnecting());
    }

    public boolean isYoutube() {
        return mType == TYPE_YOUTUBE;
    }

    public boolean isShowChromeCastMenu() {
        return (mType != TYPE_YOUTUBE && mType != PlayerFragment.TYPE_AUDIO_LIVE && mType != PlayerFragment.TYPE_VIDEO_LIVE);
    }

    public int getCurrentFragment() {
        return mType;
    }

    private MediaInfo getLocalMediaInfo(int type, @Nullable VideoData videoData) {
        String url = getNetworkUrl(type, videoData);
        if (url == null) {
            throw new IllegalStateException(" URL == null");
        }
        MediaInfo mediaInfo;
        String contentType;
        int mediaType;
        if (type == PlayerFragment.TYPE_AUDIO_WEB || type == PlayerFragment.TYPE_AUDIO_LOCAL || type == PlayerFragment.TYPE_AUDIO_LIVE) {
            mediaType = MediaMetadata.MEDIA_TYPE_MUSIC_TRACK;
            contentType = "audio/mp4";
        } else {
            mediaType = MediaMetadata.MEDIA_TYPE_MOVIE;
            if (url.contains(".mp4")) {
                contentType = "video/mp4";
            } else {
                contentType = "application/x-mpegurl";
            }

        }
        MediaMetadata movieMetadata = new MediaMetadata(mediaType);
        if (videoData != null) {
            movieMetadata.putString(MediaMetadata.KEY_TITLE, videoData.getTitle());
        } else {
            movieMetadata.putString(MediaMetadata.KEY_TITLE, "Live");
        }
        if (videoData != null && videoData.getThumbnails() != null && videoData.getThumbnails().size() > 0) {
            Thumbnail thumbnail = videoData.getThumbnails().get(0);
            movieMetadata.addImage(new WebImage(Uri.parse(thumbnail.getUrl())));
        } else {
            movieMetadata.addImage(new WebImage(Uri.EMPTY));
        }
        mediaInfo = new MediaInfo.Builder(url)
                .setContentType(contentType)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .build();
        return mediaInfo;
    }

    @Nullable
    private String getNetworkUrl(int type, VideoData videoData) {
        String url;
        switch (type) {
            case PlayerFragment.TYPE_VIDEO_WEB:
                if (!TextUtils.isEmpty(videoData.getDownloadVideoUrl())) {
                    url = videoData.getDownloadVideoUrl();
                } else {
                    url = videoData.getPlayerVideoUrl();
                }
                break;
            case PlayerFragment.TYPE_VIDEO_LOCAL:
                url = videoData.getDownloadVideoUrl();
                break;
            case PlayerFragment.TYPE_VIDEO_LIVE:
                url = liveVideoUrlToPlay;
                break;
            case PlayerFragment.TYPE_AUDIO_WEB:
                if (!TextUtils.isEmpty(videoData.getDownloadAudioUrl())) {
                    url = videoData.getDownloadVideoUrl();
                } else {
                    url = videoData.getPlayerAudioUrl();
                }
                break;
            case PlayerFragment.TYPE_AUDIO_LOCAL:
                url = videoData.getDownloadAudioUrl();
                break;
            case PlayerFragment.TYPE_AUDIO_LIVE:
                url = liveAudioUrlToPlay;
                break;
            case PlayerFragment.TYPE_VIDEO_EPG:
                url = epgVideoUrlToPlay;
                break;
//            case TYPE_YOUTUBE:
//                url = null;
//                break;
            default:
                throw new IllegalStateException("unknown type");
        }
        return url;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mCastManager != null) {
            if (mCastManager.onDispatchVolumeKeyEvent(event, ZypeApp.VOLUME_INCREMENT)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cast_player_menu, menu);
        if (isShowChromeCastMenu()) {
            mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (mCastConsumer != null) {
            mCastManager = VideoCastManager.getInstance();
            mCastManager.addVideoCastConsumer(mCastConsumer);
            mCastManager.incrementUiCounter();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCastConsumer != null) {
            mCastManager.removeVideoCastConsumer(mCastConsumer);
            mCastManager.decrementUiCounter();
        }
    }

    private void setupCastListener() {
        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata,
                                               String sessionId, boolean wasLaunched) {
                if (mType != TYPE_YOUTUBE) {
                    changeFragment(true);
                } else {
                    UiUtils.showErrorSnackbar(getBaseView(), "This format does not support");
                }
            }

            @Override
            public void onApplicationDisconnected(int errorCode) {

            }

            @Override
            public void onDisconnected() {
                changeFragment(false);
            }

            @Override
            public void onFailed(int resourceId, int statusCode) {
                Logger.v("resourceId: " + resourceId + " statusCode: " + statusCode);
            }

            @Override
            public void onConnectionSuspended(int cause) {

            }

            @Override
            public void onConnectivityRecovered() {

            }

            @Override
            public void onRemoteMediaPlayerMetadataUpdated() {
                if (mType != TYPE_YOUTUBE) {
                    changeFragment(true);
                } else {
                    UiUtils.showErrorSnackbar(getBaseView(), "This format does not support");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initVideoCastManager() {
        // this is the default behavior but is mentioned to make it clear that it is configurable.
        VideoCastManager.getInstance().setNextPreviousVisibilityPolicy(
                VideoCastController.NEXT_PREV_VISIBILITY_POLICY_DISABLED);

        // this is the default behavior but is mentioned to make it clear that it is configurable.
        VideoCastManager.getInstance().setCastControllerImmersive(true);
        mCastManager = VideoCastManager.getInstance();
        setupCastListener();
    }

    private void initVideo() {
        if (getIntent() != null && getIntent().hasExtra(BundleConstants.VIDEO_ID)
                && !TextUtils.isEmpty(getIntent().getStringExtra(BundleConstants.VIDEO_ID))) {
            playlistId = getIntent().getStringExtra(BundleConstants.PLAYLIST_ID);
            mVideoId = getIntent().getStringExtra(BundleConstants.VIDEO_ID);
            epgAppendUrl = getIntent().getStringExtra(BundleConstants.EPG_APPEND);
            mType = getIntent().getIntExtra(BundleConstants.MEDIA_TYPE, TYPE_UNKNOWN);
            if (mType == PlayerFragment.TYPE_VIDEO_LIVE) {
                requestLiveVideoUrl(mVideoId);
            } else if (mType == PlayerFragment.TYPE_AUDIO_LIVE) {
                requestLiveAudioUrl(mVideoId);
            }
            if (mType == TYPE_UNKNOWN) {
                mType = getMediaType(VideoHelper.getFullData(getContentResolver(), mVideoId));
            }
        } else {
            mType = TYPE_WEB;
            Logger.e("VideoId is empty !!");// но тут должен быть путь
        }
        if (mType == TYPE_WEB) {
            if(epgAppendUrl != null) {
                requestVideoUrl(mVideoId);
            }
//            else
//            if (!ZypeApp.get(this).getAppConfiguration().updatedPaywalls) {
//                if (AuthHelper.isVideoUnlocked(this, mVideoId, null)) {
//                    Video video = DataRepository.getInstance(getApplication()).getVideoSync(mVideoId);
//                    if (video != null &&
//                            (video.isZypeLive == 0 || VideoHelper.isLiveEventOnAir(video))) {
//                        requestVideoUrl(mVideoId);
//                    }
//                }
//            }
        }
    }

    // //////////
    // UI
    //
    protected void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public ProgressBar getVideoProgressBar() {
        return progressBar;
    }

    protected void hideVideoLayout() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_PLAYER);
        fragmentManager.beginTransaction().hide(fragment).commit();
    }

}
