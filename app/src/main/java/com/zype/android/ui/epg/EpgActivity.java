package com.zype.android.ui.epg;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import com.zype.android.R;
import com.zype.android.ui.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class EpgActivity extends BaseActivity {
  private EPG epg;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private ProgressBar progressBar;
  private static final String TAG = EpgActivity.class.getSimpleName();
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
      public void onEventClicked(EPGEvent epgEvent) {
        //launch player activity
      }

      @Override
      public void onEventSelected(EPGEvent epgEvent) {
        //dataSet(epgEvent);
      }


      @Override
      public void onResetButtonClicked() {
        // Reset button clicked
        epg.recalculateAndRedraw(null, true);
      }
    });

   compositeSubscription.add(EPGDataManager.getInstance().epgDataSubject
        .delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(epgData -> {
          epg.setEPGData(epgData);
          progressBar.setVisibility(View.GONE);
        }, throwable -> {
          progressBar.setVisibility(View.GONE);
        }));
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
  public boolean dispatchKeyEvent(KeyEvent event) {
    switch (event.getKeyCode()) {
      case KeyEvent.KEYCODE_BACK:
        if (event.getAction() == KeyEvent.ACTION_UP) {
          finish();
        }
    }
    return super.dispatchKeyEvent(event);
  }
}
