package com.zype.android.Billing;


import com.squareup.otto.Subscribe;
import com.zype.android.ui.Gallery.Model.HeroImage;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.PlanParamsBuilder;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;
import com.zype.android.webapi.events.plan.PlanEvent;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.plan.PlanData;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * Created by Evgeny Cherkasov on 23.06.2018
 */
public class SubscriptionLiveData extends LiveData<Subscription> {

    public SubscriptionLiveData(Subscription subscription) {
        super();
        setValue(subscription);
    }

    public void setVerified(boolean verified) {
        Subscription subscription = getValue();
        if (subscription != null) {
            subscription.setVerified(verified);
            setValue(subscription);
        }
    }
}
