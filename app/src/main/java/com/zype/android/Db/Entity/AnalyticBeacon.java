package com.zype.android.Db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Evgeny Cherkasov on 19.06.2018
 */

@Entity(tableName = "analytic_beacon")
public class AnalyticBeacon {

    @PrimaryKey
    @ColumnInfo(name = "beacon_id")
    @NonNull
    public Integer id;

    @ColumnInfo(name = "beacon")
    @NonNull
    public String beacon;

    @ColumnInfo(name = "device")
    @NonNull
    public String device;

    @ColumnInfo(name = "player_id")
    @NonNull
    public String playerId;

    @ColumnInfo(name = "site_id")
    @NonNull
    public String siteId;

    @ColumnInfo(name = "video_id")
    @NonNull
    public String videoId;

}
