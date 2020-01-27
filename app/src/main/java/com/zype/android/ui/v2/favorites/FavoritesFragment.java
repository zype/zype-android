package com.zype.android.ui.v2.favorites;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.v2.base.StatefulData;
import com.zype.android.ui.v2.videos.VideosAdapter;
import com.zype.android.utils.Logger;

import java.util.List;

public class FavoritesFragment extends Fragment {
    public static final String TAG = FavoritesFragment.class.getSimpleName();

    private FavoriteVideosViewModel model;

    private VideosAdapter adapter;

    private RecyclerView listVideos;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites_v2, container, false);

        listVideos = rootView.findViewById(R.id.listVideos);
        adapter = new VideosAdapter(null);
        listVideos.setAdapter(adapter);

        layoutEmpty = rootView.findViewById(R.id.layoutEmpty);

        progressBar = rootView.findViewById(R.id.progress);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Need for updating video list when return back from the video detail page
        model.retrieveVideos(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(FavoriteVideosViewModel.class);

        showProgress();
        model.getVideos().observe(this, videos -> {
            if (!AuthHelper.isLoggedIn()
                    && ZypeApp.get(getActivity()).getAppConfiguration().hideFavoritesActionWhenSignedOut) {
                showEmpty(true);
                return;
            }
            if (videos == null) {
                return;
            }
            if (videos.data == null) {
                Logger.e("getVideos(): videos list is mull");
                showEmpty(true);
            }
            else {
                Logger.d("getVideos(): size=" + videos.data.size());
                adapter.setData(videos.data);
                if (videos.data.size() > 0) {
                    showEmpty(false);
                }
                else {
                    showEmpty(true);
                }
            }
            hideProgress();
        });
        model.getSelectedVideo().observe(this, video -> {
            if (video != null) {
                NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());
                navigationHelper.handleVideoClick(getActivity(), video, null, false);
                model.onSelectedVideoProcessed();
            }
        });
        adapter.setVideoListener((video) -> {
            model.onVideoClicked(video);
        });
        adapter.setPopupMenuListener((action, video) -> {
            model.handleVideoAction(action, video, success -> {
                if (success) {
                    model.retrieveVideos(false);
                }
                else {
                    NavigationHelper.getInstance(getActivity()).switchToLoginScreen(getActivity());
                }
            });
        });

    }

    private void showEmpty(boolean value) {
        if (value) {
            listVideos.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        }
        else {
            listVideos.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
        hideProgress();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

}
