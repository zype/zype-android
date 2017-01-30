package com.zype.android.core.bus;

import com.squareup.otto.Bus;

public class BusProvider extends Bus {

    private static EventBus sBus;

    private BusProvider() {};

    public static EventBus getBus() {
        if (sBus == null) {
            sBus = new EventBus();
        }
        return sBus;
    }

    @Override
    public void register(Object obj) {
        sBus.register(obj);
    }

    @Override
    public void unregister(Object obj) {
        sBus.unregister(obj);
    }
}
