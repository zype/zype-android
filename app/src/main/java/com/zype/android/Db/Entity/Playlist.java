package com.zype.android.Db.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ColumnInfo.INTEGER;

/**
 * Created by Evgeny Cherkasov on 13.06.2018
 */

@Entity(tableName = "playlist")
public class Playlist implements PlaylistItem {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NonNull
    public String id;

    @ColumnInfo(name = "active")
    @NonNull
    public Integer active;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "deleted_at")
    public String deletedAt;

    @ColumnInfo(name = "images")
    public String images;

    @ColumnInfo(name = "marketplace_ids")
    public String marketplaceIds;

    @ColumnInfo(name = "thumbnail_layout")
    public String thumbnailLayout;

    @ColumnInfo(name = "parent_id")
    public String parentId;

    @ColumnInfo(name = "playlist_item_count")
    public Integer playlistItemCount;

    @ColumnInfo(name = "priority")
    public Integer priority;

    @ColumnInfo(name = "purchase_price")
    public String purchasePrice;

    @ColumnInfo(name = "purchase_required")
    @NonNull
    public Integer purchaseRequired;

    @ColumnInfo(name = "thumbnails")
    public String thumbnails;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;

    @Override
    public String getTitle() {
        return title;
    }
}
