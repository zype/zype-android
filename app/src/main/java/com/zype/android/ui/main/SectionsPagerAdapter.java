package com.zype.android.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ui.MyLibrary.MyLibraryFragment;
import com.zype.android.ui.main.fragments.download.DownloadFragment;
import com.zype.android.ui.main.fragments.favorite.FavoritesFragment;
import com.zype.android.ui.main.fragments.playlist.PlaylistFragment;
import com.zype.android.ui.main.fragments.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final int TAB_ID_MY_LIBRARY = 4;
    private static final int TAB_ID_SETTINGS = 5;

    private Context context;
    private List<TabData> tabs;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabs();
    }

    private void initTabs() {
        TabData tabData;
        tabs = new ArrayList<>();

        tabData = new TabData(TAB_ID_HOME, R.drawable.icn_latest, R.string.title_tab_home);
        tabs.add(tabData);

        if (ZypeConfiguration.isDownloadsEnabled(context)) {
            tabData = new TabData(TAB_ID_DOWNLOADS, R.drawable.icn_downloads, R.string.title_tab_downloads);
            tabs.add(tabData);
        }

        tabData = new TabData(TAB_ID_FAVORITES, R.drawable.icn_favorites, R.string.title_tab_favorites);
        tabs.add(tabData);

        if (ZypeConfiguration.isUniversalTVODEnabled(context)) {
            tabData = new TabData(TAB_ID_MY_LIBRARY, R.drawable.icn_downloads, R.string.main_tab_my_library);
            tabs.add(tabData);
        }

        tabData = new TabData(TAB_ID_SETTINGS, R.drawable.icn_settings, R.string.title_tab_settings);
        tabs.add(tabData);
    }

    @Override
    public Fragment getItem(int position) {
        switch (tabs.get(position).id) {
            case TAB_ID_HOME:
                return PlaylistFragment.newInstance();
            case TAB_ID_DOWNLOADS:
                return DownloadFragment.newInstance();
            case TAB_ID_FAVORITES:
                return FavoritesFragment.newInstance();
            case TAB_ID_MY_LIBRARY:
                return MyLibraryFragment.newInstance();
            case TAB_ID_SETTINGS:
                return SettingsFragment.newInstance();
        }
        throw new IllegalArgumentException("Unknown tab id");
    }

    @Override
    public int getCount() {
        return tabs.size();
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
