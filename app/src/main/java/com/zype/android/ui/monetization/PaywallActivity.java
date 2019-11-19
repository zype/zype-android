package com.zype.android.ui.monetization;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.android.billingclient.api.Purchase;
import com.zype.android.Billing.Subscription;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;

import java.util.List;

import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_PURCHASE;
import static com.zype.android.utils.BundleConstants.REQUEST_SUBSCRIPTION;

public class PaywallActivity extends AppCompatActivity {
    private static final String TAG = PaywallActivity.class.getSimpleName();

    public static final String EXTRA_PAYWALL = "Paywall";

    public static final String VALUE_PAYWALL_PLAYLIST_TVOD = "PlaylistTvod";

    private final NavigationHelper navigationHelper = NavigationHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paywall);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(String.format(getString(R.string.subscribe_or_login_title), getString(R.string.app_name)));

        showFragment(getIntent().getStringExtra(EXTRA_PAYWALL));
    }

    private void showFragment(String paywallType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (paywallType) {
            case VALUE_PAYWALL_PLAYLIST_TVOD:
                PaywallPlaylistTvodFragment fragment = PaywallPlaylistTvodFragment.getInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment, PaywallPlaylistTvodFragment.TAG)
                        .commit();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    navigationHelper.switchToSubscriptionScreen(this, getIntent().getExtras());
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (SubscriptionHelper.hasSubscription()) {
                        navigationHelper.switchToVideoDetailsScreen(this,
                                getIntent().getExtras().getString(BundleConstants.VIDEO_ID),
                                getIntent().getExtras().getString(BundleConstants.PLAYLIST_ID),
                                false);
                        finish();
                    }
                    else {
                        navigationHelper.switchToSubscriptionScreen(this, getIntent().getExtras());
                    }
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_PURCHASE:
                if (resultCode == RESULT_OK) {
                    navigationHelper.switchToVideoDetailsScreen(this,
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
}
