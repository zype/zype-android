package com.zype.android.ui.v2.videos;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.utils.Logger;

import java.util.List;

/**
 * Created by Evgeny Cherkasov on 12.02.2019
 */
public class VideosFragment extends Fragment {
    public static final String TAG = VideosFragment.class.getSimpleName();

    private static final String ARG_PLAYLIST_ID = "PlaylistId";

    private VideosViewModel model;
    private String playlistId;

    private VideosAdapter adapter;

    ProgressBar progressBar;

    public static VideosFragment newInstance(String playlistId) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistId = getArguments().getString(ARG_PLAYLIST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_videos, container, false);

        RecyclerView listVideos = rootView.findViewById(R.id.listVideos);
        adapter = new VideosAdapter(playlistId);
        listVideos.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progress);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(this).get(VideosViewModel.class);

        showProgress();
        model.getVideos(playlistId).observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                Logger.d("getVideos(): size=" + videos.size());

                adapter.setData(videos);
                hideProgress();
            }
        });
        model.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String errorMessage) {
                if (!TextUtils.isEmpty(errorMessage)) {
                    hideProgress();
                    Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

}
