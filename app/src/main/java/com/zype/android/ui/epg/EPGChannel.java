package com.zype.android.ui.epg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristoffer.
 */
public class EPGChannel implements Serializable {

  private final String name;
  private final String imageURL;
  private final int channelID;
  private final String id;
  private List<EPGEvent> events = new ArrayList();
  private EPGChannel previousChannel;
  private EPGChannel nextChannel;

  public EPGChannel(String imageURL, String name, int channelID, String id) {
    this.imageURL = imageURL;
    this.name = name;
    this.channelID = channelID;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public int getChannelID() {
    return channelID;
  }


  public String getName() {
    return name;
  }

  public String getImageURL() {
    return imageURL;
  }

  public List<EPGEvent> getEvents() {
    return events;
  }

  public EPGChannel getPreviousChannel() {
    return previousChannel;
  }

  public void setPreviousChannel(EPGChannel previousChannel) {
    this.previousChannel = previousChannel;
  }

  public EPGChannel getNextChannel() {
    return nextChannel;
  }

  public void setNextChannel(EPGChannel nextChannel) {
    this.nextChannel = nextChannel;
  }

  public EPGEvent addEvent(EPGEvent event) {
    this.events.add(event);
    return event;
  }
}
