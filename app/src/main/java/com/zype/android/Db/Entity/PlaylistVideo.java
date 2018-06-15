package com.zype.android.Db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
