package com.zype.android.ui.search;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.builder.DownloadAudioParamsBuilder;
import com.zype.android.webapi.builder.DownloadVideoParamsBuilder;
import com.zype.android.webapi.builder.FavoriteParamsBuilder;
import com.zype.android.webapi.builder.SearchParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.events.download.DownloadAudioEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.search.SearchEvent;
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.video.Pagination;
import com.zype.android.webapi.model.video.VideoData;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SearchActivity extends BaseActivity implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnVideoItemAction, OnLoginAction,
                                                    BillingManager.BillingUpdatesListener {

    private static final String SEARCH_STRING = "SEARCH_STRING";
    private static final String SELECTED_TAB = "SELECTED_TAB";
    private static final int LOADER_SEARCH_VIDEO = 9383;
    protected VideosCursorAdapter mAdapter;
    private ProgressBar searchProgress;
    private String searchString;
    private List<VideoData> mSearchList;
    private LoaderManager mLoader;
    private TextView tvSearchField;
    private WebApiManager.Job job;
    private TabHost tabHost;
    private int selectedTab;

    private SearchView viewSearch;
    private ListView listVideos;
    private TextView textEmpty;
    private TextView textErrorEmptyQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            searchString = getIntent().getStringExtra(BundleConstants.SEARCH_STRING);
        } else {
            throw new IllegalStateException("VideoId can not be empty");
        }
        searchProgress = findViewById(R.id.search_progress);
        tvSearchField = findViewById(R.id.search_field);
        tvSearchField.setText(searchString);

        viewSearch = findViewById(R.id.viewSearch);
        viewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateUI();
                if (!TextUtils.isEmpty(query)) {
                    startSearch();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        viewSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        textErrorEmptyQuery = findViewById(R.id.textErrorEmptyQuery);

//        tvSearchField.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN
//                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    startSearch();
//                    return true;
//                }
//
//                return false;
//            }
//        });
//        tvSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    startSearch();
//                    return true;
//                }
//                return false;
//            }
//        });
        textEmpty = findViewById(R.id.empty);

        mAdapter = new VideosCursorAdapter(this, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, this, this);
        listVideos = findViewById(R.id.list_search);
        listVideos.setEmptyView(findViewById(R.id.empty));
        listVideos.setAdapter(mAdapter);
        listVideos.setOnItemClickListener(this);

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tagAll");
        tabSpec.setIndicator(getString(R.string.title_tab_search_all));
        tabSpec.setContent(R.id.list_search);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tagGuests");
        tabSpec.setIndicator(getString(R.string.title_tab_search_guests));
        tabSpec.setContent(R.id.list_search);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tagTags");
        tabSpec.setIndicator(getString(R.string.title_tab_search_tags));
        tabSpec.setContent(R.id.list_search);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tagAll");
        setTabColors(tabHost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColors(tabHost);
            }
        });

        TabWidget tabs = findViewById(android.R.id.tabs);
        tabs.setVisibility(GONE);

        viewSearch.setQuery(searchString, false);
        viewSearch.setIconified(false);
        viewSearch.setFocusable(false);
        viewSearch.clearFocus();

        requestSearchResult(1, searchString);

        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            new BillingManager(this, this);
        }

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SettingsProvider.getInstance().isLoggedIn()) {
            requestConsumerData();
        }
    }

    // //////////
    // UI
    //
    private void updateUI() {
        if (TextUtils.isEmpty(searchString)) {
            textErrorEmptyQuery.setVisibility(View.VISIBLE);
            listVideos.setVisibility(GONE);
            textEmpty.setVisibility(GONE);
        }
        else {
            textErrorEmptyQuery.setVisibility(GONE);
            listVideos.setVisibility(View.VISIBLE);
            if (mAdapter.getCount() == 0) {
                textEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    // //////////
    // Menu
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestConsumerData() {
        ConsumerParamsBuilder builder = new ConsumerParamsBuilder()
                .addAccessToken();
        getApi().executeRequest(WebApiManager.Request.CONSUMER_GET, builder.build());
    }

    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        Logger.d("handleConsumer");
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
    }

    private void startSearch() {
        hideKeyboard();
        if (job != null && !job.isCanceled()) {
            job.cancel();
            job = null;
        }
//        searchString = tvSearchField.getText().toString();
        searchString = viewSearch.getQuery().toString();
        requestSearchResult(1, searchString);
        startLoadCursors(selectedTab, searchString);
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(tvSearchField.getWindowToken(), 0);
    }

    private void setTabColors(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            if (tabHost.getTabWidget().getChildAt(i).isSelected()) {
                selectedTab = i;
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.tabAdditionalBgUnselectedColor)); //unselected tab
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                tv.setTextColor(getResources().getColor(R.color.tabAdditionalTextUnselectedColor));
            } else {
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.tabAdditionalBgSelectedColor)); //unselected tab
                TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                tv.setTextColor(getResources().getColor(R.color.tabAdditionalTextSelectedColor));
            }
        }
        startLoadCursors(selectedTab, searchString);
    }

    protected void startLoadCursors(int selectedTab, String searchString) {
        if (mLoader == null) {
            mLoader = getSupportLoaderManager();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_TAB, selectedTab);
        bundle.putString(SEARCH_STRING, searchString);
        mLoader.restartLoader(LOADER_SEARCH_VIDEO, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!args.containsKey(SEARCH_STRING)) {
            throw new IllegalStateException("SEARCH_STRING should be filled");
        }
        String searchString = "%" + args.getString(SEARCH_STRING) + "%";
        if (args.containsKey(SELECTED_TAB)) {
            int tab = args.getInt(SELECTED_TAB);
            String selection;
            String[] selectionArgs;
            switch (tab) {
                case 0:
//                    selection = Contract.VideoList.COLUMN_KEYWORDS + " LIKE ? OR " +
//                            Contract.VideoList.COLUMN_TITLE + " LIKE ? OR " +
//                            Contract.VideoList.COLUMN_DESCRIPTION + " LIKE ?";
                    selectionArgs = new String[]{searchString, searchString, searchString};
                    selection = Contract.Video.COLUMN_TITLE + " LIKE ? ";
                    selectionArgs = new String[] { searchString };
                    break;
                case 1:
                    selection = Contract.Video.COLUMN_VIDEO_ZOBJECTS + " LIKE ?";
                    selectionArgs = new String[]{"%" + searchString + "%"};
                    break;
                case 2:
                    selection = Contract.Video.COLUMN_KEYWORDS + " LIKE ?";
                    selectionArgs = new String[]{"%" + searchString + "%"};
                    break;
                default:
                    throw new IllegalStateException("unknown tab id=" + tab);
            }
            return new CursorLoader(this, Contract.Video.CONTENT_URI,
                    null, selection, selectionArgs, Contract.Video.COLUMN_CREATED_AT + " DESC");
        } else {
            throw new IllegalStateException("current tab can not be null");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void requestSearchResult(int page, String searchString) {
        showProgress();
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);
        SearchParamsBuilder searchParamsBuilder = new SearchParamsBuilder()
                .addSearchText(searchString)
                .addPlaylistId(ZypeConfiguration.getRootPlaylistId(this), true)
                .addPage(page);

        job = getApi().executeRequest(WebApiManager.Request.SEARCH, searchParamsBuilder.build());
    }

    @Subscribe
    public void handleSearch(SearchEvent event) {
        if (mSearchList == null) {
            mSearchList = event.getEventData().getModelData().getVideoData();
        } else {
            mSearchList.addAll(event.getEventData().getModelData().getVideoData());
        }
        if (event.getEventData().getModelData() != null && Pagination.hasNextPage(event.getEventData().getModelData().getPagination())) {
            requestSearchResult(Pagination.getNextPage(event.getEventData().getModelData().getPagination()), searchString);
        }
        hideProgress();
        DataHelper.insertVideos(getContentResolver(), event.getEventData().getModelData().getVideoData());
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
        hideProgress();
    }

    void showProgress() {
        searchProgress.setVisibility(View.VISIBLE);
    }

    void hideProgress() {
        searchProgress.setVisibility(GONE);
    }

    @Override
    public void onLogout() {
        //igonre
    }

    @Override
    public void onRequestLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, BundleConstants.REQUEST_LOGIN);
    }

    @Override
    public void onRequestSubscription(String videoId) {
        DialogHelper.showSubscriptionAlertIssue(this);
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
    protected String getActivityName() {
        return getString(R.string.activity_name_search);
    }

    // //////////
    // UI
    //
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.d("onItemClick()");
        VideosCursorAdapter.VideosViewHolder holder = (VideosCursorAdapter.VideosViewHolder) view.getTag();
        NavigationHelper navigationHelper = NavigationHelper.getInstance(this);
        if (AuthHelper.isVideoUnlocked(this, holder.videoId, null)) {
            navigationHelper.switchToVideoDetailsScreen(this, holder.videoId, null, false);
        }
        else {
            navigationHelper.handleNotAuthorizedVideo(this, holder.videoId, null);
        }
    }

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

    //    -------------------SUBSCRIBE-------------------

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
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        String fileId = event.mFileId;
        DownloadHelper.addVideoToDownloadList(getApplicationContext(), url, fileId);
    }

    @Subscribe
    public void handleDownloadAudio(DownloadAudioEvent event) {
        Logger.d("handleDownloadAudio");
        String url = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        String fileId = event.mFileId;
        DownloadHelper.addAudioToDownloadList(getApplicationContext(), url, fileId);
    }
}
