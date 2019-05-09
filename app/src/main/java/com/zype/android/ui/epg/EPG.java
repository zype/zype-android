package com.zype.android.ui.epg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.bumptech.glide.request.target.SimpleTarget;
import com.zype.android.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;


/**
 * Classic EPG, electronic program guide, that scrolls both horizontal, vertical and diagonal.
 * It utilize onDraw() to draw the graphic on screen. So there are some private helper methods calculating positions etc.
 * Listed on Y-axis are channels and X-axis are programs/events. Data is added to EPG by using setEPGData()
 * and pass in an EPGData implementation. A click listener can be added using setEPGClickListener().
 * Created by Kristoffer, http://kmdev.se
 */
public class EPG extends ViewGroup {

  public static final int DAYS_BACK_MILLIS = 3 * 24 * 60 * 60 * 1000;        // 3 days
  public static final int DAYS_FORWARD_MILLIS = 3 * 24 * 60 * 60 * 1000;     // 3 days
  public static final int HOURS_IN_VIEWPORT_MILLIS = 2 * 60 * 60 * 1000;     // 2 hours
  public static final int TIME_LABEL_SPACING_MILLIS = 30 * 60 * 1000;        // 30 minutes
  public static int screenWidth;
  public static int screenHeight;
  public final String TAG = getClass().getSimpleName();
  private final Rect mClipRect;
  private final Rect mDrawingRect;
  private final Rect mMeasuringRect;
  private final Paint mPaint, tPaint;
  private final Scroller mScroller;
  private final GestureDetector mGestureDetector;
  private final Bitmap mResetButtonIcon;
  private final int mChannelLayoutMargin;
  private final int mChannelLayoutPadding;
  private final int mChannelLayoutHeight;
  private final int mChannelLayoutWidth;
  private final int mChannelLayoutBackground;
  private final int mEventLayoutBackground;
  private final int mEventLayoutBackgroundCurrent;
  private final int mEventLayoutBackgroundSelected;
  private final int mEventLayoutBackgroundPressed;
  private final int mEventLayoutTextColor;
  private final int mEventLayoutTextSize;
  private final int mTimeBarLineWidth;
  private final int mTimeBarLineColor;
  private final int mTimeBarHeight;
  private final int mTimeBarTextSize;
  private final int mChannelTextSize;
  private final int mResetButtonSize;
  private final int mResetButtonMargin;

  private final int mTimeBarMonthTextSize;

  private final int mEPGBackground;
  private final Map<String, Bitmap> mChannelImageCache;
  private final Map<String, SimpleTarget> mChannelImageTargetCache;
  private final int mEPGBottomStrokeBackground;
  private EPGClickListener mClickListener;
  private int mMaxHorizontalScroll;
  private int mMaxVerticalScroll;
  private long mMillisPerPixel;
  private long mTimeOffset;
  private long mTimeLowerBoundary;
  private long mTimeUpperBoundary;
  //TODO: find out why grid is shifted -> because of channels bar?
  private long mMargin = 200000;
  private EPGData epgData = null;
  //private EPGEvent selectedEvent = null;
  private int orientation;

  private EPGEvent lastSelectedEvent;
  private EPGEvent lastPressedEvent;

  public EPG(Context context) {
    this(context, null);
  }

  public EPG(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EPG(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setWillNotDraw(false);

    resetBoundaries();

    mDrawingRect = new Rect();
    mClipRect = new Rect();
    mMeasuringRect = new Rect();
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    tPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);
    mGestureDetector = new GestureDetector(context, new OnGestureListener());
    mChannelImageCache = new HashMap();
    mChannelImageTargetCache = new HashMap();

    // Adding some friction that makes the epg less flappy.
    mScroller = new Scroller(context);
    mScroller.setFriction(0.2f);

    mEPGBackground = getResources().getColor(R.color.epg_background);
    mEPGBottomStrokeBackground = getResources().getColor(R.color.lb_tv_white);
    mChannelLayoutMargin = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_margin);
    mChannelLayoutPadding = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_padding);
    mChannelLayoutHeight = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_height);
    mChannelLayoutWidth = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_width);
    mChannelLayoutBackground = getResources().getColor(R.color.epg_channel_layout_background);

    mEventLayoutBackground = getResources().getColor(R.color.epg_event_layout_background);
    mEventLayoutBackgroundCurrent = getResources().getColor(R.color.epg_event_layout_background_current);
    mEventLayoutBackgroundSelected = getResources().getColor(R.color.epg_event_layout_background_selected);
    mEventLayoutBackgroundPressed = getResources().getColor(R.color.epg_event_layout_background_pressed);
    mEventLayoutTextColor = getResources().getColor(R.color.epg_event_layout_text);
    mEventLayoutTextSize = getResources().getDimensionPixelSize(R.dimen.epg_event_layout_text);

    mTimeBarHeight = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_height);
    mTimeBarTextSize = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_text);
    mTimeBarMonthTextSize = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_month_text);
    mTimeBarLineWidth = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_line_width);
    mTimeBarLineColor = getResources().getColor(R.color.epg_time_bar);
    mChannelTextSize = getResources().getDimensionPixelSize(R.dimen.epg_channel_bar_text);

    mResetButtonSize = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_size);
    mResetButtonMargin = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_margin);

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.outWidth = mResetButtonSize;
    options.outHeight = mResetButtonSize;
    mResetButtonIcon = BitmapFactory.decodeResource(getResources(), R.drawable.reset, options);

  }


  @Override
  //save state to recover state after restart or screen rotation
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    EPGState epgState = new EPGState(superState);
    return epgState;
  }

  @Override
  //recover the state
  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof EPGState)) {
      super.onRestoreInstanceState(state);
      return;
    }
    EPGState epgState = (EPGState) state;
    super.onRestoreInstanceState(epgState.getSuperState());
  }

  private int getChannelAreaWidth() {
    return mChannelLayoutWidth + mChannelLayoutPadding + mChannelLayoutMargin;
  }

  private int getProgramAreaWidth() {
    return getWidth() - getChannelAreaWidth();
  }

  @Override
  protected void onDraw(Canvas canvas) {

    if (epgData != null && epgData.hasData()) {
      mTimeLowerBoundary = getTimeFrom(getScrollX());
      mTimeUpperBoundary = getTimeFrom(getScrollX() + getWidth());

      Rect drawingRect = mDrawingRect;
      drawingRect.left = getScrollX();
      drawingRect.top = getScrollY();
      drawingRect.right = drawingRect.left + getWidth();
      drawingRect.bottom = drawingRect.top + getHeight();

      drawChannelListItems(canvas, drawingRect);
      drawEvents(canvas, drawingRect);
      drawTimebar(canvas, drawingRect);
      //drawTimeLine(canvas, drawingRect);
      drawResetButton(canvas, drawingRect);

      // If scroller is scrolling/animating do scroll. This applies when doing a fling.
      if (mScroller.computeScrollOffset()) {
        scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      }
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    recalculateAndRedraw(true);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (lastPressedEvent != null) {
        lastPressedEvent.setPressed(false);
        invalidate();
      }
    }
    return mGestureDetector.onTouchEvent(event);
  }


  @Override
  public boolean dispatchGenericMotionEvent(MotionEvent event) {
    //return mGame.handleMotionEvent(event);
    return false;
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return super.onKeyDown(keyCode, event);
  }


  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
  }

  private void drawResetButton(Canvas canvas, Rect drawingRect) {
    // Show button when scrolled 1/3 of screen width from current time
    final long threshold = getWidth() / 3;
    if (Math.abs(getXPositionStart() - getScrollX()) > threshold) {
      drawingRect = calculateResetButtonHitArea();
      mPaint.setColor(mTimeBarLineColor);
      canvas.drawCircle(drawingRect.right - (mResetButtonSize / 2),
          drawingRect.bottom - (mResetButtonSize / 2),
          Math.min(drawingRect.width(), drawingRect.height()) / 2,
          mPaint);

      drawingRect.left += mResetButtonMargin;
      drawingRect.right -= mResetButtonMargin;
      drawingRect.top += mResetButtonMargin;
      drawingRect.bottom -= mResetButtonMargin;
      canvas.drawBitmap(mResetButtonIcon, null, drawingRect, mPaint);
    }
  }

  private void drawTimebarBottomStroke(Canvas canvas, Rect drawingRect) {
    drawingRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
    drawingRect.top = getScrollY() + mTimeBarHeight;
    drawingRect.right = drawingRect.left + getWidth();
    drawingRect.bottom = drawingRect.top + mChannelLayoutMargin;

    // Bottom stroke
    mPaint.setColor(mEPGBottomStrokeBackground);
    canvas.drawRect(drawingRect, mPaint);

    if (shouldDrawTimeLine(DateTime.now().getMillis())) {
      mPaint.setColor(mEventLayoutBackgroundSelected);
      drawingRect.right = getXFrom(DateTime.now().getMillis());
      canvas.drawRect(drawingRect, mPaint);
    }
  }

  private void drawTimebar(Canvas canvas, Rect drawingRect) {
    drawingRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
    drawingRect.top = getScrollY();
    drawingRect.right = drawingRect.left + getWidth();
    drawingRect.bottom = drawingRect.top + mTimeBarHeight;

    mClipRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
    mClipRect.top = getScrollY();
    mClipRect.right = getScrollX() + getWidth();
    mClipRect.bottom = mClipRect.top + mTimeBarHeight;

    canvas.save();
    canvas.clipRect(mClipRect);

    // Background
    mPaint.setColor(mChannelLayoutBackground);
    canvas.drawRect(drawingRect, mPaint);

    // Time stamps
    mPaint.setColor(mEventLayoutTextColor);
    mPaint.setTextSize(mTimeBarTextSize);

    for (int i = 0; i < HOURS_IN_VIEWPORT_MILLIS / TIME_LABEL_SPACING_MILLIS; i++) {
      // Get time and round to nearest half hour
      final long time = TIME_LABEL_SPACING_MILLIS *
          (((mTimeLowerBoundary + (TIME_LABEL_SPACING_MILLIS * i)) +
              (TIME_LABEL_SPACING_MILLIS / 2)) / TIME_LABEL_SPACING_MILLIS);

      canvas.drawText(EPGUtil.getShortTime(time),
          getXFrom(time),
          drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)), mPaint);
    }

    canvas.restore();

    drawTimebarDayIndicator(canvas, drawingRect);
    drawTimebarBottomStroke(canvas, drawingRect);
  }

  private void drawTimebarDayIndicator(Canvas canvas, Rect drawingRect) {
    drawingRect.left = getScrollX();
    drawingRect.top = getScrollY();
    drawingRect.right = drawingRect.left + mChannelLayoutWidth;
    drawingRect.bottom = drawingRect.top + mTimeBarHeight;

    // Background
    mPaint.setColor(mChannelLayoutBackground);
    canvas.drawRect(drawingRect, mPaint);

    // Text
    mPaint.setColor(mEventLayoutTextColor);
    mPaint.setTextSize(mTimeBarMonthTextSize);
    mPaint.setTextAlign(Paint.Align.CENTER);
    canvas.drawText(EPGUtil.getEPGdayName(mTimeLowerBoundary),
        drawingRect.left + ((drawingRect.right - drawingRect.left) / 2),
        drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarMonthTextSize / 2)), mPaint);

    mPaint.setTextAlign(Paint.Align.LEFT);
  }

  private void drawTimeLine(Canvas canvas, Rect drawingRect) {
    long now = System.currentTimeMillis();

    if (shouldDrawTimeLine(now)) {
      drawingRect.left = getXFrom(now);
      drawingRect.top = getScrollY();
      drawingRect.right = drawingRect.left + mTimeBarLineWidth;
      drawingRect.bottom = drawingRect.top + getHeight();

      mPaint.setColor(mTimeBarLineColor);
      canvas.drawRect(drawingRect, mPaint);
    }

  }

  private void drawEvents(Canvas canvas, Rect drawingRect) {
    final int firstPos = getFirstVisibleChannelPosition();
    final int lastPos = getLastVisibleChannelPosition();

    for (int channelPos = firstPos; channelPos <= lastPos; channelPos++) {

      // Set clip rectangle
      mClipRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
      mClipRect.top = getTopFrom(channelPos);
      mClipRect.right = getScrollX() + getWidth();
      mClipRect.bottom = mClipRect.top + mChannelLayoutHeight;

      canvas.save();
      canvas.clipRect(mClipRect);

      // Draw each event
      boolean foundFirst = false;

      List<EPGEvent> epgEvents = epgData.getEvents(channelPos);

      for (EPGEvent event : epgEvents) {
        if (isEventVisible(event.getStart(), event.getEnd())) {
          drawEvent(canvas, channelPos, event, drawingRect);
          foundFirst = true;
        } else if (foundFirst) {
          break;
        }
      }

      canvas.restore();
    }

  }

  private void drawEvent(final Canvas canvas, final int channelPosition, final EPGEvent event, final Rect drawingRect) {

    setEventDrawingRectangle(channelPosition, event.getStart(), event.getEnd(), drawingRect);

    // Background
    if (event.isSelected()) {
      mPaint.setColor(mEventLayoutBackgroundSelected);
    } else if (event.isPressed()) {
      mPaint.setColor(mEventLayoutBackgroundPressed);
    } else if (event.isCurrent()) {
      mPaint.setColor(mEventLayoutBackgroundCurrent);
      mPaint.setAlpha(70);
    } else {
      mPaint.setColor(mEventLayoutBackground);
    }
    canvas.drawRect(drawingRect, mPaint);

    // Add left and right inner padding
    drawingRect.left += mChannelLayoutPadding + 16;
    drawingRect.right -= mChannelLayoutPadding;

    // Text
    mPaint.setColor(mEventLayoutTextColor);
    mPaint.setTextSize(mEventLayoutTextSize);

    // Move drawing.top so text will be centered (text is drawn bottom>up)
    mPaint.getTextBounds(event.getTitle(), 0, event.getTitle().length(), mMeasuringRect);
    drawingRect.top += (((drawingRect.bottom - drawingRect.top) / 2) + (mMeasuringRect.height() / 2));

    String title = event.getTitle();
    title = title.substring(0,
        mPaint.breakText(title, true, drawingRect.right - drawingRect.left, null));
    canvas.drawText(title, drawingRect.left, drawingRect.top, mPaint);

  }

  private void setEventDrawingRectangle(final int channelPosition, final long start, final long end, final Rect drawingRect) {
    drawingRect.left = getXFrom(start);
    drawingRect.top = getTopFrom(channelPosition);
    drawingRect.right = getXFrom(end) - mChannelLayoutMargin;
    drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;
  }

  private void drawChannelListItems(Canvas canvas, Rect drawingRect) {
    // Background
    mMeasuringRect.left = getScrollX();
    mMeasuringRect.top = getScrollY();
    mMeasuringRect.right = drawingRect.left + mChannelLayoutWidth;
    mMeasuringRect.bottom = mMeasuringRect.top + getHeight();

    mPaint.setColor(mChannelLayoutBackground);
    canvas.drawRect(mMeasuringRect, mPaint);

    final int firstPos = getFirstVisibleChannelPosition();
    final int lastPos = getLastVisibleChannelPosition();

    for (int pos = firstPos; pos <= lastPos; pos++) {
      drawChannelItem(canvas, pos, drawingRect);
    }
  }

  private void drawChannelItem(final Canvas canvas, int position, Rect drawingRect) {
    drawingRect.left = getScrollX();
    drawingRect.top = getTopFrom(position);
    drawingRect.right = drawingRect.left + mChannelLayoutWidth;
    drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;

    if (this.lastSelectedEvent != null) {
      if (this.lastSelectedEvent.getChannel().getChannelID() == position) {
        tPaint.setColor(mEventLayoutBackgroundSelected);
        canvas.drawRect(drawingRect, tPaint);
      }
    }

    //local changes for setup channel name
    TextPaint textPaint = new TextPaint();
    String nameChannel = epgData.getChannel(position).getName();
    textPaint.setColor(mEventLayoutTextColor);
    textPaint.setTextSize(mChannelTextSize);
    textPaint.setTextAlign(Paint.Align.CENTER);

    CharSequence txt = TextUtils.ellipsize(nameChannel, textPaint, drawingRect.width() - 10, TextUtils.TruncateAt.END);
    canvas.drawText(txt, 0, txt.length(), drawingRect.left + ((drawingRect.right - drawingRect.left) / 2), drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mChannelTextSize / 2)), textPaint);

    // Loading channel image into target for
    final String imageURL = epgData.getChannel(position).getImageURL();

    if (mChannelImageCache.containsKey(imageURL)) {
      Bitmap image = mChannelImageCache.get(imageURL);
      drawingRect = getDrawingRectForChannelImage(drawingRect, image);
      canvas.drawBitmap(image, null, drawingRect, null);
    }
  }

  private Rect getDrawingRectForChannelImage(Rect drawingRect, Bitmap image) {
    drawingRect.left += mChannelLayoutPadding;
    drawingRect.top += mChannelLayoutPadding;
    drawingRect.right -= mChannelLayoutPadding;
    drawingRect.bottom -= mChannelLayoutPadding;

    final int imageWidth = image.getWidth();
    final int imageHeight = image.getHeight();
    final float imageRatio = imageHeight / (float) imageWidth;

    final int rectWidth = drawingRect.right - drawingRect.left;
    final int rectHeight = drawingRect.bottom - drawingRect.top;

    // Keep aspect ratio.
    if (imageWidth > imageHeight) {
      final int padding = (int) (rectHeight - (rectWidth * imageRatio)) / 2;
      drawingRect.top += padding;
      drawingRect.bottom -= padding;
    } else if (imageWidth <= imageHeight) {
      final int padding = (int) (rectWidth - (rectHeight / imageRatio)) / 2;
      drawingRect.left += padding;
      drawingRect.right -= padding;
    }

    return drawingRect;
  }

  private boolean shouldDrawTimeLine(long now) {
    return now >= mTimeLowerBoundary && now < mTimeUpperBoundary;
  }

  private boolean isEventVisible(final long start, final long end) {
    return (start >= mTimeLowerBoundary && start <= mTimeUpperBoundary)
        || (end >= mTimeLowerBoundary && end <= mTimeUpperBoundary)
        || (start <= mTimeLowerBoundary && end >= mTimeUpperBoundary);
  }

  private long calculatedBaseLine() {
    return LocalDateTime.now().toDateTime().minusMillis(DAYS_BACK_MILLIS).getMillis();
  }


  private int getFirstVisibleChannelPosition() {
    final int y = getScrollY();

    int position = (y - mChannelLayoutMargin - mTimeBarHeight)
        / (mChannelLayoutHeight + mChannelLayoutMargin);

    if (position < 0) {
      position = 0;
    }
    return position;
  }

  private int getLastVisibleChannelPosition() {
    final int y = getScrollY();
    final int totalChannelCount = epgData.getChannelCount();
    final int screenHeight = getHeight();
    int position = (y + screenHeight + mTimeBarHeight - mChannelLayoutMargin)
        / (mChannelLayoutHeight + mChannelLayoutMargin);

    if (position > totalChannelCount - 1) {
      position = totalChannelCount - 1;
    }

    // Add one extra row if we don't fill screen with current..
    return (y + screenHeight) > (position * mChannelLayoutHeight) && position < totalChannelCount - 1 ? position + 1 : position;
  }

  private void calculateMaxHorizontalScroll() {
    mMaxHorizontalScroll = (int) ((DAYS_BACK_MILLIS + DAYS_FORWARD_MILLIS - HOURS_IN_VIEWPORT_MILLIS) / mMillisPerPixel);
  }

  private void calculateMaxVerticalScroll() {
    final int maxVerticalScroll = getTopFrom(epgData.getChannelCount() - 1) + mChannelLayoutHeight;
    mMaxVerticalScroll = maxVerticalScroll < getHeight() ? 0 : maxVerticalScroll - getHeight();
  }

  private int getXFrom(long time) {
    return (int) ((time - mTimeOffset) / mMillisPerPixel) + mChannelLayoutMargin
        + mChannelLayoutWidth + mChannelLayoutMargin;
  }

  private int getTopFrom(int position) {
    int y = position * (mChannelLayoutHeight + mChannelLayoutMargin)
        + mChannelLayoutMargin + mTimeBarHeight;
    return y;
  }

  private long getTimeFrom(int x) {
    return (x * mMillisPerPixel) + mTimeOffset;
  }

  private long calculateMillisPerPixel() {
    return HOURS_IN_VIEWPORT_MILLIS / (getResources().getDisplayMetrics().widthPixels - mChannelLayoutWidth - mChannelLayoutMargin);
  }

  private int getXPositionStart() {
    return getXFrom(System.currentTimeMillis() - (HOURS_IN_VIEWPORT_MILLIS / 2));
  }

  private void resetBoundaries() {
    mMillisPerPixel = calculateMillisPerPixel();
    mTimeOffset = calculatedBaseLine();
    mTimeLowerBoundary = getTimeFrom(0);
    mTimeUpperBoundary = getTimeFrom(getWidth());
  }

  private Rect calculateChannelsHitArea() {
    mMeasuringRect.top = mTimeBarHeight;
    int visibleChannelsHeight = epgData.getChannelCount() * (mChannelLayoutHeight + mChannelLayoutMargin);
    mMeasuringRect.bottom = visibleChannelsHeight < getHeight() ? visibleChannelsHeight : getHeight();
    mMeasuringRect.left = 0;
    mMeasuringRect.right = mChannelLayoutWidth;
    return mMeasuringRect;
  }

  private Rect calculateProgramsHitArea() {
    mMeasuringRect.top = mTimeBarHeight;
    int visibleChannelsHeight = epgData.getChannelCount() * (mChannelLayoutHeight + mChannelLayoutMargin);
    mMeasuringRect.bottom = mTimeBarHeight + (visibleChannelsHeight < getHeight() ? visibleChannelsHeight : getHeight());
    mMeasuringRect.left = mChannelLayoutWidth;
    mMeasuringRect.right = getWidth();
    return mMeasuringRect;
  }


  private Rect calculateResetButtonHitArea() {
    mMeasuringRect.left = getScrollX() + getWidth() - mResetButtonSize - mResetButtonMargin;
    mMeasuringRect.top = getScrollY() + getHeight() - mResetButtonSize - mResetButtonMargin;
    mMeasuringRect.right = mMeasuringRect.left + mResetButtonSize;
    mMeasuringRect.bottom = mMeasuringRect.top + mResetButtonSize;
    return mMeasuringRect;
  }

  private int getChannelPosition(int y) {
    y -= mTimeBarHeight;
    int channelPosition = (y + mChannelLayoutMargin)
        / (mChannelLayoutHeight + mChannelLayoutMargin);

    return epgData.getChannelCount() == 0 ? -1 : channelPosition;
  }

  private int getProgramPosition(int channelPosition, long time) {
    List<EPGEvent> events = epgData.getEvents(channelPosition);

    if (events != null) {

      for (int eventPos = 0; eventPos < events.size(); eventPos++) {
        EPGEvent event = events.get(eventPos);
        if (event.getStart() <= time && event.getEnd() >= time) {
          return eventPos;
        }
      }
    }
    return -1;
  }

  private EPGEvent getProgramAtTime(int channelPosition, long time) {
    List<EPGEvent> events = epgData.getEvents(channelPosition);

    if (events != null) {

      for (int eventPos = 0; eventPos < events.size(); eventPos++) {
        EPGEvent event = events.get(eventPos);

        if (event.getStart() <= time && event.getEnd() >= time) {
          return event;
        }
      }
    }
    //if we are unable to find any channel, then return the first program for that channel
    return events.get(0);
  }

  /**
   * Add click listener to the EPG.
   *
   * @param epgClickListener to add.
   */
  public void setEPGClickListener(EPGClickListener epgClickListener) {
    mClickListener = epgClickListener;
  }

  public void reset() {
    if (lastSelectedEvent != null) {
      lastSelectedEvent.setPressed(false);
      lastSelectedEvent.setSelected(false);
    }

    lastSelectedEvent = null;

    if (lastPressedEvent != null) {
      lastPressedEvent.setPressed(false);
      lastPressedEvent.setSelected(false);
    }

    lastPressedEvent = null;
  }

  /**
   * Add data to EPG. This must be set for EPG to able to draw something.
   *
   * @param epgData pass in any implementation of EPGData.
   */
  public void setEPGData(EPGData epgData) {
    reset();
    this.epgData = epgData; //mergeEPGData(this.epgData, epgData);
    //this.epgData = epgData;
    recalculateAndRedraw(true);
  }

  /**
   * This will recalculate boundaries, maximal scroll and scroll to start position which is current time.
   * To be used on device rotation etc since the device height and width will change.
   *
   * @param withAnimation true if scroll to current position should be animated.
   */
  public void recalculateAndRedraw(boolean withAnimation) {
    if (epgData != null && epgData.hasData()) {
      resetBoundaries();
      calculateMaxVerticalScroll();
      calculateMaxHorizontalScroll();

      int scrollX = getXFrom(getMostRecentHourTimeWithOffset()) -(mChannelLayoutWidth + mChannelLayoutMargin);
      new Handler().post(() -> {
        mScroller.startScroll(0, getScrollY(),
            scrollX,
            0, withAnimation ? 600 : 0);

        redraw();
      });
    }

    redraw();
  }


  private long getMostRecentHourTime() {
    DateTime dateTime = DateTime.now().withSecondOfMinute(0);

    if (dateTime.getMinuteOfHour() >= 30) {
      dateTime = dateTime.withMinuteOfHour(30);
    } else {
      dateTime = dateTime.withMinuteOfHour(0);
    }

    return dateTime.getMillis();
  }

  private long getMostRecentHourTimeWithOffset() {
    return getMostRecentHourTime() ; //- 20 * 60 * 1000;
  }


  /**
   * Does a invalidate() and requestLayout() which causes a redraw of screen.
   */
  public void redraw() {
    invalidate();
    requestLayout();
  }

  /**
   * Clears the local image cache for channel images. Can be used when leaving epg and you want to
   * free some memory. Images will be fetched again when loading EPG next time.
   */
  public void clearEPGImageCache() {
    mChannelImageCache.clear();
  }

  public void setSelectedProgram(EPGEvent epgEvent) {
    epgEvent.setSelected(true);
    lastSelectedEvent = epgEvent;
  }


  private void loadProgramDetails(EPGEvent epgEvent) {
    // load program details
    if (mClickListener != null) {
      mClickListener.onEventSelected(epgEvent);
    }
  }

  // Configuration.ORIENTATION_PORTRAIT or Configuration.ORIENTATION_LANDSCAPE
  public void setOrientation(int orientation) {
    this.orientation = orientation;
    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
    screenWidth = dm.widthPixels;
    screenHeight = dm.heightPixels;
  }


  private void gotoPreviousDay(EPGEvent currentEvent) {
    //TODO
  }

  private void gotoNextDay(EPGEvent currentEvent) {
    //TODO
  }

  private class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

      // This is absolute coordinate on screen not taking scroll into account.
      int x = (int) e.getX();
      int y = (int) e.getY();

      // Adding scroll to clicked coordinate
      int scrollX = getScrollX() + x;
      int scrollY = getScrollY() + y;

      int channelPosition = getChannelPosition(scrollY);

      if (calculateResetButtonHitArea().contains(scrollX, scrollY)) {
        // Reset button clicked
        mClickListener.onResetButtonClicked();
        return true;
      }

      if (channelPosition != -1 && mClickListener != null && channelPosition < epgData.getChannelCount()) {
        if (calculateChannelsHitArea().contains(x, y)) {
          // Channel area is clicked
          mClickListener.onChannelClicked(channelPosition, epgData.getChannel(channelPosition));
        } else if (calculateProgramsHitArea().contains(x, y)) {
          // Event area is clicked
          int programPosition = getProgramPosition(channelPosition, getTimeFrom(getScrollX() + x - calculateProgramsHitArea().left));

          if (programPosition != -1) {

            EPGEvent epgEvent = epgData.getEvent(channelPosition, programPosition);

            if (epgEvent == null || epgEvent.isSelected()) {
              return true;
            }

            if (lastSelectedEvent != null) {
              lastSelectedEvent.setSelected(false);
            }

            lastSelectedEvent = epgEvent;
            lastSelectedEvent.setSelected(true);
            invalidate();
            mClickListener.onEventClicked(channelPosition, programPosition, epgData.getEvent(channelPosition, programPosition));
          }
        }
      }

      return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
      int dx = (int) distanceX;
      int dy = (int) distanceY;
      int x = getScrollX();
      int y = getScrollY();


      // Avoid over scrolling
      if (x + dx < 0) {
        dx = 0 - x;
      }
      if (y + dy < 0) {
        dy = 0 - y;
      }
      if (x + dx > mMaxHorizontalScroll) {
        dx = mMaxHorizontalScroll - x;
      }
      if (y + dy > mMaxVerticalScroll) {
        dy = mMaxVerticalScroll - y;
      }

      scrollBy(dx, dy);
      return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float vX, float vY) {

      mScroller.fling(getScrollX(), getScrollY(), -(int) vX,
          -(int) vY, 0, mMaxHorizontalScroll, 0, mMaxVerticalScroll);

      redraw();
      return true;
    }


    @Override
    public boolean onDown(MotionEvent e) {
      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
        return true;
      }

      // This is absolute coordinate on screen not taking scroll into account.
      int x = (int) e.getX();
      int y = (int) e.getY();

      int scrollY = getScrollY() + y;

      int channelPosition = getChannelPosition(scrollY);

      if (calculateProgramsHitArea().contains(x, y)) {
        // Event area is clicked
        int programPosition = getProgramPosition(channelPosition, getTimeFrom(getScrollX() + x - calculateProgramsHitArea().left));

        if (programPosition != -1) {

          if (lastPressedEvent != null) {
            lastPressedEvent.setPressed(false);
          }

          lastPressedEvent = epgData.getEvent(channelPosition, programPosition);
          lastPressedEvent.setPressed(true);
          invalidate();
        }
      }

      return true;
    }
  }
}
