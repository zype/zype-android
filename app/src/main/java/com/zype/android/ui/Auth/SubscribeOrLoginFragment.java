package com.zype.android.ui.Auth;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.billingclient.api.Purchase;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.Subscription;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;

import java.util.List;

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

        final List<Purchase> purchases = ZypeApp.marketplaceGateway.getBillingManager().getPurchases();
        Button buttonRestorePurchases = rootView.findViewById(R.id.buttonRestorePurchases);
        buttonRestorePurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AuthHelper.isLoggedIn()) {
                    if (purchases != null && purchases.size() > 0) {
                        Subscription subscription = ZypeApp.marketplaceGateway.findSubscriptionBySku(purchases.get(0).getSku());
                        if (subscription != null) {
                            SubscriptionsHelper.validateSubscription(subscription, purchases, ((BaseActivity) getActivity()).getApi());
                        }
                    }
                }
                else {
                    NavigationHelper.getInstance(getActivity()).switchToConsumerScreen(getActivity());
                }
            }
        });
        if (purchases != null && purchases.size() > 0) {
            buttonRestorePurchases.setVisibility(View.VISIBLE);
        }
        else {
            buttonRestorePurchases.setVisibility(View.GONE);
        }

        return rootView;
    }
}
