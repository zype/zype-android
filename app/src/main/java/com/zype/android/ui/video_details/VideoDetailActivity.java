package com.zype.android.ui.video_details;

import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Helpers.AutoplayHelper;
import com.zype.android.ui.Helpers.IPlaylistVideos;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerFragment;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.player.ThumbnailFragment;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.player.PlayerAudioEvent;
import com.zype.android.webapi.events.player.PlayerVideoEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.Advertising;
import com.zype.android.webapi.model.player.AdvertisingSchedule;
import com.zype.android.webapi.model.player.Analytics;
import com.zype.android.webapi.model.player.AnalyticsDimensions;
import com.zype.android.webapi.model.player.File;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static com.zype.android.utils.BundleConstants.REQUEST_USER;
import static com.zype.android.webapi.WebApiManager.WorkerHandler.BAD_REQUEST;

public class VideoDetailActivity extends BaseVideoActivity implements IPlaylistVideos {
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    public static final String EXTRA_AUTOPLAY = "Autoplay";

    private VideoDetailPager mViewPager;
    private TabLayout mTabLayout;

    private FrameLayout layoutImage;
    private ImageView imageVideo;

    VideoDetailViewModel videoDetailViewModel;
    PlayerViewModel playerViewModel;

    Observer<PlayerViewModel.Error> playerErrorObserver = null;
    Observer<String> contentUriObserver;    // Used in updated paywall flow
    Observer<String> playerUrlObserver = null;
    Observer<Video> videoDetailObserver;

    public static void startActivity(Activity activity, String videoId, String playlistId) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, String videoId, String playlistId, int mediaType) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        bundle.putString(BundleConstants.PLAYLIST_ID, playlistId);
        bundle.putInt(BundleConstants.MEDIA_TYPE, mediaType);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d("onCreate()");
        super.onCreate(savedInstanceState);

//        if (ZypeApp.get(this).getAppConfiguration().updatedPaywalls) {
//            initObservers();
//            initModels();
//        }
//        else {
            checkForRegistration();
//        }
    }

    private void initialize() {
        initUI();
        initObservers();
        initModel();
        checkVideoAuthorization();
    }

    private void initObservers() {
        if (contentUriObserver == null) {
            contentUriObserver = createContentUriObserver();
        }
        if (playerErrorObserver == null) {
            playerErrorObserver = createPlayerErrorObserver();
        }
        if (playerUrlObserver == null) {
            playerUrlObserver = createPlayerUrlObserver();
        }
        if  (videoDetailObserver == null) {
            videoDetailObserver = createVideoDetailObserver();
        }
    }

    private void initModels() {
        videoDetailViewModel = ViewModelProviders.of(this).get(VideoDetailViewModel.class);
        videoDetailViewModel.getVideo(mVideoId).observe(this, videoDetailObserver);

//        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class)
//            .setVideoId(mVideoId)
//            .setPlaylistId(playlistId);
//        playerViewModel.initialize();
//        playerViewModel.getContentUri().observe(this, contentUriObserver);
    }

    private void checkForRegistration() {
        if (AuthHelper.isRegistrationRequired(getApplicationContext(), mVideoId)) {
            NavigationHelper.getInstance(this).handleUnAuthorizedVideo(this);
        }
        else {
            initialize();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logger.d("onNewIntent()");
        super.onNewIntent(intent);
        checkForRegistration();
    }

    @Override
    public void onBackPressed() {
        // TODO: 'onCleared()' should be called automatically on finish activity, but it doesn't
        // for some reason. There was a bug in support library 27.1.0, should be fixed in 27.1.1.
        // But we have to use 28.0.0.alpha-3 for now and the bug may appear in this version again.
        if (videoDetailViewModel != null) {
            videoDetailViewModel.onCleared();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Logger.d("onDestroy()");

        Video video = DataRepository.getInstance(getApplication()).getVideoSync(mVideoId);
        video.playerAudioUrl = null;
        video.playerVideoUrl = null;
        DataRepository.getInstance(getApplication()).updateVideo(video);

        super.onDestroy();
    }

    @Override
    protected Class<?> getActivityClass() {
        return VideoDetailActivity.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void onFullscreenChanged(boolean isFullscreen) {
        if (isFullscreen) {
            hideSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(false);
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mActionBar.hide();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            findViewById(R.id.layoutVideo).setLayoutParams(params);
            findViewById(R.id.layoutVideo).invalidate();
        }
        else {
            showSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(true);
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mActionBar.show();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.height = (int) getResources().getDimension(R.dimen.episode_video_height);
            findViewById(R.id.layoutVideo).setLayoutParams(params);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        if (UiUtils.isLandscapeOrientation(this)) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }

    // //////////
    // UI
    //
    private void updateTabs() {
        VideoDetailPagerAdapter videoDetailPagerAdapter = new VideoDetailPagerAdapter(this, getSupportFragmentManager(), mVideoId);
        mViewPager = findViewById(R.id.pagerSections);
        mViewPager.setAdapter(videoDetailPagerAdapter);
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initUI() {

        getSupportActionBar().setTitle(VideoHelper.getFullData(getContentResolver(), mVideoId).getTitle());
        updateTabs();

        layoutImage = findViewById(R.id.layoutImage);
        imageVideo = findViewById(R.id.imageVideo);
        imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AuthHelper.isVideoUnlocked(VideoDetailActivity.this, mVideoId, playlistId)) {
                    NavigationHelper.getInstance(VideoDetailActivity.this)
                            .handleNotAuthorizedVideo(VideoDetailActivity.this, mVideoId, playlistId);
                }
                else {
                    Video video = DataRepository.getInstance(getApplication()).getVideoSync(mVideoId);
                    if (video != null) {
                        if (video.isZypeLive == 0 || VideoHelper.isLiveEventOnAir(video)) {
                            if (playerViewModel.getPlayerMode().getValue() == PlayerViewModel.PlayerMode.VIDEO) {
                                playerViewModel.loadPlayer();
//                                requestVideoUrl(mVideoId);
                            }
                        }
                    }
                }
            }
        });
    }

    private void showVideoThumbnail() {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
        List<Thumbnail> thumbnails = videoData.getThumbnails();
        layoutImage.setVisibility(View.VISIBLE);
        Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(thumbnails, 480);
        if (thumbnail != null) {
            UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, imageVideo);
        }
        else {
            imageVideo.setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.placeholder_video));
        }
    }

    private void hideVideoThumbnail() {
        layoutImage.setVisibility(View.GONE);
    }

    private void showPlayer() {
        hideVideoThumbnail();
        VideoDetailActivity.startActivity(this, mVideoId, playlistId);
    }

    // //////////
    // Actions
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_USER:
            case BundleConstants.REQUEST_CONSUMER: {
                if (resultCode == RESULT_OK) {
                    initialize();
                }
                else {
                    finish();
                }
            }
            break;
            case BundleConstants.REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    hideVideoThumbnail();
                    VideoDetailActivity.startActivity(this, mVideoId, playlistId);
                }
                return;
            case BundleConstants.REQUEST_SUBSCRIPTION:
                if (resultCode == RESULT_OK) {
                    hideVideoThumbnail();
                    VideoDetailActivity.startActivity(this, mVideoId, playlistId);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    // 'IPlaylistVideos' implementation
    //
    @Override
    public void onNext() {
        hideVideoLayout();
        showProgress();
        autoplay = true;
        AutoplayHelper.playNextVideo(this, mVideoId, playlistId);
    }

    @Override
    public void onPrevious() {
        hideVideoLayout();
        showProgress();
        autoplay = true;
        AutoplayHelper.playPreviousVideo(this, mVideoId, playlistId);
    }

    @Override
    public void onError() {
        hideProgress();
        showVideoThumbnail();
        if (WebApiManager.isHaveActiveNetworkConnection(this)) {
            DialogHelper.showErrorAlert(this, getString(R.string.video_error_bad_request));
        }
        else {
            DialogHelper.showErrorAlert(this, getString(R.string.error_internet_connection));
        }
    }

    // //////////
    // Data
    //
    private void initModel() {
        final Video video = DataRepository.getInstance(getApplication()).getVideoSync(mVideoId);
        if (video != null) {
            // Load player urls
            playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
            PlayerViewModel.PlayerMode mediaType = null;
            switch (mType) {
                case PlayerFragment.TYPE_AUDIO_LOCAL:
                case PlayerFragment.TYPE_AUDIO_WEB:
                    mediaType = PlayerViewModel.PlayerMode.AUDIO;
                    break;
                case PlayerFragment.TYPE_VIDEO_LOCAL:
                case PlayerFragment.TYPE_VIDEO_WEB:
                case BaseVideoActivity.TYPE_WEB:
                    mediaType = PlayerViewModel.PlayerMode.VIDEO;
                    break;
            }
            playerViewModel.init(video.id, playlistId, mediaType);
            playerViewModel.onPlayerError().observe(this, playerErrorObserver);
            playerViewModel.getPlayerUrl().observe(this, playerUrlObserver);

            if (video.isZypeLive == 0) {
                updateDownloadUrls();
            }
            else {
//                showVideoThumbnail();
                videoDetailViewModel = new VideoDetailViewModel(getApplication());
                final Observer<Video> videoObserver = new Observer<Video>() {
                    @Override
                    public void onChanged(@Nullable Video video) {
                        if (VideoHelper.isLiveEventOnAir(video)) {
                            if (videoDetailViewModel.updateVideoOnAir(video)) {
                                showPlayer();
                            }
                        }
                        else {
                            changeFragment(isChromecastConntected());
                            videoDetailViewModel.checkOnAir(mVideoId).observe(VideoDetailActivity.this, new Observer<Video>() {
                                @Override
                                public void onChanged(@Nullable Video video) {
                                    videoDetailViewModel.onCleared();
                                    videoDetailViewModel.updateVideoOnAir(video);
                                    showPlayer();
                                }
                            });
                        }
                        videoDetailViewModel.getVideo(mVideoId).removeObserver(this);
                    }
                };
                videoDetailViewModel.getVideo(mVideoId).observe(this, videoObserver);
            }
        }
    }

    private void updateDownloadUrls() {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
        if (!videoData.isOnAir()) {
            if (ZypeConfiguration.isDownloadsEnabled(this)
                    && (ZypeConfiguration.isDownloadsForGuestsEnabled(this) || SettingsProvider.getInstance().isLoggedIn())) {
                getDownloadUrls(mVideoId);
            }
        }
    }

    private void checkVideoAuthorization() {
        if (!AuthHelper.isVideoUnlocked(this, mVideoId, playlistId)) {
            showVideoThumbnail();
            NavigationHelper.getInstance(this).handleNotAuthorizedVideo(this, mVideoId, playlistId);
        }
    }

    private Observer<PlayerViewModel.Error> createPlayerErrorObserver() {
        return error -> {
            Logger.e("onPlayerError()::onChanged(): message=" + error);
            hideProgress();
            showVideoThumbnail();
            DialogHelper.showErrorAlert(this, error.message, () -> finish());
        };
    }

    private Observer<String> createContentUriObserver() {
        return new Observer<String>() {
            @Override
            public void onChanged(@Nullable String url) {
                Logger.d("getContentUri(): onChanged(): url=" + url);
                if (!TextUtils.isEmpty(url)) {
                    if (playerViewModel.isTrailer().getValue()) {
                        Logger.d("getContentUri(): onChanged(): playing trailer");
                        mType = PlayerFragment.TYPE_VIDEO_TRAILER;
                        Fragment fragment = PlayerFragment.newInstance(mType, url, null);
//                        showFragment(fragment);
                    }
                    else {
                        switch (playerViewModel.getPlayerMode().getValue()) {
                            case AUDIO:
                                if (playerViewModel.isAudioDownloaded()) {
                                    mType = PlayerFragment.TYPE_AUDIO_LOCAL;
                                }
                                else {
                                    mType = PlayerFragment.TYPE_AUDIO_WEB;
                                }
                                break;
                            case VIDEO:
                                if (playerViewModel.isVideoDownloaded()) {
                                    mType = PlayerFragment.TYPE_VIDEO_LOCAL;
                                }
                                else {
                                    mType = PlayerFragment.TYPE_VIDEO_WEB;
                                }
                                break;
                        }
                        hideProgress();
                        changeFragment(isChromecastConntected());
                    }
                }
                else {
                    showThumbnailView(videoDetailViewModel.getVideo().getValue());
                }
            }
        };
    }

    private Observer<Video> createVideoDetailObserver() {
        return new Observer<Video>() {
            @Override
            public void onChanged(@Nullable Video video) {
                Logger.d("getVideo(): onChanged()");
                if (video == null) {
                    // Show no video view
                }
                else {
                    // Update title
                    getSupportActionBar().setTitle(video.title);
                    // Update video details
                    updateTabs();
                    // Check video authorization
                    if (AuthHelper.isVideoUnlocked(VideoDetailActivity.this, video.id, playlistId)) {
                        // Show player view and request player data
                        showPlayerView(video);
                    }
                    else {
                        // Show paywall view
                        showThumbnailView(video);
                    }
                }
            }
        };
    }

    private Observer<String> createPlayerUrlObserver() {
        return new Observer<String>() {
            @Override
            public void onChanged(@Nullable String url) {
                Logger.d("getPlayerUrl()::onChanged(): url=" + url);
                if (!TextUtils.isEmpty(url)) {
                    switch (playerViewModel.getPlayerMode().getValue()) {
                        case AUDIO:
                            if (playerViewModel.isAudioDownloaded()) {
                                mType = PlayerFragment.TYPE_AUDIO_LOCAL;
                            }
                            else {
                                mType = PlayerFragment.TYPE_AUDIO_WEB;
                            }
                            break;
                        case VIDEO:
                            if (playerViewModel.isVideoDownloaded()) {
                                mType = PlayerFragment.TYPE_VIDEO_LOCAL;
                            }
                            else {
                                mType = PlayerFragment.TYPE_VIDEO_WEB;
                            }
                            break;
                    }
                    hideVideoThumbnail();
                    changeFragment(isChromecastConntected());
                    hideProgress();
                }
                else {
//                    requestVideoUrl(mVideoId);
                }
            }
        };
    }

    private void showPlayerView(Video video) {
//        playerViewModel.setVideoId(video.id);
//        playerViewModel.setPlaylistId(playlistId);
//        playerViewModel.initialize();
    }

    private void showThumbnailView(Video video) {
        Logger.d("showThumbnailView()");
//        Fragment fragment = ThumbnailFragment.newInstance(video.id);
//        showFragment(fragment);
    }

    // //////////
    // Subscribe
    //
    @Subscribe
    public void handleFavoriteEvent(FavoriteEvent event) {
        Logger.d("FavoriteEvent");
        List<ConsumerFavoriteVideoData> data = new ArrayList<>();
        data.add(event.getEventData().getModelData().getResponse());
        DataHelper.insertFavorites(getContentResolver(), data);
        DataHelper.setFavoriteVideo(getContentResolver(), event.getEventData().getModelData().getResponse().getVideoId(), true);
    }

    @Subscribe
    public void handleUnfavoriteEvent(UnfavoriteEvent event) {
        Logger.d("UnfavoriteEvent");
        DataHelper.setFavoriteVideo(getContentResolver(), event.getVideoId(), false);
        DataHelper.deleteFavorite(getContentResolver(), event.getVideoId());
    }

    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
        Logger.d("handleDownloadVideo");
        File file = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DataHelper.saveVideoUrl(getContentResolver(), fileId, url);

            initUI();
        }
        else {
            Logger.e("Server response must contains \"mp\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

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
            String fileId = event.mFileId;
            DataHelper.saveAudioUrl(getContentResolver(), fileId, url);

            initUI();
        }
        else {
            Logger.e("Server response must contains \"m4a\" or mp3 but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError()");
//        if (ZypeApp.get(this).getAppConfiguration().updatedPaywalls) {
//            Logger.e("handleError(): Updated paywall is used, skipping this error handling");
//            return;
//        }
        if (err.getError() == null) {
            if (err.getEventData() != WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO
                    && err.getEventData() != WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO
                    && !playerViewModel.isVideoDownloaded()
                    && !playerViewModel.isAudioDownloaded()) {
                onError();
            }
            return;
        }
        if (err.getError().getResponse().getStatus() == BAD_REQUEST) {
            if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
                onError();
            }
        }
        else {
            if (err instanceof ForbiddenErrorEvent) {
                if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
                    hideProgress();
                    showVideoThumbnail();
                    UiUtils.showErrorIndefiniteSnackbar(findViewById(R.id.root_view), err.getErrMessage());
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

    @Subscribe
    public void handleZObject(ZObjectEvent event) {
        List<ZobjectData> list = event.getEventData().getModelData().getResponse();
        if (list != null && list.size() > 0) {
            DataHelper.saveGuests(getContentResolver(), mVideoId, list);
        }
    }

    @Subscribe
    public void handleVideoPlayer(PlayerVideoEvent event) {
        Logger.d("handlePlayer");
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        DataHelper.saveVideoPlayerLink(getContentResolver(), mVideoId, url);
        Advertising advertising = event.getEventData().getModelData().getResponse().getBody().getAdvertising();
        Analytics analytics = event.getEventData().getModelData().getResponse().getBody().getAnalytics();

        if (advertising != null) {
            List<AdvertisingSchedule> schedule = advertising.getSchedule();
            DataHelper.updateAdSchedule(getContentResolver(), mVideoId, schedule);
            // TODO: Ad tags saved in separate table by 'updateAdSchedule'. Probably we don't need to keep the tag
            // in the 'VideoList' table. But we do this now because the tags are the same for all ad cue points
            if (schedule != null && !schedule.isEmpty()) {
                String adTag = advertising.getSchedule().get(0).getTag();
                DataHelper.saveAdVideoTag(getContentResolver(), mVideoId, adTag);
            }
        }

        if (analytics != null) {
            String beacon = analytics.getBeacon();
            AnalyticsDimensions dimensions = analytics.getDimensions();

            if (beacon != null && dimensions != null) {
                DataHelper.updateAnalytics(getContentResolver(), beacon, dimensions);
            }
        }

        mType = PlayerFragment.TYPE_VIDEO_WEB;
        changeFragment(isChromecastConntected());
        hideProgress();
    }

    @Subscribe
    public void handleAudioPlayer(PlayerAudioEvent event) {
//        Logger.d("handlePlayer");
//        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
//        DataHelper.saveAudioPlayerLink(getContentResolver(), mVideoId, url);
//        mType = PlayerFragment.TYPE_AUDIO_WEB;
//        changeFragment(isChromecastConntected());
    }
}
