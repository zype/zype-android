package com.zype.android.ui.player;


import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;
import com.google.android.exoplayer.hls.DefaultHlsTrackSelector;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.hls.PtsTimestampAdjusterProvider;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.os.Handler;


import java.io.IOException;

/**
 * @author vasya
 * @version 1
 *          date 9/30/15
 * A {@link CustomPlayer.RendererBuilder} for HLS.
 */
public class HlsRendererBuilder implements CustomPlayer.RendererBuilder {


    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENTS = 256;
    private static final int TEXT_BUFFER_SEGMENTS = 2;

    private final Context context;
    private final String userAgent;
    private final String url;


    private AsyncRendererBuilder currentAsyncBuilder;


    public HlsRendererBuilder(Context context, String userAgent, String url) {
        this.context = context;
        this.userAgent = userAgent;
        this.url = url;
    }


    @Override
    public void buildRenderers(CustomPlayer player) {
        currentAsyncBuilder = new AsyncRendererBuilder(context, userAgent, url, player);
        currentAsyncBuilder.init();
    }


    @Override
    public void cancel() {
        if (currentAsyncBuilder != null) {
            currentAsyncBuilder.cancel();
            currentAsyncBuilder = null;
        }
    }


    private static final class AsyncRendererBuilder implements ManifestCallback<HlsPlaylist> {


        private final Context context;
        private final String userAgent;
        private final String url;
        private final CustomPlayer player;
        private final ManifestFetcher<HlsPlaylist> playlistFetcher;


        private boolean canceled;


        public AsyncRendererBuilder(Context context, String userAgent, String url, CustomPlayer player) {
            this.context = context;
            this.userAgent = userAgent;
            this.url = url;
            this.player = player;
            HlsPlaylistParser parser = new HlsPlaylistParser();
            playlistFetcher = new ManifestFetcher<>(url,
                    new DefaultUriDataSource(context, null, userAgent, true),
                    parser);
        }


        public void init() {
            playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
        }


        public void cancel() {
            canceled = true;
        }


        @Override
        public void onSingleManifestError(IOException e) {
            if (canceled) {
                return;
            }
            player.onRenderersError(e);
        }


        @Override
        public void onSingleManifest(HlsPlaylist manifest) {
            if (canceled) {
                return;
            }

            Handler mainHandler = player.getMainHandler();
            LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            PtsTimestampAdjusterProvider timestampAdjusterProvider = new PtsTimestampAdjusterProvider();

            int[] variantIndices = null;
            if (manifest instanceof HlsMasterPlaylist) {
                HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
                try {
                    variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(
                            context, masterPlaylist.variants, null, false);
                } catch (DecoderQueryException e) {
                    player.onRenderersError(e);
                    return;
                }
                if (variantIndices.length == 0) {
                    player.onRenderersError(new IllegalStateException("No variants selected."));
                    return;
                }
            }

            DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent, true);
            HlsChunkSource chunkSource = new HlsChunkSource(true, dataSource, manifest,
                    DefaultHlsTrackSelector.newDefaultInstance(context), bandwidthMeter,
                    timestampAdjusterProvider);
            HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
                    BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, player, CustomPlayer.TYPE_VIDEO);
            MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
                    sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT,
                    5000, mainHandler, player, 15);
            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                    MediaCodecSelector.DEFAULT, null, true, player.getMainHandler(),
                    player, AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

            // Build the text renderer, preferring Webvtt where available.
            boolean preferWebvtt = false;
            if (manifest instanceof HlsMasterPlaylist) {
                preferWebvtt = !((HlsMasterPlaylist) manifest).subtitles.isEmpty();
            }
            TrackRenderer textRenderer = null;
            if (preferWebvtt) {
                DataSource textDataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent, true);
                HlsChunkSource textChunkSource = new HlsChunkSource(false, textDataSource, manifest,
                        DefaultHlsTrackSelector.newSubtitleInstance(), bandwidthMeter,
                        timestampAdjusterProvider);
                HlsSampleSource textSampleSource = new HlsSampleSource(textChunkSource, loadControl,
                        TEXT_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, player, CustomPlayer.TYPE_TEXT);
                textRenderer = new TextTrackRenderer(textSampleSource, player, mainHandler.getLooper());
            }
//            else {
//                textRenderer = new Eia608TrackRenderer(sampleSource, player, mainHandler.getLooper());
//            }

            TrackRenderer[] renderers = new TrackRenderer[CustomPlayer.RENDERER_COUNT];
            renderers[CustomPlayer.TYPE_VIDEO] = videoRenderer;
            renderers[CustomPlayer.TYPE_AUDIO] = audioRenderer;
            renderers[CustomPlayer.TYPE_TEXT] = textRenderer;
            player.onRenderers(renderers, bandwidthMeter);
        }
    }
}
