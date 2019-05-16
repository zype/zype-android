package com.zype.android.ui.epg;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.zype.android.R;
import com.zype.android.ui.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class EpgActivity extends BaseActivity {
  private static final String TAG = EpgActivity.class.getSimpleName();
  private EPG epg;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private ProgressBar progressBar;
  private EPGData epgData;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.epg_activity_layout);
    progressBar = (ProgressBar) findViewById(R.id.progressView);
    progressBar.setVisibility(View.VISIBLE);
    initListener();
  }

  private void initListener() {
    epg = (EPG) findViewById(R.id.epg);
    epg.setEPGClickListener(new EPGClickListener() {
      @Override
      public void onChannelClicked(int channelPosition, EPGChannel epgChannel) {

      }

      @Override
      public void onEventClicked(int channelPosition, int programPosition, EPGEvent epgEvent) {

      }

      @Override
      public void onEventSelected(EPGEvent epgEvent) {
        //dataSet(epgEvent);
      }


      @Override
      public void onResetButtonClicked() {
        // Reset button clicked
        if (epgData != null) {
          epg.setEPGData(epgData);
        }
      }
    });

    compositeSubscription.add(EPGDataManager.getInstance().epgDataSubject
        .delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(epgData -> {
          this.epgData = epgData;
          epg.setEPGData(epgData);
          progressBar.setVisibility(View.GONE);
        }, throwable -> {
          progressBar.setVisibility(View.GONE);
        }));
  }

  @Override
  public void onBackPressed() {

    if (epg != null) {
      epg.reset();
    }
    super.onBackPressed();
  }


  @Override
  protected void onDestroy() {
    if (epg != null) {
      epg.clearEPGImageCache();
    }

    compositeSubscription.clear();
    super.onDestroy();
  }

  @Override
  protected String getActivityName() {
    return TAG;
  }


  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

  }
}
