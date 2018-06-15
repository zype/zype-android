package com.zype.android.ui.Gallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zype.android.R;
import com.zype.android.ui.Gallery.Model.GalleryRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 14.06.2018
 */
public class GalleryRowsAdapter extends RecyclerView.Adapter<GalleryRowsAdapter.ViewHolder> {
    private List<GalleryRow> rows;

    public GalleryRowsAdapter() {
        rows = new ArrayList<>();
    }

    public void setData(List<GalleryRow> items) {
        this.rows = items;
        notifyDataSetChanged();
    }

    @Override
    public GalleryRowsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false);
        return new GalleryRowsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GalleryRowsAdapter.ViewHolder holder, int position) {
        holder.item = rows.get(position);
        holder.textTitle.setText(holder.item.playlist.title);
        if (holder.item.playlist.playlistItemCount > 0) {
            holder.itemsAdapter.setData(holder.item.videos, holder.item.playlist.id);
        }
        else {
            holder.itemsAdapter.setData(holder.item.nestedPlaylists, holder.item.playlist.id);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public GalleryRow item;
        public TextView textTitle;
        public RecyclerView listPlaylistItems;
        public GalleryRowItemsAdapter itemsAdapter;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textTitle = view.findViewById(R.id.textTitle);
            listPlaylistItems = view.findViewById(R.id.listPlaylistItems);

            itemsAdapter = new GalleryRowItemsAdapter();
            listPlaylistItems.setAdapter(itemsAdapter);
        }
    }

}
