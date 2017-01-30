package com.zype.android.webapi;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.otto.Subscribe;
import com.zype.android.ui.ActivityForTests;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.builder.VideoParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.video.RetrieveVideoEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class TestVideo extends ActivityInstrumentationTestCase2<ActivityForTests> {
    private static final int TEST_FAIL_TIME = 5000;
    private final CountDownLatch mLock = new CountDownLatch(1);
    private WebApiManager mApi;
    private ActivityForTests mActivity;

    public TestVideo() {
        super(ActivityForTests.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mApi = WebApiManager.getInstance();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mApi.subscribe(TestVideo.this);
            }
        });
    }

    @Override
    public void tearDown() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mApi.unsubscribe(this);
                mLock.countDown();
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
        super.tearDown();
    }

    @Subscribe
    public void handleError(ErrorEvent event) {
        fail("Failed : " + event.getErrMessage());
        mLock.countDown();
    }

    @Subscribe
    public void handleRetrieveVideo(RetrieveVideoEvent response) {
        Logger.d("handleVideo");
        assertTrue(response.getEventData() != null);
        assertTrue(response.getEventData().getModelData() != null);
        mLock.countDown();
    }

    @SmallTest
    public void testGetWeekVideo() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoParamsBuilder builder = new VideoParamsBuilder()
                        .addDateLimit("2015-06-28", "2015-07-04")
                        .addPage(1);
                mApi.executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }

    @SmallTest
    public void testOnAirVideo() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoParamsBuilder builder = new VideoParamsBuilder()
                        .addOnAir(true);
                mApi.executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }

    @SmallTest
    public void testHighLightVideo() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoParamsBuilder builder = new VideoParamsBuilder()
                        .addPage(1)
                        .addCategoryHighlight(true);
                mApi.executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }
}
