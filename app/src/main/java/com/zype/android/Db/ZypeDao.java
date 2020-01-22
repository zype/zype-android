package com.zype.android.Db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.FavoriteVideo;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

// TODO: Remove 'public' in method declarations
@Dao
public interface ZypeDao {

    // Ad schedule

    @Query("SELECT * FROM ad_schedule WHERE ad_schedule.video_id = :videoId ORDER BY ad_schedule.'offset'")
    public List<AdSchedule> getAdScheduleSync(String videoId);

    @Query("DELETE FROM ad_schedule WHERE ad_schedule.video_id = :videoId")
    public void deleteAdSchedule(String videoId);

    @Insert(onConflict = IGNORE)
    public void insertAdSchedule(List<AdSchedule> schedule);

    // Analytics beacon

    @Query("SELECT * FROM analytic_beacon WHERE analytic_beacon.video_id = :videoId LIMIT 1")
    public AnalyticBeacon getAnalyticsBeaconSync(String videoId);

    @Query("DELETE FROM analytic_beacon WHERE analytic_beacon.video_id = :videoId")
    public void deleteAnalyticsBeacon(String videoId);

    @Insert(onConflict = IGNORE)
    public void insertAnalyticsBeacon(AnalyticBeacon beacon);

    // Playlist

    @Query("SELECT * FROM playlist WHERE playlist.parent_id = :parentPlaylistId AND playlist.active = 1 ORDER BY playlist.priority")
    public LiveData<List<Playlist>> getPlaylists(String parentPlaylistId);

    @Query("SELECT * FROM playlist WHERE playlist.parent_id = :parentPlaylistId AND playlist.active = 1 ORDER BY playlist.priority")
    public List<Playlist> getPlaylistsSync(String parentPlaylistId);

    @Query("SELECT * FROM playlist WHERE playlist._id = :playlistId LIMIT 1")
    public Playlist getPlaylistSync(String playlistId);

    @Insert(onConflict = REPLACE)
    public void insertPlaylists(List<Playlist> playlists);

    // Video

    /*
     * Retrieve playlist videos
     */
    @Query("SELECT * FROM video INNER JOIN playlist_video ON video._id = playlist_video.video_id " +
            "WHERE playlist_video.playlist_id = :playlistId " +
            "ORDER BY playlist_video.number")
    public LiveData<List<Video>> getPlaylistVideos(String playlistId);

    @Query("SELECT * FROM video INNER JOIN playlist_video ON video._id = playlist_video.video_id " +
            "WHERE playlist_video.playlist_id = :playlistId " +
            "ORDER BY playlist_video.number")
    public List<Video> getPlaylistVideosSync(String playlistId);

    @Query("SELECT * FROM video " +
            "WHERE video.is_favorite = 1 " +
            "ORDER BY video.updated_at DESC")
    public List<Video> getFavoriteVideosSync();

    /*
     * Insert (or update) a list of videos into the playlist
     */
    @Insert(onConflict = REPLACE)
    public void insertPlaylistVideos(List<PlaylistVideo> playlistVideos);

    /*
     * Delete specified video from the playlist
     */
    @Query("DELETE FROM playlist_video WHERE playlist_video.playlist_id = :playlistId " +
            "AND playlist_video.video_id = :videoId")
    public void deletePlaylistVideo(String playlistId, String videoId);

    /*
     * Delete all videos from the playlist
     */
    @Query("DELETE FROM playlist_video WHERE playlist_video.playlist_id = :playlistId")
    public void deletePlaylistVideos(String playlistId);

    /*
     * Retrieve a video by its 'id'
     */
    @Query("SELECT * FROM video WHERE video._id = :videoId LIMIT 1")
    public Video getVideoSync(String videoId);

    /*
     * Update video. Existing video will be replaced
     */
    @Update(onConflict = REPLACE)
    public void updateVideo(Video video);

    /*
     * Insert a list of videos. Existing videos will be replaced
     */
    @Insert(onConflict = REPLACE)
    public void insertVideos(List<Video> videos);

    // Video entitlements

    @Query("SELECT * FROM video " +
            "WHERE video.is_entitled = 1 " +
            "ORDER BY video.entitlement_updated_at DESC")
    List<Video> getEntitledVideosSync();

    @Query("UPDATE video SET is_entitled = 0 WHERE video.is_entitled = 1")
    void clearVideoEntitlements();

    // Video favorites

    @Query("SELECT * FROM favorite")
    public List<FavoriteVideo> getVideoFavorites();

    @Query("SELECT * FROM favorite WHERE favorite.video_id = :videoId LIMIT 1")
    public FavoriteVideo getVideoFavoriteByVideoId(String videoId);

    @Insert(onConflict = REPLACE)
    public void addVideoFavorite(FavoriteVideo favoriteVideo);

    @Query("DELETE FROM favorite WHERE favorite.video_id = :videoId")
    public void deleteVideoFavoriteByVideoId(String videoId);

    @Query("DELETE FROM favorite")
    public void deleteVideoFavorites();
}
