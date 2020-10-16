package com.zype.android.Db.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

@Entity(tableName = "playlist_video")
public class PlaylistVideo {

    @PrimaryKey
    @ColumnInfo(name = "playlist_video_id")
    public Integer id;

    @ColumnInfo(name = "number")
    public Integer number;

    @ColumnInfo(name = "playlist_id")
    public String playlistId;

    @ColumnInfo(name = "video_id")
    public String videoId;
}
