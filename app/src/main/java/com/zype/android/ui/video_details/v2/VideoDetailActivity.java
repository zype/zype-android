package com.zype.android.ui.video_details.v2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.Player;
import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.Helpers.IPlaylistVideos;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.player.ThumbnailFragment;
import com.zype.android.ui.player.v2.PlayerFragment;
import com.zype.android.ui.video_details.VideoDetailPager;
import com.zype.android.ui.video_details.VideoDetailPagerAdapter;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.ui.video_details.fragments.OnDetailActivityFragmentListener;
import com.zype.android.ui.video_details.fragments.summary.SummaryFragment;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.zype.android.webapi.WebApiManager.WorkerHandler.BAD_REQUEST;

public class VideoDetailActivity extends BaseActivity implements OnDetailActivityFragmentListener,
        IPlaylistVideos {

    public static final String EXTRA_VIDEO_ID = "VideoId";
    public static final String EXTRA_PLAYLIST_ID = "PlaylistId";

    private VideoDetailViewModel model;
    private PlayerViewModel playerViewModel;

    Observer<Video> videoObserver = null;
    Observer<Boolean> playerIsTrailerObserver = null;
    Observer<PlayerViewModel.Error> playerErrorObserver = null;

    private FrameLayout layoutPlayer;
    private ProgressBar progressPlayer;
    private TabLayout tabs;
    private VideoDetailPager pagerSections;
    private FrameLayout layoutSummary;

    Handler handler;
    Runnable runnableHideSystemUi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("onCreate()");

        setContentView(R.layout.activity_video_detail_2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize(getIntent());

        handler = new Handler();
        runnableHideSystemUi = new Runnable() {
            @Override
            public void run() {
                hideSystemUI();
            }
        };
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            if (UiUtils.isLandscapeOrientation(VideoDetailActivity.this)) {
                                handler.postDelayed(runnableHideSystemUi, 2000);
                            }
                        } else {
                        }
                    }
                });
        onScreenOrientationChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("onNewIntent()");

        if (!playerViewModel.isInBackground()
            && !playerViewModel.isTrailer().getValue()) {
            initialize(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.d("onConfigurationChanged()");
        onScreenOrientationChanged();
    }

    @Override
    protected String getActivityName() {
        return VideoDetailActivity.class.getSimpleName();
    }

    private void initialize(Intent intent) {
        String videoId = intent.getStringExtra(EXTRA_VIDEO_ID);
        String playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID);

        layoutPlayer = findViewById(R.id.layoutPlayer);

        progressPlayer = findViewById(R.id.progressPlayer);

        model = ViewModelProviders.of(this).get(VideoDetailViewModel.class)
                .setVideoId(videoId)
                .setPlaylistId(playlistId);
        if (videoObserver == null) {
            videoObserver = createVideoObserver();
        }
        model.getVideo().observe(this, videoObserver);

        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
        if (playerIsTrailerObserver == null) {
            playerIsTrailerObserver = createPlayerIsTrailerObserver();
        }
        if (playerErrorObserver == null) {
            playerErrorObserver = createPlayerErrorObserver();
        }

        playerViewModel.isTrailer().observe(this, playerIsTrailerObserver);
        playerViewModel.onPlayerError().observe(this, playerErrorObserver);
        playerViewModel.getPlaybackState().observe(this, state -> {
            if (state != null) {
                Logger.d("getPlaybackState(): state=" + state);
                switch (state) {
                    case Player.STATE_BUFFERING: {
                        showProgress();
                        break;
                    }
                    case Player.STATE_READY: {
                        hideProgress();
                        break;
                    }
                }
            }
        });

        showSections(videoId);
    }

    private boolean hasOptions(String videoId) {
        List<PlayerViewModel.PlayerMode> playerModes = playerViewModel.getAvailablePlayerModes().getValue();
        if (playerModes != null && playerModes.size() > 1) {
            return true;
        }
        if (AuthHelper.isLoggedIn()
                || !ZypeApp.get(this).getAppConfiguration().hideFavoritesActionWhenSignedOut) {
            return true;
        }
//        if (ZypeSettings.SHARE_VIDEO_ENABLED) {
//            return true;
//        }
        if (ZypeConfiguration.isDownloadsEnabled(this) &&
                (isAudioDownloadUrlExists(videoId) || isVideoDownloadUrlExists(videoId))) {
            return true;
        }
        return false;
    }

    private void showSections(String videoId) {
        pagerSections = findViewById(R.id.pagerSections);
        tabs = findViewById(R.id.tabs);
        layoutSummary = findViewById(R.id.layoutSummary);
        if (hasOptions(videoId)) {
            pagerSections.setAdapter(new VideoDetailPagerAdapter(this,
                    getSupportFragmentManager(),
                    videoId));
            tabs.setupWithViewPager(pagerSections);
            pagerSections.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
            layoutSummary.setVisibility(GONE);
        }
        else {
            pagerSections.setVisibility(GONE);
            tabs.setVisibility(GONE);
            layoutSummary.setVisibility(View.VISIBLE);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = SummaryFragment.newInstance();
            fm.beginTransaction().replace(R.id.layoutSummary, fragment, SummaryFragment.TAG).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case BundleConstants.REQUEST_SUBSCRIBE_OR_LOGIN:
                if (resultCode == RESULT_OK) {
                    model.setVideoId(model.getVideoId());
                }
                else {

                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // View model observers

    private Observer<Video> createVideoObserver() {
        return video -> {
            Logger.d("getVideo(): onChanged()");
            if (video == null) {
                // Show no video view
            }
            else {
                // Update title
                getSupportActionBar().setTitle(video.title);
                // Check video authorization
                if (AuthHelper.isVideoUnlocked(VideoDetailActivity.this,
                        video.id, model.getPlaylistId())) {
                    // Show player view
                    if (playerViewModel.isTrailer() != null && !playerViewModel.isTrailer().getValue()) {
                        playerViewModel.init(video.id, model.getPlaylistId(), PlayerViewModel.PlayerMode.VIDEO);
                    }
                    showPlayerFragment();
                }
                else {
                    // Show paywall view
                    hideProgress();
                    showThumbnailFragment();
                }
            }
        };
    }

    private Observer<Boolean> createPlayerIsTrailerObserver() {
        return isTrailer -> {
            if (isTrailer == null) {
                return;
            }
            Logger.d("isTrailer()::onChanged(): " + isTrailer);
            if (isTrailer) {
                showPlayerFragment();
            }
        };
    }

    private Observer<PlayerViewModel.Error> createPlayerErrorObserver() {
        return error -> {
            if (error == null) {
                return;
            }
            Logger.e("onPlayerError()::onChanged(): type=" + error.type + ", message=" + error.message);
            hideProgress();
            switch (error.type) {
                case LOCKED:
                    NavigationHelper.getInstance(VideoDetailActivity.this)
                            .handleLockedVideo(VideoDetailActivity.this,
                                    model.getVideoSync(), model.getPlaylistSync());
                    break;
                case UNKNOWN:
                    DialogHelper.showErrorAlert(this, error.message, () -> finish());
                    break;
            }
//            showVideoThumbnail();
        };
    }

    // UI

    private void onScreenOrientationChanged() {
        boolean fullscreen = UiUtils.isLandscapeOrientation(this);
        if (fullscreen) {
            hideSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(false);
            tabs.setVisibility(GONE);
            pagerSections.setVisibility(GONE);
            getSupportActionBar().hide();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutPlayer.setLayoutParams(params);
            layoutPlayer.invalidate();
        }
        else {
            handler.removeCallbacks(runnableHideSystemUi);
            showSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(true);
            if (hasOptions(model.getVideo().getValue().id)) {
                tabs.setVisibility(View.VISIBLE);
                pagerSections.setVisibility(View.VISIBLE);
            }
            getSupportActionBar().show();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.height = (int) getResources().getDimension(R.dimen.episode_video_height);
            layoutPlayer.setLayoutParams(params);
        }
        model.setFullscreen(fullscreen);
//        mediaController.updateFullscreenButton(fullscreen);
//        hideControls();
    }

    private void showPlayerFragment() {
        showProgress();

        PlayerFragment fragment = PlayerFragment.newInstance(getIntent().getStringExtra(EXTRA_VIDEO_ID));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.player_container, fragment, PlayerFragment.TAG);
        fragmentTransaction.commit();
    }

    private void showThumbnailFragment() {
        Logger.d("showThumbnailView()");
        Fragment fragment = ThumbnailFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.player_container, fragment, PlayerFragment.TAG);
        fragmentTransaction.commit();
    }

    protected void showProgress() {
        progressPlayer.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        progressPlayer.setVisibility(View.INVISIBLE);
    }

    private void hideSystemUI() {
        if (UiUtils.isLandscapeOrientation(this)) {
            View decorView = getWindow().getDecorView();
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= 19) {
                visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
            decorView.setSystemUiVisibility(visibility);
        }
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    //
    // 'OnDetailActivityFragmentListener' implementation
    //

    @Override
    public void onShareVideo(String videoId) {

    }

    @Override
    public void onDownloadVideo(String videoId) {
        String videoUrl = DataHelper.getVideoUrl(getContentResolver(), videoId);
        if (TextUtils.isEmpty(videoUrl)) {
            throw new IllegalStateException("Empty url for videoId=" + videoId);
        }
        DownloadHelper.addVideoToDownloadList(getApplicationContext(), videoUrl, videoId);
    }

    @Override
    public void onDownloadAudio(String videoId) {
        String audioUrl = DataHelper.getAudioUrl(getContentResolver(), videoId);
        if (TextUtils.isEmpty(audioUrl)) {
            throw new IllegalStateException("Empty url for videoId=" + videoId);
        }
        DownloadHelper.addAudioToDownloadList(getApplicationContext(), audioUrl, videoId);
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
        return 0;
    }

    @Override
    public int getCurrentFragment() {
        // TODO: Refactor
        return BaseVideoActivity.TYPE_WEB;
    }

    //
    // 'IPlaylistVideos' implementation
    //
    @Override
    public void onNext() {
//        hideVideoLayout();
//        showProgress();
//        autoplay = true;
//        AutoplayHelper.playNextVideo(this, mVideoId, playlistId);
    }

    @Override
    public void onPrevious() {
//        hideVideoLayout();
//        showProgress();
//        autoplay = true;
//        AutoplayHelper.playPreviousVideo(this, mVideoId, playlistId);
    }


    // Event bus handlers

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
                    hideProgress();
//                    showVideoThumbnail();
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

    //

    private boolean isAudioDownloadUrlExists(String videoId) {
        return !TextUtils.isEmpty(DataHelper.getAudioUrl(getContentResolver(), videoId));
    }

    private boolean isVideoDownloadUrlExists(String videoId) {
        return !TextUtils.isEmpty(DataHelper.getVideoUrl(getContentResolver(), videoId));
    }

}
