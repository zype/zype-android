package com.zype.android.ui.launch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.LiveStreamSettingsParamsBuilder;
import com.zype.android.webapi.builder.SettingsParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.settings.LiveStreamSettingsEvent;
import com.zype.android.webapi.events.settings.SettingsEvent;

public class LaunchActivity extends BaseActivity {

    private static final long SPLASH_TIME = 5000;
    Handler mHandler;
    Runnable mJumpRunnable;

    private boolean isSettingsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("onCreate()");
        setContentView(R.layout.activity_launch);
        mJumpRunnable = new Runnable() {

            public void run() {
                jump();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mJumpRunnable, SPLASH_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SettingsParamsBuilder settingsParamsBuilder = new SettingsParamsBuilder();
        getApi().executeRequest(WebApiManager.Request.GET_SETTINGS, settingsParamsBuilder.build());
    }

    private void jump() {
        if (isFinishing() || !isSettingsLoaded)
            return;
        Logger.d("jump()");
        mJumpRunnable = null;
        mHandler = null;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void requestLiveStreamSettings() {
        getApi().executeRequest(WebApiManager.Request.LIVE_STREAM_SETTINGS, new LiveStreamSettingsParamsBuilder().build());
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Subscribe
    public void handleSettingsEvent(SettingsEvent event) {
        Logger.d("handleConsumer");
        SettingsProvider.getInstance().saveSettingsFromServer(event.getEventData().getModelData());

        requestLiveStreamSettings();
    }

    @Subscribe
    public void handleLiveStreamSettingsEvent(LiveStreamSettingsEvent event) {
        Logger.d("handleLiveStreamSettingsEvent()");
        SettingsProvider.getInstance().saveLiveStreamSettings(event.getEventData().getModelData());
        isSettingsLoaded = true;
        jump();
    }

    @Override
    protected String getActivityName() {
        return getString(R.string.activity_name_launch);
    }
}
