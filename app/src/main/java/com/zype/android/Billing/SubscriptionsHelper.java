package com.zype.android.Billing;

import com.android.billingclient.api.Purchase;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;

import java.util.List;

/**
 * Created by Evgeny Cherkasov on 10.07.2017.
 */

public class SubscriptionsHelper {
    private static final String TAG = SubscriptionsHelper.class.getSimpleName();

    /**
     * Update settings with native subscriptions count
     * @param purchases
     */
    public static void updateSubscriptionCount(List<Purchase> purchases) {
        // TODO: Filter purchases on subscriptions only to set subscription count
        if (purchases != null && !purchases.isEmpty()) {
            Logger.d("updateSubscriptionCount(): Native purchases count: " + purchases.size());
            SettingsProvider.getInstance().saveSubscriptionCount(purchases.size());
        }
        else {
            Logger.d("updateSubscriptionCount(): No native purchases");
            SettingsProvider.getInstance().saveSubscriptionCount(0);
        }
    }
}
