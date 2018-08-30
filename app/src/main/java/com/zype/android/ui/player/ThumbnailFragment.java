package com.zype.android.ui.player;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Thumbnail;

/**
 * Created by Evgeny Cherkasov on 24.08.2018
 */
public class ThumbnailFragment extends Fragment {
    public ThumbnailFragment() {}

    public static ThumbnailFragment newInstance(String videoId) {
        ThumbnailFragment fragment = new ThumbnailFragment();
        Bundle args = new Bundle();
        args.putString(BundleConstants.VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thumbnail, container, false);

        ImageView imageView = rootView.findViewById(R.id.image);

        String videoId = getArguments().getString(BundleConstants.VIDEO_ID);
        Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(videoId);
        if (video != null && video.thumbnails != null) {
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

        return rootView;
    }
}
