package com.zype.android.webapi;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.otto.Subscribe;
import com.zype.android.ui.ActivityForTests;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author vasya
 * @version 1
 *          date 7/2/15
 */
public class TestZObject extends ActivityInstrumentationTestCase2<ActivityForTests> {
    private static final int TEST_FAIL_TIME = 50000;
    private final CountDownLatch mLock = new CountDownLatch(1);
    private WebApiManager mApi;
    private ActivityForTests mActivity;

    public TestZObject() {
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
                mApi.subscribe(TestZObject.this);
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

    @SmallTest
    public void testZObjectGuest() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
                        .addType(ZObjectParamsBuilder.TYPE_GUEST);
                mApi.executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }

    @SmallTest
    public void testZObjectNotification() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
                        .addType(ZObjectParamsBuilder.TYPE_NOTIFICATIONS);
                mApi.executeRequest(WebApiManager.Request.Z_OBJECT, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }

    @Subscribe
    public void handleZObject(ZObjectEvent response) {
        Logger.d("handleZObject " + response.getEventData());
        assertTrue(response.getEventData() != null);
        assertTrue(response.getEventData().getModelData() != null);
        mLock.countDown();
    }

    @SmallTest
    public void testContentObject() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
                        .addType(ZObjectParamsBuilder.TYPE_CONTENT);
                mApi.executeRequest(WebApiManager.Request.VIDEO_LATEST_GET, builder.build());
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }


}
