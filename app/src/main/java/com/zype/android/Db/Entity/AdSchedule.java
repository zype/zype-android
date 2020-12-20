package com.zype.android.Db.Entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zype.android.domain.model.AdBreak;

/**
 * Created by Evgeny Cherkasov on 19.06.2018
 */

@Entity(tableName = "ad_schedule")
public class AdSchedule {

    @PrimaryKey
    @ColumnInfo(name = "ad_schedule_id")
    @NonNull
    public Integer id;

    @ColumnInfo(name = "offset")
    @NonNull
    public Integer offset;

    @ColumnInfo(name = "tag")
    @NonNull
    public String tag;

    @ColumnInfo(name = "video_id")
    @NonNull
    public String videoId;

    public AdBreak toDomain() {
        return new AdBreak(tag, offset.floatValue(), 0f, false);
    }
}
