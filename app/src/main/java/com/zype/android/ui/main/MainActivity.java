package com.zype.android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.service.DownloaderService;
import com.zype.android.ui.LoginActivity;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.Subscription.SubscriptionActivity;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.ui.main.fragments.videos.VideosActivity;
import com.zype.android.ui.main.fragments.playlist.PlaylistActivity;
import com.zype.android.ui.search.SearchActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.builder.DownloadAudioParamsBuilder;
import com.zype.android.webapi.builder.DownloadVideoParamsBuilder;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.builder.SettingsParamsBuilder;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.player.PlayerVideoEvent;
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.File;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnMainActivityFragmentListener, OnVideoItemAction, OnLoginAction,
                                                    BillingManager.BillingUpdatesListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mSectionsPagerAdapter.getTabView(i));
        }

        SettingsParamsBuilder settingsParamsBuilder = new SettingsParamsBuilder();
        getApi().executeRequest(WebApiManager.Request.GET_SETTINGS, settingsParamsBuilder.build());

        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            new BillingManager(this, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SettingsProvider.getInstance().isLogined()) {
            requestConsumerData();
        }
    }

    @Override
    public void onLatestVideoClick(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription();
        } else {
            VideoDetailActivity.startActivity(this, videoId);
        }
    }

    @Override
    public void onDownloadVideo(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription();
        } else {
            DownloadVideoParamsBuilder downloadVideoParamsBuilder = new DownloadVideoParamsBuilder()
                    .addVideoId(videoId);
            getApi().executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO, downloadVideoParamsBuilder.build());
        }
    }

    @Override
    public void onDownloadAudio(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription();
        } else {
            DownloadAudioParamsBuilder playerParamsBuilder = new DownloadAudioParamsBuilder()
                    .addAudioId(videoId);
            getApi().executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_AUDIO, playerParamsBuilder.build());
        }
    }

    @Override
    public void onFavoriteVideoClick(String videoId, boolean isFavorite) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(this)) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                onRequestSubscription();
            }
            else {
                VideoDetailActivity.startActivity(this, videoId);
            }
        }
        else {
            VideoDetailActivity.startActivity(this, videoId);
        }
    }

    @Override
    public void onDownloadedVideoClick(String videoId) {
//        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
//            onRequestSubscription();
//        } else {
            VideoDetailActivity.startActivity(this, videoId);
//        }
    }

    @Override
    public void onFavoriteVideo(String videoId) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(this)) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                FavoriteParamsBuilder builder = new FavoriteParamsBuilder()
                        .addVideoId(videoId)
                        .addAccessToken();
                getApi().executeRequest(WebApiManager.Request.FAVORITE, builder.build());
            } else {
                onRequestLogin();
            }
        }
        else {
            DataHelper.setFavoriteVideo(getContentResolver(), videoId, true);
        }
    }

    @Override
    public void onUnFavoriteVideo(String videoId) {
        if (ZypeConfiguration.isUniversalSubscriptionEnabled(this)) {
            if (SettingsProvider.getInstance().isLoggedIn()) {
                FavoriteParamsBuilder builder = new FavoriteParamsBuilder()
                        .addPathFavoriteId(DataHelper.getFavoriteId(getContentResolver(), videoId))
                        .addPathVideoId(videoId)
                        .addAccessToken();
                getApi().executeRequest(WebApiManager.Request.UN_FAVORITE, builder.build());
            }
            else {
                onRequestLogin();
            }
        }
        else {
            DataHelper.setFavoriteVideo(getContentResolver(), videoId, false);
        }
    }

    @Override
    public void onShareVideo(String videoId) {
        UiUtils.shareVideo(this, videoId);
    }

    @Override
    public void onSearch(String searchString) {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.SEARCH_STRING, searchString);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPlaylist(String parentId) {
        Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PARENT_ID, parentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPlaylistWithVideos(String parentId) {
        Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PARENT_ID, parentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLogout() {
        SettingsProvider.getInstance().logout();
        DataHelper.clearAllVideos(getContentResolver());
        DownloaderService.cancelAllDownloads();
    }

    @Override
    public void onRequestLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, BundleConstants.REQ_LOGIN);
    }

    @Override
    public void onRequestSubscription() {
        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            NavigationHelper.getInstance(this).switchToSubscriptionScreen(this);
        }
        else {
            DialogHelper.showSubscriptionAlertIssue(this);
        }
    }

    private void requestConsumerData() {
        ConsumerParamsBuilder builder = new ConsumerParamsBuilder()
                .addAccessToken();
        getApi().executeRequest(WebApiManager.Request.CONSUMER_GET, builder.build());
    }

    protected View getBaseView() {
        return ((ViewGroup) this
                .findViewById(R.id.root_view)).getChildAt(0);
    }

    // //////////
    // UI
    //
//    private void switchToSubscriptionScreen() {
//        Intent intent = new Intent(getApplicationContext(), SubscriptionActivity.class);
//        startActivity(intent);
//    }

    //
    // 'BillinggManager' listener implementation
    //
    @Override
    public void onBillingClientSetupFinished() {
    }

    @Override
    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        SubscriptionsHelper.updateSubscriptionCount(purchases);
    }

//    @Override
//    public void openVideoFragment(String url) {
//        Logger.d("openVideoFragment " + url);
////        LatestFragment f = (LatestFragment) mSectionsPagerAdapter.getItem(0);
////        f.showVideoFragment(url);
//    }

//    -------------------SUBSCRIBE-------------------

    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        //TODO
        Logger.d("handleConsumer");
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
    }

//    @Subscribe
//    public void handleError(ErrorEvent err) {
//        Logger.e("handleError");
//        if (err.getEventData() == WebApiManager.Request.UN_FAVORITE) {
//            //IGNORE
//        } else {
//            UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
//        }
//    }

    @Subscribe
    public void handlePlayer(PlayerVideoEvent event) {
        //TODO
        Logger.d("handlePlayer");
    }

    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
        Logger.d("handleDownloadVideo");
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DownloadHelper.addVideoToDownloadList(getApplicationContext(), url, fileId);
        } else {
//            throw new IllegalStateException("url is null");
            UiUtils.showErrorSnackbar(getBaseView(), "Server has returned an empty url for video file");
            Logger.e("Server response must contains \"mp\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleDownloadAudio(DownloadAudioEvent event) {
        Logger.d("handleDownloadAudio");
        File file = ListUtils.getStringWith(event.getEventData().getModelData().getResponse().getBody().getFiles(), "m4a");
        String url;
        if (file != null) {
            url = file.getUrl();
            String fileId = event.mFileId;
            DownloadHelper.addAudioToDownloadList(getApplicationContext(), url, fileId);
        } else {
//            throw new IllegalStateException("url is null");
            UiUtils.showErrorSnackbar(getBaseView(), "Server has returned an empty url for audio file");
            Logger.e("Server response must contains \"m4a\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

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

    @Override
    protected String getActivityName() {
        return getString(R.string.activity_name_main);
    }
}
