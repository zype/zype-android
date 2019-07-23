package com.zype.android.ui.player;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.player.PlayerVideoEvent;

public class EpgPlayerActivity extends BaseVideoActivity {

    public static void startActivity(Activity activity, String videoId, String appendUrl) {
        Intent intent = new Intent(activity, EpgPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        bundle.putString(BundleConstants.PLAYLIST_ID, null);
        bundle.putString(BundleConstants.EPG_APPEND, appendUrl);

        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    protected Class<?> getActivityClass() {
        return EpgPlayerActivity.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_epg_player;
    }

    @Override
    protected String getActivityName() {
        return EpgPlayerActivity.class.getName();
    }

//    @Override
//    public void onShowAudio() {
//        //IGNORE
//    }

    @Override
    public void onFullscreenChanged(boolean isFullscreen) {
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

    @Override
    public void onError() {
        hideProgress();
        DialogHelper.showErrorAlert(this, getString(R.string.video_error_bad_request));
    }

    @Subscribe
    public void handleVideoPlayer(PlayerVideoEvent event) {
        epgVideoUrlToPlay = event.getEventData().getModelData().getResponse().getBody().getFiles().get(0).getUrl();
        if (!TextUtils.isEmpty(epgAppendUrl)) {
            epgVideoUrlToPlay = epgVideoUrlToPlay + epgAppendUrl;
        }

        mType = PlayerFragment.TYPE_VIDEO_EPG;
        changeFragment(isChromecastConntected());
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), err.getErrMessage());
    }
}

