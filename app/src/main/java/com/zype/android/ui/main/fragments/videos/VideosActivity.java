package com.zype.android.ui.main.fragments.videos;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.service.DownloadConstants;
import com.zype.android.service.DownloadHelper;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.ListUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.EntitlementParamsBuilder;
import com.zype.android.webapi.builder.ParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.download.DownloadVideoEvent;
import com.zype.android.webapi.events.entitlements.VideoEntitlementEvent;
import com.zype.android.webapi.events.favorite.FavoriteEvent;
import com.zype.android.webapi.events.favorite.UnfavoriteEvent;
import com.zype.android.webapi.events.video.VideoListEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.entitlements.VideoEntitlement;
import com.zype.android.webapi.model.player.File;
import com.zype.android.webapi.model.video.Pagination;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;

import static com.zype.android.utils.BundleConstants.REQUEST_SUBSCRIBE_OR_LOGIN;

public class VideosActivity extends MainActivity implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,
                                                        BillingManager.BillingUpdatesListener {

    private static final int LOADER_VIDEO = 0;
    private long currentDate;
    private ListView mListView;
    private VideosCursorAdapter mAdapter;
    private final BroadcastReceiver downloaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateItemsUI(intent);
        }
    };
    private OnMainActivityFragmentListener mListener;
    private OnVideoItemAction mOnVideoItemActionListener;
    private OnLoginAction mOnLoginListener;
    private TextView mTvDate;
    private ImageView mBanner;
    private TextView mTvEmpty;
    private LoaderManager mLoader;
    private ArrayList<VideoData> mVideoList;
    private String playlistId = null;
    private String selectedVideoId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addListeners();

        if (getIntent() != null) {
            playlistId = getIntent().getStringExtra(BundleConstants.PARENT_ID);
        } else {
            throw new IllegalStateException("Playlist Id can not be empty");
        }

        updateTitle();

        mListView = (ListView) findViewById(R.id.list_latest);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(findViewById(R.id.empty));
        mAdapter = new VideosCursorAdapter(this, VideosCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                mOnVideoItemActionListener, mOnLoginListener);
        mListView.setAdapter(mAdapter);
        mTvEmpty = (TextView) findViewById(R.id.empty);

        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)
                || ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(this)) {
            new BillingManager(this, this);
        }

        startLoadCursors();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLoadCursors();
        loadVideosFromPlaylist(1);
        IntentFilter filter = new IntentFilter(DownloadConstants.ACTION);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(downloaderReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(downloaderReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLoader != null) {
            mLoader.destroyLoader(LOADER_VIDEO);
            mLoader = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListener = null;
        mOnVideoItemActionListener = null;
        mOnLoginListener = null;
    }

    protected String getActivityName() {
        return getString(R.string.activity_name_videos);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SUBSCRIBE_OR_LOGIN:
                if (resultCode == RESULT_OK) {
                    NavigationHelper.getInstance(this).switchToVideoDetailsScreen(this, selectedVideoId, playlistId, false);
                }
                break;
        }
    }

    // //////////
    // UI
    //
    private void updateTitle() {
        Cursor playlistCursor = CursorHelper.getPlaylistCursor(getContentResolver(), playlistId);
        if (playlistCursor != null && playlistCursor.moveToFirst()) {
            getSupportActionBar().setTitle(playlistCursor.getString(playlistCursor.getColumnIndex(Contract.Playlist.COLUMN_TITLE)));
            playlistCursor.close();
        }
    }

    private void addListeners() {
        try {
            mListener = (OnMainActivityFragmentListener) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try {
            mOnVideoItemActionListener = (OnVideoItemAction) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement OnEpisodeItemAction");
        }

        try {
            mOnLoginListener = (OnLoginAction) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement OnLoginAction");
        }
    }

    private void updateItemsUI(Intent intent) {
        int action = intent.getIntExtra(DownloadConstants.ACTION_TYPE, 0);
        String fileId = intent.getStringExtra(DownloadConstants.EXTRA_FILE_ID);
        if (mAdapter.getCount() > 0) {
            int position = mAdapter.getPositionById(fileId);
            if (position > -1) {
                View view = mAdapter.getViewByPosition(position, mListView);
                if (view != null) {
                    VideosCursorAdapter.VideosViewHolder holder = (VideosCursorAdapter.VideosViewHolder) view.getTag();
                    updateListItem(this, mListView, intent, action, fileId, holder);
                }
            }
        }
    }

    // Download progress
    protected static void updateListItem(Activity activity, View view, Intent intent, int action, String fileId, VideosCursorAdapter.VideosViewHolder viewHolder) {
        if (TextUtils.equals(fileId, viewHolder.videoId)) {
            String errorMessage;
            int progress;
            switch (action) {
                case DownloadConstants.PROGRESS_CANCELED_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    UiUtils.showWarningSnackbar(view, "Audio download has canceled");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_CANCELED_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    UiUtils.showWarningSnackbar(view, "VideoList Download has canceled");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_END_AUDIO:
                    viewHolder.isAudioDownloaded = true;
                    UiUtils.showPositiveSnackbar(view, "Audio was downloaded");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_END_VIDEO:
                    viewHolder.isVideoDownloaded = true;
                    UiUtils.showPositiveSnackbar(view, "VideoList was downloaded");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_FAIL_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                    UiUtils.showErrorSnackbar(view, errorMessage);
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_FAIL_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                    UiUtils.showErrorSnackbar(view, errorMessage);
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_START_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    UiUtils.showPositiveSnackbar(view, "Audio downloading has started");
                    updateListView(viewHolder, 0);
                    break;
                case DownloadConstants.PROGRESS_START_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    UiUtils.showPositiveSnackbar(view, "VideoList downloading was started");
                    updateListView(viewHolder, 0);
                    break;
                case DownloadConstants.PROGRESS_UPDATE_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                    updateListView(viewHolder, progress);
                    break;
                case DownloadConstants.PROGRESS_UPDATE_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                    updateListView(viewHolder, progress);
                    break;
                case DownloadConstants.PROGRESS_FREE_SPACE:
                    viewHolder.isVideoDownloaded = false;
                    viewHolder.isAudioDownloaded = false;
                    hideProgress(viewHolder);
                    if (activity instanceof BaseActivity) {
                        int error = intent.getIntExtra(BundleConstants.PROGRESS_ERROR_MESSAGE, -1);
                        if (error == -1) {
                            error = R.string.alert_dialog_message_free_space;
                        }
                        DialogFragment newFragment = CustomAlertDialog.newInstance(
                                R.string.alert_dialog_title_free_space, error);
                        newFragment.show(((BaseActivity) activity).getSupportFragmentManager(), "dialog_free_space");
                    } else {
                        UiUtils.showWarningSnackbar(view, activity.getString(R.string.alert_dialog_message_free_space));
                    }

                    break;
                default:
                    throw new IllegalStateException("unknown action=" + action);
            }
        }
    }

    private static void updateListView(VideosCursorAdapter.VideosViewHolder viewHolder, int newProgress) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.progressBar.setProgress(newProgress);
//        viewHolder.detailsView.setVisibility(View.GONE);
    }

    private static void hideProgress(VideosCursorAdapter.VideosViewHolder viewHolder) {
        viewHolder.progressBar.setVisibility(View.GONE);
//        viewHolder.detailsView.setVisibility(View.VISIBLE);
        viewHolder.progressBar.setProgress(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.d("onItemClick()");
        VideosCursorAdapter.VideosViewHolder holder = (VideosCursorAdapter.VideosViewHolder) view.getTag();

        selectedVideoId = holder.videoId;
        NavigationHelper navigationHelper = NavigationHelper.getInstance(this);
        if (AuthHelper.isVideoUnlocked(this, holder.videoId, playlistId)) {
            navigationHelper.switchToVideoDetailsScreen(this, holder.videoId, playlistId, false);
        }
        else {
            navigationHelper.handleNotAuthorizedVideo(this, holder.videoId, playlistId);
        }
//        if (ZypeConfiguration.isUniversalTVODEnabled(this) && holder.purchaseRequired) {
//            if (holder.isEntitled) {
//                VideoDetailActivity.startActivity(this, holder.videoId, playlistId);
//            }
//            else {
//                if (SettingsProvider.getInstance().isLoggedIn()) {
//                    requestEntitled(holder.videoId);
//                }
//                else {
//                    NavigationHelper.getInstance(this).switchToLoginScreen(this);
//                }
//            }
//            return;
//        }
//        if (holder.subscriptionRequired) {
//            NavigationHelper.getInstance(this).checkSubscription(this, holder.videoId, playlistId, holder.onAir);
//        }
//        else {
//            VideoDetailActivity.startActivity(this, holder.videoId, playlistId);
//        }
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

    // //////////
    // Data
    //
    private void loadVideosFromPlaylist(int page) {
        Logger.d("load Videos from playlist");
        VideoParamsBuilder builder = new VideoParamsBuilder();
        builder.addPlaylistId(playlistId);
        builder.addPage(page);
        builder.addPerPage(ParamsBuilder.PER_PAGE_DEFAULT);
        getApi().executeRequest(WebApiManager.Request.VIDEO_FROM_PLAYLIST, builder.build());
    }

    protected void startLoadCursors() {
        mAdapter.changeCursor(null);
        mTvEmpty.setText(R.string.videos_loading);

        if (mLoader == null) {
            mLoader = getSupportLoaderManager();
        }
        Bundle bundle = new Bundle();
        mLoader.restartLoader(LOADER_VIDEO, bundle, this);
    }

    //
    // Loader implementation
    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.PlaylistVideo.PLAYLIST_ID + "=?" ;
        String[] selectionArgs = new String[] { playlistId };
        return new CursorLoader(
                this,
                Contract.PlaylistVideo.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                Contract.PlaylistVideo.NUMBER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Logger.d("onLoadFinished(): size=" + cursor.getCount());
        if (cursor.getCount() == 0) {
            mTvEmpty.setText(R.string.videos_empty);
            mAdapter.changeCursor(null);
        }
        else {
            mAdapter.changeCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void requestEntitled(String videoId) {
        EntitlementParamsBuilder builder = new EntitlementParamsBuilder()
                .addAccessToken()
                .addVideoId(videoId);
        getApi().executeRequest(WebApiManager.Request.CHECK_VIDEO_ENTITLEMENT, builder.build());
    }

    // //////////
    // Subscriptions
    //
    @Subscribe
    public void handleRetrieveVideo(VideoListEvent event) {
        List<VideoData> result = event.getEventData().getModelData().getVideoData();
        Pagination pagination = event.getEventData().getModelData().getPagination();
        if (result != null) {
            Logger.d("handleRetrieveVideo(): size=" + result.size());
            if (result.size() > 0) {
                if (mVideoList == null || pagination.getCurrent() == 1) {
                    mVideoList = new ArrayList<>(result);
                }
                else {
                    mVideoList.addAll(result);
                }

                if (Pagination.hasNextPage(pagination)) {
                    loadVideosFromPlaylist(Pagination.getNextPage(pagination));
                }
                else {
                    int videosAdded = DataHelper.insertVideos(this.getContentResolver(), mVideoList);
                    Logger.d("handleRetrieveVideo(): added " + videosAdded + " videos");
                    DataHelper.addVideosToPlaylist(this.getContentResolver(), mVideoList, playlistId);
                    DataHelper.clearPlaylistVideo(this.getContentResolver(), playlistId);
                    int itemsInsertedPlaylistVideo = DataHelper.insertPlaylistVideo(this.getContentResolver(), mVideoList, playlistId, 0);
                    Logger.d("handleRetrieveVideo(): PlaylistVideo inserted=" + itemsInsertedPlaylistVideo);
//                    if (videosAdded > 0) {
//                        startLoadCursors();
//                    }
                }
            }
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
            UiUtils.showErrorSnackbar(mListView, "Server has returned an empty url for video file");
            Logger.e("Server response must contains \"mp\" but server has returned:" + Logger.getObjectDump(event.getEventData().getModelData().getResponse().getBody().getFiles()));
        }
    }

    @Subscribe
    public void handleVideoEntitlementEvent(VideoEntitlementEvent event) {
        Logger.d("handleVideoEntitlementEvent()");
        Bundle requestOptions = event.getOptions();
        HashMap<String, String> pathParams = (HashMap<String, String>) requestOptions.getSerializable(ParamsBuilder.PATH_PARAMS);
        String videoId = pathParams.get(EntitlementParamsBuilder.VIDEO_ID);
        VideoDetailActivity.startActivity(this, videoId, playlistId);
    }

    @Subscribe
    public void handleError(ErrorEvent event) {
        Logger.e("handleError()");
        if (event.getEventData() == WebApiManager.Request.CHECK_VIDEO_ENTITLEMENT) {
            RetrofitError error = event.getError();
            if (error != null) {
                VideoEntitlement response = (VideoEntitlement) event.getError().getBodyAs(VideoEntitlement.class);
                DialogHelper.showEntitlementAlert(this, response.message);
            }
        }
    }
}
