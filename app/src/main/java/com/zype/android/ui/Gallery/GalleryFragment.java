package com.zype.android.ui.Gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.ui.Widget.CustomViewPager;
import com.zype.android.utils.Logger;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;


/**
 * Created by Evgeny Cherkasov on 12.06.2018
 */
public class GalleryFragment extends Fragment {
    public static final String TAG = GalleryFragment.class.getSimpleName();

    private static final String ARG_PARENT_PLAYLIST_ID = "ParentPlaylistId";

    private HeroImagesViewModel modelHeroImages;
    private GalleryViewModel model;
    private String parentPlaylistId;

    private Observer<Integer> sliderPageObserver;
    private Observer<List<GalleryRow>> galleryRowsObserver;

    private HeroImagesPagerAdapter adapterHeroImages;
    private GalleryRowsAdapter adapter;

    private CustomViewPager pagerHeroImages;
    private ProgressBar progressBar;
    private LinearLayout emptyLayout;

    public static GalleryFragment newInstance(String parentPlaylistId) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARENT_PLAYLIST_ID, parentPlaylistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentPlaylistId = getArguments().getString(ARG_PARENT_PLAYLIST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        pagerHeroImages = rootView.findViewById(R.id.pagerHeroImages);
        pagerHeroImages.setScrollDuration(500);
        if (heroImagesEnabled()) {
            adapterHeroImages = new HeroImagesPagerAdapter(getChildFragmentManager());
            pagerHeroImages.setAdapter(adapterHeroImages);
        }
        else {
            pagerHeroImages.setVisibility(View.GONE);
        }
        pagerHeroImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                Logger.d("onPageSelected(): page=" + position);
                if (position == 0 && adapterHeroImages != null) {
                    modelHeroImages.setCurrentPage(adapterHeroImages.getCount() - 3);
                }
                else if (adapterHeroImages != null && position == adapterHeroImages.getCount() - 1) {
                    modelHeroImages.setCurrentPage(0);
                }
                else {
                    modelHeroImages.setCurrentPage(position - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE && adapterHeroImages != null) {
                    Logger.d("onPageScrollStateChanged(): page=" + pagerHeroImages.getCurrentItem());
                    if (pagerHeroImages.getCurrentItem() == 0) {
                        pagerHeroImages.setCurrentItem(adapterHeroImages.getCount() - 2, false);
                    }
                    if (pagerHeroImages.getCurrentItem() == adapterHeroImages.getCount() - 1) {
                        pagerHeroImages.setCurrentItem(1, false);
                    }
                }
            }
        });

        RecyclerView listGallery = rootView.findViewById(R.id.listGallery);
        adapter = new GalleryRowsAdapter();
        listGallery.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progress);
        emptyLayout = rootView.findViewById(R.id.layoutEmpty);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (heroImagesEnabled()) {
            if (sliderPageObserver == null) {
                sliderPageObserver = createSliderPageObserver();
            }
            modelHeroImages = ViewModelProviders.of(getActivity()).get(HeroImagesViewModel.class);
            modelHeroImages.getHeroImages().observe(this, new Observer<List<HeroImage>>() {
                @Override
                public void onChanged(@Nullable final List<HeroImage> heroImages) {
                    Logger.d("onChanged(): Hero images changed, size=" + heroImages.size());
                    modelHeroImages.stopTimer();
                    if (adapterHeroImages != null) {
                        adapterHeroImages.setData(heroImages);
                    }
                    if (heroImages.size() > 0) {
                        pagerHeroImages.setCurrentItem(1, false);
                        pagerHeroImages.setVisibility(View.VISIBLE);
                        if (model.getGalleryRowsState() == GalleryRow.State.UPDATED) {
                            if (adapterHeroImages != null && adapterHeroImages.getCount() > 1) {
                                modelHeroImages.startTimer(1).observe(GalleryFragment.this, sliderPageObserver);
                            }
                        }
                    }
                    else {
                        pagerHeroImages.setVisibility(View.GONE);
                    }
                }
            });
        }

        if (galleryRowsObserver == null) {
            galleryRowsObserver = createGalleryRowObserver();
        }
        model = ViewModelProviders.of(getActivity()).get(GalleryViewModel.class);
        model.setPlaylistId(parentPlaylistId);
        showProgress();
        model.getGalleryRows().observe(this, galleryRowsObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean heroImagesEnabled() {
        return ZypeConfiguration.playlistGalleryHeroImages(getActivity())
                && parentPlaylistId.equals(ZypeConfiguration.getRootPlaylistId(getActivity()));
    }

    private Observer<Integer> createSliderPageObserver() {
        return page -> {
            Logger.d("sliderPageObserver: page=" + page + ", currentPage=" + pagerHeroImages.getCurrentItem());
            if (page != null && adapterHeroImages != null) {
                if (page == 0) {
                    if (pagerHeroImages.getCurrentItem() == adapterHeroImages.getCount() - 2) {
                        pagerHeroImages.setCurrentItem(pagerHeroImages.getCurrentItem() + 1, true);
                    }
                }
                else if (page == adapterHeroImages.getCount() - 3) {
                    if (pagerHeroImages.getCurrentItem() != 0) {
                        pagerHeroImages.setCurrentItem(page + 1, true);
                    }
                }
                else {
                    pagerHeroImages.setCurrentItem(page + 1, true);
                }

            }
        };
    }

    private Observer<List<GalleryRow>> createGalleryRowObserver() {
        return galleryRows -> {
            Logger.d("getGalleryRows()::onChanged(): size=" + galleryRows.size() + ", state=" + model.getGalleryRowsState());
            if (model.getGalleryRowsState() == GalleryRow.State.LOADING) {
                showProgress();
            }
            else {
                adapter.setData(galleryRows);
                hideProgress();
                if (model.getGalleryRowsState() == GalleryRow.State.UPDATED) {
                    if (adapterHeroImages != null && adapterHeroImages.getCount() > 1) {
                        modelHeroImages.startTimer(1).observe(GalleryFragment.this, sliderPageObserver);
                    }
                if (galleryRows.isEmpty()) {
                    showEmpty();
                }
                else {
                    hideEmpty();
                }
            }
        };
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showEmpty() {
        emptyLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmpty() {
        emptyLayout.setVisibility(View.GONE);
    }
}
