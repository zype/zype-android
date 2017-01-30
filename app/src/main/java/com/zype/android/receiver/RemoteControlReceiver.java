package com.zype.android.receiver;

import com.zype.android.ui.player.PlayerFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Observable;

public class RemoteControlReceiver extends BroadcastReceiver {

    private static final String TAG = "RemoteControlReceiver";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && (event.getAction() == KeyEvent.ACTION_UP)) {
                int keycode = event.getKeyCode();
                getObservable().stateChanged(keycode);
            } else {
                Log.i(TAG, "null event");
            }
        } else if (intent.getAction().equals(PlayerFragment.MEDIA_STOP)) {
            getObservable().stateChanged(PlayerFragment.MEDIA_STOP_CODE);
        }
    }

    public static class RemoteControlObservable extends Observable {
        private static RemoteControlObservable instance = null;

        private RemoteControlObservable() {
            // Exist to defeat instantiation.
        }

        public void stateChanged(int keycode) {
            setChanged();
            notifyObservers(keycode);
        }

        public static RemoteControlObservable getInstance() {
            if (instance == null) {
                instance = new RemoteControlObservable();
            }
            return instance;
        }
    }

    public static RemoteControlObservable getObservable() {
        return RemoteControlObservable.getInstance();
    }
}
