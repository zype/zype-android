package com.zype.android.ui.epg;

import java.util.ArrayList;
import java.util.List;

public class EPGDataImpl implements EPGData {

  private List<EPGChannel> epgChannels = new ArrayList<>();

  public EPGDataImpl(List<EPGChannel> channels) {
      epgChannels.addAll(channels);
  }

  @Override
  public EPGChannel getChannel(int position) {
    return epgChannels.get(position);
  }

  @Override
  public List<EPGEvent> getEvents(int channelPosition) {
    return epgChannels.get(channelPosition).getEvents();
  }

  @Override
  public EPGEvent getEvent(int channelPosition, int programPosition) {
    return epgChannels.get(channelPosition).getEvents().get(programPosition);
  }

  @Override
  public int getChannelCount() {
    return epgChannels.size();
  }

  @Override
  public boolean hasData() {
    return getChannelCount() > 0;
  }
}
