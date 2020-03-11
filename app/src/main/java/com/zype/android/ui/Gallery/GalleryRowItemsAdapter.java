package com.zype.android.ui.Gallery;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.Db.Entity.PlaylistItem;
import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.model.video.Image;
import com.zype.android.webapi.model.video.Thumbnail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Evgeny Cherkasov on 14.06.2018
 */
public class GalleryRowItemsAdapter extends RecyclerView.Adapter<GalleryRowItemsAdapter.ViewHolder> {
    private List<? extends PlaylistItem> items;
    private String playlistId;
    private boolean usePoster;

    public GalleryRowItemsAdapter(boolean usePoster) {
        items = new ArrayList<>();
         this.usePoster = usePoster;
    }

    public void setData(List<? extends PlaylistItem> items, String playlistId) {
        this.items = items;
        this.playlistId = playlistId;
        notifyDataSetChanged();
    }

    @Override
    public GalleryRowItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (usePoster) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_row_poster_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_row_list_item, parent, false);
        }
        return new GalleryRowItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GalleryRowItemsAdapter.ViewHolder holder, int position) {
        holder.item = items.get(position);

        updateTitle(holder);
        updateInlineTitle(holder);
        loadThumbnail(holder);
        updateLockIcon(holder);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper navigationHelper = NavigationHelper.getInstance(holder.view.getContext());
                if (holder.item instanceof Video) {
                    Video video = (Video) holder.item;
                    navigationHelper.handleVideoClick((Activity) holder.view.getContext(), video, playlistId, false);
                }
                else if (holder.item instanceof Playlist) {
                    Playlist playlist = (Playlist) holder.item;
                    navigationHelper.handlePlaylistClick((Activity) holder.view.getContext(), playlist);
                }
            }
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
        public PlaylistItem item;
        public FrameLayout layoutTitle;
        public TextView textTitle;
        public TextView textInlineTitle;
        public ImageView imageThumbnail;
        public ImageView imageLocked;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            layoutTitle = view.findViewById(R.id.layoutTitle);
            textTitle = view.findViewById(R.id.textTitle);
            textInlineTitle = view.findViewById(R.id.textInlineTitle);
            imageThumbnail = view.findViewById(R.id.imageThumbnail);
            imageLocked = view.findViewById(R.id.imageLocked);
        }
    }

    private void loadThumbnail(ViewHolder holder) {
        if (holder.item instanceof Video) {
            Video video = (Video) holder.item;

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
        else if (holder.item instanceof Playlist) {
            Playlist playlist = (Playlist) holder.item;

            boolean thumbnailAssigned = false;
            if (usePoster && playlist.images != null) {
                Image posterThumbnail = VideoHelper.getPosterThumbnail(playlist);
                if (posterThumbnail != null) {
                    thumbnailAssigned = true;
                    UiUtils.loadImage(posterThumbnail.getUrl(), R.drawable.outline_video_library_white_48, holder.imageThumbnail);
                }
            }

            if (playlist.thumbnails != null && !thumbnailAssigned) {
                Type thumbnailType = new TypeToken<List<Thumbnail>>(){}.getType();
                List<Thumbnail> thumbnails = new Gson().fromJson(playlist.thumbnails, thumbnailType);
                Thumbnail thumbnail = VideoHelper.getThumbnailByHeight(thumbnails, 240);
                if (thumbnail != null) {
                    UiUtils.loadImage(thumbnail.getUrl(), R.drawable.outline_video_library_white_48, holder.imageThumbnail);
                }
                else {
                    holder.imageThumbnail.setImageDrawable(ContextCompat.getDrawable(holder.view.getContext(),
                            R.drawable.outline_video_library_white_48));
                }
            }
        }
    }

    private void updateTitle(ViewHolder holder) {
        if (ZypeConfiguration.playlistGalleryItemTitles(holder.view.getContext())) {
            holder.layoutTitle.setVisibility(View.VISIBLE);
            holder.textTitle.setText(holder.item.getTitle());
        }
        else {
            holder.layoutTitle.setVisibility(GONE);
        }
    }

    private void updateInlineTitle(ViewHolder holder) {
        if (ZypeConfiguration.playlistGalleryItemInlineTitles()) {
            holder.textInlineTitle.setVisibility(View.VISIBLE);
            holder.textInlineTitle.setText(holder.item.getTitle());
        }
        else {
            holder.textInlineTitle.setVisibility(GONE);
        }
    }

    private void updateLockIcon(ViewHolder holder) {
        if (holder.item instanceof Video) {
            Video video = (Video) holder.item;
            if (AuthHelper.isPaywalledVideo(holder.view.getContext(), video.id, playlistId)) {
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
        else {
            holder.imageLocked.setVisibility(GONE);
        }
    }

}

