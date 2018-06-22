package com.zype.android.ui.Auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;

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
    protected String getActivityName() {
        return TAG;
    }
}
