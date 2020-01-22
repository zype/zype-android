package com.zype.android.Db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Evgeny Cherkasov on 19.06.2018
 */

@Entity(tableName = "favorite")
public class FavoriteVideo {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NonNull
    public String id;

    @ColumnInfo(name = "consumer_id")
    public String consumerId;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "deleted_at")
    public String deletedAt;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;

    @ColumnInfo(name = "video_id")
    @NonNull
    public String videoId;

}
