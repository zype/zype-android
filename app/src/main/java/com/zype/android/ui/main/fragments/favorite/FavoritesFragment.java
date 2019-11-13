package com.zype.android.ui.main.fragments.favorite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;
import com.zype.android.ui.v2.videos.VideoActionsHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.consumer.ConsumerFavoriteVideoEvent;
import com.zype.android.webapi.events.video.VideoListEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideo;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.video.Pagination;
import com.zype.android.webapi.model.video.VideoList;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends BaseFragment implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_FAVORITE_VIDEO = 0;

    private ListView listFavorites;
//    private TextView textEmpty;
    private LinearLayout layoutEmpty;

    private LoaderManager loaderManager;
    private VideosCursorAdapter adapter;
    private OnMainActivityFragmentListener listener;
    private OnVideoItemAction onVideoItemActionListener;
    private OnLoginAction onLoginListener;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences prefs;

    public FavoritesFragment() {
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_favorites, null);

        layoutEmpty = rootView.findViewById(R.id.layoutEmpty);

        onVideoItemActionListener = new OnVideoItemAction() {
            @Override
            public void onFavoriteVideo(String videoId) {
                Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(videoId);
                VideoActionsHelper.onFavorite(video, getActivity().getApplication(), (success) -> startLoadCursors());
            }

            @Override
            public void onUnFavoriteVideo(String videoId) {
                Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(videoId);
                VideoActionsHelper.onUnfavorite(video, getActivity().getApplication(), (success) -> startLoadCursors());
            }

            @Override
            public void onShareVideo(String videoId) {

            }

            @Override
            public void onDownloadVideo(String videoId) {

            }

            @Override
            public void onDownloadAudio(String videoId) {

            }
        };
        adapter = new VideosCursorAdapter(getActivity(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, onVideoItemActionListener, onLoginListener);
        listFavorites = rootView.findViewById(R.id.listFavorites);
//        textEmpty = (TextView) view.findViewById(R.id.empty);
//        listFavorites.setEmptyView(layoutEmpty);
        listFavorites.setOnItemClickListener(this);
        listFavorites.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                updateTextEmpty();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoadCursors();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnMainActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
//            onVideoItemActionListener = (OnVideoItemAction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEpisodeItemAction");
        }

        try {
            onLoginListener = (OnLoginAction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginAction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
//        onVideoItemActionListener = null;
        onLoginListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loaderManager != null) {
            loaderManager.destroyLoader(LOADER_FAVORITE_VIDEO);
            loaderManager = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateTextEmpty();
//        if (ZypeConfiguration.isUniversalSubscriptionEnabled(getActivity())) {
           if (SettingsProvider.getInstance().isLoggedIn()) {
                requestConsumerFavoriteVideo(1);
            }
//        }
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_favorite);
    }

    // //////////
    // UI
    //
//    private void updateTextEmpty() {
//        if (SettingsProvider.getInstance().isLoggedIn()) {
//            textEmpty.setText(SettingsProvider.getInstance().getNoFavoritesMessage());
//        }
//        else {
//            textEmpty.setText(SettingsProvider.getInstance().getNoFavoritesMessageNotLoggedIn());
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideosCursorAdapter.VideosViewHolder holder = (VideosCursorAdapter.VideosViewHolder) view.getTag();
//        listener.onFavoriteVideoClick(holder.videoId, holder.isFavorite);
        NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());
        Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(holder.videoId);
        navigationHelper.handleVideoClick(getActivity(), video, null, false);
    }

    // //////////
    // Data
    //
    void requestConsumerFavoriteVideo(int page) {
        ConsumerParamsBuilder builder = new ConsumerParamsBuilder()
                .addAccessToken()
                .addPage(page);
        getApi().executeRequest(WebApiManager.Request.CONSUMER_FAVORITE_VIDEO_GET, builder.build());
    }

    protected void startLoadCursors() {
        if (AuthHelper.isLoggedIn()
                || !ZypeConfiguration.isUniversalSubscriptionEnabled(getActivity())) {
            if (loaderManager == null) {
                loaderManager = getLoaderManager();
            }
            loaderManager.restartLoader(LOADER_FAVORITE_VIDEO, null, this);
        }
    }

    //
    // Loader implementation
    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.Video.COLUMN_IS_FAVORITE + " =?";
        String[] selectionArgs = new String[] { String.valueOf(1) };
        return new CursorLoader(
                getActivity(),
                Contract.Video.CONTENT_URI,
                null,
                selection,
                selectionArgs, Contract.Video.COLUMN_CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean isFavoritesAvailable = AuthHelper.isLoggedIn()
                || !ZypeApp.get(getContext()).getAppConfiguration().hideFavoritesActionWhenSignedOut;
        if (isFavoritesAvailable) {
            adapter.changeCursor(cursor);
            if (cursor != null && cursor.getCount() > 0) {
                layoutEmpty.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        }
        else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    // ///////////
    // Subscriptions
    //
    @Subscribe
    public void handleConsumerFavoriteVideo(ConsumerFavoriteVideoEvent event) {
        Logger.d("handleConsumerFavoriteVideo");
        ConsumerFavoriteVideo favorite = event.getEventData().getModelData();
        if (Pagination.hasNextPage(favorite.getPagination()) && !favorite.getResponse().isEmpty()) {
            requestConsumerFavoriteVideo(Pagination.getNextPage(favorite.getPagination()));
        }
        DataHelper.insertFavorites(getActivity().getContentResolver(), favorite.getResponse());
        // Retrieve videos
        for (ConsumerFavoriteVideoData item : favorite.getResponse()) {
            VideoParamsBuilder builder = new VideoParamsBuilder()
                    .addVideoId(item.getVideoId());
            getApi().executeRequest(WebApiManager.Request.VIDEO_LIST, builder.build());
        }
    }

    @Subscribe
    public void handleRetrieveVideo(VideoListEvent event) {
        Logger.d("handleRetrieveVideo(): size=" + event.getEventData().getModelData().getVideoData().size());
        VideoList data = event.getEventData().getModelData();
        if (data.getVideoData().size() > 0) {
            for (VideoData item : data.getVideoData()) {
                if (!TextUtils.isEmpty(DataHelper.getFavoriteId(getActivity().getContentResolver(), item.getId()))) {
                    List<VideoData> videos = new ArrayList<>();
                    videos.add(item);
                    DataHelper.insertVideos(getActivity().getContentResolver(), videos);
                    DataHelper.setFavoriteVideo(getActivity().getContentResolver(), item.getId(), true);
                }
            }
        }
    }

}
