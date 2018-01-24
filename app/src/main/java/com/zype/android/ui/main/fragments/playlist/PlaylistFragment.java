package com.zype.android.ui.main.fragments.playlist;

import android.animation.Animator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.settings.SettingsEvent;
import com.zype.android.webapi.model.playlist.Playlist;
import com.zype.android.webapi.model.playlist.PlaylistData;


import java.util.ArrayList;

import io.codetail.animation.ViewAnimationUtils;

public class PlaylistFragment extends BaseFragment implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_VIDEO = 0;
    private long currentDate;
    private CardView layoutSearch;
    private SearchView viewSearch;
    private FloatingActionButton buttonSearch;

    private ListView mListView;
    private PlaylistCursorAdapter mAdapter;

    private Animator animatorSearchReverse;

//    private RecyclerView listPlaylists;
//    private PlaylistAdapter adapter;

    private OnMainActivityFragmentListener mListener;
    private OnLoginAction mOnLoginListener;
    private TextView mTvEmpty;
    private LoaderManager mLoader;
    private ArrayList<PlaylistData> mPlaylistList;
    private String parentId;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("onCreate");
        parentId = ZypeConfiguration.getRootPlaylistId(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        mListView = (ListView) view.findViewById(R.id.list_playlist);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(view.findViewById(R.id.empty));
        mAdapter = new PlaylistCursorAdapter(getActivity(), PlaylistCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, null, mOnLoginListener);
        mListView.setAdapter(mAdapter);
        mTvEmpty = (TextView) view.findViewById(R.id.empty);

//        listPlaylists = (RecyclerView) view.findViewById(R.id.listPlaylists);
//        adapter = new PlaylistAdapter();
//        adapter.setCursorAdapter(mAdapter);
//        listPlaylists.setAdapter(adapter);

//        final EditText searchField = (EditText) view.findViewById(R.id.search_field);
//        searchField.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    mListener.onSearch(searchField.getText().toString());
//                    searchField.setText(null);
////                    if (SettingsProvider.getInstance().isLogined()) {
////                        mListener.onSearch(searchFiled.getText().toString());
////                    } else {
////                        mOnLoginListener.onRequestLogin();
////                    }
//                    return true;
//                }
//
//                return false;
//            }
//        });
//        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    mListener.onSearch(searchField.getText().toString());
//                    searchField.setText(null);
////                    if (SettingsProvider.getInstance().isLogined()) {
////                        mListener.onSearch(searchFiled.getText().toString());
////                    } else {
////                        mOnLoginListener.onRequestLogin();
////                    }
//                    return true;
//                }
//                return false;
//            }
//        });

//        final Animation animationSearchShow = AnimationUtils.loadAnimation(getActivity(), R.anim.search_show);

//        int startRadius = 0;
//        int endRadius = (int) Math.hypot(searchField.getWidth(), searchField.getHeight());
//        Animator anim = ViewAnimationUtils.createCircularReveal(searchField, x, y, startRadius, endRadius);
//
        layoutSearch = (CardView) view.findViewById(R.id.layoutSearch);
        viewSearch = (SearchView) view.findViewById(R.id.viewSearch);
        viewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mListener.onSearch(query);
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
                hideSearch();
                return true;
            }
        });
        viewSearch.setIconified(false);
        viewSearch.setFocusable(true);

        layoutSearch.setVisibility(View.INVISIBLE);
        buttonSearch = (FloatingActionButton) view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = (layoutSearch.getLeft() + layoutSearch.getRight());
                int cy = layoutSearch.getTop();
                int radius = Math.max(layoutSearch.getWidth(), layoutSearch.getHeight());
                Animator animator = ViewAnimationUtils.createCircularReveal(layoutSearch, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(400);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        onShowSearch();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

                // Prepare reverse animation to hide search bar later
                animatorSearchReverse = ViewAnimationUtils.createCircularReveal(layoutSearch, cx, cy, radius, 0);
                animatorSearchReverse.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSearchReverse.setDuration(400);
                animatorSearchReverse.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layoutSearch.setVisibility(View.INVISIBLE);
                        buttonSearch.show();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                layoutSearch.setVisibility(View.VISIBLE);
                animator.start();
                buttonSearch.hide();
            }
        });

        return view;
    }

    private void updatePlaylistList() {
        Logger.d("Update Playlist List");
        mAdapter.changeCursor(null);
        mTvEmpty.setText(R.string.videos_loading);

        getPlaylists();
        startLoadCursors();
    }

    private void getPlaylists() {
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addParentId(parentId)
                .addPerPage(100);
        getApi().executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlaylistCursorAdapter.PlaylistViewHolder holder =
                (PlaylistCursorAdapter.PlaylistViewHolder) view.getTag();

        if (holder.playlistId != null){
            if (holder.playlistItemCount == 0){
                mListener.onPlaylist(holder.playlistId);
            } else {
                mListener.onPlaylistWithVideos(holder.playlistId);
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
        mOnLoginListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        updatePlaylistList();
        hideSearch();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLoader != null) {
            mLoader.destroyLoader(LOADER_VIDEO);
            mLoader = null;
        }
    }

    // //////////
    // UI
    //
    private void onShowSearch() {
        UiUtils.showKeyboard(getActivity(), viewSearch);
    }

    private void hideSearch() {
        if (animatorSearchReverse != null && layoutSearch.getVisibility() == View.VISIBLE) {
            try {
                animatorSearchReverse.start();
            }
            catch (Exception e) {
                layoutSearch.setVisibility(View.INVISIBLE);
                buttonSearch.show();
            }
        }
    }

    protected void startLoadCursors() {
        if (mLoader == null) {
            mLoader = getLoaderManager();
        }
        Bundle bundle = new Bundle();
        mLoader.restartLoader(LOADER_VIDEO, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = Contract.Playlist.COLUMN_PARENT_ID + " = ?";
        String[] selectionArgs = new String[]{parentId};
        return new CursorLoader(
                getActivity(),
                Contract.Playlist.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                Contract.Playlist.COLUMN_PRIORITY + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Logger.d("onLoadFinished size=" + cursor.getCount());
        mAdapter.changeCursor(cursor);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_playlist);
    }

    // //////////
    // UI
    //
//    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
//        private PlaylistCursorAdapter cursorAdapter;
//
//        public void setCursorAdapter(PlaylistCursorAdapter cursorAdapter) {
//            this.cursorAdapter = cursorAdapter;
//        }
//
//        @Override
//        public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = cursorAdapter.newView(getActivity(), cursorAdapter.getCursor(), parent);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(PlaylistAdapter.ViewHolder holder, int position) {
//            cursorAdapter.getCursor().moveToPosition(position);
//            cursorAdapter.bindView(holder.itemView, getActivity(), cursorAdapter.getCursor());
//        }
//
//        @Override
//        public int getItemCount() {
//            return cursorAdapter.getCount();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            View view;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                view = itemView;
//            }
//        }
//    }

    //    -------------------SUBSCRIBE-------------------

    @Subscribe
    public void handleSettingsEvent(SettingsEvent event) {
        Logger.d("handleSettingsEvent");
        SettingsProvider.getInstance().saveSettingsFromServer(event.getEventData().getModelData());
    }

    @Subscribe
    public void handleRetrievePlaylist(PlaylistEvent event) {
        Logger.d("handlePlaylistEvent size=" + event.getEventData().getModelData().getResponse().size());
        Playlist data = event.getEventData().getModelData();
        if (data.getResponse().size() > 0) {
            if (mPlaylistList == null) {
                mPlaylistList = new ArrayList<>();
            }
            mPlaylistList.addAll(data.getResponse());
            // Clear all playlists of the parent from local DB before inserting to be consistent
            // with platform in case some palylists were deleted
            if (event.getEventData().getModelData().getPagination().getCurrent() == 1) {
                DataHelper.deletePlaylistsByParentId(getActivity().getContentResolver(), parentId);
            }
            int i = DataHelper.insertPlaylists(getActivity().getContentResolver(), data.getResponse());
            Logger.d("added " + i + " playlists");
        }
        else {
            mTvEmpty.setText(R.string.videos_empty);
        }
    }
}
