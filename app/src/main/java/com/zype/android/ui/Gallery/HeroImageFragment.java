package com.zype.android.ui.Gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.UiUtils;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImageFragment extends Fragment {
    public static final String TAG = HeroImageFragment.class.getSimpleName();

    private static final String ARG_IMAGE_URL = "ImageUrl";
    private static final String ARG_PLAYLIST_ID = "PlaylistId";
    private static final String ARG_VIDEO_ID = "VideoId";

    private String imageUrl;
    private String playlistId;
    private String videoId;

    public static HeroImageFragment newInstance(String imageUrl, String playlistId, String videoId) {
        HeroImageFragment fragment = new HeroImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_PLAYLIST_ID, playlistId);
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUrl = getArguments().getString(ARG_IMAGE_URL);
        playlistId = getArguments().getString(ARG_PLAYLIST_ID);
        videoId = getArguments().getString(ARG_VIDEO_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hero_image, container, false);

        final NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());

        ImageView imageHero = rootView.findViewById(R.id.imageHero);
        UiUtils.loadImage(getActivity(), imageUrl, imageHero);
        imageHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(videoId)) {
                    Video video = DataRepository.getInstance(getActivity().getApplication()).getVideoSync(videoId);
                    if (video == null || video.active != 1) {
                        DialogHelper.showErrorAlert(getActivity(), getString(R.string.gallery_hero_image_error_video));
                    }
                    else {
                        navigationHelper.handleVideoClick(getActivity(), video, null, false);
                    }
                }
                else if (!TextUtils.isEmpty(playlistId)) {
                    Playlist playlist = DataRepository.getInstance(getActivity().getApplication()).getPlaylistSync(playlistId);
                    if (playlist == null || playlist.active != 1) {
                        DialogHelper.showErrorAlert(getActivity(), getString(R.string.gallery_hero_image_error_playlist));
                    }
                    else {
                        navigationHelper.handlePlaylistClick(getActivity(), playlist);
                    }
                }
            }
        });

        return rootView;
    }

}
