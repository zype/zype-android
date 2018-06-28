package com.zype.android.Billing;

import android.arch.lifecycle.LiveData;

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

/**
 * Created by Evgeny Cherkasov on 23.06.2018
 */
public class SubscriptionLiveData extends LiveData<Subscription> {
    @Override
    protected void onActive() {
        WebApiManager.getInstance().subscribe(this);
    }

    @Override
    protected void onInactive() {
        WebApiManager.getInstance().unsubscribe(this);
    }

    public void loadPlan(String planId) {
        PlanParamsBuilder builder = new PlanParamsBuilder(planId);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.PLAN, builder.build());
    }

    @Subscribe
    public void handlePlan(PlanEvent event) {
        Logger.d("handlePlan()");
        PlanData data = event.getEventData().getModelData().data;
        Subscription subscription = new Subscription();
        subscription.setZypePlan(data);
        setValue(subscription);
    }
}
