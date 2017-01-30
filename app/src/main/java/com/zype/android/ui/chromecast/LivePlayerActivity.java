package com.zype.android.ui.chromecast;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.player.PlayerFragment;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.onair.OnAirAudioEvent;
import com.zype.android.webapi.events.onair.OnAirVideoEvent;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * @author vasya
 * @version 1
 *          date 9/9/15
 */
public class LivePlayerActivity extends BaseVideoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    protected Class<?> getActivityClass() {
        return LivePlayerActivity.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chromecast;
    }

    @Override
    protected String getActivityName() {
        return getString(R.string.activity_name_live);
    }

    @Override
    public void onShowAudio() {
        //IGNORE
    }

    @Override
    public void onFullscreenChanged() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isFullscreen = false;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullscreen = true;
        }
        if (isFullscreen) {
            mActionBar.hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            mActionBar.show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Subscribe
    public void handleVideoPlayer(OnAirVideoEvent event) {
        Logger.d("live handlePlayer");
        liveVideoUrlToPlay = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        mType = PlayerFragment.TYPE_VIDEO_LIVE;
        changeFragment(isChromecastConntected());
    }

    @Subscribe
    public void handleAudioPlayer(OnAirAudioEvent event) {
        Logger.d("live handlePlayer");
        liveAudioUrlToPlay = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        mType = PlayerFragment.TYPE_AUDIO_LIVE;
        changeFragment(isChromecastConntected());
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
    }
}
