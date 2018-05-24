package com.zype.android.ui.Subscription;

import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class SubscriptionHelper {

    public static boolean hasSubscription() {
        return (SettingsProvider.getInstance().getSubscriptionCount() > 0);
    }
}
