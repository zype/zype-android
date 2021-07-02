package com.zype.android.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ui.Gallery.GalleryFragment;
import com.zype.android.ui.epg.EPGFragment;
import com.zype.android.ui.v2.library.LibraryFragment;
import com.zype.android.ui.main.Model.Section;
import com.zype.android.ui.main.fragments.download.DownloadFragment;
import com.zype.android.ui.main.fragments.playlist.PlaylistFragment;
import com.zype.android.ui.main.fragments.settings.SettingsFragment;
import com.zype.android.ui.v2.favorites.FavoritesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static android.view.View.GONE;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_ID_HOME = 1;
    private static final int TAB_ID_DOWNLOADS = 2;
    private static final int TAB_ID_FAVORITES = 3;
    private static final int TAB_ID_LIBRARY = 4;
    private static final int TAB_ID_SETTINGS = 5;

    private Context context;
    private List<TabData> tabs;
    Map<Integer, Section> sections;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public void setData(Map<Integer, Section> sections) {
        this.sections = sections;
        notifyDataSetChanged();
    }

    public int getSectionPosition(int id) {
        List<Integer> keys = new ArrayList<>(sections.keySet());
        return keys.indexOf(id);
    }

    @Override
    public Fragment getItem(int position) {
        List<Integer> keys = new ArrayList<>(sections.keySet());
        return sections.get(keys.get(position)).fragment;
    }

    @Override
    public int getCount() {
        if (sections == null) {
            return 0;
        }
        else {
            return sections.size();
        }
    }

    public View getTabView(int position) {
        TabData tabData = tabs.get(position);

        View view = LayoutInflater.from(context).inflate(R.layout.tab_main, null);
        TextView textTitle = (TextView) view.findViewById(R.id.tab_title);
        String title = context.getString(tabData.titleResId).toUpperCase(Locale.getDefault());
        textTitle.setText(title);

        ImageView imageIcon = (ImageView) view.findViewById(R.id.tab_icon);
        imageIcon.setImageResource(tabData.iconResId);
        // TODO: Probably need make configurable the tab icon visibility
        imageIcon.setVisibility(GONE);

        return view;
    }

    private class TabData {
        public int id;
        public int iconResId;
        public int titleResId;

        public TabData(int id, int iconResId, int titleResId) {
            this.id = id;
            this.iconResId = iconResId;
            this.titleResId = titleResId;
        }
    }
}
