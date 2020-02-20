package com.zype.android.ui.player.v2;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.zype.android.BuildConfig;
import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.receiver.PhoneCallReceiver;
import com.zype.android.receiver.RemoteControlReceiver;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.dialog.ErrorDialogFragment;
import com.zype.android.ui.player.PlayerViewModel;
import com.zype.android.ui.player.SensorViewModel;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.ui.video_details.v2.VideoDetailActivity;
import com.zype.android.utils.AdMacrosHelper;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Thumbnail;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PlayerFragment extends Fragment implements  AdEvent.AdEventListener,
        AdErrorEvent.AdErrorListener, AudioCapabilitiesReceiver.Listener, java.util.Observer {
    public static final String TAG = PlayerFragment.class.getSimpleName();

    private static final String ARG_VIDEO_ID = "VideoId";

    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;

    private MediaSessionCompat mediaSession;

    private PlayerViewModel playerViewModel;
    private VideoDetailViewModel videoViewModel;
    private SensorViewModel sensorViewModel;
    private Thumbnail thumbnail;

    private Observer<String> playerUrlObserver;
    private Observer<PlayerViewModel.PlayerMode> playerModeObserver;
    private Observer<PlayerViewModel.Error> playerErrorObserver;

    private PlayerView playerView;
    private ImageView imageThumbnail;
    private ImageButton buttonFullscreen;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    private ImageButton buttonSubtitles;
    private TextView textPosition;
    private TextView textPositionLive;
    private TextView textDuration;
    private TextView textDurationLive;
    private DefaultTimeBar viewTimeBar;

    private Handler handlerTimer;

    private boolean isReceiversRegistered = false;
    private AudioManager audioManager;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private BroadcastReceiver callReceiver;
    private ComponentName remoteControlResponder;

    // IMA SDK

    // Factory class for creating SDK objects.
    private ImaSdkFactory sdkFactory;
    // The AdsLoader instance exposes the requestAds method.
    private AdsLoader adsLoader;
    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager adsManager;
    // Whether an ad is displayed.
    private boolean isAdDisplayed;

    private int nextAdIndex = -1;
    private Runnable runnablePlaybackTime;

    // Limiting live stream
    private Runnable runnableTimer;
    private Calendar liveStreamTimeStart;


    public PlayerFragment() {}

    public static PlayerFragment newInstance(String videoId) {
        Logger.d("newInstance()");
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlerTimer = new Handler();

        callReceiver = new CallReceiver();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        playerView = rootView.findViewById(R.id.player_view);
        imageThumbnail = rootView.findViewById(R.id.imageThumbnail);

        buttonFullscreen = playerView.findViewById(R.id.exo_fullscreen);
        buttonFullscreen.setOnClickListener(view -> {
            boolean fullscreen = UiUtils.isLandscapeOrientation(getActivity());
            if (fullscreen) {
                setPortraitOrientation();
            }
            else {
                setLandscapeOrientation();
            }
        });

        buttonNext = playerView.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> {
            onNext();
        });

        buttonPrevious = playerView.findViewById(R.id.buttonPrevious);
        buttonPrevious.setOnClickListener(v -> {
            onPrevious();
        });

        buttonSubtitles = playerView.findViewById(R.id.buttonSubtitles);
        buttonSubtitles.setOnClickListener(v -> {
            onSubtitles();
        });

        textPosition = playerView.findViewById(R.id.exo_position);
        textPositionLive = playerView.findViewById(R.id.textPositionLive);
        textDuration = playerView.findViewById(R.id.exo_duration);
        textDurationLive = playerView.findViewById(R.id.textDurationLive);
        viewTimeBar = playerView.findViewById(R.id.exo_progress);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d("onActivityCreated()");

        initIMA();

        initMediaSession();

        sensorViewModel = ViewModelProviders.of(getActivity()).get(SensorViewModel.class);

        playerViewModel = ViewModelProviders.of(getActivity()).get(PlayerViewModel.class);
        if (playerModeObserver == null)
            playerModeObserver = createPlayerModeObserver();
        if (playerUrlObserver == null)
            playerUrlObserver = createPlayerUrlObserver();
        if (playerErrorObserver == null)
            playerErrorObserver = createPlayerErrorObserver();

        playerViewModel.getPlayerMode().observe(this, playerModeObserver);
        playerViewModel.getPlayerUrl().observe(this, playerUrlObserver);
        playerViewModel.onPlayerError().observe(this, playerErrorObserver);
        if (playerViewModel.isTrailer().getValue()) {
            ImageButton buttonCloseTrailer = getView().findViewById(R.id.buttonCloseTrailer);
            buttonCloseTrailer.setVisibility(View.VISIBLE);
            buttonCloseTrailer.setOnClickListener(v -> {
                stop();
                videoViewModel.onVideoFinished(true);
                playerViewModel.setTrailerVideoId(null);
                setPortraitOrientation();
            });
            setLandscapeOrientation();
        }

        videoViewModel = ViewModelProviders.of(getActivity()).get(VideoDetailViewModel.class);
        videoViewModel.getVideo().observe(this, video -> {
            thumbnail = VideoHelper.getThumbnailByHeight(video, 480);
            if (thumbnail != null) {
                UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, imageThumbnail);
            }
            initProgress(video);
        });
        videoViewModel.isFullscreen().observe(this, fullscreen -> {
            if (fullscreen != null) {
                updateFullscreenButton(fullscreen);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("onStart()");

        initPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume()");

        hideNotification();

        // Retrieve device id
        AdMacrosHelper.fetchDeviceId(getActivity(), new AdMacrosHelper.IDeviceIdListener() {
            @Override
            public void onDeviceId(String deviceId) {
                Logger.d("onDeviceId(): deviceId=" + deviceId);
            }
        });

        registerReceivers();

//        start();

        // IMA SDK

        if (adsManager != null && isAdDisplayed) {
            adsManager.resume();
        }

        startAdsTimer();
    }

    @Override
    public void onPause() {
        super.onPause();

        Logger.d("onPause()");
        if (player != null) {
            if (playerViewModel.isBackgroundPlaybackEnabled()) {
//                player.setBackgrounded(true);
            }
            else {
                stopTimer();
            }
        }

        // IMA SDK

        if (adsManager != null && isAdDisplayed) {
            adsManager.pause();
        }

        stopAdsTimer();
    }

    @Override
    public void onStop() {
        Logger.d("onStop()");
        if (player != null) {
            if (playerViewModel.isBackgroundPlaybackEnabled()) {
                showNotification();
            }
            else {
                pause();
            }
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logger.d("onDestroy()");
        hideNotification();

        if (player != null) {
            stop();
        }
        unregisterReceivers();

        super.onDestroy();
    }

    // View model observers

    private Observer<PlayerViewModel.PlayerMode> createPlayerModeObserver() {
        Logger.d("createPlayerModeObserver()");
        return playerMode -> {
            Logger.d("getPlayerMode(): playerMode=" + playerMode);
            if (playerMode != null) {
                if (playerViewModel.playbackPositionRestored()) {
                    playerViewModel.savePlaybackPosition(player.getCurrentPosition());
                }
                if (playerMode == PlayerViewModel.PlayerMode.VIDEO) {
                    playerView.setUseArtwork(false);
                }
                else if (playerMode == PlayerViewModel.PlayerMode.AUDIO) {
                    playerView.setUseArtwork(true);
                    showThumbnail();
                }
//                initPlayer();
            }
        };

    }

    private Observer<String> createPlayerUrlObserver() {
        Logger.d("createPlayerUrlObserver()");
        return playerUrl -> {
            Logger.d("getPlayerUrl(): playerUrl=" + playerUrl);
            if (!TextUtils.isEmpty(playerUrl)) {
                if (player != null
                        && playerViewModel.getPlayerMode().getValue() == PlayerViewModel.PlayerMode.AUDIO
                        && !playerViewModel.isMediaTypeAvailable(PlayerViewModel.PlayerMode.VIDEO)
                        && player.getRendererCount() > 0) {
                    playerViewModel.onPlaybackPositionRestored();
                    Logger.d("getPlayerUrl(): This is the audio only video. Skip player reinitialization");
                    return;
                }

                attachPlayerToAnalyticsManager(playerUrl);

                MediaSource mediaSource = playerViewModel.getMediaSource(getActivity(), playerUrl);
                if (mediaSource != null && player != null) {
                    if (videoViewModel.getVideoSync().onAir != 1
                        && !playerViewModel.isTrailer().getValue()) {
                        player.seekTo(playerViewModel.getPlaybackPosition());
                        playerViewModel.onPlaybackPositionRestored();
                    }
                    player.prepare(mediaSource, false, false);
                    if (!playerViewModel.isTrailer().getValue()) {
                        startAds();
                    }
                }
                updateNextPreviousButtons();
            }
        };
    }

    private Observer<PlayerViewModel.Error> createPlayerErrorObserver() {
        return error -> {
            if (error == null) {
                return;
            }
            switch (error.type) {
                case LOCKED:
//                    playerView.setUseArtwork(true);
//                    showThumbnail();
                    break;
            }
        };
    }

    // UI

    private void setPortraitOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        listenForDeviceRotation(Configuration.ORIENTATION_PORTRAIT);
    }

    private void setLandscapeOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        listenForDeviceRotation(Configuration.ORIENTATION_LANDSCAPE);
    }

    private void enablePlayerControls() {
        playerView.setUseController(true);
    }

    private void disablePlayerControls() {
        playerView.setUseController(false);
    }

    private boolean isPlayerControlsEnabled() {
        return playerView.getUseController();
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(VISIBLE);
    }

    private void updateNextPreviousButtons() {
        if (ZypeConfiguration.autoplayEnabled(getActivity())
                && SettingsProvider.getInstance().getBoolean(SettingsProvider.AUTOPLAY)) {
            buttonNext.setVisibility(VISIBLE);
            buttonPrevious.setVisibility(VISIBLE);
            setButtonEnabled(playerViewModel.isThereNextVideo(), buttonNext);
            setButtonEnabled(playerViewModel.isTherePreviousVideo(), buttonPrevious);
        }
        else {
            buttonNext.setVisibility(GONE);
            buttonPrevious.setVisibility(GONE);
        }
    }

    private void updateFullscreenButton(boolean fullscreen) {
        if (fullscreen) {
            buttonFullscreen.setImageResource(R.drawable.baseline_fullscreen_exit_white_24);
        }
        else {
            buttonFullscreen.setImageResource(R.drawable.baseline_fullscreen_white_24);
        }
    }

    private void updateSubtitlesButton(int subtitlesTrackIndex) {
        if (subtitlesTrackIndex != -1) {
            buttonSubtitles.setVisibility(VISIBLE);
            buttonSubtitles.setTag(subtitlesTrackIndex);
        }
        else {
            buttonSubtitles.setVisibility(GONE);
            buttonSubtitles.setTag(null);
        }
    }

    private void initProgress(Video video) {
        if (video.onAir == 1) {
            textPosition.setVisibility(GONE);
            textPositionLive.setVisibility(VISIBLE);
            textDuration.setVisibility(GONE);
            textDurationLive.setVisibility(VISIBLE);
            viewTimeBar.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                    updatePositionLive(player.getCurrentTimeline(), position);
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                }
            });
        }
        else {
            textPosition.setVisibility(VISIBLE);
            textPositionLive.setVisibility(GONE);
            textDuration.setVisibility(VISIBLE);
            textDurationLive.setVisibility(GONE);
        }
    }

    private void updatePositionLive(Timeline timeline, long position) {
        Timeline.Window window = new Timeline.Window();
        timeline.getWindow(0, window);
        long duration = C.usToMs(window.durationUs);
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder, Locale.getDefault());
        textPositionLive.setText("-" + Util.getStringForTime(builder, formatter, duration - position));
    }

    private void showThumbnail() {
        if (thumbnail != null) {
            UiUtils.loadImage(getActivity(), thumbnail.getUrl(), R.drawable.placeholder_video,
                    createPlayerViewTarget());
        }
        else {
            playerView.setDefaultArtwork(ContextCompat.getDrawable(getActivity(),
                    R.drawable.placeholder_video));
        }
    }

    private BaseTarget<BitmapDrawable> createPlayerViewTarget() {
        return new BaseTarget<BitmapDrawable>() {
            @Override
            public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
                playerView.setDefaultArtwork(bitmap);
            }

            @Override
            public void getSize(SizeReadyCallback cb) {
                cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
            }

            @Override
            public void removeCallback(SizeReadyCallback cb) {}
        };
    }

    private void registerReceivers() {
        if (!isReceiversRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
            getActivity().registerReceiver(callReceiver, filter);

            if (audioCapabilitiesReceiver == null) {
                audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getContext(), this);
                audioCapabilitiesReceiver.register();
            }
            if (audioManager == null || remoteControlResponder == null) {
                audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                remoteControlResponder = new ComponentName(getActivity().getPackageName(),
                        RemoteControlReceiver.class.getName());
            }
            audioManager.registerMediaButtonEventReceiver(remoteControlResponder);
            RemoteControlReceiver.getObservable().addObserver(this);
            isReceiversRegistered = true;
        }
    }

    private void unregisterReceivers() {
        if (callReceiver != null && isReceiversRegistered) {
            try {
                getActivity().unregisterReceiver(callReceiver);
            } catch (IllegalArgumentException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        if (audioCapabilitiesReceiver != null) {
            audioCapabilitiesReceiver.unregister();
            audioCapabilitiesReceiver = null;
        }
        if (audioManager != null)
            audioManager.unregisterMediaButtonEventReceiver(remoteControlResponder);
        RemoteControlReceiver.getObservable().deleteObserver(this);
        isReceiversRegistered = false;
    }

    // Player

    private void initPlayer() {
        Logger.d("initPlayer()");

        if (player == null) {
            trackSelector = new DefaultTrackSelector();

            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            player.addListener(new PlayerEventListener());

            playerView.setPlayer(player);
            playerView.setControlDispatcher(new PlayerControlDispatcher());
        }

        if (isPlayerControlsEnabled()) {
            play();
        }
        else {
            pause();
        }
    }

    private void releasePlayer() {
        Logger.d("releasePlayer()");
        AnalyticsManager.getInstance().trackStop();

        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private class PlayerControlDispatcher extends DefaultControlDispatcher {
        @Override
        public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
            if (playerViewModel.getPlaybackState() != null) {
                if (playerViewModel.getPlaybackState().getValue() == Player.STATE_IDLE) {
                    NavigationHelper.getInstance(getActivity())
                            .handleLockedVideo(getActivity(),
                                    videoViewModel.getVideoSync(), videoViewModel.getPlaylistSync());
                }
            }
            return super.dispatchSetPlayWhenReady(player, playWhenReady);
        }
    }

    private class PlayerEventListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Logger.d("onPlayerStateChanged(): playWhenReady=" + playWhenReady + ", playbackState=" + playbackState);
            imageThumbnail.setVisibility(GONE);
            switch (playbackState) {
                case Player.STATE_IDLE:
                    imageThumbnail.setVisibility(VISIBLE);
                    break;
                case Player.STATE_READY: {
                    mediaSession.setActive(true);
                    if (player != null) {
                        if (player.getPlayWhenReady()) {
                            playerViewModel.onPlaybackStarted();
                        }
                    }
                    break;
                }
                case Player.STATE_ENDED: {
                    if (playerViewModel.isTrailer().getValue()) {
                        videoViewModel.onVideoFinished(true);
                        playerViewModel.setTrailerVideoId(null);
                        setPortraitOrientation();
                        break;
                    }
                    if (playerViewModel.getPlaybackState().getValue() != playbackState) {
                        AnalyticsManager.getInstance().trackStop();

                        playerViewModel.savePlaybackPosition(0);
                        playerViewModel.onPlaybackFinished();

                        if (ZypeConfiguration.autoplayEnabled(getActivity())
                                && SettingsProvider.getInstance().getBoolean(SettingsProvider.AUTOPLAY)) {
                            onNext();
                        }
                    }
                    break;
                }
            }
            playerViewModel.setPlaybackState(playbackState);
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            Logger.d("PlayerEventListener::onPositionDiscontinuity():");
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            Log.e(TAG, "onPlayerError(): " + e.getMessage());
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Logger.d("onTracksChanged():");

            // Update subtitles
            updateSubtitlesButton(getSubtitlesTrack());

            // When we are in VIDEO player mode, check if the player actually has video track to play.
            // If it has no video tracks available, switch to AUDIO mode
            if (playerViewModel.getPlayerMode().getValue() == PlayerViewModel.PlayerMode.VIDEO) {
                if (!hasVideoTrack(trackGroups)) {
                    playerViewModel.setMediaTypeAvailable(PlayerViewModel.PlayerMode.VIDEO, false);
                    playerViewModel.setPlayerMode(PlayerViewModel.PlayerMode.AUDIO);
                }
            }
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            Logger.d("PlayerEventListener::onTimelineChanged():");
            if (videoViewModel.getVideoSync().onAir == 1) {
                updatePositionLive(timeline, player.getCurrentPosition());
            }
        }
    }

    private void play() {
        Logger.d("play()");
        if (player != null) {
            player.setPlayWhenReady(true);
            mediaSession.setActive(true);
            startAdsTimer();
        }
    }

    private void pause() {
        Logger.d("pause()");
        if (player != null) {
            player.setPlayWhenReady(false);
            stopAdsTimer();
        }
    }

    private void start() {
    }

    private void stop() {
        pause();
        if (playerViewModel.playbackPositionRestored()) {
            playerViewModel.savePlaybackPosition(player.getCurrentPosition());
        }
        releasePlayer();
        mediaSession.setActive(false);
    }

    private boolean hasVideoTrack(TrackGroupArray trackGroups) {
        boolean result = false;
        for (int i = 0; i < trackGroups.length; i++) {
            if (trackGroups.get(i).length > 0
                    && MimeTypes.isVideo(trackGroups.get(i).getFormat(0).sampleMimeType)) {
                result = true;
                break;
            }
        }
        return result;
    }

    // Media session

    private void initMediaSession() {
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

    private void onNext() {
        videoViewModel.nextVideo();
    }

    private void onPrevious() {
        videoViewModel.previousVideo();
    }

    private void onSubtitles() {
        Pair<AlertDialog, TrackSelectionView> dialog = TrackSelectionView
                .getDialog(getActivity(), "Subtitles", trackSelector, (Integer) buttonSubtitles.getTag());
        dialog.second.setShowDisableOption(true);
        dialog.first.show();
    }

    /**
     * Find subtitles track
     *
     * @return Subtitles track index, -1 if there is no subtitles track
     */
    private int getSubtitlesTrack() {
        MappingTrackSelector.MappedTrackInfo info = trackSelector.getCurrentMappedTrackInfo();
        if (info == null) {
            return -1;
        }
        for (int i = 0; i < info.getRendererCount(); i++) {
            if (player.getRendererType(i) == C.TRACK_TYPE_TEXT) {
                if (info.getTrackGroups(i).length > 0)
                    return i;
                else
                    return -1;
            }
        }
        return -1;
    }


    // Sensor

    private void listenForDeviceRotation(final int requiredOrientation) {
        sensorViewModel.getOrientation().observe(this, orientation -> {
            Logger.d("listenForDeviceRotation(): orientation=" + orientation);
            if (orientation == requiredOrientation) {
                sensorViewModel.stopListeningOrientation();
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        });
    }


    // IMA SDK

    private void initIMA() {
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
    }

    private void requestAds(String adTagUrl) {
        AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(playerView);

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

    private void startAds() {
        if (!playerViewModel.getAdSchedule().isEmpty()) {
            long playTime = playerViewModel.getPlaybackPosition();
            nextAdIndex = seekAdByPosition(playTime);
            if (!checkNextAd(playTime)) {
                startAdsTimer();
            }
        }
    }

    private void startAdsTimer() {
        if (nextAdIndex == -1)
            return;

        stopAdsTimer();
        if (handlerTimer != null) {
            if (runnablePlaybackTime != null) {
                handlerTimer.post(runnablePlaybackTime);
            }
        }
    }

    private void stopAdsTimer() {
        if (handlerTimer != null) {
            if (runnablePlaybackTime != null) {
                handlerTimer.removeCallbacks(runnablePlaybackTime);
            }
        }
    }

    private int seekAdByPosition(long position) {
        List<AdSchedule> adSchedule = playerViewModel.getAdSchedule();
        for (int i = 0; i < adSchedule.size(); i++) {
            if (((int) position / 1000) * 1000 <= adSchedule.get(i).offset) {
                Logger.d("seekAdByPosition(): Next ad index: " + i + ", position=" + position);
                return i;
            }
        }
        Logger.d("seekAdByPosition(): No ads to play, position=" + position);
        return -1;
    }

    private void updateNextAd() {
        List<AdSchedule> adSchedule = playerViewModel.getAdSchedule();
        if (nextAdIndex == -1)
            return;
        if (nextAdIndex + 1 < adSchedule.size()) {
            nextAdIndex += 1;
//            if (handlerTimer != null && runnablePlaybackTime != null) {
//                handlerTimer.postDelayed(runnablePlaybackTime, 1000);
//            }
        }
        else {
            nextAdIndex = -1;
        }
    }

    private boolean checkNextAd(long position) {
        List<AdSchedule> adSchedule = playerViewModel.getAdSchedule();
        if (nextAdIndex >= 0) {
            if (SettingsProvider.getInstance().getSubscriptionCount() <= 0) {
                Logger.d("checkNextAd(): position=" + position + ", nextAdIndex="+ nextAdIndex);
                if (position >= adSchedule.get(nextAdIndex).offset) {
                    // Disable media controls and pause the video
                    disablePlayerControls();
                    pause();
                    // Request the ad
                    Logger.d("checkNextAd(): Requesting ad");
                    String adTag = AdMacrosHelper.updateAdTagParameters(getActivity(), adSchedule.get(nextAdIndex).tag);
                    Logger.d("Ad tag with macros: " + adTag);
                    requestAds(adTag);
                    // TODO: Show progress while the ad is loading
                    return true;
                }
            }
        }
        return false;
    }

    // 'AdEvent.AdEventListener' implementation

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
//                hideControls();
                pause();
                break;
            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                isAdDisplayed = false;
                // Update next ad to play
                updateNextAd();
                if (!checkNextAd(player.getCurrentPosition())) {
                    // Resume video
                    enablePlayerControls();
                    play();
                }
                break;
            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                adsLoader.contentComplete();
                break;
//            case AD_PROGRESS:
//                if (player.getPlayWhenReady()) {
//                    pause();
//                }
//                break;
            default:
                break;
        }
    }

    // 'AdErrorEvent.AdErrorListener' implementation

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Logger.e("Ad error: " + adErrorEvent.getError().getMessage());
        updateNextAd();
        if (player != null) {
            if (!checkNextAd(player.getCurrentPosition())) {
                // Resume video
                enablePlayerControls();
                play();
            }
        }
    }


    // Analytics

    private void attachPlayerToAnalyticsManager(String url) {
        AnalyticBeacon analyticsBeacon = playerViewModel.getAnalyticBeacon();
        if (player != null && analyticsBeacon != null) {
            videoViewModel.getVideo().observe(this, video -> {
                Context context = getActivity().getApplicationContext();
                String beacon = analyticsBeacon.beacon;

                Map<String, String> customDimensions = getCustomDimensions(analyticsBeacon, video.title);

                AnalyticsManager manager = AnalyticsManager.getInstance();
                manager.trackPlay(context, player, beacon, url, customDimensions);
            });
        }
    }

    private Map<String, String> getCustomDimensions(AnalyticBeacon analyticsBeacon, String title) {
        Map<String, String> customDimensions = new HashMap<>();

        customDimensions.put("videoId", analyticsBeacon.videoId);
        customDimensions.put("playerId", analyticsBeacon.playerId);
        customDimensions.put("siteId", analyticsBeacon.siteId);
        customDimensions.put("device", analyticsBeacon.device);
        customDimensions.put("title", title);
        String consumerId;
        if ((consumerId = SettingsProvider.getInstance().getConsumerId()) != null) {
            customDimensions.put("consumerId", consumerId);
        }

        return customDimensions;
    }


    // Limiting live stream

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
                        pause();
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


    class CallReceiver extends PhoneCallReceiver {

        @Override
        protected void onIncomingCallStarted(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onIncomingCallStarted");
            pause();
        }

        @Override
        protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onOutgoingCallStarted");
            pause();
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
            Logger.d("PlayerFragment call onIncomingCallEnded");
            play();
        }

        @Override
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
            Logger.d("PlayerFragment call onOutgoingCallEnded");
            play();
        }

        @Override
        protected void onMissedCall(Context ctx, String number, Date start) {
            Logger.d("PlayerFragment call onMissedCall");
            play();
        }
    }

    public void showNotification() {
        Logger.d("showNotification()");
        if (player == null) {
            return;
        }

        Video video = videoViewModel.getVideoSync();
        if (video != null) {
            String title = video.getTitle();

            Intent notificationIntent;
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.VIDEO_ID, video.id);
            notificationIntent = new Intent(getActivity(), VideoDetailActivity.class);
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

            if (player != null) {
                if (player.getPlayWhenReady()) {
                    builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "Pause",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));
                }
                else {
                    builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Play",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));
                }
            }
            builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken())
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                            PlaybackStateCompat.ACTION_STOP)));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.notify(ZypeApp.NOTIFICATION_ID, builder.build());

            playerViewModel.setToBackground(true);
        }
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
        playerViewModel.setToBackground(false);
    }


    // 'AudioCapabilitiesReceiver.Listener' implementation

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
//        boolean backgrounded = player.getBackgrounded();
//        boolean playWhenReady = player.getPlayWhenReady();
//        releasePlayer();
//        initPlayer(playWhenReady);
//        player.setBackgrounded(backgrounded);
    }


    // 'java.util.Observer' implementation

    @Override
    public void update(Observable observable, Object data) {
        Logger.d("Observer.update(): code=" + data);
        int keycode = (int) data;
        switch (keycode) {
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        pause();
                    }
                    else {
                        play();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        pause();
                    }
                    else {
                        play();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        pause();
                    }
                    else {
                        play();
                    }
                    if (playerViewModel.isInBackground()) {
                        showNotification();
                    }
                }
                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
                if (player != null) {
                    if (player.getPlayWhenReady()) {
                        pause();
                    }
                    else {
                        play();
                    }
                }
                break;
//            case MEDIA_STOP_CODE: {
//                if (player != null) {
//                    if (player.getPlayerControl().isPlaying()) {
//                        player.getPlayerControl().pause();
//                    }
//                    Logger.d("MEDIA_STOP_CODE");
//                    hideNotification();
//                }
//                break;
//            }
            default:
                break;
        }
    }
}
