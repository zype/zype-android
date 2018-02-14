package com.zype.android.core.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.zype.android.core.db.ZypeDatabase;
import com.zype.android.utils.Logger;

import java.util.Arrays;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class ZypeContentProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = Contract.CONTENT_AUTHORITY;

    private static final int URI_MATCHER_CODE_VIDEOS = 1;
    private static final int URI_MATCHER_CODE_FAVORITES = 10;
    private static final int URI_MATCHER_CODE_PLAYLISTS = 20;
    private static final int URI_MATCHER_CODE_PLAYLIST_VIDEO = 30;
    private static final int URI_MATCHER_CODE_AD_SCHEDULE = 40;
    private static final int URI_MATCHER_CODE_ANALYTICS_BEACON = 50;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.TABLE_NAME_VIDEO, URI_MATCHER_CODE_VIDEOS);

        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.TABLE_NAME_FAVORITE, URI_MATCHER_CODE_FAVORITES);

        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.TABLE_NAME_PLAYLIST, URI_MATCHER_CODE_PLAYLISTS);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.PlaylistVideo.TABLE_NAME, URI_MATCHER_CODE_PLAYLIST_VIDEO);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.AdSchedule.TABLE_NAME, URI_MATCHER_CODE_AD_SCHEDULE);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, Contract.AnalyticBeacon.TABLE_NAME, URI_MATCHER_CODE_ANALYTICS_BEACON);
    }


    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new ZypeDatabase(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_VIDEOS:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.TABLE_NAME_VIDEO,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            case URI_MATCHER_CODE_FAVORITES:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.TABLE_NAME_FAVORITE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            case URI_MATCHER_CODE_PLAYLISTS:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.TABLE_NAME_PLAYLIST,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            case URI_MATCHER_CODE_PLAYLIST_VIDEO:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.TABLE_NAME_VIDEO + " INNER JOIN " + Contract.PlaylistVideo.TABLE_NAME + " ON " + Contract.TABLE_NAME_VIDEO + "." + Contract.Video.COLUMN_ID + "=" + Contract.PlaylistVideo.VIDEO_ID,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            case URI_MATCHER_CODE_AD_SCHEDULE:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.AdSchedule.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;

            case URI_MATCHER_CODE_ANALYTICS_BEACON:
                cursor = sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                Contract.AnalyticBeacon.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final long insertedId;
        Logger.d("inserting" + uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_VIDEOS:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                Contract.TABLE_NAME_VIDEO,
                                null,
                                values
                        );
                break;
            case URI_MATCHER_CODE_FAVORITES:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                Contract.TABLE_NAME_FAVORITE,
                                null,
                                values
                        );
                break;
            case URI_MATCHER_CODE_PLAYLISTS:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                Contract.TABLE_NAME_PLAYLIST,
                                null,
                                values
                        );
                break;
            case URI_MATCHER_CODE_PLAYLIST_VIDEO:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                Contract.PlaylistVideo.TABLE_NAME,
                                null,
                                values
                        );
                break;
            case URI_MATCHER_CODE_ANALYTICS_BEACON:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                Contract.AnalyticBeacon.TABLE_NAME,
                                null,
                                values
                        );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " ContentValues=" + Logger.getObjectDump(values));
        }
        if (insertedId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, insertedId);
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int numberOfRowsAffected;


        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_VIDEOS:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                Contract.TABLE_NAME_VIDEO,
                                values,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_FAVORITES:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                Contract.TABLE_NAME_FAVORITE,
                                values,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_PLAYLISTS:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                Contract.TABLE_NAME_PLAYLIST,
                                values,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_PLAYLIST_VIDEO:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                Contract.PlaylistVideo.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs
                        );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        }
        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Logger.e("No one row was updated !!!");
        }
        return numberOfRowsAffected;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int numberOfRowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_VIDEOS:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.TABLE_NAME_VIDEO,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_FAVORITES:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.TABLE_NAME_FAVORITE,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_PLAYLISTS:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.TABLE_NAME_PLAYLIST,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_PLAYLIST_VIDEO:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.PlaylistVideo.TABLE_NAME,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_AD_SCHEDULE:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.AdSchedule.TABLE_NAME,
                                selection,
                                selectionArgs
                        );
                break;
            case URI_MATCHER_CODE_ANALYTICS_BEACON:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                Contract.AnalyticBeacon.TABLE_NAME,
                                selection,
                                selectionArgs
                        );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
//        Logger.i("bulkInsert " + uri + " count:" + values.length);
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        int numberOfRowsInserted = 0;
        int ignoreCount = 0;
        database.beginTransaction();
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_VIDEOS:
                for (ContentValues cv : values) {
                    long newID = database.insertWithOnConflict(Contract.Video.TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
//                    Logger.i("bulkInsert result " + newID + " " + cv.toString());
                    if (newID <= 0) {
                        String id = (String) cv.get(Contract.Video.COLUMN_ID);
                        update(uri, cv, Contract.Video.COLUMN_ID + "=?", new String[]{id});
                    } else {
                        numberOfRowsInserted++;
                    }
                }
                break;
            case URI_MATCHER_CODE_FAVORITES:
                for (ContentValues cv : values) {
                    long newID = database.insertWithOnConflict(Contract.Favorite.TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
//                    Logger.i("bulkInsert result " + newID + " " + cv.toString());
                    if (newID <= 0) {
                        String id = (String) cv.get(Contract.Favorite.COLUMN_ID);
                        int update = update(uri, cv, Contract.Favorite.COLUMN_ID + "=?", new String[]{id});
                        if (update < 0) {
                            Logger.e("Update problem");
                        }
                    } else {
                        numberOfRowsInserted++;
                    }
                }
                break;
            case URI_MATCHER_CODE_PLAYLISTS:
                for (ContentValues cv : values) {
                    long newID = database.insertWithOnConflict(Contract.Playlist.TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
//                    Logger.i("bulkInsert result " + newID + " " + cv.toString());
                    if (newID <= 0) {
                        String id = (String) cv.get(Contract.Playlist.COLUMN_ID);
                        int update = update(uri, cv, Contract.Playlist.COLUMN_ID + "=?", new String[]{id});
                        if (update < 0) {
                            Logger.e("Update problem");
                        }
                    } else {
                        numberOfRowsInserted++;
                    }
                }
                break;
            case URI_MATCHER_CODE_PLAYLIST_VIDEO:
                for (ContentValues cv : values) {
                    long newID = database.insertWithOnConflict(Contract.PlaylistVideo.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                    if (newID <= 0) {
                        long id = (long) cv.get(Contract.PlaylistVideo.ID);
                        int update = update(uri, cv, Contract.PlaylistVideo.PLAYLIST_ID + "=? AND " + Contract.PlaylistVideo.VIDEO_ID,
                                            new String[] { cv.getAsString(Contract.PlaylistVideo.PLAYLIST_ID), cv.getAsString(Contract.PlaylistVideo.VIDEO_ID) } );
                        if (update < 0) {
                            Logger.e("Update problem");
                        }
                    } else {
                        numberOfRowsInserted++;
                    }
                }
                break;
            case URI_MATCHER_CODE_AD_SCHEDULE:
                for (ContentValues cv : values) {
                    long newID = database.insertWithOnConflict(Contract.AdSchedule.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                    if (newID <= 0) {
                        long id = (long) cv.get(Contract.AdSchedule.ID);
                        int update = update(uri, cv, Contract.AdSchedule.VIDEO_ID, new String[] { cv.getAsString(Contract.AdSchedule.VIDEO_ID) } );
                        if (update < 0) {
                            Logger.e("Update problem");
                        }
                    } else {
                        numberOfRowsInserted++;
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " ContentValues=" + Logger.getObjectDump(values));
        }
//        Logger.d(String.format("bulkInsert result: save=%d ignore=%s", numberOfRowsInserted, ignoreCount));
        database.setTransactionSuccessful();
        database.endTransaction();
        if (numberOfRowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsInserted;
    }
}