package com.zype.android.ui.main.fragments.playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zype.android.R;
import com.zype.android.core.provider.Contract;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.playlist.Image;
import com.zype.android.webapi.model.video.Thumbnail;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.List;

public class PlaylistCursorAdapter extends CursorAdapter {

    private final OnLoginAction mOnLoginListener;
    private final OnVideoItemAction mOnVideoItemAction;
    private int COL_PLAYLIST_ID = -1;
    private int COL_PLAYLIST_TITLE = -1;
    private int COL_PLAYLIST_THUMBNAILS = -1;
    private int COL_PLAYLIST_PARENT_ID = -1;
    private int COL_PLAYLIST_ITEM_COUNT = -1;
    private int COL_PLAYLIST_IMAGES = -1;

    // Title of playlist thumbnail
    private String PLAYLIST_THUMBNAIL_TITLE = "mobile";

    private Activity mActivity;

    public PlaylistCursorAdapter(Activity activity, int flags, OnVideoItemAction onVideoItemActionListener, OnLoginAction onLogin) {
        super(activity, null, flags);
        mOnVideoItemAction = onVideoItemActionListener;
        mOnLoginListener = onLogin;
        mActivity = activity;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        View view;
        final PlaylistViewHolder viewHolder = new PlaylistViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.list_item_playlist, parent, false);
        viewHolder.title = ((TextView) view.findViewById(R.id.title));
        viewHolder.thumbnail = (ImageView) view.findViewById(R.id.icon);
        viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.setTag(viewHolder);
        return view;
    }



    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public int getPositionById(String fileId) {
        Cursor cursor = getCursor();
        if (cursor != null && !isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }
        int pos = -1;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                if (TextUtils.equals(cursor.getString(COL_PLAYLIST_ID), fileId)) {
                    pos = cursor.getPosition();
                    break;
                }
            } while (cursor.moveToNext());
        }
        return pos;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final PlaylistViewHolder viewHolder = (PlaylistViewHolder) view.getTag();
        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        viewHolder.title.setText(cursor.getString(COL_PLAYLIST_TITLE));
        //viewHolder.thumbnail.setImageDrawable(null);

        if (cursor.getString(COL_PLAYLIST_THUMBNAILS) != null) {
            loadImage(context, cursor, viewHolder);
        }

        viewHolder.playlistId = cursor.getString(COL_PLAYLIST_ID);
        viewHolder.parentId = cursor.getString(COL_PLAYLIST_PARENT_ID);
        viewHolder.playlistItemCount = cursor.getInt(COL_PLAYLIST_ITEM_COUNT);
       /* viewHolder.isFavorite = cursor.getInt(COL_VIDEO_IS_FAVORITE) == 1;
//        viewHolder.isDownloaded = cursor.getInt(COL_VIDEO_IS_DOWNLOADED) == 1;
        viewHolder.isVideoDownloaded = cursor.getInt(COL_VIDEO_IS_VIDEO_DOWNLOADED) == 1;
        viewHolder.isAudioDownloaded = cursor.getInt(COL_VIDEO_IS_AUDIO_DOWNLOADED) == 1;
        viewHolder.videoPath = cursor.getString(COL_DOWNLOAD_VIDEO_PATH);
        viewHolder.isTranscoded = cursor.getInt(COL_TRANSCODED) == 1;
        viewHolder.youtubePath = cursor.getString(COL_YOUTUBE);
        if (TextUtils.isEmpty(cursor.getString(COL_VIDEO_EPISODE))) {
            viewHolder.episodeView.setVisibility(View.GONE);
        } else {
            viewHolder.episodeView.setVisibility(View.VISIBLE);
            viewHolder.episode.setText(cursor.getString(COL_VIDEO_EPISODE));
        }



        final String episodeCreationDate = DateUtils.getConvertedText(cursor.getString(COL_VIDEO_PUBLISHED_AT), context);
        if (episodeCreationDate != null) {
            viewHolder.episodeDate.setText(episodeCreationDate);
        } else {
            viewHolder.episodeDate.setText("");
        }
        int currentProgress = DownloaderService.currentProgress(viewHolder.videoId);
        if (currentProgress > -1) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setProgress(currentProgress);
            viewHolder.detailsView.setVisibility(View.GONE);
        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.detailsView.setVisibility(View.VISIBLE);
        }

        final boolean isYoutubeVideo = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_YOUTUBE_ID)));
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        viewHolder.popupButton.setOnClickListener(onClick);
        viewHolder.downloadButton.setOnClickListener(onClick);
        if (viewHolder.isTranscoded) {
            viewHolder.downloadButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.downloadButton.setVisibility(View.INVISIBLE);
        }*/
    }




    private void loadImage(final Context context, final Cursor cursor, final PlaylistViewHolder viewHolder) {
        final String thumbnailsString = cursor.getString(COL_PLAYLIST_THUMBNAILS);
        final String imagesString = cursor.getString(COL_PLAYLIST_IMAGES);

        String placeholderUrl = "https://placeholdit.imgix.net/~text?txtsize=40&txt=No%20thumbnail%20available&w=720&h=240";

        // Find playlist thumbnail
        Image playlistMobileThumbnail = null;
        if (imagesString != null) {
            Type imageType = new TypeToken<List<Image>>(){}.getType();
            List<Image> images = (new Gson().fromJson(imagesString, imageType));

            if (images.size() > 0) {
                for (Image image: images) {
                    if (image.getTitle().equals(PLAYLIST_THUMBNAIL_TITLE)) {
                        playlistMobileThumbnail = image;
                        break;
                    }
                }
            }
        }

        if (playlistMobileThumbnail != null) {
            UiUtils.loadImage(context, playlistMobileThumbnail.getUrl(), 0, viewHolder.thumbnail, viewHolder.progressBar);
        }
        else if (thumbnailsString != null) {
            Type thumbnailType = new TypeToken<List<Thumbnail>>() {
            }.getType();
            List<Thumbnail> thumbnails = (new Gson().fromJson(thumbnailsString, thumbnailType));

            if (thumbnails.size() > 0) {
                UiUtils.loadImage(context, thumbnails.get(1).getUrl(), 0, viewHolder.thumbnail, viewHolder.progressBar);
            }
            else {
                UiUtils.loadImage(context, placeholderUrl, 0, viewHolder.thumbnail, viewHolder.progressBar);
            }
        }
        else {
            UiUtils.loadImage(context, placeholderUrl, 0, viewHolder.thumbnail, viewHolder.progressBar);
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (COL_PLAYLIST_ID >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        COL_PLAYLIST_ID = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_ID);
        COL_PLAYLIST_TITLE = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_TITLE);
        COL_PLAYLIST_THUMBNAILS = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_THUMBNAILS);
        COL_PLAYLIST_PARENT_ID = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_PARENT_ID);
        COL_PLAYLIST_ITEM_COUNT = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_PLAYLIST_ITEM_COUNT);
        COL_PLAYLIST_IMAGES = cursor.getColumnIndexOrThrow(Contract.Playlist.COLUMN_IMAGES);
    }

    public class PlaylistViewHolder {
        public String playlistId;
        public TextView title;
        public ImageView thumbnail;
        public ProgressBar progressBar;
        public String parentId;
        public int playlistItemCount;
    }

}
