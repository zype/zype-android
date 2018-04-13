package com.zype.android.ui.player;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

/**
 * @author vasya
 * @version 1
 *          date 9/30/15
 *          A {@link CustomPlayer.RendererBuilder} for streams that can be read using an {@link Extractor}.
 */
public class ExtractorRendererBuilder implements CustomPlayer.RendererBuilder {


    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    private final Context context;
    private final String userAgent;
    private final Uri uri;
    private final Extractor extractor;

    public ExtractorRendererBuilder(Context context, String userAgent, Uri uri, Extractor extractor) {
        this.context = context;
        this.userAgent = userAgent;
        this.uri = uri;
        this.extractor = extractor;
    }


//    @Override
//    public void buildRenderers(CustomPlayer player) {
//        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
//
//
//        // Build the video and audio renderers.
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(player.getMainHandler(),
//                null);
//        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
//        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource,
//                allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, extractor);
//        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
//                null, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player.getMainHandler(),
//                player, 50);
//        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
//                null, true, player.getMainHandler(), player, AudioCapabilities.getCapabilities(context));
////        TrackRenderer textRenderer = new TextTrackRenderer(sampleSource, player,
////                player.getMainHandler().getLooper());
//
//
//        // Invoke the callback.
//        TrackRenderer[] renderers = new TrackRenderer[CustomPlayer.RENDERER_COUNT];
//        renderers[CustomPlayer.TYPE_VIDEO] = videoRenderer;
//        renderers[CustomPlayer.DEFAULT_TYPE_AUDIO] = audioRenderer;
////        renderers[CustomPlayer.TYPE_TEXT] = textRenderer;
//        player.onRenderers(renderers, bandwidthMeter);
//    }

    @Override
    public void buildRenderers(CustomPlayer player) {
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);

        // Build the video and audio renderers.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(player.getMainHandler(),
                null);
        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent, true);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
                sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, player.getMainHandler(),
                player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, player.getMainHandler(), player, AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        // Invoke the callback.
        TrackRenderer[] renderers = new TrackRenderer[CustomPlayer.RENDERER_COUNT];
        renderers[CustomPlayer.TYPE_VIDEO] = videoRenderer;
        renderers[CustomPlayer.TYPE_AUDIO] = audioRenderer;
        player.onRenderers(renderers, bandwidthMeter);
    }


    @Override
    public void cancel() {
        // Do nothing.
    }


}