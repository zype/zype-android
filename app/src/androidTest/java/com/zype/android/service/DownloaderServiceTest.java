package com.zype.android.service;

import android.test.ActivityInstrumentationTestCase2;

import com.zype.android.ui.ActivityForTests;
import com.zype.android.webapi.WebApiManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author vasya
 * @version 1
 *          date 8/6/15
 */
public class DownloaderServiceTest extends ActivityInstrumentationTestCase2<ActivityForTests> {

    private static final int TEST_FAIL_TIME = 500000;
    private final CountDownLatch mLock = new CountDownLatch(1);
    private WebApiManager mApi;
    private ActivityForTests mActivity;

    //    public DownloaderServiceTest(Class<ActivityForTests> activityClass) {
//        super(activityClass);
//    }
    public DownloaderServiceTest() {
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
                mApi.subscribe(DownloaderServiceTest.this);
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

    public void testDownload() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String url = "http://video.zype.com/54ecae4069702d523c000000/55ba566669702d04e5b62801/55ba7db269702d0722074101/550b0c6269702d0707cd0200/audio.m4a";
                String fileId = "55ba566669702d04e5b62801";
                DownloaderService.downloadAudio(mActivity, url, fileId);
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));

    }

    public void testDownloadFile() throws InterruptedException {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                String url = "http://download.zype.com/54ecae4069702d523c000000/55b192aa69702d04de00a003/55b1bc4e69702d04e2c14200/550b0b2669702d0707940200/video.mp4?Expires=1439114482&Signature=X1YdkmPpxv6OeE8AODe8qSXGTYk~u~NGNJcS2bXDq-l8IovIzTBsNuZ5g0GwJhS22dV5vKJeZSGqdJ6IoUtkr6WWReYMv3ttAk2idQJmTRcR5oF8DmDxP5kq36II9GTcIabuVnhsmQPJe7qODarYn3nOT9IGs4zFtJFPRtPJcBcliMtZgEAUDvbM~0dalmcVpiNxRQu6eEbqGqOhhGa0EUPE9H3IGYIJi~aaQB1AmZixiGoyTgtY2C7gE2zrbSQN5FW~gDBnN0ySqr8fA6zth~NtUSdw6A3LNBeaMqMsym4-QsFJ4fUpO3zKjAmUgpDANdEcPSxv3bucMeAuQw3tGA__&Key-Pair-Id=APKAIDSGABTG5TCDS6VA";
//                String fileId = "55b192aa69702d04de00a003";
//                DownloaderService.downloadVideo(mActivity, url, fileId);
            }
        });
        assertTrue(mLock.await(TEST_FAIL_TIME, TimeUnit.MILLISECONDS));
    }
}