package com.zype.android.ui.monetization;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;

import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_PURCHASE;

public class PaywallActivity extends AppCompatActivity {
    private static final String TAG = PaywallActivity.class.getSimpleName();

    public static final String EXTRA_PAYWALL_TYPE = "PaywallType";

    private PaywallViewModel model;
    private Observer<Boolean> purchasePlaylistVerificationListener;

    private ProgressDialog dialogProgress;

    private final NavigationHelper navigationHelper = NavigationHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paywall);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(String.format(getString(R.string.subscribe_or_login_title), getString(R.string.app_name)));

        // TODO: REMOVE clearing purchases in the release
//        ZypeApp.marketplaceGateway.getBillingManager().clearPurchases();

        model = ViewModelProviders.of(this).get(PaywallViewModel.class);
        model.setPaywallType((PaywallType) getIntent().getSerializableExtra(EXTRA_PAYWALL_TYPE));
        model.setPlaylistId(getIntent().getStringExtra(BundleConstants.PLAYLIST_ID));
        model.setVideoId(getIntent().getStringExtra(BundleConstants.VIDEO_ID));

        purchasePlaylistVerificationListener = createPurchasePlaylistVerificationListener();

        model.isPurchased().observe(this, isPurchased -> {
            Log.d(TAG, "isPurchased(): " + isPurchased);
            if (isPurchased) {
                showProgress(getString(R.string.paywall_verifying_purchase));
                ZypeApp.marketplaceGateway.verifyPlaylistPurchase(model.getPlaylist(), model.getSelectedItem())
                        .observe(this, purchasePlaylistVerificationListener);
            }
        });

        model.getState().observe(this, state -> {
            Log.d(TAG, "getState(): " + state.name());
            switch(state) {
                case READY_FOR_PURCHASE:
                    showPurchaseFragment();
                    break;
                case SIGN_IN_REQUIRED:
                case SIGNED_IN:
                    showPaywallFragment(model.getPaywallType());
                    break;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (model.getState().getValue() == PaywallViewModel.State.READY_FOR_PURCHASE) {
            model.setState(PaywallViewModel.State.SIGNED_IN);
            return;
        }
        super.onBackPressed();
    }

    private Observer<Boolean> createPurchasePlaylistVerificationListener() {
        return result -> {
            if (result) {
                model.updateEntitlements(success -> {
                    PaywallActivity.this.hideProgress();
                    PaywallActivity.this.setResult(RESULT_OK);
                    PaywallActivity.this.openVideo();
                });
            } else {
                PaywallActivity.this.hideProgress();
                DialogHelper.showErrorAlert(PaywallActivity.this,
                        PaywallActivity.this.getString(R.string.paywall_error_validation));
            }
        };
    }

    private void showPaywallFragment(PaywallType paywallType) {
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

    private void showPurchaseFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PurchaseFragment fragment = PurchaseFragment.getInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment, PurchaseFragment.TAG)
                .commit();
    }

    private void showProgress(String message) {
        if (!isFinishing()) {
            dialogProgress = new ProgressDialog(this);
            dialogProgress.setMessage(message);
            dialogProgress.setCancelable(false);
            dialogProgress.show();
        }
    }

    private void hideProgress() {
        if (!isFinishing()) {
            if (dialogProgress != null) {
                dialogProgress.dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    if (AuthHelper.isVideoUnlocked(PaywallActivity.this,
                            model.getVideoId(), model.getPlaylistId())) {
                        openVideo();
                    }
                    else {
                        model.setState(PaywallViewModel.State.READY_FOR_PURCHASE);
                    }
                }
                else {
//                    close();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (AuthHelper.isVideoUnlocked(PaywallActivity.this,
                            model.getVideoId(), model.getPlaylistId())) {
                        openVideo();
                    }
                    else {
                        model.setState(PaywallViewModel.State.SIGNED_IN);
                    }
                }
                else {
//                    close();
                }
                break;
            case REQUEST_PURCHASE:
                if (resultCode == RESULT_OK) {
                    openVideo();
                }
                else {
//                    close();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openVideo() {
        navigationHelper.switchToVideoDetailsScreen(this,
                model.getVideoId(), model.getPlaylistId(), false);
        finish();
    }

    private void close() {
        finish();
    }
}
