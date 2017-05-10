package com.zype.android.ui.main.fragments.playlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.fragments.videos.VideosActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.model.playlist.Playlist;
import com.zype.android.webapi.model.playlist.PlaylistData;

import java.util.ArrayList;

public class PlaylistActivity extends BaseActivity implements ListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PLAYLIST_ID = "PLAYLIST_ID";
    private ListView mListView;
    private PlaylistCursorAdapter mAdapter;
    private TextView mTvEmpty;
    private LoaderManager mLoader;
    private ArrayList<PlaylistData> mPlaylistList;
    private String parentId = "";
    private static final int LOADER_PLAYLIST_ACTIVITY = 9397;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            parentId = getIntent().getStringExtra(BundleConstants.PARENT_ID);
        } else {
            throw new IllegalStateException("Playlist Id can not be empty");
        }

        updateTitle();

        mTvEmpty = (TextView) findViewById(R.id.empty);
        //mTvEmpty.setText("Yo");

        mListView = (ListView) findViewById(R.id.list_playlist);
        mListView.setOnItemClickListener(this);
        mAdapter = new PlaylistCursorAdapter(this,
                PlaylistCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, null, null);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlaylistList();
    }

    @Override
    public void onPause() {
        super.onPause();

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

    private void updateTitle() {
        Cursor playlistCursor = CursorHelper.getPlaylistCursor(getContentResolver(), parentId);
        if (playlistCursor != null && playlistCursor.moveToFirst()) {
            getSupportActionBar().setTitle(playlistCursor.getString(playlistCursor.getColumnIndex(Contract.Playlist.COLUMN_TITLE)));
            playlistCursor.close();
        }
    }

    private void updatePlaylistList() {
        Logger.d("Update Playlist List from Activity");
        mAdapter.changeCursor(null);
        //mTvEmpty.setText(R.string.latest_empty_list);

        getPlaylists();
        startLoadCursors(parentId);
    }

    private void getPlaylists() {
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addParentId(parentId)
                .addPerPage(100);
        getApi().executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
    }


    protected void startLoadCursors(String playlistId) {
        if (mLoader == null) {
            mLoader = getSupportLoaderManager();
        }
        Bundle bundle = new Bundle();
        bundle.putString(PLAYLIST_ID, playlistId);
        //bundle.putString(SEARCH_STRING, searchString);
        mLoader.restartLoader(LOADER_PLAYLIST_ACTIVITY, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!args.containsKey(PLAYLIST_ID)) {
            throw new IllegalStateException("PLAYLIST_ID should be filled");
        }
        String playlistId = args.getString(PLAYLIST_ID);
        String selection = Contract.Playlist.COLUMN_PARENT_ID + " = ?";
        String[] selectionArgs = new String[]{playlistId};
        return new CursorLoader(
                this,
                Contract.Playlist.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                Contract.Playlist.COLUMN_PRIORITY + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlaylistCursorAdapter.PlaylistViewHolder holder =
                (PlaylistCursorAdapter.PlaylistViewHolder) view.getTag();
        Logger.d("playlist clicked");
        if (holder.playlistId != null){
            if (holder.playlistItemCount == 0){
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleConstants.PARENT_ID, holder.playlistId);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), VideosActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleConstants.PARENT_ID, holder.playlistId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }

    }

    @Subscribe
    public void handleRetrievePlaylist(PlaylistEvent event) {
        Logger.d("Activity handlePlaylistEvent size=" + event.getEventData().getModelData().getResponse().size());
        Playlist data = event.getEventData().getModelData();
        if (data.getResponse().size() > 0) {
            if (mPlaylistList == null) {
                mPlaylistList = new ArrayList<>();
            }
            mPlaylistList.addAll(data.getResponse());
            // Clear all playlists of the parent from local DB before inserting to be consistent
            // with platform in case some palylists were deleted
            if (event.getEventData().getModelData().getPagination().getCurrent() == 1) {
                DataHelper.deletePlaylistsByParentId(this.getContentResolver(), parentId);
            }
            int i = DataHelper.insertPlaylists(this.getContentResolver(), data.getResponse());
            Logger.d("added " + i + " playlists");
        }
        else {
            mTvEmpty.setText(R.string.videos_empty);
        }
    }

    protected String getActivityName() {
        return getString(R.string.activity_name_playlist);
    }
}
