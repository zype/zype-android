package com.zype.android.Db.Entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    public String device;

    @ColumnInfo(name = "player_id")
    public String playerId;

    @ColumnInfo(name = "site_id")
    public String siteId;

    @ColumnInfo(name = "video_id")
    @NonNull
    public String videoId;

}
