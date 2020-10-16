package com.zype.android.ui.main.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;

import static android.view.View.GONE;

/**
 * @author vasya
 * @version 1
 *          date 7/23/15
 */
public abstract class AbstractTabFragment extends BaseFragment implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String SELECTED_TAB = "SELECTED_TAB";
    protected OnMainActivityFragmentListener mListener;
    protected OnVideoItemAction mOnVideoItemActionListener;
    protected OnLoginAction mOnLoginListener;
    protected LoaderManager mLoader;
    protected VideosCursorAdapter mAdapter;
//    protected TextView mEmpty;
    protected ListView mListView;
    private TabHost tabHost;
    protected int selectedTab = 0;

    private LinearLayout layoutEmpty;

    protected abstract void startLoadCursors(int selectedTab);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_base_tab_list, null);

        layoutEmpty = rootView.findViewById(R.id.layoutEmpty);

        mAdapter = new VideosCursorAdapter(getActivity(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mOnVideoItemActionListener, mOnLoginListener);
        mListView = rootView.findViewById(R.id.list_tab);
//        mEmpty = rootView.findViewById(R.id.empty);
//        mListView.setEmptyView(mEmpty);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        tabHost = rootView.findViewById(android.R.id.tabhost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.title_tab_search_all));
        tabSpec.setContent(R.id.list_tab);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.title_tab_search_audio));
        tabSpec.setContent(R.id.list_tab);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator(getString(R.string.title_tab_search_video));
        tabSpec.setContent(R.id.list_tab);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(1);
        tabHost.setCurrentTab(0);
        setTabColors(tabHost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                setTabColors(tabHost);
                startLoadCursors(selectedTab);
            }
        });
        if (!ZypeApp.get(getActivity()).getAppConfiguration().audioOnlyPlaybackEnabled) {
            rootView.findViewById(android.R.id.tabs).setVisibility(GONE);
        }
        return rootView;
    }

    private void setTabColors(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            if (tabHost.getTabWidget().getChildAt(i).isSelected()) {
                selectedTab = i;
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
            } else {
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            }
        }
    }

    public void setEmptyText(String title, String message) {
        ((TextView) layoutEmpty.findViewById(R.id.textEmptyTitle)).setText(title);
        ((TextView) layoutEmpty.findViewById(R.id.textEmptyMessage)).setText(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMainActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            mOnVideoItemActionListener = (OnVideoItemAction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEpisodeItemAction");
        }

        try {
            mOnLoginListener = (OnLoginAction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginAction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mOnVideoItemActionListener = null;
        mOnLoginListener = null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            layoutEmpty.setVisibility(GONE);
        }
        else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public int getSelectedTab() {
        return selectedTab;
    }
}
