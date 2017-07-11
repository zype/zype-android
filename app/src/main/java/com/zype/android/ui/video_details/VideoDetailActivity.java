package com.zype.android.ui.video_details;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ZypeSettings;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionActivity;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerFragment;
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
import com.zype.android.webapi.model.player.File;
import com.zype.android.webapi.model.video.VideoData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends BaseVideoActivity {

    private VideoDetailPager mViewPager;
    private TabLayout mTabLayout;


    public static void startActivity(Activity activity, String videoId) {
        Intent intent = new Intent(activity, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        getSupportActionBar().setTitle(VideoHelper.getFullData(getContentResolver(), mVideoId).getTitle());

        initTabs();
        updateDownloadUrls();
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
    public void onFullscreenChanged() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isFullscreen = false;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullscreen = true;
        }
        if (isFullscreen) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mActionBar.hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mActionBar.show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected String getActivityName() {
        return getString(R.string.activity_name_episode_details);
    }

    private void initTabs() {
        VideoDetailPagerAdapter videoDetailPagerAdapter = new VideoDetailPagerAdapter(this, getSupportFragmentManager(), mVideoId);
        mViewPager = (VideoDetailPager) findViewById(R.id.pager);
        mViewPager.setAdapter(videoDetailPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    // //////////
    // UI
    //

    // //////////
    // Data
    //
    private void updateDownloadUrls() {
        VideoData videoData = VideoHelper.getFullData(getContentResolver(), mVideoId);
        if (!videoData.isOnAir()) {
            if (ZypeSettings.isDownloadsEnabled()
                    && (ZypeSettings.isDownloadsEnabledForGuests() || SettingsProvider.getInstance().isLoggedIn())) {
                getDownloadUrls(mVideoId);
            }
        }
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
//        throw new IllegalStateException("UnfavoriteEvent check this method. This method does not work before this moment");
        Logger.d("UnfavoriteEvent");
        DataHelper.setFavoriteVideo(getContentResolver(), event.getVideoId(), false);
        DataHelper.deleteFavorite(getContentResolver(), event.getVideoId());
//        DataHelper.setFavoriteVideo(getContentResolver(), event.getEventData().getEventData().getResponse().getVideoId(), false);
    }

    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
//        if (!ZypeSettings.isDownloadsEnabled())
//            return;
        Logger.d("handleDownloadVideo");
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DataHelper.saveVideoUrl(getContentResolver(), fileId, url);

            initTabs();
        }
        else {
//            UiUtils.showErrorSnackbar(getBaseView(), "Server has returned an empty url for video file");
            Logger.e("Server response must contains \"mp\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleDownloadAudio(DownloadAudioEvent event) {
//        if (!ZypeSettings.isDownloadsEnabled())
//            return;
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "m4a");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DataHelper.saveAudioUrl(getContentResolver(), fileId, url);

            initTabs();
        }
        else {
//            UiUtils.showErrorSnackbar(getBaseView(), "Server has returned an empty url for audio file");
            Logger.e("Server response must contains \"m4a\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        if (err instanceof ForbiddenErrorEvent) {
            if (err.getEventData() == WebApiManager.Request.PLAYER_VIDEO) {
                if (ZypeSettings.NATIVE_SUBSCRIPTION_ENABLED) {
                    NavigationHelper.getInstance(this).switchToSubscriptionScreen();
                }
                else {
                    DialogHelper.showSubscriptionAlertIssue(this);
                }
            }
        }
        else {
            if (err.getEventData() != WebApiManager.Request.UN_FAVORITE) {
                UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
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
        if (advertising != null) {
            List<AdvertisingSchedule> schedule = advertising.getSchedule();
            DataHelper.updateAdSchedule(getContentResolver(), mVideoId, schedule);
            // TODO: Ad tags saved in separate table by 'updateAdSchedule'. Probably we don't need to keep the tag
            // in the 'Video' table. But we do this now because the tags are the same for all ad cue points
            if (schedule != null && !schedule.isEmpty()) {
                String adTag = advertising.getSchedule().get(0).getTag();
                DataHelper.saveAdVideoTag(getContentResolver(), mVideoId, adTag);
            }
        }
        mType = PlayerFragment.TYPE_VIDEO_WEB;
        changeFragment(isChromecastConntected());
    }

    @Subscribe
    public void handleAudioPlayer(PlayerAudioEvent event) {
        Logger.d("handlePlayer");
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        DataHelper.saveAudioPlayerLink(getContentResolver(), mVideoId, url);
        mType = PlayerFragment.TYPE_AUDIO_WEB;
        changeFragment(isChromecastConntected());
    }
}
