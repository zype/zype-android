package com.zype.android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.DbHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.service.DownloaderService;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.Widget.CustomViewPager;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.Model.Section;
import com.zype.android.ui.main.fragments.playlist.PlaylistActivity;
import com.zype.android.ui.main.fragments.videos.VideosActivity;
import com.zype.android.ui.search.SearchActivity;
import com.zype.android.ui.video_details.VideoDetailActivity;
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
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.player.File;
import com.zype.android.zypeapi.ZypeApi;
import com.zype.android.zypeapi.model.VideoResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zype.android.utils.BundleConstants.REQUEST_USER;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnMainActivityFragmentListener, OnVideoItemAction, OnLoginAction,
        BillingManager.BillingUpdatesListener {

    BottomNavigationView bottomNavigationView;

    CustomViewPager pagerSections;
    Map<Integer, Section> sections;

    SectionsPagerAdapter adapterSections;
    private int lastSelectedTabId = R.id.menuNavigationHome;
    private boolean refreshTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.menu_navigation_home);

        adapterSections = new SectionsPagerAdapter(this, getSupportFragmentManager());
        setupSections();

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setupNavigation();

        pagerSections = findViewById(R.id.pagerSections);
        pagerSections.setAdapter(adapterSections);
        pagerSections.setSwipeEnabled(false);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(pagerSections);
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            tab.setCustomView(adapterSections.getTabView(i));
//        }
//
        SettingsParamsBuilder settingsParamsBuilder = new SettingsParamsBuilder();
        getApi().executeRequest(WebApiManager.Request.GET_SETTINGS, settingsParamsBuilder.build());

        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            new BillingManager(this, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (SettingsProvider.getInstance().isLogined()) {
//            requestConsumerData();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshTab) {
            bottomNavigationView.setSelectedItemId(lastSelectedTabId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuMainSearch:
                NavigationHelper.getInstance(this).switchToSearchScreen(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSections() {
        sections = new LinkedHashMap<>();
        sections.put(R.id.menuNavigationHome, new Section(getString(R.string.menu_navigation_home)));
        sections.put(R.id.menuNavigationGuide, new Section(getString(R.string.menu_navigation_guide)));
        sections.put(R.id.menuNavigationLive, new Section(getString(R.string.menu_navigation_live)));
        sections.put(R.id.menuNavigationFavorites, new Section(getString(R.string.menu_navigation_favorites)));
        sections.put(R.id.menuNavigationDownloads, new Section(getString(R.string.menu_navigation_downloads)));
        sections.put(R.id.menuNavigationLibrary, new Section(getString(R.string.menu_navigation_library)));
        sections.put(R.id.menuNavigationSettings, new Section(getString(R.string.menu_navigation_settings)));
        adapterSections.setData(sections);
    }

    private void setupNavigation() {
        bottomNavigationView.getMenu().clear();

        // Home
        bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationHome,
                Menu.NONE, R.string.menu_navigation_home)
                .setIcon(R.drawable.baseline_home_black_24);

        if (ZypeSettings.EPG_ENABLED) {
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationGuide,
                    Menu.NONE, R.string.menu_navigation_guide)
                    .setIcon(R.drawable.baseline_guide_black);
        }

        if (ZypeSettings.SHOW_LIVE) {
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationLive,
                    Menu.NONE, R.string.menu_navigation_live)
                    .setIcon(R.drawable.icon_live);
        }

        // Favorites
        bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationFavorites,
                Menu.NONE, R.string.menu_navigation_favorites)
                .setIcon(R.drawable.baseline_star_rate_black_24);

        // Downloads
        if (ZypeConfiguration.isDownloadsEnabled(this)) {
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationDownloads,
                    Menu.NONE, R.string.menu_navigation_downloads)
                    .setIcon(R.drawable.baseline_cloud_download_black_24);
        }

        // Library
        if (ZypeSettings.LIBRARY_ENABLED) {
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationLibrary,
                    Menu.NONE, R.string.menu_navigation_library)
                    .setIcon(R.drawable.baseline_video_library_black_24);
        }

        // Settings
        bottomNavigationView.getMenu().add(Menu.NONE, R.id.menuNavigationSettings,
                Menu.NONE, R.string.menu_navigation_settings)
                .setIcon(R.drawable.baseline_settings_black_24);


        adapterSections = new SectionsPagerAdapter(this, getSupportFragmentManager());
        adapterSections.setData(sections);
    }

    // Actions

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_USER:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            String videoId = extras.getString(BundleConstants.VIDEO_ID);
                            String playlistId = extras.getString(BundleConstants.PLAYLIST_ID);
                            NavigationHelper.getInstance(this)
                                    .switchToVideoDetailsScreen(this, videoId, playlistId, false);
                        }
                    }
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //
    // 'BottomNavigationView.OnNavigationItemSelectedListener' implementation
    //
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        refreshTab = true;
        switch (item.getItemId()) {
            case R.id.menuNavigationDownloads:
            case R.id.menuNavigationHome:
            case R.id.menuNavigationFavorites:
            case R.id.menuNavigationGuide:
            case R.id.menuNavigationLibrary:
            case R.id.menuNavigationSettings: {
                lastSelectedTabId = item.getItemId();
                Section section = sections.get(item.getItemId());
                pagerSections.setCurrentItem(adapterSections.getSectionPosition(item.getItemId()));
                setTitle(section.title);
                return true;
            }
            case R.id.menuNavigationLive: {
                switchToLiveVideo();
                return true;
            }
        }
        return false;
    }

    private void switchToLiveVideo() {
        //show loader
        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        ZypeApi zypeApi = ZypeApi.getInstance();

        zypeApi.getVideo(ZypeSettings.LIVE_VIDEO_ID, false, response -> {
            progressBar.setVisibility(View.GONE);
            if(response.isSuccessful) {
                VideoResponse videoResponse = (VideoResponse) response.data;
                DataRepository repo = DataRepository.getInstance(getApplication());
                Video video = repo.getVideoSync(ZypeSettings.LIVE_VIDEO_ID);
                if (video != null) {
                    video = DbHelper.videoUpdateEntityByApi(video, videoResponse.videoData);
                    repo.updateVideo(video);
                }
                else {
                    video = DbHelper.videoApiToEntity(videoResponse.videoData);
                    List<Video> videos = new ArrayList<>();
                    videos.add(video);
                    repo.insertVideos(videos);
                }

                NavigationHelper.getInstance(this)
                        .switchToVideoDetailsScreen(this, video.id, null, false);
            }else{
                UiUtils.showErrorSnackbar(findViewById(R.id.root_view), getString(R.string.live_video_load_error_message));
                if (refreshTab) {
                    bottomNavigationView.setSelectedItemId(lastSelectedTabId);
                }
            }
        });
    }

    @Override
    public void onLatestVideoClick(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription(videoId);
        } else {
            VideoDetailActivity.startActivity(this, videoId, null);
        }
    }

    @Override
    public void onDownloadVideo(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription(videoId);
        } else {
            DownloadVideoParamsBuilder downloadVideoParamsBuilder = new DownloadVideoParamsBuilder()
                    .addVideoId(videoId);
            getApi().executeRequest(WebApiManager.Request.PLAYER_DOWNLOAD_VIDEO, downloadVideoParamsBuilder.build());
        }
    }

    @Override
    public void onDownloadAudio(String videoId) {
        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
            onRequestSubscription(videoId);
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
                onRequestSubscription(videoId);
            } else {
                VideoDetailActivity.startActivity(this, videoId, null);
            }
        } else {
            VideoDetailActivity.startActivity(this, videoId, null);
        }
    }

    @Override
    public void onDownloadedVideoClick(String videoId, int mediaType) {
//        if (SettingsProvider.getInstance().getSubscriptionCount() == 0) {
//            onRequestSubscription();
//        } else {
//            VideoDetailActivity.startActivity(this, videoId, null, mediaType);
//        }
        NavigationHelper navigationHelper = NavigationHelper.getInstance(this);
        Video video = DataRepository.getInstance(this.getApplication()).getVideoSync(videoId);
        if (video != null) {
            navigationHelper.handleVideoClick(this, video, null, false);
        }
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
        } else {
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
            } else {
                onRequestLogin();
            }
        } else {
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
//        DataHelper.clearAllVideos(getContentResolver());
        DownloaderService.cancelAllDownloads();
        DataRepository.getInstance(getApplication()).deleteVideoFavorites();
        DataRepository.getInstance(getApplication()).clearVideoEntitlements();
    }

    @Override
    public void onRequestLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
    }

    @Override
    public void onRequestSubscription(String videoId) {
        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            Bundle extras = new Bundle();
            extras.putString(BundleConstants.VIDEO_ID, videoId);
            extras.putString(BundleConstants.PLAYLIST_ID, null);
            NavigationHelper.getInstance(this).switchToSubscriptionScreen(this, extras);
        } else {
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
        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            SubscriptionsHelper.updateSubscriptionCount(purchases);
        }
    }

//    @Override
//    public void openVideoFragment(String url) {
//        Logger.d("openVideoFragment " + url);
////        LatestFragment f = (LatestFragment) adapterPager.getItem(0);
////        f.showVideoFragment(url);
//    }

//    -------------------SUBSCRIBE-------------------

    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        Logger.d("handleConsumer");
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
    }

    @Subscribe
    public void handleDownloadVideo(DownloadVideoEvent event) {
        Logger.d("handleDownloadVideo");
        File file = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "mp4");
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
        File file = ListUtils.getFileByType(event.getEventData().getModelData().getResponse().getBody().getFiles(), "m4a");
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
