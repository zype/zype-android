package com.zype.android.Db;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.zype.android.Db.Entity.AdSchedule;
import com.zype.android.Db.Entity.AnalyticBeacon;
import com.zype.android.Db.Entity.FavoriteVideo;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistVideo;
import com.zype.android.Db.Entity.Video;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

@Database(entities = {AdSchedule.class, AnalyticBeacon.class, FavoriteVideo.class, Playlist.class, PlaylistVideo.class, Video.class},
            version = 9)
public abstract class ZypeDb extends RoomDatabase {
    public abstract ZypeDao zypeDao();

    private static ZypeDb INSTANCE;

    public static ZypeDb getDatabase(final Application application) {
        if (INSTANCE == null) {
            synchronized (ZypeDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(application, ZypeDb.class, "zype.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
