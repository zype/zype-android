package com.zype.android.ui.monetization;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.R;
import com.zype.android.databinding.FragmentPaywallPlaylistTvodBinding;
import com.zype.android.ui.NavigationHelper;

public class PaywallPlaylistTvodFragment extends Fragment {
    public static final String TAG = PaywallPlaylistTvodFragment.class.getSimpleName();

    private PaywallViewModel model;

    private FragmentPaywallPlaylistTvodBinding binding;

    public PaywallPlaylistTvodFragment() {}

    public static PaywallPlaylistTvodFragment getInstance() {
        PaywallPlaylistTvodFragment fragment = new PaywallPlaylistTvodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaywallPlaylistTvodBinding.inflate(inflater, container, false);

        final NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());
        binding.buttonBuyPlaylist.setOnClickListener(v -> {
            if (AuthHelper.isLoggedIn()) {
                model.setState(PaywallViewModel.State.READY_FOR_PURCHASE);
            }
            else {
                navigationHelper.switchToConsumerScreen(getActivity());
            }
        });
        binding.buttonSignIn.setOnClickListener(v ->
                navigationHelper.switchToLoginScreen(getActivity(), null));

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(PaywallViewModel.class);

        Playlist playlist = model.getPlaylist();
        if (playlist != null) {
            binding.setNumberOfVideos(playlist.playlistItemCount);
            binding.setPlaylistPrice(playlist.purchasePrice);
        }

        binding.setIsSignedIn(model.getState().getValue() == PaywallViewModel.State.SIGNED_IN);
    }
}
