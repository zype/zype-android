package com.zype.android.ui.video_details.fragments.summary;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.model.video.VideoData;

import java.util.List;

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
            textTitle.setText(video.title);
            if (!TextUtils.isEmpty(video.episode)) {
                textVideoEpisode.setVisibility(View.VISIBLE);
                textVideoEpisode.setText(String.format(getActivity().getString(R.string.videos_episode), video.episode));
            } else {
                textVideoEpisode.setVisibility(View.GONE);
            }
            textDescription.setText(video.description);

            Button buttonPlayTrailer = getView().findViewById(R.id.buttonPlayTrailer);
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
