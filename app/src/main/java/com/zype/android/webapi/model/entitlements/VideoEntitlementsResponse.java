package com.zype.android.webapi.model.entitlements;

import com.zype.android.webapi.model.DataModel;
import com.zype.android.webapi.model.consumers.ConsumerFavoriteVideo;

/**
 * Created by Evgeny Cherkasov on 12.03.2018.
 */

public class VideoEntitlementsResponse extends DataModel<VideoEntitlements> {
    public VideoEntitlementsResponse(VideoEntitlements data) {
        super(data);
    }
}
