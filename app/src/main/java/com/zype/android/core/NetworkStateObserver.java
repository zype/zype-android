package com.zype.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.zype.android.core.bus.BusProvider;
import com.zype.android.core.events.NetworkDisabledEvent;
import com.zype.android.core.events.NetworkEnabledEvent;
import com.zype.android.core.events.WifiDisabledStateEvent;
import com.zype.android.core.events.WifiEnabledStateEvent;

public class NetworkStateObserver {

    private static NetworkStateObserver sInstance;
    //    private WeakReference<Context> contextHolder;
    private static boolean networkEnabled;
    private static boolean wifiEnabled;
    private  BroadcastReceiver receiver;

    public NetworkStateObserver() {
        networkEnabled = true;
        wifiEnabled = true;
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                checkNetworkState(context);
//                checkWifiState(context);
//            }
//        };
    }

    public static NetworkStateObserver getInstance() {
        if (sInstance == null) {
            sInstance = new NetworkStateObserver();
        }
        return sInstance;
    }

    public static boolean isWiFiEnable() {
        return wifiEnabled;
    }

    public static boolean isNetworkEnabled() {
        return networkEnabled;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkNetworkState(context);
                checkWifiState(context);
            }
        };
        context.registerReceiver(receiver, filter);
        checkNetworkState(context);
        checkWifiState(context);
    }

    public void unregister(Context context) {
        if (context == null) return;
        context.unregisterReceiver(receiver);
        receiver = null;
    }

    private void checkNetworkState(Context context) {
        if (context == null) return;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected == networkEnabled){ return;}

        networkEnabled = isConnected;
        if (!isConnected) {
            BusProvider.getBus().post(new NetworkDisabledEvent());
        }else{
            BusProvider.getBus().post(new NetworkEnabledEvent());
        }

    }

    private void checkWifiState(Context context) {
        if (context == null) return;

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean isEnabled = wm.isWifiEnabled();
        if (isEnabled == wifiEnabled){ return;}

        wifiEnabled = isEnabled;

        if (!isEnabled) {
            BusProvider.getBus().post(new WifiDisabledStateEvent());
        }else{
            BusProvider.getBus().post(new WifiEnabledStateEvent());
        }

    }

}
