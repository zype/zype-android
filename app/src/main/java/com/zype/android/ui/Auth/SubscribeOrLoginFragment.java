package com.zype.android.ui.Auth;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;

import static android.app.Activity.RESULT_OK;
import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_SUBSCRIPTION;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribeOrLoginFragment extends Fragment {


    public SubscribeOrLoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscribe_or_login, container, false);

        Button buttonSubscribe = rootView.findViewById(R.id.buttonSubscribe);
        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.getInstance(getActivity()).switchToConsumerScreen(getActivity());
            }
        });

        Button buttonLogin = rootView.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.getInstance(getActivity()).switchToLoginScreen(getActivity(), null);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    NavigationHelper.getInstance(getActivity()).switchToSubscriptionScreen(getActivity());
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {

                }
                else {
                    onCancel();
                }
            case REQUEST_SUBSCRIPTION:
                if (resultCode == RESULT_OK) {

                }
                else {
                    onCancel();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onCancel() {
        getActivity().finish();
    }
}
