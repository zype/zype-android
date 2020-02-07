package com.zype.android.ui.player;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.Util;
import com.zype.android.BuildConfig;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.receiver.PhoneCallReceiver;
import com.zype.android.receiver.RemoteControlReceiver;
import com.zype.android.ui.Helpers.IPlaylistVideos;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.chromecast.LivePlayerActivity;
import com.zype.android.ui.dialog.ErrorDialogFragment;
import com.zype.android.ui.dialog.SubtitlesDialogFragment;
import com.zype.android.ui.video_details.VideoDetailActivity;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.ui.video_details.fragments.video.MediaControlInterface;
import com.zype.android.ui.video_details.fragments.video.OnVideoAudioListener;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.FileUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.AdMacrosHelper;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.model.player.AdvertisingSchedule;
import com.zype.android.webapi.model.player.Analytics;
import com.zype.android.webapi.model.player.AnalyticsDimensions;
import com.zype.android.webapi.model.video.Thumbnail;
import com.zype.android.webapi.model.video.VideoData;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class PlayerFragment extends BaseFragment implements
        CustomPlayer.Listener, AudioCapabilitiesReceiver.Listener, MediaControlInterface, Observer,
        AdEvent.AdEventListener, AdErrorEvent.AdErrorListener, CustomPlayer.CaptionListener,
        PlayerControlView.IPlayerControlListener {

    public static final int TYPE_AUDIO_LOCAL = 1;
    public static final int TYPE_AUDIO_WEB = 2;
    public static final int TYPE_VIDEO_LOCAL = 3;
    public static final int TYPE_VIDEO_WEB = 4;
    public static final int TYPE_AUDIO_LIVE = 5;
    public static final int TYPE_VIDEO_LIVE = 6;
    public static final int TYPE_VIDEO_EPG = 7;
    public static final int TYPE_VIDEO_TRAILER = 8;

    public static final String CONTENT_TYPE_TYPE = "content_type";
    public static final String CONTENT_URL = "content_url";
    public static final String CONTENT_ID_EXTRA = "content_id";
    public static final String PARAMETERS_AD_TAG = "AdTag";
    public static final String PARAMETERS_AUTOPLAY = "Autoplay";
    public static final String PARAMETERS_ON_AIR = "OnAir";

    private static final CookieManager defaultCookieManager;
    public static final int MEDIA_STOP_CODE = 115756;
    public static final String ACTION_STOP = "ACTION_STOP";

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    //    private MediaController mediaController;
    private PlayerControlView mediaController;
    private AspectRatioFrameLayout videoFrame;
    private CustomPlayer player;
    private MediaSessionCompat mediaSession;
    private SurfaceView surfaceView;
    private SubtitleLayout subtitleLayout;

    private boolean playerNeedsPrepare;

    private String contentUri;
    private int contentType;
    private List<Thumbnail> mThumbnailList;
    private String fileId;
    private String adTag;
    private boolean onAir;

    private OnVideoAudioListener mListener;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private ImageView thumbnailView;
    private View mainView;
    private boolean isNeedToSeekToLatestListenPosition = true;
    private BroadcastReceiver callReceiver;
    private AudioManager mAudioManager;
    private ComponentName mRemoteControlResponder;
    private boolean isReceiversRegistered = false;
    private boolean deleteFileBeforeExit = false;
    private boolean isControlsEnabled = true;

    private boolean autoplay = false;
    private boolean fullscreenSelected = false;

    //
    // IMA SDK
    //
    // Factory class for creating SDK objects.
    private ImaSdkFactory sdkFactory;
    // The AdsLoader instance exposes the requestAds method.
    private AdsLoader adsLoader;
    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager adsManager;
    // Whether an ad is displayed.
    private boolean isAdDisplayed;

    private List<AdvertisingSchedule> adSchedule;
    private int nextAdIndex = -1;
    private Runnable runnablePlaybackTime;

    // Analytics
    private Analytics analytics;

    // Limiting live stream
    private Handler handlerTimer;
    private Runnable runnableTimer;
    private Calendar liveStreamTimeStart;

    // Sensors
    PlayerViewModel playerViewModel;
    SensorViewModel sensorViewModel;
    VideoDetailViewModel videoDetailViewModel;

    public static PlayerFragment newInstance(int mediaType, String filePath, String fileId) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(CONTENT_TYPE_TYPE, mediaType);
        args.putString(CONTENT_URL, filePath);
        args.putString(CONTENT_ID_EXTRA, fileId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlayerFragment newInstance(int mediaType, String filePath, String adTag, boolean onAir, String fileId, boolean autoplay) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(CONTENT_TYPE_TYPE, mediaType);
        args.putString(CONTENT_URL, filePath);
        args.putString(PARAMETERS_AD_TAG, adTag);
        args.putBoolean(PARAMETERS_ON_AIR, onAir);
        args.putString(CONTENT_ID_EXTRA, fileId);
        args.putBoolean(PARAMETERS_AUTOPLAY, autoplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentUri = getArguments().getString(CONTENT_URL, "");
            Logger.i("url: " + contentUri);
            contentType = getArguments().getInt(CONTENT_TYPE_TYPE, BaseVideoActivity.TYPE_UNKNOWN);
            fileId = getArguments().getString(CONTENT_ID_EXTRA);
            if (!TextUtils.isEmpty(fileId)) {
                mThumbnailList = DataHelper.getThumbnailList(getActivity().getContentResolver(), fileId);
                adSchedule = VideoHelper.getAdSchedule(getActivity().getContentResolver(), fileId);
                analytics = VideoHelper.getAnalytics(getActivity().getContentResolver(), fileId);
            }
            autoplay = getArguments().getBoolean(PARAMETERS_AUTOPLAY);
            adTag = getArguments().getString(PARAMETERS_AD_TAG);
            onAir = getArguments().getBoolean(PARAMETERS_ON_AIR);
        }
        isNeedToSeekToLatestListenPosition = true && !autoplay;
        callReceiver = new CallReceiver();
        handlerTimer = new Handler();

        // Get saved closed captions state
        ccEnabled = SettingsProvider.getInstance().getBoolean(SettingsProvider.CLOSED_CAPTIONS_ENABLED);
        if (ccEnabled) {
            ccTrack = SettingsProvider.getInstance().getString(SettingsProvider.SELECTED_CLOSED_CAPTIONS_TRACK);
        }

        playerViewModel = ViewModelProviders.of(getActivity()).get(PlayerViewModel.class);

        // Listener to detect current playback time
        runnablePlaybackTime = new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    long currentPosition = player.getCurrentPosition();
                    if (!checkNextAd(currentPosition)) {
                        handlerTimer.postDelayed(this, 1000);
                    }
                }
                else {
                    Logger.d("runnablePlaybackTime: Player is not ready");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_custom_player, container, false);
        mainView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        mainView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                } else {
                    return mediaController.dispatchKeyEvent(event);
                }
            }
        });

        videoFrame = mainView.findViewById(R.id.video_frame);

        surfaceView = mainView.findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(surfaceCallback);

        subtitleLayout = mainView.findViewById(R.id.subtitles);

        thumbnailView = mainView.findViewById(R.id.thumbnailView);
        if (contentType == TYPE_AUDIO_WEB || contentType == TYPE_AUDIO_LOCAL) {
            thumbnailView.setVisibility(View.VISIBLE);
        } else if (contentType == TYPE_AUDIO_LIVE) {
            thumbnailView.setVisibility(View.VISIBLE);
            UiUtils.loadImage(getActivity().getApplicationContext(), SettingsProvider.getInstance().getOnAirPictureUrl(), thumbnailView);
        } else {
            thumbnailView.setVisibility(View.GONE);
        }

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }
        audioCapabilitiesReceiverRegister();
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
        // IMA SDK
        //
        // Create an AdsLoader.
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(this.getActivity());
        // Add listeners for when ads are loaded and for errors.
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                adsManager = adsManagerLoadedEvent.getAdsManager();
                // Attach event and error event listeners.
                adsManager.addAdErrorListener(PlayerFragment.this);
                adsManager.addAdEventListener(PlayerFragment.this);
                adsManager.init();
            }
        });

        sensorViewModel = ViewModelProviders.of(getActivity()).get(SensorViewModel.class);
        videoDetailViewModel = ViewModelProviders.of(getActivity()).get(VideoDetailViewModel.class);

        if (playerViewModel.isTrailer().getValue()) {
            ImageButton buttonCloseTrailer = getView().findViewById(R.id.buttonCloseTrailer);
            buttonCloseTrailer.setVisibility(View.VISIBLE);
            buttonCloseTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stop();
                    videoDetailViewModel.onVideoFinished(true);
                    playerViewModel.setTrailerVideoId(null);
                }
            });
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onScreenOrientationChanged();
    }

    private void onScreenOrientationChanged() {
        int rotate = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Logger.d("onScreenOrientationChanged(): rotate=" + rotate);
        boolean fullscreen = UiUtils.isLandscapeOrientation(getActivity());
        mListener.onFullscreenChanged(fullscreen);
        mediaController.updateFullscreenButton(fullscreen);
        hideControls();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d("onAttach()");
        try {
            mListener = (OnVideoAudioListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoAudioListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            Logger.d("onStart()");
        }
        hideNotification();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Logger.d("onResume()");
        }

        if (isHidden()) {
            return;
        }

        // Retrieve device id
        AdMacrosHelper.fetchDeviceId(getActivity(), new AdMacrosHelper.IDeviceIdListener() {
            @Override
            public void onDeviceId(String deviceId) {
                Logger.d("onDeviceId(): deviceId=" + deviceId);
            }
        });

        configureSubtitleView();

        mListener.onFullscreenChanged(UiUtils.isLandscapeOrientation(getActivity()));
        registerReceivers();
        if (player == null) {
            if (!TextUtils.isEmpty(fileId)) {
                analytics = VideoHelper.getAnalytics(getActivity().getContentResolver(), fileId);
            }
            preparePlayer(true);
        }
        else {
            if (playerViewModel.isBackgroundPlaybackEnabled()) {
                player.setBackgrounded(false);
            }

            if (!TextUtils.isEmpty(fileId)) {
                analytics = VideoHelper.getAnalytics(getActivity().getContentResolver(), fileId);
            }
            attachPlayerToAnalyticsManager();

            player.getPlayerControl().start();
            player.setSurface(surfaceView.getHolder().getSurface());
            if (onAir && (!SettingsProvider.getInstance().isLoggedIn() || SettingsProvider.getInstance().getSubscriptionCount() <= 0)) {
                startTimer();
            }
        }
        if (mThumbnailList != null) {
            if (mThumbnailList.size() > 0) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int position = getNearestPosition(width, mThumbnailList);
                if (position >= 0) {
                    UiUtils.loadImage(getActivity().getApplicationContext(), mThumbnailList.get(position).getUrl(), thumbnailView);
                }
                else {
                    UiUtils.loadImage(getActivity().getApplicationContext(), SettingsProvider.getInstance().getOnAirPictureUrl(), thumbnailView);
                }
            } else {
                UiUtils.loadImage(getActivity().getApplicationContext(), SettingsProvider.getInstance().getOnAirPictureUrl(), thumbnailView);
            }
        }
        // IMA SDK
        if (adsManager != null && isAdDisplayed) {
            adsManager.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            Logger.d("onPause()");
        }
        hideControls();
        if (player != null) {
            if (playerViewModel.isBackgroundPlaybackEnabled()) {
                player.setBackgrounded(true);
            }
            stopTimer();
        }
        // IMA SDK
        if (adsManager != null && isAdDisplayed) {
            adsManager.pause();
        }

        if (handlerTimer != null) {
            if (runnablePlaybackTime != null) {
                handlerTimer.removeCallbacks(runnablePlaybackTime);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) {
            Logger.d("onStop()");
        }
        if (playerViewModel.isBackgroundPlaybackEnabled()) {
            if (contentType == TYPE_AUDIO_LIVE || contentType == TYPE_VIDEO_LIVE) {
                showNotification(true, contentType);
            }
            else {
                showNotification(false, contentType);
            }
        }
        else {
            isNeedToSeekToLatestListenPosition= true;
            if (player != null) {
                player.removeListener(this);
            }
            stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy()");

        hideNotification();
        if (mediaController != null) {
            mediaController.hide();
            mediaController = null;
        }
        audioCapabilitiesReceiverUnregister();
        releasePlayer();
        if (callReceiver != null) {
            try {
                getActivity().unregisterReceiver(callReceiver);
            } catch (IllegalArgumentException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        if (mAudioManager != null)
            mAudioManager.unregisterMediaButtonEventReceiver(
                    mRemoteControlResponder);
        RemoteControlReceiver.getObservable().deleteObserver(this);
        isReceiversRegistered = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (BuildConfig.DEBUG) {
            Logger.d("onDetach()");
        }
        audioCapabilitiesReceiverUnregister();
        releasePlayer();
        mListener = null;
    }

    private void initMediaSession() {
//        ComponentName mediaButtonReceiver = new ComponentName(getContext().getApplicationContext(),
//                MediaButtonReceiver.class);
        ComponentName mediaButtonReceiver = new ComponentName(getContext().getApplicationContext(),
                RemoteControlReceiver.class);
        mediaSession = new MediaSessionCompat(getContext().getApplicationContext(), "TAG_MEDIA_SESSION",
                mediaButtonReceiver, null);

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(getActivity(), RemoteControlReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
            }

            @Override
            public void onPause() {
                super.onPause();
            }

            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                super.onPlayFromMediaId(mediaId, extras);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });

        MediaControllerCompat mediaController = new MediaControllerCompat(getActivity(), mediaSession);
        MediaControllerCompat.setMediaController(getActivity(), mediaController);
    }

    // //////////
    // Menu
    //
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.player, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem itemCC = menu.findItem(R.id.menuClosedCaptions);
//        if (ccEnabled) {
//            itemCC.setIcon(R.drawable.ic_closed_caption_black_24dp);
//            itemCC.setChecked(true);
//        }
//        else {
//            itemCC.setIcon(null);
//            itemCC.setChecked(false);
//        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menuClosedCaptions:
//                if (ccEnabled) {
//                    ccEnabled = false;
//                    SettingsProvider.getInstance().setBoolean(SettingsProvider.CLOSED_CAPTIONS_ENABLED, ccEnabled);
//                    updateClosedCaptionsTrack();
//                    getActivity().invalidateOptionsMenu();
//                }
//                else {
//                    showClosedCaptionsDialog();
//                }
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerReceivers() {
        if (!isReceiversRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//            filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
            getActivity().registerReceiver(callReceiver, filter);

            if (audioCapabilitiesReceiver == null) {
                audioCapabilitiesReceiverRegister();
            }
            if (mAudioManager == null || mRemoteControlResponder == null) {
                mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                mRemoteControlResponder = new ComponentName(getActivity().getPackageName(),
                        RemoteControlReceiver.class.getName());
            }
            mAudioManager.registerMediaButtonEventReceiver(
                    mRemoteControlResponder);
            RemoteControlReceiver.getObservable().addObserver(this);
            isReceiversRegistered = true;
        }
    }

    private void audioCapabilitiesReceiverRegister() {
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getContext(), this);
        audioCapabilitiesReceiver.register();
    }

    private void audioCapabilitiesReceiverUnregister() {
        if (audioCapabilitiesReceiver != null) {
            audioCapabilitiesReceiver.unregister();
            audioCapabilitiesReceiver = null;
        }
    }

    private int getNearestPosition(int width, List<Thumbnail> thumbnailList) {
        int position = -1;
        if (thumbnailList != null) {
            for (int i = 0; i < thumbnailList.size(); i++)
                if (Math.abs(width - position) > Math.abs(thumbnailList.get(i).getWidth() - width)) {
                    position = i;
                }
        }
        return position;
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    private CustomPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getContext(), WebApiManager.CUSTOM_HEADER_VALUE);
        switch (contentType) {
            case TYPE_VIDEO_EPG:
            case TYPE_VIDEO_WEB:
            case TYPE_VIDEO_LIVE:
            case TYPE_AUDIO_WEB:
            case TYPE_AUDIO_LIVE:
            case TYPE_VIDEO_TRAILER:
                if (contentUri != null &&
                        (contentUri.contains(".mp4") || contentUri.contains(".m4a") || contentUri.contains(".mp3"))) {
                    return new ExtractorRendererBuilder(getContext(), userAgent, Uri.parse(contentUri), new Mp4Extractor());
                }
                else {
                    return new HlsRendererBuilder(getContext(), userAgent, contentUri);
                }
            case TYPE_VIDEO_LOCAL:
            case TYPE_AUDIO_LOCAL:
                return new ExtractorRendererBuilder(getContext(), userAgent, Uri.parse(contentUri), new Mp4Extractor());
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (getActivity() == null) {
            return;
        }
        if (player == null) {
            player = new CustomPlayer(getActivity().getApplicationContext(), getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            playerNeedsPrepare = true;
            player.setInfoListener(playerViewModel);
//            mediaController = new MediaController(getContext());
            mediaController = new PlayerControlView(getContext());
            mediaController.setAnchorView(mainView);
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
            mediaController.setPlayerControlListener(this);

            initMediaSession();

            attachPlayerToAnalyticsManager();

            player.setInternalErrorListener(new CustomPlayer.InternalErrorListener() {
                @Override
                public void onRendererInitializationError(Exception e) {
                    Logger.e("onRendererInitializationError(): videoId=" + fileId, e);
                    if (!WebApiManager.isHaveActiveNetworkConnection(getActivity())) {
                        UiUtils.showErrorSnackbar(getView(), "Video is not available right now. " + getActivity().getString(R.string.connection_error));
                    }
                    else {
                        mListener.onError();
                    }
                    if (player.getPlaybackState() != ExoPlayer.STATE_ENDED
                            && playerViewModel.playbackPositionRestored()) {
                        mListener.saveCurrentTimeStamp(player.getCurrentPosition());
                        playerViewModel.savePlaybackPosition(player.getCurrentPosition());
                    }
                    player.getPlayerControl().pause();
                }

                @Override
                public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
                    Logger.e("onAudioTrackInitializationError", e);
                    Toast.makeText(getContext(), "onAudioTrackInitializationError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAudioTrackWriteError(AudioTrack.WriteException e) {
                    Logger.e("onAudioTrackWriteError", e);
                    Toast.makeText(getContext(), "onAudioTrackWriteError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
                    Logger.e("onDecoderInitializationError", e);
                    Toast.makeText(getContext(), "onDecoderInitializationError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCryptoError(MediaCodec.CryptoException e) {
                    Logger.e("onCryptoError", e);
                    Toast.makeText(getContext(), "onCryptoError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadError(int sourceId, IOException e) {
                    Logger.e("onLoadError(): videoId=" + fileId, e);
                    if (!WebApiManager.isHaveActiveNetworkConnection(getActivity())) {
                        UiUtils.showErrorSnackbar(getView(), "VideoList is not available right now. " + getActivity().getString(R.string.connection_error));
                    }
                    else {
                        mListener.onError();
                    }
                    releasePlayer();
                }

                @Override
                public void onDrmSessionManagerError(Exception e) {
                    Logger.e("onDrmSessionManagerError", e);
                    Toast.makeText(getContext(), "onDrmSessionManagerError", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        if (surfaceView == null) {
            surfaceView = (SurfaceView) mainView.findViewById(R.id.surface_view);
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);

        if (playWhenReady && adSchedule != null && !adSchedule.isEmpty() && !TextUtils.isEmpty(fileId)) {
            long playTime = DataHelper.getPlayTime(getActivity().getContentResolver(), fileId);
            nextAdIndex = seekAdByPosition(playTime);
            if (!checkNextAd(playTime)) {
                // Start playback time listener
                if (handlerTimer != null) {
                    if (runnablePlaybackTime != null) {
                        handlerTimer.removeCallbacks(runnablePlaybackTime);
                    }
                    handlerTimer.post(runnablePlaybackTime);
                }
            }
        }
    }

    private void releasePlayer() {
        AnalyticsManager manager = AnalyticsManager.getInstance();
        manager.trackStop();

        if (player != null) {
//            mediaController.hide();
            if (player.getPlaybackState() != ExoPlayer.STATE_ENDED
                    && playerViewModel.playbackPositionRestored()) {
                mListener.saveCurrentTimeStamp(player.getCurrentPosition());
                playerViewModel.savePlaybackPosition(player.getCurrentPosition());
            }

//            AnalyticsManager manager = AnalyticsManager.getInstance();
//            manager.trackStop();
//
            player.release();
            player = null;
            videoFrame = null;
            surfaceView.getHolder().removeCallback(surfaceCallback);
            surfaceView = null;
            System.gc();
        }
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_ENDED:
                AnalyticsManager manager = AnalyticsManager.getInstance();
                manager.trackStop();

                showControls();
                if (contentType == TYPE_VIDEO_LOCAL || contentType == TYPE_VIDEO_WEB || contentType == TYPE_VIDEO_EPG) {
                    if (playWhenReady) {
                        if (playerViewModel.isTrailer().getValue()) {
                            videoDetailViewModel.onVideoFinished(true);
                            playerViewModel.setTrailerVideoId(null);
                        }
                        else {
                            mListener.videoFinished();
                        }
                    }
                } else if (contentType == TYPE_AUDIO_LOCAL || contentType == TYPE_AUDIO_WEB) {
                    mListener.audioFinished();
                } else if (contentType == TYPE_AUDIO_LIVE || contentType == TYPE_VIDEO_LIVE) {
                    //IGNORE
                    Logger.d("onStateChanged(): ExoPlayer.STATE_ENDED start live");
                } else {
                    Logger.e("onStateChanged(): ExoPlayer.STATE_ENDED unknown type " + contentType);
                }
                if (SettingsProvider.getInstance().isUserPreferenceAutoRemoveWatchedContentSet()) {
                    deleteFileBeforeExit = true;
                }
                break;
            case ExoPlayer.STATE_READY:
                mediaSession.setActive(true);

                if (isNeedToSeekToLatestListenPosition && contentType != TYPE_VIDEO_EPG && !TextUtils.isEmpty(fileId)) {
                    long playerPosition = 0;
                    if (contentType != TYPE_AUDIO_LIVE && contentType != TYPE_VIDEO_LIVE) {
                        playerPosition = DataHelper.getPlayTime(getActivity().getContentResolver(), fileId);
                    }
                    player.seekTo(playerPosition);
                    isNeedToSeekToLatestListenPosition = false;
                }
                playerViewModel.onPlaybackPositionRestored();

                updateClosedCaptionsTrack();

                if (contentType == TYPE_VIDEO_LOCAL || contentType == TYPE_VIDEO_WEB || contentType == TYPE_VIDEO_EPG) {
                    Logger.d(String.format("onStateChanged(): ExoPlayer.STATE_READY: playWhenReady=%1$s", playWhenReady));
                    // Count play time of the live stream if user is not logged in and is not subscribed
                    if (onAir && (!SettingsProvider.getInstance().isLoggedIn() || SettingsProvider.getInstance().getSubscriptionCount() <= 0)) {
                        if (playWhenReady) {
                            startTimer();
                        } else {
                            stopTimer();
                        }
                    }
                    if (mListener != null) {
                        mListener.videoStarted();
                    }
                } else if (contentType == TYPE_AUDIO_LOCAL || contentType == TYPE_AUDIO_WEB) {
                    if (mListener != null) {
                        mListener.audioStarted();
                    }
                } else if (contentType == TYPE_AUDIO_LIVE || contentType == TYPE_VIDEO_LIVE) {
                    //IGNORE
                    Logger.d("onStateChanged(): ExoPlayer.STATE_READY ready live");
                } else {
                    Logger.e("onStateChanged(): ExoPlayer.STATE_READY unknown type " + contentType);
                }
                showControls();
                break;
            default:
                Logger.e("onStateChanged(): state=" + playbackState);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (deleteFileBeforeExit && !TextUtils.isEmpty(fileId)) {
            Logger.d("ExoPlayer.STATE_ENDED remove download content");
            if (contentType == TYPE_VIDEO_LOCAL) {
                FileUtils.deleteVideoFile(fileId, getActivity());
                DataHelper.setAudioDeleted(getActivity().getContentResolver(), fileId);
            } else if (contentType == TYPE_AUDIO_LOCAL) {
                FileUtils.deleteAudioFile(fileId, getActivity());
                DataHelper.setAudioDeleted(getActivity().getContentResolver(), fileId);
            }
        }
    }

    @Override
    public void onError(Exception e) {
        Logger.e("onError", e);
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(getContext(), stringId, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
        if (videoFrame != null) {
            videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
        }
    }

    // User controls

    private void toggleControlsVisibility() {
        if (mediaController != null) {
            if (mediaController.isShowing() || !isControlsEnabled) {
                hideControls();
            } else {
                showControls();
            }
        }
    }

    private void hideControls() {
        if (mediaController != null) {
            if (contentType == TYPE_VIDEO_LOCAL || contentType == TYPE_VIDEO_WEB || contentType == TYPE_VIDEO_LIVE || contentType == TYPE_VIDEO_EPG) {
                mediaController.hide();
            }
            else {
                mediaController.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaController != null) {
                            mediaController.show(0);
                        }
                    }
                });
            }
        }
    }

    private void showControls() {
        if (mediaController != null) {
            updateClosedCaptionsVisibility();
            if (contentType == TYPE_VIDEO_LOCAL || contentType == TYPE_VIDEO_WEB || contentType == TYPE_VIDEO_LIVE || contentType == TYPE_VIDEO_EPG) {
                if (isControlsEnabled) {
                    mediaController.show(5000);
                }
                else {
                    hideControls();
                }
            }
            else {
                mediaController.show(0);
            }
        }
    }

    Callback surfaceCallback = new Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (player != null) {
                player.setSurface(holder.getSurface());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (player != null) {
                player.blockingClearSurface();
            }
        }
    };

    @Override
    public void seekToMillis(int ms) {
        if (player != null) {
            player.seekTo(ms);
        }
    }

    @Override
    public int getCurrentTimeStamp() {
        if (player != null) {
            return (int) player.getCurrentPosition();
        } else {
            return -1;
        }
    }

    @Override
    public void play() {
        if (player != null) {
            attachPlayerToAnalyticsManager();
            player.getPlayerControl().start();
            mediaSession.setActive(true);
        }
    }

    @Override
    public void stop() {
        Logger.d("fragment stop");
        if (player != null) {
            player.getPlayerControl().pause();
            releasePlayer();
            mediaSession.setActive(false);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Logger.d("fragment remote action received code=" + data);
        int keycode = (int) data;
        switch (keycode) {
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    } else {
                        player.getPlayerControl().start();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    } else {
                        player.getPlayerControl().start();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    } else {
                        player.getPlayerControl().start();
                    }
                    if (player.getBackgrounded()) {
                        showNotification(false, contentType);
                    }
                }
                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    } else {
                        player.getPlayerControl().start();
                    }
                }
                break;
            case MEDIA_STOP_CODE: {
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    }
                    Logger.d("MEDIA_STOP_CODE");
                    hideNotification();
                }
            }
            break;
            default:
                break;
        }
    }

    class CallReceiver extends PhoneCallReceiver {

        @Override
        protected void onIncomingCallStarted(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onIncomingCallStarted");
            if (player != null)
                player.getPlayerControl().pause();
        }

        @Override
        protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onOutgoingCallStarted");
            if (player != null)
                player.getPlayerControl().pause();
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
            Logger.d("PlayerFragment call onIncomingCallEnded");
            if (player != null)
                player.getPlayerControl().start();
        }

        @Override
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
            Logger.d("PlayerFragment call onOutgoingCallEnded");
            if (player != null)
                player.getPlayerControl().start();
        }

        @Override
        protected void onMissedCall(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onMissedCall");
            if (player != null)
                player.getPlayerControl().start();
        }
    }

    public void showNotification(boolean isLive, int mediaType) {
        Logger.d("showNotification()");
        if (player == null || TextUtils.isEmpty(fileId)) {
            return;
        }

        VideoData video = VideoHelper.getVideo(getActivity().getContentResolver(), fileId);
        String title = "";
        if (video != null) {
            title = video.getTitle();
        } else {
            title = "Live";
        }
        Intent notificationIntent;
        Bundle bundle = new Bundle();
        bundle.putInt(BundleConstants.MEDIA_TYPE, mediaType);
        bundle.putString(BundleConstants.VIDEO_ID, fileId);
        if (isLive) {
            notificationIntent = new Intent(getActivity(), LivePlayerActivity.class);
            title = "Live";
        }
        else {
            notificationIntent = new Intent(getActivity(), VideoDetailActivity.class);
        }
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        PendingIntent intent = PendingIntent.getActivity(getActivity(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(),
                ZypeApp.NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(intent)
                .setContentTitle(getActivity().getString(R.string.app_name))
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_background_playback)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setOngoing(true)
                .setWhen(0);

//        Intent intentStop = new Intent();
//        intentStop.setAction(ACTION_STOP);
//        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(getActivity(), 12345, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.ic_stop_black_24px, "Stop", pendingIntentStop);
        if (player != null) {
            if (player.getPlayerControl().isPlaying()) {
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)));
            } else {
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)));
            }
        }
        builder.setStyle(new MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_STOP)));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(ZypeApp.NOTIFICATION_ID, builder.build());
    }

    public void hideNotification() {
        Logger.d("hideNotification()");
        if (getActivity() != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.cancel(ZypeApp.NOTIFICATION_ID);
        }
        else {
            Logger.d("hideNotification(): Activity is not exist");
        }
    }

    //
    // IMA SDK
    //
    private void requestAds(String adTagUrl) {
        AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(videoFrame);

        // Create the ads request.
        AdsRequest request = sdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (isAdDisplayed || player == null || player.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(player.getCurrentPosition(), player.getDuration());
            }
        });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        adsLoader.requestAds(request);
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Logger.i("Ad event: " + adEvent.getType());

        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {
            case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.
                adsManager.start();
                break;
            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                isAdDisplayed = true;
                hideControls();
                if (player != null) {
                    player.getPlayerControl().pause();
                }
                break;
            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                isAdDisplayed = false;
                // Update next ad to play
                updateNextAd();
                // Resume video
                isControlsEnabled = true;
                if (player != null) {
                    player.getPlayerControl().start();
                    showControls();
                }
                else {
                    preparePlayer(true);
                }
                break;
            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                adsLoader.contentComplete();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Logger.e("Ad error: " + adErrorEvent.getError().getMessage());
        updateNextAd();
        isControlsEnabled = true;
        if (player != null) {
            player.getPlayerControl().start();
        }
        else {
            preparePlayer(true);
        }
    }

//    private void showAd() {
//        if (SettingsProvider.getInstance().getSubscriptionCount() <= 0 || BuildConfig.DEBUG) {
//            // IMA SDK test tag
////        requestAds("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=");
//            // Test VAST 3
////            requestAds("https://s3.amazonaws.com/demo.jwplayer.com/advertising/assets/vast3_jw_ads.xml");
//            // Test VAST 2
////        requestAds("http://loopme.me/api/vast/ads?appId=e18c19fa43&vast=2&campid=6029");
//            Logger.d("adTag=" + adTag);
//            requestAds(adTag);
//        }
//    }

    private int seekAdByPosition(long position) {
        for (int i = 0; i < adSchedule.size(); i++) {
            if (((int) position / 1000) * 1000 <= adSchedule.get(i).getOffset()) {
                Logger.d("seekAdByPosition(): Next ad index: " + i + ", position=" + position);
                return i;
            }
        }
        Logger.d("seekAdByPosition(): No ads to play, position=" + position);
        return -1;
    }

    private void updateNextAd() {
        if (nextAdIndex + 1 < adSchedule.size()) {
            nextAdIndex += 1;
            if (handlerTimer != null && runnablePlaybackTime != null) {
                handlerTimer.postDelayed(runnablePlaybackTime, 1000);
            }
        }
        else {
            nextAdIndex = -1;
        }
    }

    private boolean checkNextAd(long position) {
        if (nextAdIndex >= 0) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0 || BuildConfig.DEBUG) {
                Logger.d("checkNextAd(): next ad " + adSchedule.get(nextAdIndex).getOffset()
                        +", current position " + position);
                if (position >= adSchedule.get(nextAdIndex).getOffset()) {
                    // Disable media controls and pause the video
                    isControlsEnabled = false;
                    if (player != null) {
                        player.getPlayerControl().pause();
                    }
                    // Request the ad
                    Logger.d("checkNextAd(): Requesting ad");
                    String adTag = AdMacrosHelper.updateAdTagParameters(getActivity(), adSchedule.get(nextAdIndex).getTag());
                    Logger.d("Ad tag with macros: " + adTag);
                    requestAds(adTag);
                    // TODO: Show progress while the ad is loading
                    return true;
                }
            }
        }
        return false;
    }

    //
    // Limiting live stream
    //
    private void startTimer() {
        // Live stream limit is 0 means limit live stream feature is turned off
        if (SettingsProvider.getInstance().getLiveStreamLimit() == 0) return;

        if (handlerTimer != null) {
            runnableTimer = new Runnable() {
                @Override
                public void run() {
                    stopTimer();
                    if (isLiveStreamLimitHit()) {
                        Logger.d("startTimer(): Live stream limit has been hit");
                        player.getPlayerControl().pause();
                        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(SettingsProvider.getInstance().getLiveStreamMessage(), null, null);
                        dialog.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.TAG);
                    }
                    else {
                        handlerTimer.postDelayed(runnableTimer, 1000);
                    }
                }
            };
            liveStreamTimeStart = Calendar.getInstance();
            handlerTimer.postDelayed(runnableTimer, 1000);
        }
    }

    private void stopTimer() {
        if (handlerTimer != null && runnableTimer != null) {
            handlerTimer.removeCallbacks(runnableTimer);
        }
        if (liveStreamTimeStart != null) {
            addLiveStreamPlayTime();
        }
    }

    private void addLiveStreamPlayTime() {
        Calendar currentTime = Calendar.getInstance();
        int liveStreamTime = SettingsProvider.getInstance().getLiveStreamTime();
        liveStreamTime += (int) (currentTime.getTimeInMillis() - liveStreamTimeStart.getTimeInMillis()) / 1000;
        SettingsProvider.getInstance().saveLiveStreamTime(liveStreamTime);
        liveStreamTimeStart.setTime(currentTime.getTime());
        Logger.d(String.format("addLiveStreamPlayTime(): liveStreamTime=%1$s", liveStreamTime));
    }

    private boolean isLiveStreamLimitHit() {
        return SettingsProvider.getInstance().getLiveStreamTime() >= SettingsProvider.getInstance().getLiveStreamLimit();
    }

    //
    //
    @Override
    protected String getFragmentName() {
        return "Player";
    }

    //
    // Analytics
    //
    private void attachPlayerToAnalyticsManager(){
        if (player != null && analytics != null){
            VideoData video = VideoHelper.getVideo(getActivity().getContentResolver(), fileId);

            if(video == null) {
                return;
            }

            AnalyticsDimensions dimensions = analytics.getDimensions();

            Context context = getActivity().getApplicationContext();
            String beacon = analytics.getBeacon();
            String videoUrl = video.getPlayerVideoUrl();

            Map<String, String> customDimensions = getCustomDimensions(video, dimensions);

            AnalyticsManager manager = AnalyticsManager.getInstance();
            manager.trackPlay(context, player, beacon, videoUrl, customDimensions);
        }
    }

    private Map<String, String> getCustomDimensions(VideoData video, AnalyticsDimensions dimensions) {
        Map<String, String> customDimensions = new HashMap<String, String>();

        String videoId, playerId, siteId, device, title, consumerId;

        if ((videoId = dimensions.getVideoId()) != null) { customDimensions.put("videoId", videoId); }
        if ((playerId = dimensions.getPlayerId()) != null) { customDimensions.put("playerId", playerId); }
        if ((siteId = dimensions.getSiteId()) != null) { customDimensions.put("siteId", siteId); }
        if ((device = dimensions.getDevice()) != null) { customDimensions.put("device", device); }
        if ((title = video.getTitle()) != null) { customDimensions.put("title", title); }
        if ((consumerId = SettingsProvider.getInstance().getConsumerId()) != null) { customDimensions.put("consumerId", consumerId); }

        return customDimensions;
    }

    //
    // 'PlayerControlView.IPlayerControlListener' implementation
    //
    @Override
    public void onNext() {
        ((IPlaylistVideos) getActivity()).onNext();
    }

    @Override
    public void onPrevious() {
        ((IPlaylistVideos) getActivity()).onPrevious();
    }

    @Override
    public void onClosedCaptions() {
        showClosedCaptionsDialog();
    }

    public void onFullscreen() {
        boolean fullscreen = UiUtils.isLandscapeOrientation(getActivity());
        if (fullscreen) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            listenForDeviceRotation(Configuration.ORIENTATION_PORTRAIT);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            listenForDeviceRotation(Configuration.ORIENTATION_LANDSCAPE);
        }
        mediaController.updateFullscreenButton(!fullscreen);
        fullscreenSelected = !fullscreenSelected;
    }

    //
    // Closed captions
    //
    private boolean ccEnabled;
    private String ccTrack;

    private void updateClosedCaptionsVisibility() {
        if (player != null) {
            if (player.getTrackCount(CustomPlayer.TYPE_TEXT) > 0) {
                mediaController.showCC();
            } else {
                mediaController.hideCC();
            }
        }
    }

    // 'CaptionListener' implementation
    @Override
    public void onCues(List<Cue> cues) {
        subtitleLayout.setCues(cues);
    }

    private void configureSubtitleView() {
        CaptionStyleCompat style;
        float fontScale;
        if (Util.SDK_INT >= 19) {
            style = getUserCaptionStyleV19();
            fontScale = getUserCaptionFontScaleV19();
        }
        else {
            style = CaptionStyleCompat.DEFAULT;
            fontScale = 1.0f;
        }
        subtitleLayout.setStyle(style);
        subtitleLayout.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager = (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager = (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }

    private void updateClosedCaptionsTrack() {
        if (player != null) {
            if (ccEnabled) {
                player.setSelectedTrack(CustomPlayer.TYPE_TEXT, getClosedCaptionsTrackIndex(ccTrack));
            }
            else {
                player.setSelectedTrack(CustomPlayer.TYPE_TEXT, CustomPlayer.TRACK_DISABLED);
            }
        }
    }

    private int getClosedCaptionsTrackIndex(String track) {
        int result = -1;
        if (player != null) {
            int type = CustomPlayer.TYPE_TEXT;
            if (TextUtils.isEmpty(track)) {
                if (player.getTrackCount(type) > 0) {
                    result = 0;
                }
            }
            else {
                for (int i = 0; i < player.getTrackCount(type); i++) {
                    MediaFormat mediaFormat = player.getTrackFormat(type, i);
                    if (track.equalsIgnoreCase(mediaFormat.trackId)) {
                        result = i;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void showClosedCaptionsDialog() {
        if (player == null) {
            return;
        }
        // Get CC tracks
        final List<CharSequence> tracks = new ArrayList<>();
        for (int i = 0; i < player.getTrackCount(CustomPlayer.TYPE_TEXT); i++) {
            MediaFormat mediaFormat = player.getTrackFormat(CustomPlayer.TYPE_TEXT, i);
            tracks.add(mediaFormat.trackId);
        }
        tracks.add(getString(R.string.subtitles_off));
        int selectedIndex;
        if (ccEnabled) {
            selectedIndex = getClosedCaptionsTrackIndex(SettingsProvider.getInstance().getString(SettingsProvider.SELECTED_CLOSED_CAPTIONS_TRACK));
        }
        else {
            selectedIndex = tracks.size() - 1;
        }

        // Show selection dialog
        SubtitlesDialogFragment.createAndShowSubtitlesDialogFragment(getActivity(),
                "Select track",
                tracks.toArray(new CharSequence[tracks.size()]),
                selectedIndex,
                new SubtitlesDialogFragment.ISubtitlesDialogListener() {
                    @Override
                    public void onItemSelected(SubtitlesDialogFragment dialog, int selectedItem) {
                        if (selectedItem == tracks.size() - 1) {
                            ccEnabled = false;
                            ccTrack = "";
                        }
                        else {
                            ccEnabled = true;
                            ccTrack = tracks.get(selectedItem).toString();
                        }
                        SettingsProvider.getInstance().setBoolean(SettingsProvider.CLOSED_CAPTIONS_ENABLED, ccEnabled);
                        SettingsProvider.getInstance().setString(SettingsProvider.SELECTED_CLOSED_CAPTIONS_TRACK, ccTrack);
                        updateClosedCaptionsTrack();
                        getActivity().invalidateOptionsMenu();
                        dialog.dismiss();
                    }
                });
    }

    //
    // Sensors
    //
    private void listenForDeviceRotation(final int requiredOrientation) {
        sensorViewModel.getOrientation().observe(this, new android.arch.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer orientation) {
                Logger.d("listenForDeviceRotation(): orientation=" + orientation);
                if (orientation == requiredOrientation) {
                    sensorViewModel.stopListeningOrientation();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        });
    }
}
