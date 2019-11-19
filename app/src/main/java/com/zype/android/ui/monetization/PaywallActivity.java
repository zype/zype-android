package com.zype.android.ui.monetization;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.SubscriptionHelper;
import com.zype.android.utils.BundleConstants;

import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_PURCHASE;

public class PaywallActivity extends AppCompatActivity {
    private static final String TAG = PaywallActivity.class.getSimpleName();

    public static final String EXTRA_PAYWALL_TYPE = "PaywallType";

    private PaywallViewModel model;

    private final NavigationHelper navigationHelper = NavigationHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paywall);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(String.format(getString(R.string.subscribe_or_login_title), getString(R.string.app_name)));

        model = ViewModelProviders.of(this).get(PaywallViewModel.class);
        model.setPaywallType((PaywallType) getIntent().getSerializableExtra(EXTRA_PAYWALL_TYPE));
        model.setPlaylistId(getIntent().getStringExtra(BundleConstants.PLAYLIST_ID));

        model.isPurchased().observe(this, isPurchased -> {
            if (isPurchased)
                openVideo();
        });

        showFragment(model.getPaywallType());
    }

    private void showFragment(PaywallType paywallType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (paywallType) {
            case PLAYLIST_TVOD:
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
                    model.makePurchase(this);
                }
                else {
                    close();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (SubscriptionHelper.hasSubscription()) {
                        openVideo();
                    }
                    else {
                        model.makePurchase(this);
                    }
                }
                else {
                    close();
                }
                break;
            case REQUEST_PURCHASE:
                if (resultCode == RESULT_OK) {
                    openVideo();
                }
                else {
                    close();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openVideo() {
        navigationHelper.switchToVideoDetailsScreen(this,
                getIntent().getExtras().getString(BundleConstants.VIDEO_ID),
                getIntent().getExtras().getString(BundleConstants.PLAYLIST_ID),
                false);
        finish();
    }

    private void close() {
        finish();
    }
}
