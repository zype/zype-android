package com.zype.android.ui.v2.videos;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.service.DownloadHelper;
import com.zype.android.service.DownloaderService;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.dialog.VideoMenuDialogFragment;
import com.zype.android.ui.main.fragments.videos.VideosMenuItem;
import com.zype.android.utils.FileUtils;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Image;
import com.zype.android.webapi.model.video.Thumbnail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    private List<Video> items;
    private String playlistId;
    boolean usePoster = false;
    private boolean showDownloadOptions = false;
    private IPopupMenu menuListener;
    private IVideoListener videoListener;

    private static final int ITEM_UNFAVORITE = 0;
    private static final int ITEM_FAVORITE = 1;
    private static final int ITEM_DELETE_AUDIO = 2;
    private static final int ITEM_DELETE_VIDEO = 3;
    private static final int ITEM_DOWNLOAD_STOP = 4;
    private static final int ITEM_DOWNLOAD_VIDEO = 5;
    private static final int ITEM_DOWNLOAD_AUDIO = 6;
    private static final int ITEM_SHARE = 10;

    public interface IPopupMenu {
        void onMenuItemSelected(int action, Video video);
    }

    public interface IVideoListener {
        void onVideoClicked(Video video);
    }

    public VideosAdapter(String playlistId) {
        items = new ArrayList<>();
        this.playlistId = playlistId;
    }

    public void setPopupMenuListener(IPopupMenu listener) {
        this.menuListener = listener;
    }

    public void setVideoListener(IVideoListener listener) {
        this.videoListener = listener;
    }

    public void setData(List<Video> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_list_item, parent, false);
        ViewHolder holder = new VideosAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.textTitle.setText(holder.item.title);
        updateInfo(holder);
        loadThumbnail(holder);
        updateLockIcon(holder);
        updateDownloadProgress(holder);
        updatePopupMenu(holder);
        holder.view.setOnClickListener(v -> {
            if (videoListener != null) {
                videoListener.onVideoClicked(holder.item);
            }
//            NavigationHelper navigationHelper = NavigationHelper.getInstance(holder.view.getContext());
//            Video video = holder.item;
//            navigationHelper.handleVideoClick((Activity) holder.view.getContext(), video, playlistId, false);
        });
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        else {
            return items.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public Video item;
        public TextView textTitle;
        public TextView textInfo;
        public ImageView imageLocked;
        public ImageView imagePopup;
        public ImageView imageThumbnail;
        public ProgressBar progressDownload;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textTitle = view.findViewById(R.id.textTitle);
            textInfo = view.findViewById(R.id.textInfo);
            imageLocked = view.findViewById(R.id.imageLocked);
            imagePopup = view.findViewById(R.id.imagePopup);
            imageThumbnail = view.findViewById(R.id.imageThumbnail);
            progressDownload = view.findViewById(R.id.progressDownload);
        }
    }

    private void updateInfo(ViewHolder holder) {
        String info = "";
        String episode = "";
        if (!TextUtils.isEmpty(holder.item.episode)) {
            episode = String.format(holder.view.getContext().getString(R.string.videos_episode), holder.item.episode);
        }
        if (!TextUtils.isEmpty(episode)) {
            info += episode;
        }
        holder.textInfo.setText(info);
    }

    private void loadThumbnail(ViewHolder holder) {
        Video video = holder.item;

        boolean thumbnailAssigned = false;
        if (usePoster && video.images != null) {
            Image posterThumbnail = VideoHelper.getPosterThumbnail(video);
            if (posterThumbnail != null) {
                thumbnailAssigned = true;
                UiUtils.loadImage(posterThumbnail.getUrl(), R.drawable.placeholder_video, holder.imageThumbnail);
            }
        }

        if (video.thumbnails != null && !thumbnailAssigned) {
            Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(video, 240);
            if (thumbnail != null) {
                UiUtils.loadImage(thumbnail.getUrl(), R.drawable.placeholder_video, holder.imageThumbnail);
            }
            else {
                holder.imageThumbnail.setImageDrawable(ContextCompat.getDrawable(holder.view.getContext(),
                        R.drawable.placeholder_video));
            }
        }
    }

    private void updateLockIcon(ViewHolder holder) {
        Video video = holder.item;
        if (!TextUtils.isEmpty(playlistId)
                && AuthHelper.isPaywalledVideo(holder.view.getContext(), video.id, playlistId)) {
            holder.imageLocked.setVisibility(View.VISIBLE);
            if (AuthHelper.isVideoUnlocked(holder.view.getContext(), video.id, playlistId)) {
                holder.imageLocked.setImageResource(R.drawable.baseline_lock_open_white_18);
                UiUtils.setImageColor(holder.imageLocked,
                        ContextCompat.getColor(holder.view.getContext(), R.color.icon_unlocked));
            }
            else {
                holder.imageLocked.setImageResource(R.drawable.baseline_lock_white_18);
                UiUtils.setImageColor(holder.imageLocked,
                        ContextCompat.getColor(holder.view.getContext(), R.color.icon_locked));
            }
        }
        else {
            holder.imageLocked.setVisibility(GONE);
        }
    }

    private void updateDownloadProgress(ViewHolder holder) {
        int currentProgress = DownloaderService.currentProgress(holder.item.id);
        if (currentProgress > -1) {
            holder.progressDownload.setVisibility(View.VISIBLE);
            holder.progressDownload.setProgress(currentProgress);
        }
        else {
            holder.progressDownload.setVisibility(GONE);
        }
    }

    private void updatePopupMenu(final ViewHolder holder) {
        final ArrayList<VideosMenuItem> items = new ArrayList<>(getPopupMenuItems(holder));
        View.OnClickListener listener = view -> {
            holder.view.showContextMenu();

            final VideoMenuDialogFragment fragment = VideoMenuDialogFragment.newInstance(items);
            fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, String> event;
                    Tracker tracker = ZypeApp.getTracker();
                    switch (fragment.getList().get(position).getId()) {
                        case ITEM_UNFAVORITE:
                            if (menuListener != null) {
                                menuListener.onMenuItemSelected(VideoActionsHelper.ACTION_UNFAVORITE, holder.item);
                            }
//                            VideoActionsHelper.onUnfavorite(holder.item, (Activity) holder.view.getContext(), null);
                            event = new HitBuilders.EventBuilder()
                                    .setAction("Unfavorite")
                                    .setLabel("id=" + holder.item.id)
                                    .build();
                            break;
                        case ITEM_FAVORITE:
                            if (menuListener != null) {
                                menuListener.onMenuItemSelected(VideoActionsHelper.ACTION_FAVORITE, holder.item);
                            }
//                            VideoActionsHelper.onFavorite(holder.item, (Activity) holder.view.getContext(), null);
                            event = new HitBuilders.EventBuilder()
                                    .setAction("Favorite")
                                    .setLabel("id=" + holder.item.id)
                                    .build();
                            break;
//                            case ITEM_SHARE:
//                                videoActionListener.onShareVideo(holder.item.id);
//                                event = new HitBuilders.EventBuilder()
//                                        .setAction("Share")
//                                        .setLabel("id=" + holder.item.id)
//                                        .build();
//                                break;
                            case ITEM_DOWNLOAD_STOP:
                                DownloadHelper.stopDownload(holder.view.getContext().getContentResolver(), holder.item.id);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Stop Download")
                                        .setLabel("id=" + holder.item.id)
                                        .build();
                                break;
//                            case ITEM_DOWNLOAD_AUDIO:
//                                videoActionListener.onDownloadAudio(holder.item.id);
//                                event = new HitBuilders.EventBuilder()
//                                        .setAction("Download Audio")
//                                        .setLabel("id=" + holder.item.id)
//                                        .build();
//                                break;
//                            case ITEM_DOWNLOAD_VIDEO:
//                                videoActionListener.onDownloadVideo(holder.item.id);
//                                event = new HitBuilders.EventBuilder()
//                                        .setAction("Download VideoList")
//                                        .setLabel("id=" + holder.item.id)
//                                        .build();
//                                break;
                            case ITEM_DELETE_AUDIO:
                                FileUtils.deleteAudioFile(holder.item.id, holder.view.getContext());
                                DataHelper.setAudioDeleted(holder.view.getContext().getContentResolver(), holder.item.id);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Delete Downloaded Audio")
                                        .setLabel("id=" + holder.item.id)
                                        .build();
                                break;
                            case ITEM_DELETE_VIDEO:
                                FileUtils.deleteVideoFile(holder.item.id, holder.view.getContext());
                                DataHelper.setVideoDeleted(holder.view.getContext().getContentResolver(), holder.item.id);
                                event = new HitBuilders.EventBuilder()
                                        .setAction("Delete Downloaded VideoList")
                                        .setLabel("id=" + holder.item.id)
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
            fragment.show(((Activity) holder.view.getContext()).getFragmentManager(), "menu");
//            }
        };
        if (items.isEmpty()) {
            holder.imagePopup.setVisibility(GONE);
        }
        else {
            holder.imagePopup.setVisibility(View.VISIBLE);
            holder.imagePopup.setOnClickListener(listener);
        }

    }

    private List<VideosMenuItem> getPopupMenuItems(ViewHolder holder) {
        List<VideosMenuItem> list = new ArrayList<>();

        int currentProgress = DownloaderService.currentProgress(holder.item.id);
        if (AuthHelper.isLoggedIn()
                || !ZypeApp.get(holder.view.getContext()).getAppConfiguration().hideFavoritesActionWhenSignedOut) {
            if (holder.item.isFavorite != null && holder.item.isFavorite == 1) {
                list.add(new VideosMenuItem(ITEM_UNFAVORITE, R.string.menu_unfavorite));
            }
            else {
                list.add(new VideosMenuItem(ITEM_FAVORITE, R.string.menu_favorite));
            }
        }
        if (ZypeConfiguration.isDownloadsEnabled(holder.view.getContext()) && showDownloadOptions) {
            if (currentProgress > -1) {
                list.add(new VideosMenuItem(ITEM_DOWNLOAD_STOP, R.string.menu_download_stop));
            }
            else {
                if (holder.item.onAir != 1) {
                    if (holder.item.isDownloadedVideo == 1) {
                        list.add(new VideosMenuItem(ITEM_DELETE_VIDEO, R.string.menu_download_delete_video));
                    }
//                    else {
//                        list.add(new VideosMenuItem(ITEM_DOWNLOAD_VIDEO, R.string.menu_download_video));
//                    }
//
                    if (holder.item.isDownloadedAudio == 1) {
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

    public void setShowDownloadOptions(boolean show) {
        showDownloadOptions = show;
    }


}
