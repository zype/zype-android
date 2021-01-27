package com.zype.android.ui.Subscription;

import com.zype.android.core.settings.SettingsProvider;

import java.util.HashSet;

/**
 * Created by Evgeny Cherkasov on 21.05.2018.
 */

public class SubscriptionHelper {

    public static boolean hasSubscription() {
        return (SettingsProvider.getInstance().getSubscriptionCount() > 0);
    }

    public static String getSubscriptionId() {
        HashSet<String> subscriptionIds = SettingsProvider.getInstance().getSubscriptionIds();
        if (subscriptionIds.isEmpty()) return null;
        return subscriptionIds.iterator().next();
    }
}
