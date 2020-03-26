package com.zype.android.ui.Gallery;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ui.Gallery.Model.GalleryRow;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.ui.Widget.CustomViewPager;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * Created by Evgeny Cherkasov on 12.06.2018
 */
public class GalleryFragment extends Fragment {
    public static final String TAG = GalleryFragment.class.getSimpleName();

    private static final String ARG_PARENT_PLAYLIST_ID = "ParentPlaylistId";

    private HeroImagesViewModel modelHeroImages;
    private GalleryViewModel model;
    private String parentPlaylistId;

    private Observer<List<GalleryRow>> galleryRowsObserver;

    private HeroImagesPagerAdapter adapterHeroImages;
    private GalleryRowsAdapter adapter;

    CustomViewPager pagerHeroImages;
    ProgressBar progressBar;

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
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
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (heroImagesEnabled()) {
            modelHeroImages = ViewModelProviders.of(getActivity()).get(HeroImagesViewModel.class);
            modelHeroImages.getHeroImages().observe(this, new Observer<List<HeroImage>>() {
                @Override
                public void onChanged(@Nullable final List<HeroImage> heroImages) {
                    Logger.d("onChanged(): Hero images changed, size=" + heroImages.size());
                    modelHeroImages.stopTimer();
                    adapterHeroImages.setData(heroImages);
                    if (heroImages.size() > 0) {
                        pagerHeroImages.setCurrentItem(1, false);
                        pagerHeroImages.setVisibility(View.VISIBLE);
                        if (heroImages.size() > 1) {
                            modelHeroImages.startTimer(0).observe(GalleryFragment.this, new Observer<Integer>() {
                                @Override
                                public void onChanged(Integer page) {
                                    pagerHeroImages.setCurrentItem(pagerHeroImages.getCurrentItem() + 1, true);
                                }
                            });
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
        updateGalleryRows();
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
//        showProgress();
//        updateGalleryRows();
//        adapter.notifyDataSetChanged();
    }

    private boolean heroImagesEnabled() {
        return ZypeConfiguration.playlistGalleryHeroImages(getActivity())
                && parentPlaylistId.equals(ZypeConfiguration.getRootPlaylistId(getActivity()));
    }

    private void updateGalleryRows() {
        showProgress();
//        model.getGalleryRows(parentPlaylistId).observe(this, new Observer<List<GalleryRow>>() {
//            @Override
//            public void onChanged(@Nullable List<GalleryRow> galleryRows) {
//                Logger.d("onChanged(): Gallery rows changed, size=" + galleryRows.size());
////                if (allDataLoaded(galleryRows)) {
//                    adapter.setData(galleryRows);
//                    hideProgress();
////                }
//            }
//        });
        model.getGalleryRows().observe(this, galleryRowsObserver);
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
            }
        };
    }

//    private boolean allDataLoaded(List<GalleryRow> galleryRows) {
//        boolean result = true;
//        for (GalleryRow item : galleryRows) {
//            if ((item.videos == null || item.videos.isEmpty())
//                    && (item.nestedPlaylists == null || item.nestedPlaylists.isEmpty())) {
//                result = false;
//                break;
//            }
//        }
//        Logger.d("allDataLoaded(): " + result);
//        return result;
//    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

}
