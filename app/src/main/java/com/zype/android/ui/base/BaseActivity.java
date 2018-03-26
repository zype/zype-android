package com.zype.android.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;
import com.zype.android.ZypeApp;
import com.zype.android.core.NetworkStateObserver;
import com.zype.android.core.bus.BusProvider;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.events.NetworkDisabledEvent;
import com.zype.android.core.events.NetworkEnabledEvent;
import com.zype.android.core.events.WifiDisabledStateEvent;
import com.zype.android.core.events.WifiEnabledStateEvent;
import com.zype.android.service.DownloadHelper;
import com.zype.android.webapi.WebApiManager;

/**
 * @author vasya
 * @version 1
 *          date 6/26/15
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final NetworkEventHandler mNetworkEventHandler = new NetworkEventHandler();
    private final ErrorHandler mEventsHandler = new ErrorHandler();
    private WebApiManager mApi;

    public WebApiManager getApi() {
        return mApi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WebApiManager.getInstance();
        mApi.subscribe(mEventsHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApi.unsubscribe(mEventsHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApi.subscribe(this);
        BusProvider.getBus().register(mNetworkEventHandler);
        NetworkStateObserver.getInstance().register(this);
        DownloadHelper.checkDownloadTasks(getApplicationContext());
        Tracker t = ZypeApp.getTracker();
        if (t != null) {
            t.setScreenName(getActivityName());
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApi.unsubscribe(this);
        BusProvider.getBus().unregister(mNetworkEventHandler);
        NetworkStateObserver.getInstance().unregister(this);
    }

    protected void handleAuthorizationError(AuthorizationErrorEvent event) {
//        mApi.cancelPendingRequests(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract String getActivityName();

    private class NetworkEventHandler {
        @Subscribe
        public void onNetworkStateDisabled(NetworkDisabledEvent event) {
            Toast.makeText(BaseActivity.this, "onNetworkStateChanged ", Toast.LENGTH_SHORT).show();
        }

        @Subscribe
        public void onNetworkStateEnabled(NetworkEnabledEvent event) {
            Toast.makeText(BaseActivity.this, "NetworkEnabledEvent ", Toast.LENGTH_SHORT).show();
            DownloadHelper.checkDownloadTasks(getApplicationContext());
        }

        @Subscribe
        public void onWifiStateDisabled(WifiDisabledStateEvent event) {
            Toast.makeText(BaseActivity.this, "WifiDisabledStateEvent ", Toast.LENGTH_SHORT).show();
        }

        @Subscribe
        public void onWifiStateEnabled(WifiEnabledStateEvent event) {
            Toast.makeText(BaseActivity.this, "WifiEnabledStateEvent ", Toast.LENGTH_SHORT).show();
            DownloadHelper.checkDownloadTasks(getApplicationContext());
        }
    }

    private class ErrorHandler {
        @Subscribe
        public void handleAuthorizationErrorInternal(AuthorizationErrorEvent error) {
            handleAuthorizationError(error);
        }
    }
}
