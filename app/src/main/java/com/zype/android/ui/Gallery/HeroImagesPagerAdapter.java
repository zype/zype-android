package com.zype.android.ui.Gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import com.zype.android.ui.Gallery.Model.HeroImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImagesPagerAdapter extends FragmentStatePagerAdapter {
    private List<HeroImage> data;

    public HeroImagesPagerAdapter(FragmentManager fm) {
        super(fm);
        data = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        HeroImage heroImage = data.get(position);
        return HeroImageFragment.newInstance(heroImage.imageUrl, heroImage.playlistId);
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        else {
            return data.size();
        }
    }

    public void setData(List<HeroImage> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
