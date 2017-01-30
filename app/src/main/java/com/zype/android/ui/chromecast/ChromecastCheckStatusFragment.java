package com.zype.android.ui.chromecast;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import com.zype.android.R;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.video_details.fragments.video.MediaControlInterface;

public class ChromecastCheckStatusFragment extends BaseFragment implements MediaControlInterface {

    private static final String ARG_VIDEO_ID = "arg_video_id";

    private View mProgress;
    private View mPlay;

    private String mVideoID;

    private MediaInfo mLocalMediaInfo;
    private boolean mIsYoutube;
    private View mLabelYouTube;
    private View.OnClickListener mPlaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int position = (int) DataHelper.getPlayTime(getActivity().getContentResolver(), mVideoID);
                VideoCastManager.getInstance().loadMedia(mLocalMediaInfo, true, position);
                mPlay.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.VISIBLE);
            } catch (TransientNetworkDisconnectionException e) {
                e.printStackTrace();
            } catch (NoConnectionException e) {
                e.printStackTrace();
            }
        }
    };

    public static final ChromecastCheckStatusFragment newInstance(MediaInfo info, String videoID) {
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoID);
        if(info != null) {
            Bundle wrapperMediaInfo = Utils.mediaInfoToBundle(info);
            args.putBundle(VideoCastManager.EXTRA_MEDIA, wrapperMediaInfo);
        }
        ChromecastCheckStatusFragment fragment = new ChromecastCheckStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(VideoCastManager.EXTRA_MEDIA)) {
            Bundle wrapperMediaInfo = getArguments().getBundle(VideoCastManager.EXTRA_MEDIA);
            mLocalMediaInfo = Utils.bundleToMediaInfo(wrapperMediaInfo);
        }
        mVideoID = getArguments().getString(ARG_VIDEO_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chromecast_checck, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIsYoutube = ((BaseVideoActivity)getActivity()).isYoutube();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mProgress = view.findViewById(R.id.progress);
        mPlay = view.findViewById(R.id.play);
        mPlay.setOnClickListener(mPlaClickListener);
        mLabelYouTube = view.findViewById(R.id.label_youtube);
        updateUI();
    }

    public void updateUI() {
        if(mIsYoutube) {
            mPlay.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mLabelYouTube.setVisibility(View.VISIBLE);
        } else {
            if(mLocalMediaInfo == null) {
                mPlay.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
            } else {
                mPlay.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected String getFragmentName() {
        return "ChromecastCheckStatusFragment";
    }

    @Override
    public void seekToMillis(int ms) {
//        IGNORE
    }

    @Override
    public int getCurrentTimeStamp() {
        return 0;
    }

    @Override
    public void play() {
//        IGNORE
    }

    @Override
    public void stop() {
//        IGNORE
    }
}
