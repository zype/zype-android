package com.zype.android.ui.video_details.v2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.Player;
import com.squareup.otto.Subscribe;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.Helpers.AutoplayHelper;
import com.zype.android.ui.Helpers.IPlaylistVideos;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.player.v2.PlayerFragment;
import com.zype.android.ui.video_details.VideoDetailPager;
import com.zype.android.ui.video_details.VideoDetailPagerAdapter;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.ui.video_details.fragments.OnDetailActivityFragmentListener;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.File;

import java.util.ArrayList;
import java.util.List;

import static com.zype.android.webapi.WebApiManager.WorkerHandler.BAD_REQUEST;

public class VideoDetailActivity extends BaseActivity implements OnDetailActivityFragmentListener,
        IPlaylistVideos {

    public static final String EXTRA_VIDEO_ID = "VideoId";
    public static final String EXTRA_PLAYLIST_ID = "PlaylistId";

    private VideoDetailViewModel model;
    private PlayerViewModel playerViewModel;

    private FrameLayout layoutPlayer;
    private ProgressBar progressPlayer;
    private TabLayout tabs;
    private VideoDetailPager pagerSections;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_detail_2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);
        String playlistId = getIntent().getStringExtra(EXTRA_PLAYLIST_ID);

        layoutPlayer = findViewById(R.id.layoutPlayer);

        pagerSections = findViewById(R.id.pagerSections);
        pagerSections.setAdapter(new VideoDetailPagerAdapter(this, getSupportFragmentManager(),
                videoId));
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(pagerSections);

        progressPlayer = findViewById(R.id.progressPlayer);

        model = ViewModelProviders.of(this).get(VideoDetailViewModel.class)
            .setVideoId(videoId)
            .setPlaylistId(playlistId);
        model.init();
        model.getVideo().observe(this, new Observer<Video>() {
            @Override
            public void onChanged(@Nullable Video video) {
                getSupportActionBar().setTitle(video.title);
            }
        });

        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class)
            .setVideoId(videoId)
            .setPlaylistId(playlistId);
        playerViewModel.init();

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

        showPlayerFragment();
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


    // UI

    private void onScreenOrientationChanged() {
        boolean fullscreen = UiUtils.isLandscapeOrientation(this);
        if (fullscreen) {
            hideSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(false);
            tabs.setVisibility(View.GONE);
            pagerSections.setVisibility(View.GONE);
            getSupportActionBar().hide();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutPlayer.setLayoutParams(params);
            layoutPlayer.invalidate();
        }
        else {
            showSystemUI();
            findViewById(R.id.layoutRoot).setFitsSystemWindows(true);
            tabs.setVisibility(View.VISIBLE);
            pagerSections.setVisibility(View.VISIBLE);
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

    protected void showProgress() {
        progressPlayer.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        progressPlayer.setVisibility(View.INVISIBLE);
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

}
