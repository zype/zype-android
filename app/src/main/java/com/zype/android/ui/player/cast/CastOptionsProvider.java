package com.zype.android.ui.player.cast;

import android.content.Context;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {
        String applicationId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        return new CastOptions.Builder()
                .setReceiverApplicationId(applicationId)
                .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(
            Context context) {
        return null;
    }
}
