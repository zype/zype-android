package com.zype.android.ui.player;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.subscription.SubscriptionHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Thumbnail;

/**
 * Created by Evgeny Cherkasov on 24.08.2018
 */
public class ThumbnailFragment extends Fragment {

//    private String videoId;
//
    private VideoDetailViewModel model;
    private Observer<Video> videoObserver;

    public ThumbnailFragment() {}

//    public static ThumbnailFragment newInstance(String videoId) {
    public static ThumbnailFragment newInstance() {
        ThumbnailFragment fragment = new ThumbnailFragment();
        Bundle args = new Bundle();
//        args.putString(BundleConstants.VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            videoId = getArguments().getString(BundleConstants.VIDEO_ID);
//        }
//        else {
//            throw new IllegalStateException("VideoId can not be empty");
//        }

        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thumbnail, container, false);
//
//        ImageView imageView = rootView.findViewById(R.id.image);
//
//        String videoId = getArguments().getString(BundleConstants.VIDEO_ID);
//        Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(videoId);
//        if (video != null && video.thumbnails != null) {
//            Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
//            if (thumbnail != null) {
//                UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, imageView);
//            }
//            else {
//                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_video));
//            }
//        }
//        else {
//            imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_video));
//        }
//
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(VideoDetailViewModel.class);
        model.getVideo().observe(this, videoObserver);
    }

    private void initialize() {
        if (videoObserver == null) {
            videoObserver = createVideoObserver();
        }
    }

    private Observer<Video> createVideoObserver() {
        return video -> {
            Logger.d("getVideo(): onChanged()");

            ImageView imageView = getView().findViewById(R.id.image);
            Button buttonWatchNow = getView().findViewById(R.id.buttonWatchNow);

            if (video == null) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_video));
                buttonWatchNow.setVisibility(View.GONE);
            }
            else {
                if (video.thumbnails != null) {
                    Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
                    if (thumbnail != null) {
                        UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, imageView);
                    }
                    else {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_video));
                    }
                }
                else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_video));
                }

                if (SubscriptionHelper.isVideoUnlocked(getActivity(), video.id, model.getPlaylistId())) {
                    buttonWatchNow.setVisibility(View.GONE);
                }
                else {
                    buttonWatchNow.setVisibility(View.VISIBLE);
                    buttonWatchNow.setOnClickListener(v -> NavigationHelper.getInstance(getActivity())
                            .handleLockedVideo(getActivity(), video, model.getPlaylistSync()));
                    if (Integer.parseInt(video.purchaseRequired) == 1
                        && ZypeConfiguration.isNativeTvodEnabled(getContext())) {
                        // For purchase required videos, when in-pp purchases are enabled, disable
                        // "Watch now" button, because we have a "Buy Video" button in the Video detail screen
                        buttonWatchNow.setEnabled(false);
                    }
                }
            }
        };
    }

}
