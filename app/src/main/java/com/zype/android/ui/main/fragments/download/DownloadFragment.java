package com.zype.android.ui.main.fragments.download;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.main.fragments.AbstractTabFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;

public class DownloadFragment extends AbstractTabFragment {
    private static final int LOADER_DOWNLOAD_VIDEO = 0;

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
        return view;
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
        mEmpty.setText(SettingsProvider.getInstance().getNoDownloadsMessage());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey(SELECTED_TAB)) {
            int tab = args.getInt(SELECTED_TAB);
            String selection;
            String[] selectionArgs;
            switch (tab) {
                case 0:
                    selection = Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + " =? OR " + Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + " =?";
                    selectionArgs = new String[]{String.valueOf(1), String.valueOf(1)};
                    break;
                case 1:
                    selection = Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO + " =?";
                    selectionArgs = new String[]{String.valueOf(1)};
                    break;
                case 2:
                    selection = Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO + " =? ";
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
        mListener.onDownloadedVideoClick(((VideosCursorAdapter.VideosViewHolder) view.getTag()).videoId);
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_download);
    }
}
