package com.zype.android.ui.video_details;

import android.content.Context;

import com.zype.android.R;
import com.zype.android.ui.video_details.fragments.options.OptionsFragment;
import com.zype.android.ui.video_details.fragments.summary.SummaryFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * @author vasya
 * @version 1
 *          date 7/7/15
 */
public class VideoDetailPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private String videoId;
    private int[] stringResId = {
            R.string.title_tab_summary,
            R.string.title_tab_options
    };

    public VideoDetailPagerAdapter(Context context, FragmentManager fm, String videoId) {
        super(fm);
        this.mContext = context;
        this.videoId = videoId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SummaryFragment.newInstance();
            case 1:
                return OptionsFragment.newInstance(videoId);
            default:
                throw new RuntimeException("Illegal position");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position > getCount() || position < 0) {
            throw new RuntimeException("Illegal position=" + position);
        }
        return mContext.getString(stringResId[position]);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
