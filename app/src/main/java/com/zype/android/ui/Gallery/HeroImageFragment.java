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
import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.utils.UiUtils;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImageFragment extends Fragment {
    public static final String TAG = HeroImageFragment.class.getSimpleName();

    private static final String ARG_IMAGE_URL = "ImageUrl";
    private static final String ARG_PLAYLIST_ID = "PlaylistId";

    private String imageUrl;
    private String playlistId;

    public static HeroImageFragment newInstance(String imageUrl, String playlistId) {
        HeroImageFragment fragment = new HeroImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUrl = getArguments().getString(ARG_IMAGE_URL);
        playlistId = getArguments().getString(ARG_PLAYLIST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hero_image, container, false);

        ImageView imageHero = rootView.findViewById(R.id.imageHero);
        UiUtils.loadImage(getActivity(), imageUrl, imageHero);
        imageHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(playlistId)) {
                    Playlist playlist = DataRepository.getInstance(getActivity().getApplication()).getPlaylistSync(playlistId);
                    if (playlist.playlistItemCount > 0) {
                        NavigationHelper.getInstance(getActivity()).switchToPlaylistVideosScreen(getActivity(), playlistId);
                    }
                    else {
                        NavigationHelper.getInstance(getActivity()).switchToPlaylistScreen(getActivity(), playlistId);
                    }
                }
            }
        });

        return rootView;
    }

}
