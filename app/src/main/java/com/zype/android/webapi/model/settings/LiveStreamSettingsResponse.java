package com.zype.android.webapi.model.settings;

import com.zype.android.webapi.model.DataModel;

/**
 * Created by Evgeny Cherkasov on 21.11.2016.
 */

public class LiveStreamSettingsResponse  extends DataModel<LiveStreamSettings> {
    public LiveStreamSettingsResponse(LiveStreamSettings liveStreamSettings) {
        super(liveStreamSettings);
    }
}
