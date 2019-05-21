package com.zype.android.ui.v2.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;

import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.ui.v2.base.StatefulData;
import com.zype.android.ui.v2.videos.VideosAdapter;
import com.zype.android.utils.Logger;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel model;

    private VideosAdapter adapter;

    private SearchView viewSearch;
    private RecyclerView listVideos;
    private TextView textEmptyResult;
    private TextView textErrorEmptyQuery;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_v2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listVideos = findViewById(R.id.listVideos);
        adapter = new VideosAdapter(null);
        listVideos.setAdapter(adapter);

        progressBar = findViewById(R.id.progress);

        viewSearch = findViewById(R.id.viewSearch);
        viewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    model.search(query, ZypeConfiguration.getRootPlaylistId(SearchActivity.this));
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
                hideProgress();
                model.clearSearchResults();
                return false;
            }
        });

        textEmptyResult = findViewById(R.id.textEmptyResult);
        textErrorEmptyQuery = findViewById(R.id.textErrorEmptyQuery);

        viewSearch.setQuery("", false);
        viewSearch.setIconified(false);
        viewSearch.setFocusable(false);
        viewSearch.clearFocus();

        model = ViewModelProviders.of(this).get(SearchViewModel.class);
        model.getVideos().observe(this, createVideosObserver());
    }

    private Observer<StatefulData<List<Video>>> createVideosObserver() {
        return new Observer<StatefulData<List<Video>>>() {
            @Override
            public void onChanged(@Nullable StatefulData<List<Video>> videos) {
                if (videos.state == DataState.READY) {
                    hideKeyboard();
                    hideProgress();
                    adapter.setData(videos.data);
                    if (videos.data == null || videos.data.isEmpty()) {
                        listVideos.setVisibility(GONE);
                        textEmptyResult.setVisibility(VISIBLE);
                        textErrorEmptyQuery.setVisibility(GONE);
                    }
                    else {
                        listVideos.setVisibility(VISIBLE);
                        textEmptyResult.setVisibility(GONE);
                        textErrorEmptyQuery.setVisibility(GONE);
                    }
                }
                else if (videos.state == DataState.LOADING) {
                    hideKeyboard();
                    showProgress();
                    listVideos.setVisibility(GONE);
                    textEmptyResult.setVisibility(GONE);
                    textErrorEmptyQuery.setVisibility(GONE);
                }
                else if (videos.state == DataState.ERROR) {
                    hideProgress();
                    adapter.setData(null);
                    listVideos.setVisibility(GONE);
                    textEmptyResult.setVisibility(VISIBLE);
                    textErrorEmptyQuery.setVisibility(GONE);
                }
                else {
                    Logger.e("getVideos()::onChanged(): Unknown state");
                }
            }
        };
    }

    //
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

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(findViewById(R.id.search_field).getWindowToken(), 0);
    }

    private void showProgress() {
        progressBar.setVisibility(VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
