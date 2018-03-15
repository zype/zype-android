package com.zype.android.ui.MyLibrary;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.ForbiddenErrorEvent;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.builder.EntitlementsParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.consumer.ConsumerFavoriteVideoEvent;
import com.zype.android.webapi.events.entitlements.VideoEntitlementsEvent;
import com.zype.android.webapi.events.video.RetrieveVideoEvent;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideo;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;
import com.zype.android.webapi.model.entitlements.VideoEntitlementData;
import com.zype.android.webapi.model.entitlements.VideoEntitlements;
import com.zype.android.webapi.model.video.Pagination;
import com.zype.android.webapi.model.video.Video;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class MyLibraryFragment extends BaseFragment implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MyLibraryFragment.class.getSimpleName();

    ListView listMyLibrary;
    private ProgressBar progressBar;
    private RelativeLayout layoutSignIn;
    private TextView textEmpty;

    private LoaderManager loaderManager;
    private VideosCursorAdapter adapter;
    private OnMainActivityFragmentListener listener;
    private OnVideoItemAction onVideoItemActionListener;
    private OnLoginAction onLoginListener;

    private static final int LOADER_VIDEO_ENTITLEMENTS = 200;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences prefs;

    private Map<String, VideoEntitlementData> videoEntitlements = new HashMap<>();

    public MyLibraryFragment() {
    }

    public static MyLibraryFragment newInstance() {
        return new MyLibraryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        adapter = new VideosCursorAdapter(getActivity(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, onVideoItemActionListener, onLoginListener);

        View view = inflater.inflate(R.layout.fragment_my_library, null);

        listMyLibrary = (ListView) view.findViewById(R.id.listMyLibrary);
        listMyLibrary.setEmptyView(textEmpty);
        listMyLibrary.setOnItemClickListener(this);
        listMyLibrary.setAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        layoutSignIn = (RelativeLayout) view.findViewById(R.id.layoutSignIn);
        Button buttonSignIn = (Button) view.findViewById(R.id.buttonSighIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginListener.onRequestLogin();
            }
        });

        textEmpty = (TextView) view.findViewById(R.id.empty);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                updateViews();
                updateTextEmpty();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        updateViews();

        return view;
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
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
        try {
            onVideoItemActionListener = (OnVideoItemAction) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnEpisodeItemAction");
        }

        try {
            onLoginListener = (OnLoginAction) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLoginAction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        onVideoItemActionListener = null;
        onLoginListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loaderManager != null) {
            loaderManager.destroyLoader(LOADER_VIDEO_ENTITLEMENTS);
            loaderManager = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsProvider.getInstance().isLoggedIn()) {
            showProgress();
            requestVideoEntitlements(1);
        }
    }

    @Override
    protected String getFragmentName() {
        return TAG;
    }

    // //////////
    // UI
    //
    private void updateViews() {
        if (SettingsProvider.getInstance().isLoggedIn()) {
            layoutSignIn.setVisibility(View.GONE);
            listMyLibrary.setVisibility(View.VISIBLE);
        }
        else {
            layoutSignIn.setVisibility(View.VISIBLE);
            listMyLibrary.setVisibility(View.GONE);
        }
    }

    private void updateTextEmpty() {
        if (SettingsProvider.getInstance().isLoggedIn()) {
            textEmpty.setText(SettingsProvider.getInstance().getNoFavoritesMessage());
        }
        else {
            textEmpty.setText(SettingsProvider.getInstance().getNoFavoritesMessageNotLoggedIn());
        }
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideosCursorAdapter.VideosViewHolder holder = (VideosCursorAdapter.VideosViewHolder) view.getTag();
        VideoDetailActivity.startActivity(getActivity(), holder.videoId);
    }

    // //////////
    // Data
    //
    void requestVideoEntitlements(int page) {
        if (page == 1) {
            videoEntitlements = new HashMap<>();
        }

        EntitlementsParamsBuilder builder = new EntitlementsParamsBuilder()
                .addAccessToken()
                .addPage(page);
        getApi().executeRequest(WebApiManager.Request.VIDEO_ENTITLEMENTS, builder.build());
    }

    protected void startLoadCursors() {
        if (SettingsProvider.getInstance().isLoggedIn()) {
            showProgress();
            if (loaderManager == null) {
                loaderManager = getLoaderManager();
            }
            loaderManager.restartLoader(LOADER_VIDEO_ENTITLEMENTS, null, this);
        }
    }

    //
    // Loader implementation
    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.Video.IS_ENTITLED + " =?";
        String[] selectionArgs = new String[] { String.valueOf(1) };
        return new CursorLoader(
                getActivity(),
                Contract.Video.CONTENT_URI,
                null,
                selection,
                selectionArgs, Contract.Video.ENTITLEMENT_UPDATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            textEmpty.setVisibility(View.GONE);
        } else {
            textEmpty.setVisibility(View.VISIBLE);
        }
        adapter.changeCursor(cursor);
        hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    // ///////////
    // Subscriptions
    //
    @Subscribe
    public void handleVideoEntitlements(VideoEntitlementsEvent event) {
        VideoEntitlements data = event.getEventData().getModelData();
        Logger.d("handleVideoEntitlements(): size=" + data.videoEntitlements.size());
        for (VideoEntitlementData item : data.videoEntitlements) {
            videoEntitlements.put(item.videoId, item);
        }

        if (Pagination.hasNextPage(data.pagination) && !data.videoEntitlements.isEmpty()) {
            requestVideoEntitlements(Pagination.getNextPage(data.pagination));
        }
        else {
            // Clear entitlement flag in all existing videos
            VideoHelper.setEntitlement(getActivity().getContentResolver(), null, false, "");
            // Load videos with entitlements
            for (String videoId : videoEntitlements.keySet()) {
                VideoParamsBuilder builder = new VideoParamsBuilder()
                        .addVideoId(videoId);
                getApi().executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        }
    }

    @Subscribe
    public void handleRetrieveVideo(RetrieveVideoEvent event) {
        Logger.d("handleRetrieveVideo(): size=" + event.getEventData().getModelData().getVideoData().size());
        Video data = event.getEventData().getModelData();
        if (data.getVideoData().size() > 0) {
            for (VideoData item : data.getVideoData()) {
                if (videoEntitlements.containsKey(item.getId())) {
                    List<VideoData> videos = new ArrayList<>();
                    videos.add(item);
                    DataHelper.insertVideos(getActivity().getContentResolver(), videos);

                    VideoEntitlementData entitlementData = videoEntitlements.get(item.getId());
                    if (entitlementData != null) {
                        VideoHelper.setEntitlement(getActivity().getContentResolver(), item.getId(), true, entitlementData.updatedAt);
                    }
                }
            }
        }
    }

}
