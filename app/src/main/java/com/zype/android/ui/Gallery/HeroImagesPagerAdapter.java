package com.zype.android.ui.Gallery;


import com.zype.android.ui.Gallery.Model.HeroImage;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
        return HeroImageFragment.newInstance(heroImage.imageUrl, heroImage.playlistId, heroImage.videoId);
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
        if (data != null && data.size() > 1) {
            this.data = new ArrayList<>();
            this.data.add(data.get(data.size() - 1));
            this.data.addAll(data);
            this.data.add(data.get(0));
        }
        else {
            this.data = data;
        }
        notifyDataSetChanged();
    }
}
