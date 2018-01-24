package com.zype.android.Billing;

import android.content.Context;

import com.android.billingclient.api.Purchase;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.Logger;

import java.util.ArrayList;
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
        if (purchases != null && !purchases.isEmpty()) {
            int subscriptionCount = 0;
            for (Purchase item : purchases) {
                if (getSkuList().contains(item.getSku())) {
                    subscriptionCount += 1;
                }
            }
            Logger.d("updateSubscriptionCount(): Native purchases count: " + subscriptionCount);
            SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
        }
        else {
            Logger.d("updateSubscriptionCount(): No native purchases");
            SettingsProvider.getInstance().saveSubscriptionCount(0);
        }
    }

    public static List<String> getSkuList() {
        List<String> result = new ArrayList<>();
        result.add("com.zype.android.demo.monthly");
        result.add("com.zype.android.demo.yearly");
        return result;
    }

    public static boolean isUserSubscribed(Context context) {
        if (ZypeConfiguration.isNativeSubscriptionEnabled(context)) {
            return (SettingsProvider.getInstance().getSubscriptionCount() > 0);
        }
        else {
            return (SettingsProvider.getInstance().isLoggedIn() && SettingsProvider.getInstance().getSubscriptionCount() > 0);
        }
    }
}
