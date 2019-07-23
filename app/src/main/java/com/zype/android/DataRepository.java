package com.zype.android;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.google.gson.Gson;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;
import com.zype.android.Db.ZypeDb;
import com.zype.android.webapi.model.video.VideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

public class DataRepository {
    private static DataRepository INSTANCE;

    private ZypeDb db;

    private DataRepository(Application application) {
        db = ZypeDb.getDatabase(application);
    }

    public static DataRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    // Playlist

    public LiveData<List<Playlist>> getPlaylists(String parentId) {
        return db.zypeDao().getPlaylists(parentId);
    }

    public List<Playlist> getPlaylistsSync(String parentId) {
        return db.zypeDao().getPlaylistsSync(parentId);
    }

    public Playlist getPlaylistSync(String playlistId) {
        return db.zypeDao().getPlaylistSync(playlistId);
    }

    public void insertPlaylists(List<Playlist> playlists) {
        db.zypeDao().insertPlaylists(playlists);
    }

    // Video

    public LiveData<List<Video>> getPlaylistVideos(String playlistId) {
        return db.zypeDao().getPlaylistVideos(playlistId);
    }

    public List<Video> getPlaylistVideosSync(String playlistId) {
        return db.zypeDao().getPlaylistVideosSync(playlistId);
    }

    public void insertPlaylistVideos(List<PlaylistVideo> playlistVideos) {
        db.zypeDao().insertPlaylistVideos(playlistVideos);
    }

    public void deletePlaylistVideos(String playlistId) {
        db.zypeDao().deletePlaylistVideos(playlistId);
    }

    public void insertPlaylistVideos(List<Video> videos, Playlist playlist) {
        List<PlaylistVideo> playlistVideos = new ArrayList<>();
        int number = db.zypeDao().getPlaylistVideosSync(playlist.id).size() + 1;
        for (Video video : videos) {
            PlaylistVideo playlistVideo = new PlaylistVideo();
            playlistVideo.number = number;
            playlistVideo.playlistId = playlist.id;
            playlistVideo.videoId = video.id;
            playlistVideos.add(playlistVideo);
            number++;
        }
        db.zypeDao().insertPlaylistVideos(playlistVideos);
    }

    public void deletePlaylistVideos(Playlist playlist, List<Video> videos) {
        for (Video video : videos) {
            db.zypeDao().deletePlaylistVideo(playlist.id, video.id);
        }
    }

    public Video getVideoSync(String videoId) {
        Video video = db.zypeDao().getVideoSync(videoId);
        if (video != null) {
            if (video.isDownloadedAudio == null) video.isDownloadedAudio = 0;
            if (video.isDownloadedVideo == null) video.isDownloadedVideo = 0;
            if (video.isZypeLive == null) video.isZypeLive = 0;
            if (video.onAir == null) video.onAir = 0;
        }
        return video;
    }

    public void updateVideo(Video video) {
        db.zypeDao().updateVideo(video);
    }

    public void insertVideos(List<Video> videos) {
        db.zypeDao().insertVideos(videos);
    }


}
