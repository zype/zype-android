package com.zype.android.ui.video_details.fragments.video;


import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.receiver.RemoteControlReceiver;
import com.zype.android.utils.GoogleKeyUtils;
import com.zype.android.utils.Logger;

import java.util.Observable;
import java.util.Observer;

public class YouTubeFragment extends YouTubePlayerSupportFragment implements MediaControlInterface, Observer {

    private static final String ARG_VIDEO_ID = "ARG_VIDEO_ID";
    private String youtubeId = "youtube_id";
    private YouTubePlayer activePlayer;
    private OnVideoAudioListener mListener;
    private String fileId;
    private long playerPosition;
    private AudioManager mAudioManager;
    private ComponentName mRemoteControlResponder;

    public static YouTubeFragment newInstance(String videoId) {

        YouTubeFragment playerYouTubeFrag = new YouTubeFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_VIDEO_ID, videoId);
        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            fileId = getArguments().getString(ARG_VIDEO_ID);
            youtubeId = DataHelper.getYoutubeLink(getActivity().getContentResolver(), fileId);
            playerPosition = DataHelper.getPlayTime(getActivity().getContentResolver(), fileId);
        } else {
            throw new IllegalStateException("youtubeId can not be empty");
        }
        init(getActivity());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onScreenOrientationChanged();
    }

    private void onScreenOrientationChanged() {
        mListener.onFullscreenChanged();
    }

    public void init(Context context) {

        String googleApiKey = GoogleKeyUtils.getGoogleApiKey(context);
        if (googleApiKey == null) {
            throw new NullPointerException("Google API key must not be null. Set your api key as meta data in AndroidManifest.xml file.");
        }
        initialize(googleApiKey, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                activePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                activePlayer.setShowFullscreenButton(false);
                if (!wasRestored) {
                    activePlayer.loadVideo(youtubeId);
                    activePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {

                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {
                            if (mListener != null) {
                                mListener.videoStarted();
                                activePlayer.seekToMillis((int) playerPosition);
                            }
                        }

                        @Override
                        public void onVideoEnded() {
                            if (mListener != null) {
                                mListener.videoFinished();
                            }
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            Logger.e(errorReason.toString());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnVideoAudioListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoAudioListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (activePlayer != null) {
            activePlayer.release();
        }
        mListener = null;
    }

    @Override
    public void seekToMillis(int ms) {
        activePlayer.seekToMillis(ms);
    }

    @Override
    public int getCurrentTimeStamp() {
        if (activePlayer != null) {
            return activePlayer.getCurrentTimeMillis();
        } else {
            return -1;
        }
    }

    @Override
    public void play() {
        activePlayer.play();
    }

    @Override
    public void stop() {
        if (activePlayer != null) {
            activePlayer.pause();
        }
        activePlayer = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAudioManager == null || mRemoteControlResponder == null) {
            mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mRemoteControlResponder = new ComponentName(getActivity().getPackageName(),
                    RemoteControlReceiver.class.getName());
        }
        mAudioManager.registerMediaButtonEventReceiver(
                mRemoteControlResponder);
        RemoteControlReceiver.getObservable().addObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activePlayer != null) {
            mListener.saveCurrentTimeStamp(activePlayer.getCurrentTimeMillis());
        } else {
            mListener.saveCurrentTimeStamp(-1);
        }
        if (mAudioManager != null)
            mAudioManager.unregisterMediaButtonEventReceiver(
                    mRemoteControlResponder);
        RemoteControlReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        Logger.d("fragment remote action received code=" + data);
        int keycode = (int) data;
        switch (keycode) {
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (activePlayer != null) {
                    if (activePlayer.isPlaying()) {
                        activePlayer.pause();
                    } else {
                        activePlayer.play();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (activePlayer != null) {
                    if (activePlayer.isPlaying()) {
                        activePlayer.pause();
                    } else {
                        activePlayer.play();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (activePlayer != null) {
                    if (activePlayer.isPlaying()) {
                        activePlayer.pause();
                    } else {
                        activePlayer.play();
                    }
                }
                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (activePlayer != null) {
                    if (activePlayer.isPlaying()) {
                        activePlayer.pause();
                    } else {
                        activePlayer.play();
                    }
                }
                break;
            default:
                break;
        }
    }
}
