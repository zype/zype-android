package com.zype.android.ui.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.Subscription;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.webapi.events.bifrost.BifrostEvent;

import java.util.List;

import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_SUBSCRIPTION;

public class SubscribeOrLoginActivity extends BaseActivity {
    private static final String TAG = SubscribeOrLoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_or_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.subscribe_or_login_title));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    List<Purchase> purchases = ZypeApp.marketplaceGateway.getBillingManager().getPurchases();
                    if (purchases != null && purchases.size() > 0) {
                        Subscription subscription = ZypeApp.marketplaceGateway.findSubscriptionBySku(purchases.get(0).getSku());
                        if (subscription != null) {
                            SubscriptionsHelper.validateSubscription(subscription, purchases, getApi());
                        }
                    }
                    else {
                        NavigationHelper.getInstance(this).switchToSubscriptionScreen(this, getIntent().getExtras());
                    }
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (SubscriptionHelper.hasSubscription()) {
                        NavigationHelper.getInstance(this).switchToVideoDetailsScreen(this,
                                getIntent().getExtras().getString(BundleConstants.VIDEO_ID),
                                getIntent().getExtras().getString(BundleConstants.PLAYLIST_ID),
                                false);
                        finish();
                    }
                    else {
                        NavigationHelper.getInstance(this).switchToSubscriptionScreen(this, getIntent().getExtras());
                    }
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_SUBSCRIPTION:
                if (resultCode == RESULT_OK) {
                    NavigationHelper.getInstance(this).switchToVideoDetailsScreen(this,
                            getIntent().getExtras().getString(BundleConstants.VIDEO_ID),
                            getIntent().getExtras().getString(BundleConstants.PLAYLIST_ID),
                            false);
                    finish();
                }
                else {
                    onCancel();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onCancel() {
        finish();
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }

    // //////////
    // Event bus listeners
    //
    @Subscribe
    public void handleBifrost(BifrostEvent event) {
        // TODO: Check response data to properly update subscription count
        SettingsProvider.getInstance().saveSubscriptionCount(1);
        setResult(RESULT_OK);
        finish();
    }

}
