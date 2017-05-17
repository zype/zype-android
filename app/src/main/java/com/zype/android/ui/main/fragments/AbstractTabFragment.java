package com.zype.android.ui.main.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.zype.android.R;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;

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
    protected TextView mEmpty;
    protected ListView mListView;
    private TabHost tabHost;
    protected int selectedTab = 0;

    protected abstract void startLoadCursors(int selectedTab);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAdapter = new VideosCursorAdapter(getActivity(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mOnVideoItemActionListener, mOnLoginListener);
        View view = inflater.inflate(R.layout.fragment_base_tab_list, null);
        mListView = (ListView) view.findViewById(R.id.list_tab);
        mEmpty = (TextView) view.findViewById(R.id.empty);
        mListView.setEmptyView(mEmpty);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);

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

        tabHost.setCurrentTabByTag("tag1");
        setTabColors(tabHost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {
                setTabColors(tabHost);
                startLoadCursors(selectedTab);
            }
        });
        return view;
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
        if (cursor != null && cursor.getCount() > 0) {
            mEmpty.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.VISIBLE);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
