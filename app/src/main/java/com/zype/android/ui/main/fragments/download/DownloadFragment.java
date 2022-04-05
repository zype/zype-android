package com.zype.android.ui.main.fragments.download;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.main.fragments.AbstractTabFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;
import com.zype.android.ui.player.PlayerFragment;

import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import static com.zype.android.ui.base.BaseVideoActivity.TYPE_UNKNOWN;
import static com.zype.android.ui.main.MainActivity.childrenVidIds;

import java.util.List;

public class DownloadFragment extends AbstractTabFragment {
    private static final int LOADER_DOWNLOAD_VIDEO = 0;
    int itemSize = 0;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public static DownloadFragment newInstance() {
        return new DownloadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mAdapter.setShowDownloadOptions(true);
        getDownloadedVideos();
        setEmptyText(getString(R.string.downloads_empty_title), getString(R.string.downloads_empty_message));
        return view;
    }

    public void getDownloadedVideos(){
        itemSize = 0;
        List<Video> videoList = DataRepository.getInstance(requireActivity().getApplication()).getDownloadedVideosFromDB();
        if (childrenVidIds.isEmpty()) {
            for (Video video : videoList) {
                childrenVidIds.add(video.id);
            }
        }
        DataRepository.getInstance(requireActivity().getApplication()).getDownloadedVideos(response -> {
            if (response.isSuccessful) {

                itemSize++;

                if (itemSize >= videoList.size()){
                    if (mLoader == null) {
                        mLoader = getLoaderManager();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(SELECTED_TAB, selectedTab);
                    mLoader.restartLoader(LOADER_DOWNLOAD_VIDEO, bundle, this);
                }
            }
        });
    }

    @Override
    protected void startLoadCursors(int selectedTab) {
        if (SettingsProvider.getInstance().isLoggedIn()
                || ZypeConfiguration.isDownloadsForGuestsEnabled(getActivity())) {
            if (mLoader == null) {
                mLoader = getLoaderManager();
            }
            Bundle bundle = new Bundle();
            bundle.putInt(SELECTED_TAB, selectedTab);
            mLoader.restartLoader(LOADER_DOWNLOAD_VIDEO, bundle, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLoader != null) {
            mLoader.destroyLoader(LOADER_DOWNLOAD_VIDEO);
            mLoader = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoadCursors(selectedTab);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mEmpty.setText(SettingsProvider.getInstance().getNoDownloadsMessage());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey(SELECTED_TAB)) {
            int tab = args.getInt(SELECTED_TAB);
            String selection;
            String[] selectionArgs;
            String childVideoIds = TextUtils.join("','", childrenVidIds);


            switch (tab) {
                case 0:

                    String query = "("+ Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + " =? OR " + Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + " =? ) AND "+Contract.Video.COLUMN_ACTIVE+"=? AND "+Contract.Video.COLUMN_ID+" IN ('"+childVideoIds+"')";
                    selection = query;
                    selectionArgs = new String[]{String.valueOf(1), String.valueOf(1), String.valueOf(1)};
                    break;
                case 1:
                    String query_for_audio = Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + " =? AND "+Contract.Video.COLUMN_ACTIVE+"=? AND "+Contract.Video.COLUMN_ID+" IN ('"+childVideoIds+"')";

                    selection = query_for_audio;
                    selectionArgs = new String[]{String.valueOf(1), String.valueOf(1)};
                    break;
                case 2:
                    String query_for_video = Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + " =? AND "+Contract.Video.COLUMN_ACTIVE+"=? AND "+Contract.Video.COLUMN_ID+" IN ('"+childVideoIds+"')";

                    selection = query_for_video;
                    selectionArgs = new String[]{String.valueOf(1)};
                    break;
                default:
                    throw new IllegalStateException("unknown tab id=" + tab);
            }
            return new CursorLoader(
                    getActivity(), Contract.Video.CONTENT_URI, null, selection,
                    selectionArgs, Contract.Video.COLUMN_CREATED_AT + " DESC");

        } else {
            throw new IllegalStateException("current tab can not be null");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int mediaType = TYPE_UNKNOWN;
        switch (getSelectedTab()) {
            case 1:
                mediaType = PlayerFragment.TYPE_AUDIO_LOCAL;
                break;
            case 2:
                mediaType = PlayerFragment.TYPE_VIDEO_LOCAL;
                break;
        }
        mListener.onDownloadedVideoClick(((VideosCursorAdapter.VideosViewHolder) view.getTag()).videoId, mediaType);
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_download);
    }
}
