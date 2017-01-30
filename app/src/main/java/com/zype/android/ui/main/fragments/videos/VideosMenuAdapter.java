package com.zype.android.ui.main.fragments.videos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zype.android.R;

import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/25/15
 */
public class VideosMenuAdapter extends ArrayAdapter<VideosMenuItem> {

    public VideosMenuAdapter(Context context, int resource, List<VideosMenuItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideosMenuItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_option_dialog, parent, false);
            viewHolder.tvName = ((TextView) convertView.findViewById(android.R.id.text1));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.id = item.id;
        viewHolder.tvName.setText(getContext().getString(item.stringId));
        return convertView;
    }

    public class ViewHolder {
        public TextView tvName;
        public int id;
    }
}
