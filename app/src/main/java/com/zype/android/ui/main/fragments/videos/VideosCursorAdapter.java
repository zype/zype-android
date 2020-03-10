package com.zype.android.ui.main.fragments.videos;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.service.DownloadHelper;
import com.zype.android.service.DownloaderService;
import com.zype.android.ui.OnVideoItemAction;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.dialog.VideoMenuDialogFragment;
import com.zype.android.utils.FileUtils;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Thumbnail;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class VideosCursorAdapter extends CursorAdapter {

    private static final int ITEM_UNFAVORITE = 0;
    private static final int ITEM_FAVORITE = 1;
    private static final int ITEM_DELETE_AUDIO = 2;
    private static final int ITEM_DELETE_VIDEO = 3;
    private static final int ITEM_DOWNLOAD_STOP = 4;
    private static final int ITEM_DOWNLOAD_VIDEO = 5;
    private static final int ITEM_DOWNLOAD_AUDIO = 6;
    private static final int ITEM_SHARE = 10;
    private final OnLoginAction mOnLoginListener;
    private final OnVideoItemAction mOnVideoItemAction;
    private int COL_VIDEO_ID = -1;
    private int COL_VIDEO_TITLE = -1;
    private int COL_VIDEO_EPISODE = -1;
    private int COL_VIDEO_THUMBNAILS = -1;
    private int COL_VIDEO_IMAGES = -1;
    private int COL_VIDEO_PUBLISHED_AT = -1;
    private int COL_VIDEO_IS_FAVORITE = -1;
    private int COL_VIDEO_IS_PLAY_STARTED = -1;
    private int COL_VIDEO_IS_PLAY_FINSHED = -1;
    private int COL_VIDEO_IS_VIDEO_DOWNLOADED = -1;
    private int COL_VIDEO_IS_AUDIO_DOWNLOADED = -1;
    private int COL_DOWNLOAD_AUDIO_PATH = -1;
    private int COL_DOWNLOAD_VIDEO_PATH = -1;
    private int COL_ON_AIR = -1;
    private int COL_SUBSCRIPTION_REQUIRED = -1;
    private int COL_TRANSCODED = -1;
    private int COL_YOUTUBE = -1;
    private Activity mActivity;

    private boolean showDownloadOptions = false;

    public VideosCursorAdapter(Activity activity, int flags, OnVideoItemAction onVideoItemActionListener, OnLoginAction onLogin) {
        super(activity, null, flags);
        mOnVideoItemAction = onVideoItemActionListener;
        mOnLoginListener = onLogin;
        mActivity = activity;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        View view;
        final VideosViewHolder viewHolder = new VideosViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.list_item_video, parent, false);
        viewHolder.title = ((TextView) view.findViewById(R.id.title));
//        viewHolder.episode = ((TextView) view.findViewById(R.id.episode));
        viewHolder.thumbnail = (ImageView) view.findViewById(R.id.icon);
        viewHolder.imageLocked = (ImageView) view.findViewById(R.id.imageLocked);
//        viewHolder.imageUnlocked = (ImageView) view.findViewById(R.id.imageUnlocked);
        viewHolder.progressBarThumbnail = (ProgressBar) view.findViewById(R.id.progressBarThumbnail);
//        viewHolder.episodeView = view.findViewById(R.id.episode_view);
//        viewHolder.detailsView = view.findViewById(R.id.details_view);
        viewHolder.downloadButton = (ImageView) view.findViewById(R.id.button_download);
        viewHolder.reviewIndicator = (ImageView) view.findViewById(R.id.review_indicator);
//        viewHolder.episodeDate = (TextView) view.findViewById(R.id.episode_date);

        viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        viewHolder.popupButton = (ImageView) view.findViewById(R.id.button_popup);

        view.setTag(viewHolder);
        return view;
    }

    private List<VideosMenuItem> getListForMenu(VideosViewHolder viewHolder, boolean isYoutubeVideo) {
        List<VideosMenuItem> list = new ArrayList<>();

        int currentProgress = DownloaderService.currentProgress(viewHolder.videoId);
        if (SettingsProvider.getInstance().isLoggedIn()
                || !ZypeConfiguration.isUniversalSubscriptionEnabled(mActivity)) {
            if (viewHolder.isFavorite) {
                list.add(new VideosMenuItem(ITEM_UNFAVORITE, R.string.menu_unfavorite));
            }
            else {
                list.add(new VideosMenuItem(ITEM_FAVORITE, R.string.menu_favorite));
            }
        }
        if (ZypeConfiguration.isDownloadsEnabled(mActivity) && showDownloadOptions) {
            if (currentProgress > -1) {
                list.add(new VideosMenuItem(ITEM_DOWNLOAD_STOP, R.string.menu_download_stop));
            }
            else {
                if (!isYoutubeVideo && !viewHolder.onAir) {
                    if (viewHolder.isVideoDownloaded) {
                        list.add(new VideosMenuItem(ITEM_DELETE_VIDEO, R.string.menu_download_delete_video));
                    }
//                    else {
//                        list.add(new VideosMenuItem(ITEM_DOWNLOAD_VIDEO, R.string.menu_download_video));
//                    }
//
                    if (viewHolder.isAudioDownloaded) {
                        list.add(new VideosMenuItem(ITEM_DELETE_AUDIO, R.string.menu_download_delete_audio));
                    }
//                    else {
//                        list.add(new VideosMenuItem(ITEM_DOWNLOAD_AUDIO, R.string.menu_download_audio));
//                    }
                }
            }
        }
        if (ZypeSettings.SHARE_VIDEO_ENABLED) {
            list.add(new VideosMenuItem(ITEM_SHARE, R.string.menu_share));
        }
        return list;
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
                if (TextUtils.equals(cursor.getString(COL_VIDEO_ID), fileId)) {
                    pos = cursor.getPosition();
                    break;
                }
            } while (cursor.moveToNext());
        }
        return pos;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final VideosViewHolder viewHolder = (VideosViewHolder) view.getTag();
        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }
        viewHolder.videoId = cursor.getString(COL_VIDEO_ID);
        viewHolder.title.setText(cursor.getString(COL_VIDEO_TITLE));
        viewHolder.thumbnail.setImageDrawable(null);
        viewHolder.isFavorite = cursor.getInt(COL_VIDEO_IS_FAVORITE) == 1;
//        viewHolder.isDownloaded = cursor.getInt(COL_VIDEO_IS_DOWNLOADED) == 1;
        viewHolder.isVideoDownloaded = cursor.getInt(COL_VIDEO_IS_VIDEO_DOWNLOADED) == 1;
        viewHolder.isAudioDownloaded = cursor.getInt(COL_VIDEO_IS_AUDIO_DOWNLOADED) == 1;
        viewHolder.videoPath = cursor.getString(COL_DOWNLOAD_VIDEO_PATH);
        viewHolder.isTranscoded = cursor.getInt(COL_TRANSCODED) == 1;
        viewHolder.onAir = cursor.getInt(COL_ON_AIR) == 1;
        viewHolder.subscriptionRequired = cursor.getInt(COL_SUBSCRIPTION_REQUIRED) == 1;
        viewHolder.youtubePath = cursor.getString(COL_YOUTUBE);
//        if (TextUtils.isEmpty(cursor.getString(COL_VIDEO_EPISODE))) {
//            viewHolder.episodeView.setVisibility(GONE);
//        } else {
//            viewHolder.episodeView.setVisibility(View.VISIBLE);
//            viewHolder.episode.setText(cursor.getString(COL_VIDEO_EPISODE));
//        }
        viewHolder.isEntitled = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.IS_ENTITLED)) == 1;
        viewHolder.purchaseRequired = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.PURCHASE_REQUIRED)) == 1;

        loadThumbnail(viewHolder, cursor);
        updateLockIcon(viewHolder);

//        if (cursor.getString(COL_VIDEO_THUMBNAILS) != null) {
//            loadImage(context, cursor, viewHolder);
//        }
//        if (viewHolder.subscriptionRequired) {
//            if (hasPermissionToPlayVideo(viewHolder)) {
//                viewHolder.imageLocked.setVisibility(GONE);
//                viewHolder.imageUnlocked.setVisibility(View.VISIBLE);
//            }
//            else {
//                viewHolder.imageLocked.setVisibility(View.VISIBLE);
//                viewHolder.imageUnlocked.setVisibility(View.GONE);
//            }
//        }
//        else {
//            viewHolder.imageLocked.setVisibility(GONE);
//            viewHolder.imageUnlocked.setVisibility(GONE);
//        }

//        final String episodeCreationDate = DateUtils.getConvertedText(cursor.getString(COL_VIDEO_PUBLISHED_AT), context);
//        if (episodeCreationDate != null) {
//            viewHolder.episodeDate.setText(episodeCreationDate);
//        } else {
//            viewHolder.episodeDate.setText("");
//        }
        int currentProgress = DownloaderService.currentProgress(viewHolder.videoId);
        if (currentProgress > -1) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setProgress(currentProgress);
//            viewHolder.detailsView.setVisibility(GONE);
        } else {
            viewHolder.progressBar.setVisibility(GONE);
//            viewHolder.detailsView.setVisibility(View.VISIBLE);
        }

        final boolean isYoutubeVideo = !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_YOUTUBE_ID)));
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (SettingsProvider.getInstance().isLoggedIn()) {
                mActivity.openContextMenu(v);
                ArrayList<VideosMenuItem> items = new ArrayList<>();
                items.addAll(getListForMenu(viewHolder, isYoutubeVideo));

                final VideoMenuDialogFragment fragment = VideoMenuDialogFragment.newInstance(items);
                fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map<String, String> event;
                        Tracker tracker = ZypeApp.getTracker();
                        switch (fragment.getList().get(position).getId()) {
                            case ITEM_UNFAVORITE:
                                mOnVideoItemAction.onUnFavoriteVideo(viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Unfavorite")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_FAVORITE:
                                mOnVideoItemAction.onFavoriteVideo(viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Favorite")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_SHARE:
                                mOnVideoItemAction.onShareVideo(viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Share")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_DOWNLOAD_STOP:
                                DownloadHelper.stopDownload(mContext.getContentResolver(), viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Stop Download")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_DOWNLOAD_AUDIO:
                                mOnVideoItemAction.onDownloadAudio(viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Download Audio")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_DOWNLOAD_VIDEO:
                                mOnVideoItemAction.onDownloadVideo(viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Download VideoList")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_DELETE_AUDIO:
                                FileUtils.deleteAudioFile(viewHolder.videoId, context);
                                DataHelper.setAudioDeleted(context.getContentResolver(), viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Delete Downloaded Audio")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            case ITEM_DELETE_VIDEO:
                                FileUtils.deleteVideoFile(viewHolder.videoId, context);
                                DataHelper.setVideoDeleted(context.getContentResolver(), viewHolder.videoId);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Delete Downloaded VideoList")
                                        .setLabel("id=" + viewHolder.videoId)
                                        .build();
                                break;
                            default:
                                throw new IllegalStateException("unknown id=" + fragment.getList().get(position).getId());
                        }
                        fragment.dismiss();
                        if (tracker != null) {
                            tracker.send(event);
                        }
                    }
                });
                fragment.show(mActivity.getFragmentManager(), "menu");
//                }
//                else {
//                    mOnLoginListener.onRequestLogin();
//                }
            }
        };
        if (getListForMenu(viewHolder, isYoutubeVideo).isEmpty()) {
            viewHolder.popupButton.setVisibility(GONE);
        }
        else {
            viewHolder.popupButton.setVisibility(View.VISIBLE);
            viewHolder.popupButton.setOnClickListener(onClick);
        }
//        viewHolder.downloadButton.setOnClickListener(onClick);
//        if (viewHolder.isTranscoded && ZypeSettings.isDownloadsEnabled()) {
//            viewHolder.downloadButton.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.downloadButton.setVisibility(View.INVISIBLE);
//        }

//        updateDownloadedIndicator(cursor, viewHolder);
//        updatePlayedIndicator(cursor, viewHolder);
    }

    private void updateDownloadedIndicator(Cursor cursor, VideosViewHolder viewHolder) {
        if (isFileDownloaded(cursor)) {
            if (!TextUtils.isEmpty(cursor.getString(COL_DOWNLOAD_AUDIO_PATH))) {
                viewHolder.downloadButton.setImageResource(R.drawable.icn_audio);
            } else if (!TextUtils.isEmpty(cursor.getString(COL_DOWNLOAD_VIDEO_PATH))) {
                viewHolder.downloadButton.setImageResource(R.drawable.icn_video);
            } else {
                throw new IllegalStateException("DB not contains records about downloaded files for ID=" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_ID)));
            }
        } else {
            viewHolder.downloadButton.setImageResource(R.drawable.icn_cloud);
        }
    }

    private void updatePlayedIndicator(Cursor cursor, VideosViewHolder viewHolder) {
        if (isFileDownloaded(cursor)) {
            if (cursor.getInt(COL_VIDEO_IS_PLAY_STARTED) == 0) {
                viewHolder.reviewIndicator.setVisibility(View.VISIBLE);
                viewHolder.reviewIndicator.setImageResource(R.drawable.ic_play_full);
            } else if (cursor.getInt(COL_VIDEO_IS_PLAY_FINSHED) == 1) {
                viewHolder.reviewIndicator.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.reviewIndicator.setVisibility(View.VISIBLE);
                viewHolder.reviewIndicator.setImageResource(R.drawable.ic_play_half);
            }
        } else {
            viewHolder.reviewIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isFileDownloaded(Cursor cursor) {
        return cursor.getInt(COL_VIDEO_IS_VIDEO_DOWNLOADED) == 1 || cursor.getInt(COL_VIDEO_IS_AUDIO_DOWNLOADED) == 1;
    }

    private void loadImage(final Context context, final Cursor cursor, final VideosViewHolder viewHolder) {
        final String thumbnailsString = cursor.getString(COL_VIDEO_THUMBNAILS);
        if (thumbnailsString != null) {
            Type thumbnailType = new TypeToken<List<Thumbnail>>() {}.getType();
            List<Thumbnail> thumbnails = (new Gson().fromJson(thumbnailsString, thumbnailType));
            Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(thumbnails, 240);
            if (thumbnail != null) {
                UiUtils.loadImage(thumbnail.getUrl(), R.drawable.outline_play_circle_filled_white_white_48, viewHolder.thumbnail);
            }
            else {
                viewHolder.thumbnail.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.outline_play_circle_filled_white_white_48));
            }
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (COL_VIDEO_ID >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        COL_VIDEO_ID = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_ID);
        COL_VIDEO_TITLE = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_TITLE);
        COL_VIDEO_EPISODE = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_EPISODE);
        COL_VIDEO_THUMBNAILS = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_THUMBNAILS);
        COL_VIDEO_IMAGES = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IMAGES);
        COL_VIDEO_PUBLISHED_AT = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_PUBLISHED_AT);
        COL_VIDEO_IS_FAVORITE = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_FAVORITE);
        COL_VIDEO_IS_PLAY_STARTED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_PLAY_STARTED);
        COL_VIDEO_IS_PLAY_FINSHED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_PLAY_FINISHED);
        COL_VIDEO_IS_VIDEO_DOWNLOADED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO);
        COL_VIDEO_IS_AUDIO_DOWNLOADED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO);
        COL_DOWNLOAD_AUDIO_PATH = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_AUDIO_PATH);
        COL_DOWNLOAD_VIDEO_PATH = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_DOWNLOAD_VIDEO_PATH);
        COL_ON_AIR = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_ON_AIR);
        COL_SUBSCRIPTION_REQUIRED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_SUBSCRIPTION_REQUIRED);
        COL_TRANSCODED = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_TRANSCODED);
        COL_YOUTUBE = cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_YOUTUBE_ID);
    }

    public void setShowDownloadOptions(boolean show) {
        showDownloadOptions = show;
    }

    private boolean hasPermissionToPlayVideo(VideosViewHolder holder) {
        if (ZypeConfiguration.isUniversalTVODEnabled(mContext) && holder.purchaseRequired) {
            if (holder.isEntitled) {
                return true;
            }
        }
        if (holder.subscriptionRequired) {
            if (SubscriptionsHelper.isUserSubscribed(mActivity)) {
                return true;
            }
        }
        return false;
    }

    private void loadThumbnail(VideosViewHolder holder, final Cursor cursor) {
        if (!TextUtils.isEmpty(cursor.getString(COL_VIDEO_THUMBNAILS))) {
            Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(cursor, 240);
            if (thumbnail != null) {
                UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, holder.thumbnail);
            }
            else {
                holder.thumbnail.setImageDrawable(ContextCompat.getDrawable(holder.thumbnail.getContext(),
                        R.drawable.placeholder_video));
            }
        }
    }

    private void updateLockIcon(VideosViewHolder holder) {
        if (AuthHelper.isPaywalledVideo(holder.thumbnail.getContext(), holder.videoId, null)) {
            holder.imageLocked.setVisibility(View.VISIBLE);
            if (AuthHelper.isVideoUnlocked(holder.thumbnail.getContext(), holder.videoId, null)) {
                holder.imageLocked.setImageResource(R.drawable.baseline_lock_open_white_18);
                UiUtils.setImageColor(holder.imageLocked,
                        ContextCompat.getColor(holder.thumbnail.getContext(), R.color.icon_unlocked));
            }
            else {
                holder.imageLocked.setImageResource(R.drawable.baseline_lock_white_18);
                UiUtils.setImageColor(holder.imageLocked,
                        ContextCompat.getColor(holder.thumbnail.getContext(), R.color.icon_locked));
            }
        }
        else {
            holder.imageLocked.setVisibility(GONE);
        }
    }

    public class VideosViewHolder {
        public boolean isFavorite;
        public ProgressBar progressBar;
        public boolean isTranscoded;
        public String videoId;
        //        public View detailsView;
        public TextView title;
        public TextView episode;
        //        public TextView episodeDate;
        public ImageView thumbnail;
        public ProgressBar progressBarThumbnail;
        public ImageView imageLocked;
        public ImageView imageUnlocked;
        //        public View episodeView;
        public boolean isVideoDownloaded;
        public boolean isAudioDownloaded;
        public boolean onAir;
        public boolean subscriptionRequired;
        public boolean isEntitled;
        public boolean purchaseRequired;

        ImageView downloadButton;
        String videoPath;
        String youtubePath;
        ImageView popupButton;
        ImageView reviewIndicator;
    }

}
