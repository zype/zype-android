package com.zype.android.ui.main.fragments.settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zype.android.R;

import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/25/15
 */
public class SettingsListAdapter extends ArrayAdapter<SettingsItem> {
    public SettingsListAdapter(Context context, int resource, List<SettingsItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingsItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_settings, parent, false);
            viewHolder.title = ((TextView) convertView.findViewById(R.id.setting_title));
            viewHolder.icon = ((ImageView) convertView.findViewById(R.id.setting_icon));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.id = item.id;
        viewHolder.title.setText(item.title);
        if (item.iconId != -1) {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(item.iconId);
            viewHolder.icon.setColorFilter(ContextCompat.getColor(getContext(), item.iconColorRes), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        ImageView icon;
        int id;
    }


}
