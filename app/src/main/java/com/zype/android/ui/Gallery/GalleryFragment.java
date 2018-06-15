package com.zype.android.ui.Gallery;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlaylistParamsBuilder;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.playlist.PlaylistEvent;
import com.zype.android.webapi.events.video.RetrieveVideoEvent;
import com.zype.android.webapi.model.playlist.Playlist;
import com.zype.android.webapi.model.playlist.PlaylistData;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 12.06.2018
 */
public class GalleryFragment extends Fragment {
    public static final String TAG = GalleryFragment.class.getSimpleName();

    private static final String ARG_PARENT_PLAYLIST_ID = "ParentPlaylistId";

    private GalleryViewModel model;
    private String parentPlaylistId;

    private GalleryRowsAdapter adapter;

    public static GalleryFragment newInstance(String parentPlaylistId) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARENT_PLAYLIST_ID, parentPlaylistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentPlaylistId = getArguments().getString(ARG_PARENT_PLAYLIST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView listGallery = rootView.findViewById(R.id.listGallery);
        adapter = new GalleryRowsAdapter();
        listGallery.setAdapter(adapter);

        WebApiManager.getInstance().subscribe(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(GalleryViewModel.class);
        model.getGalleryRows(parentPlaylistId).observe(this, new Observer<List<GalleryRow>>() {
            @Override
            public void onChanged(@Nullable List<GalleryRow> galleryRows) {
                adapter.setData(galleryRows);
            }
        });
    }

    // TODO: Remove loading data from API related code below from here
    @Override
    public void onResume() {
        super.onResume();
        loadPlaylists(parentPlaylistId);
    }

    @Override
    public void onDestroyView() {
        WebApiManager.getInstance().unsubscribe(this);
        super.onDestroyView();
    }

    private void loadPlaylists(String parentPlaylistId) {
        PlaylistParamsBuilder builder = new PlaylistParamsBuilder()
                .addParentId(parentPlaylistId)
                .addPerPage(100);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.PLAYLIST_GET, builder.build());
    }

    @Subscribe
    public void handleRetrievePlaylist(PlaylistEvent event) {
        Logger.d("handlePlaylistEvent size=" + event.getEventData().getModelData().getResponse().size());
        Playlist data = event.getEventData().getModelData();
        List<PlaylistData> playlists = data.getResponse();
        if (playlists.size() > 0) {
            DataRepository.getInstance(getActivity().getApplication()).insertPlaylists(playlistDataToEntity(playlists));
            
            // Load videos
            for (PlaylistData playlistData : playlists) {
                if (playlistData.getParentId().equals(parentPlaylistId)) {
                    loadPlaylistVideos(playlistData.getId(), 1);
                    loadPlaylists(playlistData.getId());
                }
            }
        }
    }

    private List<com.zype.android.Db.Entity.Playlist> playlistDataToEntity(List<PlaylistData> playlists) {
        List<com.zype.android.Db.Entity.Playlist> result = new ArrayList<>(playlists.size());
        for (PlaylistData item : playlists) {
            com.zype.android.Db.Entity.Playlist playlistEntity = new com.zype.android.Db.Entity.Playlist();
            playlistEntity.id = item.getId();
            playlistEntity.createdAt = item.getCreatedAt();
            playlistEntity.deletedAt = item.getDeletedAt();
            playlistEntity.images = new Gson().toJson(item.getImages());
            playlistEntity.parentId = item.getParentId();
            playlistEntity.playlistItemCount = item.getPlaylistItemCount();
            playlistEntity.priority = item.getPriority();
            playlistEntity.thumbnails = new Gson().toJson(item.getThumbnails());
            playlistEntity.title = item.getTitle();
            playlistEntity.updatedAt = item.getUpdatedAt();
            result.add(playlistEntity);
        }
        return result;
    }

    private void loadPlaylistVideos(String playlistId, int page) {
        Logger.d("Load playlist videos, page=" + page);
        VideoParamsBuilder builder = new VideoParamsBuilder();
        builder.addPlaylistId(playlistId);
        builder.addPage(page);
        builder.addPerPage(100);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.VIDEO_FROM_PLAYLIST, builder.build());
    }

    @Subscribe
    public void handleRetrieveVideo(RetrieveVideoEvent event) {
        List<VideoData> videoData = event.getEventData().getModelData().getVideoData();
        if (videoData != null) {
            Logger.d("handleRetrieveVideo(): size=" + videoData.size());
            if (videoData.size() > 0) {
                DataRepository.getInstance(getActivity().getApplication()).insertVideos(videoDataToVideoEntity(videoData));
                DataRepository.getInstance(getActivity().getApplication()).deletePlaylistVideos(event.getPlaylistId());
                DataRepository.getInstance(getActivity().getApplication()).insertPlaylistVideos(videoDataToPlaylistVideoEntity(videoData, event.getPlaylistId()));
            }
        }
    }
    
    private List<PlaylistVideo> videoDataToPlaylistVideoEntity(List<VideoData> videoData, String playlistId) {
        List<PlaylistVideo> result = new ArrayList<>(videoData.size());
        int number = 1;
        for (VideoData item : videoData) {
            PlaylistVideo entity = new PlaylistVideo();
            entity.number = number;
            entity.playlistId = playlistId;
            entity.videoId = item.getId();
            result.add(entity);
            number++;
        }
        return result;
    }

    private List<Video> videoDataToVideoEntity(List<VideoData> videoData) {
        List<Video> result = new ArrayList<>(videoData.size());
        for (VideoData item : videoData) {
            Video entity = new Video();
            entity.id = item.getId();
            entity.active = item.isActive() ? 1 : 0;
            entity.category = new Gson().toJson(item.getCategories());
            entity.country = item.getCountry();
            entity.createdAt = item.getCreatedAt();
            entity.crunchyrollId = item.getCrunchyrollId();
            entity.description = (item.getDescription() == null) ? "" : item.getDescription();
            entity.discoveryUrl = item.getDiscoveryUrl();
            entity.duration = item.getDuration();
            entity.episode = String.valueOf(item.getEpisode());
            entity.expireAt = item.getExpireAt();
            entity.featured = String.valueOf(item.isFeatured() ? 1 : 0);
            entity.foreignId = item.getForeignId();
            entity.huluId = item.getHuluId();
            entity.keywords = new Gson().toJson(item.getKeywords());
            entity.matureContent = String.valueOf(item.isMatureContent() ? 1 : 0);
            entity.onAir = item.isOnAir() ? 1 : 0;
            entity.publishedAt = item.getPublishedAt();
            entity.purchaseRequired = String.valueOf(item.isPurchaseRequired() ? 1 : 0);
            entity.rating = String.valueOf(item.getRating());
            entity.relatedPlaylistIds = new Gson().toJson(item.getRelatedPlaylistIds());
            entity.requestCount = String.valueOf(item.getRequestCount());
            entity.season = item.getSeason();
            entity.segments = new Gson().toJson(item.getSegments());
            entity.shortDescription = item.getShortDescription();
            entity.siteId = item.getSiteId();
            entity.startAt = item.getStartAt();
            entity.status = item.getStatus();
            entity.subscriptionRequired = String.valueOf(item.isSubscriptionRequired() ? 1 : 0);
            entity.thumbnails = new Gson().toJson(item.getThumbnails());
            entity.title = item.getTitle();
            entity.transcoded = item.isTranscoded() ? 1 : 0;
            entity.updatedAt = item.getUpdatedAt();
            entity.videoZObject = new Gson().toJson(item.getVideoZobjects());
            entity.youtubeId = item.getYoutubeId();
            entity.zobjectIds = new Gson().toJson(item.getZobjectIds());
            result.add(entity);
        }
        return result;
    }
}
