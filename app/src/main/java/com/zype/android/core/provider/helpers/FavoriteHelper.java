package com.zype.android.core.provider.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.zype.android.core.provider.Contract;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideoData;

/**
 * @author vasya
 * @version 1
 *          date 7/8/15
 */
public class FavoriteHelper {

    @NonNull
    public static ContentValues objectToContentValues(@NonNull ConsumerFavoriteVideoData favorite) {
        final ContentValues contentValues = new ContentValues(6); // wow, such optimization

        contentValues.put(Contract.Favorite.COLUMN_ID, favorite.getId());
        contentValues.put(Contract.Favorite.COLUMN_CREATED_AT, favorite.getCreatedAt());
        contentValues.put(Contract.Favorite.COLUMN_CONSUMER_ID, favorite.getConsumerId());
        contentValues.put(Contract.Favorite.COLUMN_UPDATED_AT, favorite.getUpdatedAt());
        contentValues.put(Contract.Favorite.COLUMN_DELETED_AT, favorite.getDeletedAt());
        contentValues.put(Contract.Favorite.COLUMN_VIDEO_ID, favorite.getVideoId());
        return contentValues;
    }

    @NonNull
    public static ConsumerFavoriteVideoData objectFromCursor(@NonNull Cursor cursor) {
        ConsumerFavoriteVideoData favorite = new ConsumerFavoriteVideoData();

        favorite.setId(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_ID)));
        favorite.setCreatedAt(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_CREATED_AT)));
        favorite.setConsumerId(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_CONSUMER_ID)));
        favorite.setDeletedAt(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_DELETED_AT)));
        favorite.setUpdatedAt(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_UPDATED_AT)));
        favorite.setVideoId(cursor.getString(cursor.getColumnIndex(Contract.Favorite.COLUMN_VIDEO_ID)));
        return favorite;
    }
}
