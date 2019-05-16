package com.zype.android.ui.epg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zype.android.R;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.utils.Logger;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class EPGFragment extends BaseFragment {

  private EPG epgLayout;
  private ProgressBar progressBar;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private EPGData epgData;

  public EPGFragment() {
  }

  public static EPGFragment newInstance() {
    return new EPGFragment();
  }

  @Override
  protected String getFragmentName() {
    return "EPGFragment";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Logger.d("onCreateView");
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.epg_activity_layout, container, false);

    epgLayout = (EPG) view.findViewById(R.id.epg);
    progressBar = (ProgressBar) view.findViewById(R.id.progressView);
    initialize();
    return view;
  }

  private void initialize() {
    progressBar.setVisibility(View.VISIBLE);

    epgLayout.setEPGClickListener(new EPGClickListener() {
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
          epgLayout.setEPGData(epgData);
        }
      }
    });

    compositeSubscription.add(EPGDataManager.getInstance().epgDataSubject
        .delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(epgData -> {
          this.epgData = epgData;
          epgLayout.setEPGData(epgData);
          progressBar.setVisibility(View.GONE);
        }, throwable -> {
          progressBar.setVisibility(View.GONE);
        }));
  }

}
