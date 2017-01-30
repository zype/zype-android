package com.zype.android.ui.chromecast;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.player.OnVideoCastControllerListener;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastController;
import com.google.android.libraries.cast.companionlibrary.cast.tracks.ui.TracksChooserDialog;
import com.google.android.libraries.cast.companionlibrary.utils.LogUtils;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import com.zype.android.R;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.video_details.fragments.video.MediaControlInterface;
import com.zype.android.ui.video_details.fragments.video.OnVideoAudioListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.google.android.libraries.cast.companionlibrary.utils.LogUtils.LOGD;
import static com.google.android.libraries.cast.companionlibrary.utils.LogUtils.LOGE;


public class ChromecastFragment extends BaseFragment
        implements MediaControlInterface, VideoCastController {

    public static final String TASK_TAG = "task";
    public static final String DIALOG_TAG = "dialog";
    private static final String ARG_VIDEO_ID = "arg_video_id";
    private static final String TAG = LogUtils
            .makeLogTag(ChromecastFragment.class);
    private VideoCastManager mCastManager;
    private View mPageView;
    private ImageButton mPlayPause;
    private TextView mLiveText;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;
    private TextView mLine2;
    private ProgressBar mLoading;
    private double mVolumeIncrement;
    private View mControllers;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private Drawable mStopDrawable;
    private OnVideoCastControllerListener mControllerListener;
    private int mStreamType;
    //    private ImageButton mClosedCaptionIcon;
//    private ImageButton mSkipNext;
//    private ImageButton mSkipPrevious;
    private View mPlaybackControls;
    private MiniController mMini;
    private int mNextPreviousVisibilityPolicy
            = VideoCastController.NEXT_PREV_VISIBILITY_POLICY_DISABLED;

    private int mCurrentPosition;

    private String mVideoID;
    private OnVideoAudioListener mListener;


    public static ChromecastFragment newInstance(MediaInfo mediaInfo, String videoID) {
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoID);
        if (mediaInfo != null) {
            Bundle wrapperMediaInfo = Utils.mediaInfoToBundle(mediaInfo);
            args.putBundle(VideoCastManager.EXTRA_MEDIA, wrapperMediaInfo);
        }
        ChromecastFragment fragment = new ChromecastFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCastManager = VideoCastManager.getInstance();
        super.onCreate(savedInstanceState);
        mVolumeIncrement = mCastManager.getVolumeStep();
        mVideoID = getArguments().getString(ARG_VIDEO_ID);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle extras = getArguments();
        if (extras == null) {
            throw new IllegalStateException("extras == null");
        }

        FragmentManager fm = getFragmentManager();
        VideoCastControllerFragment videoCastControllerFragment
                = (VideoCastControllerFragment) fm.findFragmentByTag(
                TASK_TAG);

        if (videoCastControllerFragment != null) {
            fm.beginTransaction().remove(videoCastControllerFragment).commit();
        }
        videoCastControllerFragment = VideoCastControllerFragment.newInstance(extras);
        fm.beginTransaction().add(videoCastControllerFragment, TASK_TAG).commit();
        mControllerListener = videoCastControllerFragment;

        try {
            mListener = (OnVideoAudioListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoAudioListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chromecast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadAndSetupViews(view);
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_chromecast);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCastManager.addMiniController(mMini);
    }

    @Override
    public void onPause() {
        mCastManager.removeMiniController(mMini);
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mControllerListener = null;
    }

    @Override
    public void seekToMillis(int ms) {
        if (mCastManager != null) {
            try {
                mCastManager.seek(ms);
            } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCurrentTimeStamp() {
        if (mCastManager != null) {
            try {
                mCurrentPosition = (int) mCastManager.getCurrentMediaPosition();
            } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
                e.printStackTrace();
            }
        }
        return mCurrentPosition;
    }

    @Override
    public void play() {

    }

    @Override
    public void stop() {

    }

    private void loadAndSetupViews(View v) {
        mPauseDrawable = getResources().getDrawable(com.google.android.libraries.cast.companionlibrary.R.drawable.ic_pause_circle_white_80dp);
        mPlayDrawable = getResources().getDrawable(com.google.android.libraries.cast.companionlibrary.R.drawable.ic_play_circle_white_80dp);
        mStopDrawable = getResources().getDrawable(com.google.android.libraries.cast.companionlibrary.R.drawable.ic_stop_circle_white_80dp);
        mPageView = v.findViewById(R.id.pageview);
        mPlayPause = (ImageButton) v.findViewById(R.id.play_pause_toggle);
        mLiveText = (TextView) v.findViewById(R.id.live_text);
        mStart = (TextView) v.findViewById(R.id.start_text);
        mEnd = (TextView) v.findViewById(R.id.end_text);
        mSeekbar = (SeekBar) v.findViewById(R.id.seekbar);
        mLine2 = (TextView) v.findViewById(R.id.textview2);
        mLoading = (ProgressBar) v.findViewById(R.id.progressbar1);
        mControllers = v.findViewById(R.id.controllers);
//        mClosedCaptionIcon = (ImageButton) v.findViewById(R.id.cc);
//        mSkipNext = (ImageButton) v.findViewById(R.id.next);
//        mSkipPrevious = (ImageButton) v.findViewById(R.id.previous);
        mPlaybackControls = v.findViewById(R.id.playback_controls);
        mMini = (MiniController) v.findViewById(R.id.miniController1);
        mMini.setCurrentVisibility(false);
        setClosedCaptionState(CC_DISABLED);
        mPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    mControllerListener.onPlayPauseClicked(v);
                } catch (TransientNetworkDisconnectionException e) {
                    LOGE(TAG, "Failed to toggle playback due to temporary network issue", e);
                    Utils.showToast(getActivity(),
                            com.google.android.libraries.cast.companionlibrary.R.string.ccl_failed_no_connection_trans);
                } catch (NoConnectionException e) {
                    LOGE(TAG, "Failed to toggle playback due to network issues", e);
                    Utils.showToast(getActivity(),
                            com.google.android.libraries.cast.companionlibrary.R.string.ccl_failed_no_connection);
                } catch (Exception e) {
                    LOGE(TAG, "Failed to toggle playback due to other issues", e);
                    Utils.showToast(getActivity(),
                            com.google.android.libraries.cast.companionlibrary.R.string.ccl_failed_perform_action);
                }
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (mControllerListener != null) {
                        mControllerListener.onStopTrackingTouch(seekBar);
                    }
                } catch (Exception e) {
                    LOGE(TAG, "Failed to complete seek", e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    if (mControllerListener != null) {
                        mControllerListener.onStartTrackingTouch(seekBar);
                    }
                } catch (Exception e) {
                    LOGE(TAG, "Failed to start seek", e);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mStart.setText(Utils.formatMillis(progress));
                try {
                    if (mControllerListener != null) {
                        mControllerListener.onProgressChanged(seekBar, progress, fromUser);
                    }
                } catch (Exception e) {
                    LOGE(TAG, "Failed to set the progress result", e);
                }
            }
        });

//        mClosedCaptionIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    showTracksChooserDialog();
//                } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
//                    LOGE(TAG, "Failed to get the media", e);
//                }
//            }
//        });

//        mSkipNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    mListener.onSkipNextClicked(v);
//                } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
//                    LOGE(TAG, "Failed to move to the next item in the queue", e);
//                }
//            }
//        });
//
//        mSkipPrevious.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    mListener.onSkipPreviousClicked(v);
//                } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
//                    LOGE(TAG, "Failed to move to the previous item in the queue", e);
//                }
//            }
//        });
    }

    private void showTracksChooserDialog()
            throws TransientNetworkDisconnectionException, NoConnectionException {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            transaction.remove(prev);
        }
        transaction.addToBackStack(null);

        // Create and show the dialog.
        TracksChooserDialog dialogFragment = TracksChooserDialog
                .newInstance(mCastManager.getRemoteMediaInformation());
        dialogFragment.show(transaction, DIALOG_TAG);
    }


    @Override
    public void showLoading(boolean visible) {
        mLoading.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void adjustControllersForLiveStream(boolean isLive) {
        int visibility = isLive ? View.INVISIBLE : View.VISIBLE;
        mLiveText.setVisibility(isLive ? View.VISIBLE : View.INVISIBLE);
        mStart.setVisibility(visibility);
        mEnd.setVisibility(visibility);
        mSeekbar.setVisibility(visibility);
    }

    @Override
    public void setClosedCaptionState(int status) {
        switch (status) {
//            case CC_ENABLED:
//                mClosedCaptionIcon.setVisibility(View.VISIBLE);
//                mClosedCaptionIcon.setEnabled(true);
//                break;
//            case CC_DISABLED:
//                mClosedCaptionIcon.setVisibility(View.VISIBLE);
//                mClosedCaptionIcon.setEnabled(false);
//                break;
//            case CC_HIDDEN:
//                mClosedCaptionIcon.setVisibility(View.GONE);
//                break;
//            default:
//                LOGE(TAG, "setClosedCaptionState(): Invalid state requested: " + status);
        }
    }

    @Override
    public void onQueueItemsUpdated(int queueLength, int position) {
        boolean prevAvailable = position > 0;
        boolean nextAvailable = position < queueLength - 1;
        switch (mNextPreviousVisibilityPolicy) {
            case VideoCastController.NEXT_PREV_VISIBILITY_POLICY_HIDDEN:
//                if (nextAvailable) {
//                    mSkipNext.setVisibility(View.VISIBLE);
//                    mSkipNext.setEnabled(true);
//                } else {
//                    mSkipNext.setVisibility(View.INVISIBLE);
//                }
//                if (prevAvailable) {
//                    mSkipPrevious.setVisibility(View.VISIBLE);
//                    mSkipPrevious.setEnabled(true);
//                } else {
//                    mSkipPrevious.setVisibility(View.INVISIBLE);
//                }
//                break;
//            case VideoCastController.NEXT_PREV_VISIBILITY_POLICY_ALWAYS:
//                mSkipNext.setVisibility(View.VISIBLE);
//                mSkipNext.setEnabled(true);
//                mSkipPrevious.setVisibility(View.VISIBLE);
//                mSkipPrevious.setEnabled(true);
//                break;
//            case VideoCastController.NEXT_PREV_VISIBILITY_POLICY_DISABLED:
//                if (nextAvailable) {
//                    mSkipNext.setVisibility(View.VISIBLE);
//                    mSkipNext.setEnabled(true);
//                } else {
//                    mSkipNext.setVisibility(View.VISIBLE);
//                    mSkipNext.setEnabled(false);
//                }
//                if (prevAvailable) {
//                    mSkipPrevious.setVisibility(View.VISIBLE);
//                    mSkipPrevious.setEnabled(true);
//                } else {
//                    mSkipPrevious.setVisibility(View.VISIBLE);
//                    mSkipPrevious.setEnabled(false);
//                }
                break;
            default:
                LOGE(TAG, "onQueueItemsUpdated(): Invalid NextPreviousPolicy has been set");
        }
    }

    @Override
    public void setPlaybackStatus(int state) {
        if (isVisible()) {
            LOGD(TAG, "setPlaybackStatus(): state = " + state);
            switch (state) {
                case MediaStatus.PLAYER_STATE_PLAYING:
                    mLoading.setVisibility(View.INVISIBLE);
                    mSeekbar.setEnabled(true);
                    mPlaybackControls.setVisibility(View.VISIBLE);
                    if (mStreamType == MediaInfo.STREAM_TYPE_LIVE) {
                        mPlayPause.setImageDrawable(mStopDrawable);
                    } else {
                        mPlayPause.setImageDrawable(mPauseDrawable);
                    }

                    mLine2.setText(getString(com.google.android.libraries.cast.companionlibrary.R.string.ccl_casting_to_device,
                            mCastManager.getDeviceName()));
                    mControllers.setVisibility(View.VISIBLE);
                    break;
                case MediaStatus.PLAYER_STATE_PAUSED:
                    mControllers.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.INVISIBLE);
                    mSeekbar.setEnabled(true);
                    mPlaybackControls.setVisibility(View.VISIBLE);
                    mPlayPause.setImageDrawable(mPlayDrawable);
                    mLine2.setText(getString(com.google.android.libraries.cast.companionlibrary.R.string.ccl_casting_to_device,
                            mCastManager.getDeviceName()));
                    break;
                case MediaStatus.PLAYER_STATE_IDLE:
                    mLoading.setVisibility(View.INVISIBLE);
                    mSeekbar.setEnabled(true);
                    mPlayPause.setImageDrawable(mPlayDrawable);
                    mPlaybackControls.setVisibility(View.VISIBLE);
                    mLine2.setText(getString(com.google.android.libraries.cast.companionlibrary.R.string.ccl_casting_to_device,
                            mCastManager.getDeviceName()));
                    break;
                case MediaStatus.PLAYER_STATE_BUFFERING:
                    mPlaybackControls.setVisibility(View.INVISIBLE);
                    mLoading.setVisibility(View.VISIBLE);
                    mSeekbar.setEnabled(false);
                    mLine2.setText(getString(com.google.android.libraries.cast.companionlibrary.R.string.ccl_loading));
                    break;
                default:
            }
        }
    }

    @Override
    public void updateSeekbar(int position, int duration) {
        mSeekbar.setProgress(position);
        mSeekbar.setMax(duration);
        mStart.setText(Utils.formatMillis(position));
        mEnd.setText(Utils.formatMillis(duration));
        if (getActivity() != null) {
            mListener.saveCurrentTimeStamp(position);
//            DataHelper.setPlayTime(getActivity().getContentResolver(), mVideoID, position);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setImage(Bitmap bitmap) {
        if (bitmap != null) {
            if (mPageView instanceof ImageView) {
                ((ImageView) mPageView).setImageBitmap(bitmap);
            } else {
                mPageView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    @Override
    public void setTitle(String text) {
    }

    @Override
    public void setSubTitle(String text) {
        mLine2.setText(text);
    }

    @Override
    public void setOnVideoCastControllerChangedListener(OnVideoCastControllerListener listener) {
        if (listener != null) {
            //mListener = listener;
        }
    }

    @Override
    public void setStreamType(int streamType) {
        this.mStreamType = streamType;
    }

    @Override
    public void updateControllersStatus(boolean enabled) {
        mControllers.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        if (enabled) {
            adjustControllersForLiveStream(mStreamType == MediaInfo.STREAM_TYPE_LIVE);
        }
    }

    @Override
    public void closeActivity() {

    }

    @Override // from VideoCastController
    public void setNextPreviousVisibilityPolicy(int policy) {
        mNextPreviousVisibilityPolicy = policy;
    }

}
