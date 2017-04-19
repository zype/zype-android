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
import com.zype.android.ZypeSettings;
import com.zype.android.ui.main.fragments.download.DownloadFragment;
import com.zype.android.ui.main.fragments.favorite.FavoritesFragment;
import com.zype.android.ui.main.fragments.playlist.PlaylistFragment;
import com.zype.android.ui.main.fragments.settings.SettingsFragment;

import java.util.Locale;

import static android.view.View.GONE;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private int[] imageResId;

    private int[] stringResId;


    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        setDownloadsData();

    }

    private void setDownloadsData() {
        if (ZypeSettings.isDownloadsEnabled()){
            imageResId = new int[] {
                    R.drawable.icn_latest,
                    R.drawable.icn_downloads,
                    R.drawable.icn_favorites,
                    R.drawable.icn_settings
            };

            stringResId = new int[] {
                    R.string.title_tab_home,
                    R.string.title_tab_download,
                    R.string.title_tab_favorite,
                    R.string.title_tab_settings
            };
        } else {
            imageResId = new int[] {
                    R.drawable.icn_latest,
                    R.drawable.icn_favorites,
                    R.drawable.icn_settings
            };
            stringResId = new int[] {
                    R.string.title_tab_home,
                    R.string.title_tab_favorite,
                    R.string.title_tab_settings
            };
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (ZypeSettings.isDownloadsEnabled()) {
            switch (position) {
                case 0:
                    return PlaylistFragment.newInstance();
                case 1:
                    return DownloadFragment.newInstance();
                case 2:
                    return FavoritesFragment.newInstance();
                case 3:
                    return SettingsFragment.newInstance();
                default:
                    throw new RuntimeException("Illegal position");
            }
        } else {
            switch (position) {
                case 0:
                    return PlaylistFragment.newInstance();
                case 1:
                    return FavoritesFragment.newInstance();
                case 2:
                    return SettingsFragment.newInstance();
                default:
                    throw new RuntimeException("Illegal position");
            }
        }
    }

    @Override
    public int getCount() {
        if (ZypeSettings.isDownloadsEnabled())
            return 4;
        else
            return 3;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab_main, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_title);
        Locale l = Locale.getDefault();
        String title = context.getString(stringResId[position]).toUpperCase(l);
        tv.setText(title);
        ImageView img = (ImageView) v.findViewById(R.id.tab_icon);
        img.setImageResource(imageResId[position]);
        img.setVisibility(GONE);
        return v;
    }
}
