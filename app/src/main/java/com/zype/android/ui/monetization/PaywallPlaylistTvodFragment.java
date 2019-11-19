package com.zype.android.ui.monetization;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;

public class PaywallPlaylistTvodFragment extends Fragment {
    public static final String TAG = PaywallPlaylistTvodFragment.class.getSimpleName();

    private PaywallViewModel model;

    public PaywallPlaylistTvodFragment() {}

    public static PaywallPlaylistTvodFragment getInstance() {
        PaywallPlaylistTvodFragment fragment = new PaywallPlaylistTvodFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paywall_playlist_tvod, container, false);

        final NavigationHelper navigationHelper = NavigationHelper.getInstance(getActivity());

        TextView textTitle = rootView.findViewById(R.id.textTitle);
        textTitle.setText(String.format(getString(R.string.paywall_title), getString(R.string.app_name)));

        Button buttonBuyPlaylist = rootView.findViewById(R.id.buttonBuyPlaylist);
        buttonBuyPlaylist.setOnClickListener(v -> {
            if (AuthHelper.isLoggedIn()) {
                model.makePurchase(getActivity());
            }
            else {
                navigationHelper.switchToConsumerScreen(getActivity());
            }
        });

        Button buttonSignIn = rootView.findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(v ->
                navigationHelper.switchToLoginScreen(getActivity(), null));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(PaywallViewModel.class);
    }
}
