package com.zype.android.ui.v2.videos;

import com.google.android.material.snackbar.Snackbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zype.android.R;
import com.zype.android.service.DownloadConstants;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.ui.v2.base.DataState;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Evgeny Cherkasov on 12.02.2019
 */
public class VideosFragment extends Fragment {
    public static final String TAG = VideosFragment.class.getSimpleName();

    private static final String ARG_PLAYLIST_ID = "PlaylistId";

    private PlaylistVideosViewModel model;
    private String playlistId;

    private VideosAdapter adapter;

    private ProgressBar progressBar;

    private final BroadcastReceiver downloaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(DownloadConstants.ACTION_TYPE, 0);
            switch (action) {
                case DownloadConstants.PROGRESS_START_AUDIO:
                case DownloadConstants.PROGRESS_START_VIDEO:
                case DownloadConstants.PROGRESS_UPDATE_AUDIO:
                case DownloadConstants.PROGRESS_UPDATE_VIDEO:
                case DownloadConstants.PROGRESS_END_AUDIO:
                case DownloadConstants.PROGRESS_END_VIDEO:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    throw new IllegalStateException("unknown action=" + action);
            }
        }
    };

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

        model = ViewModelProviders.of(this).get(PlaylistVideosViewModel.class);
        model.setPlaylistId(playlistId);

        showProgress();
        model.getVideos().observe(this, videos -> {
            if (videos.state == DataState.READY) {
                Logger.d("getVideos(): size=" + videos.data.size());
                adapter.setData(videos.data);
                hideProgress();
            }
            else if (videos.state == DataState.LOADING) {
                showProgress();
            }
            else if (videos.state == DataState.ERROR) {
                hideProgress();
                if (!TextUtils.isEmpty(videos.errorMessage)) {
                    hideProgress();
                    Snackbar.make(getView(), videos.errorMessage, Snackbar.LENGTH_INDEFINITE).show();
                }
            }
        });
        model.getSelectedVideo().observe(this, video -> {
            if (video != null) {
                NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());
                navigationHelper.handleVideoClick(getActivity(), video, playlistId, false);
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

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DownloadConstants.ACTION);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .registerReceiver(downloaderReceiver, filter);
        model.retrieveVideos(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                .unregisterReceiver(downloaderReceiver);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

}
