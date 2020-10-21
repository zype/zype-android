package com.zype.android.ui.video_details.fragments.summary;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.utils.Logger;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class SummaryFragment extends Fragment {
    public static final String TAG = SummaryFragment.class.getSimpleName();

    private VideoDetailViewModel videoViewModel;
    private PlayerViewModel playerViewModel;

    private Observer<Video> videoObserver;

    private TextView textTitle;
    private TextView textDescription;
    private TextView textVideoEpisode;
    public SummaryFragment() {
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_summary, container, false);
        textTitle = view.findViewById(R.id.textVideoTitle);
        textDescription = view.findViewById(R.id.textVideoDescription);
        textVideoEpisode = view.findViewById(R.id.textVideoEpisode);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         videoViewModel = ViewModelProviders.of(getActivity()).get(VideoDetailViewModel.class);

        if (videoObserver == null) {
            videoObserver = createVideoObserver();
        }
        videoViewModel.getVideo().observe(this, videoObserver);

        playerViewModel = ViewModelProviders.of(getActivity()).get(PlayerViewModel.class);
    }

    private Observer<Video> createVideoObserver() {
        return video -> {
            if(video == null){
                return;
            }
                textTitle.setText(video.title);
                if (!TextUtils.isEmpty(video.episode)) {
                    textVideoEpisode.setVisibility(View.VISIBLE);

                    if (getActivity() != null)
                    textVideoEpisode.setText(String.format(getActivity().getString(R.string.videos_episode), video.episode));

                } else {
                    textVideoEpisode.setVisibility(View.GONE);
                }
                textDescription.setText(video.description);

                Button buttonPlayTrailer = Objects.requireNonNull(getView()).findViewById(R.id.buttonPlayTrailer);
                if (ZypeConfiguration.trailers()) {
                    final List<String> previewIds = VideoHelper.getPreviewIdsList(video);
                    if (previewIds.isEmpty()) {
                        buttonPlayTrailer.setVisibility(View.GONE);
                    }
                    else {
                        buttonPlayTrailer.setVisibility(View.VISIBLE);
                        buttonPlayTrailer.setOnClickListener(v -> playTrailer(previewIds.get(0)));
                    }
                }
                else {
                    buttonPlayTrailer.setVisibility(View.GONE);
                }
        };
    }

//    @Override
//    protected String getFragmentName() {
//        return getString(R.string.fragment_name_summary);
//    }

    private void playTrailer(String previewId) {
        Logger.d("playTrailer(): previewId = " + previewId);
        playerViewModel.setTrailerVideoId(previewId);
    }

}
