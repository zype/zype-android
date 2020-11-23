package com.zype.android.ui.video_details.v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.zype.android.databinding.FragmentActionBuyVideoBinding;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.monetization.PaywallViewModel;
import com.zype.android.ui.video_details.VideoDetailViewModel;
import com.zype.android.utils.BundleConstants;

import static android.app.Activity.RESULT_OK;

public class ActionBuyVideoFragment extends Fragment {
    public static final String TAG = ActionBuyVideoFragment.class.getSimpleName();

    private VideoDetailViewModel model;

    private FragmentActionBuyVideoBinding binding;

    public ActionBuyVideoFragment() {}

    public static ActionBuyVideoFragment getInstance() {
        ActionBuyVideoFragment fragment = new ActionBuyVideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActionBuyVideoBinding.inflate(inflater, container, false);

        final NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());
        binding.actionLayout.setOnClickListener(v -> {
            if (AuthHelper.isLoggedIn()) {
                showPurchaseScreen();
            }
            else {
                navigationHelper.switchToLoginScreen(this);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(VideoDetailViewModel.class);

        model.getVideo().observe(this, video -> {
            if (video != null) {
                binding.setVideoPrice(video.purchasePrice);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BundleConstants.REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    showPurchaseScreen();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPurchaseScreen() {
        Log.d(TAG, "showPurchaseScreen()");
    }
}
