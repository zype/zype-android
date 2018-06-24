package com.zype.android.ui.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;

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
        setTitle("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    NavigationHelper.getInstance(this).switchToSubscriptionScreen(this);
                }
                else {
                    onCancel();
                }
                break;
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    NavigationHelper.getInstance(this).switchToSubscriptionScreen(this);
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
        finish();
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }
}
