package com.zype.android.ui.video_details.fragments.options;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zype.android.R;

import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/25/15
 */
public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {
    private List<Options> items;
    String fileId;
    private OptionClickListener optionClickListener;

    public OptionsAdapter(List<Options> objects, String fileId, OptionClickListener listener) {
        items = objects;
        this.fileId = fileId;
        optionClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_options, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Options item = items.get(position);
        holder.id = item.id;
        holder.tvOptionsName.setText(item.title);
        if (item.secondText != null) {
            holder.ivOptionsImage.setVisibility(View.GONE);
            holder.tvOptionsText.setVisibility(View.VISIBLE);
            holder.tvOptionsText.setText(item.secondText);
        } else {
            holder.ivOptionsImage.setVisibility(View.VISIBLE);
            holder.tvOptionsText.setVisibility(View.GONE);
            holder.ivOptionsImage.setImageResource(item.drawableId);
        }
        if (item.progress > -1) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(item.progress);
        }
        else {
            holder.progressBar.setVisibility(View.GONE);
            holder.progressBar.setProgress(0);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void changeList(List<Options> optionsList) {
        items = optionsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvOptionsText;
        public TextView tvOptionsName;
        public ImageView ivOptionsImage;
        public ProgressBar progressBar;
        public String videoId;
        public int id;

        public ViewHolder(View itemView) {
            super(itemView);
            tvOptionsName = ((TextView) itemView.findViewById(R.id.options_title));
            tvOptionsText = ((TextView) itemView.findViewById(R.id.options_text));
            ivOptionsImage = (ImageView) itemView.findViewById(R.id.options_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            optionClickListener.onItemClick( this);
        }
    }

    interface OptionClickListener {
        void onItemClick(ViewHolder viewHolder);
    }

    public Options getItemByOptionId(int id) {
        for (Options item : items) {
            if (item.id == id)
                return item;
        }
        return null;
    }

    public int getItemPosition(Options item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id == item.id) {
                return i;
            }
        }
        return -1;
    }
}
