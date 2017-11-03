package com.zype.android.ui.Subscription;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Consumer.Model.Consumer;
import com.zype.android.ui.Subscription.Model.SubscriptionItem;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerCreateParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 27.06.2017.
 */
public class SubscriptionActivity extends BaseActivity implements BillingManager.BillingUpdatesListener {
    private static final String TAG = SubscriptionActivity.class.getSimpleName();

    private RecyclerView listSubscriptions;

    private ProgressDialog dialogProgress;

    private SubscriptionsAdapter adapter;

    // In-app billing
    private BillingManager billingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.subscription_title);

        initParameters(savedInstanceState);

        listSubscriptions = (RecyclerView) findViewById(R.id.listSubscriptions);
        adapter = new SubscriptionsAdapter();
        listSubscriptions.setAdapter(adapter);

        billingManager = new BillingManager(this, this);
        hideProgress();
        bindViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }

    private void initParameters(Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState != null) {
            args = savedInstanceState;
        }
        else {
            args = getIntent().getExtras();
        }
        if (args != null) {
        }
        else {
        }
    }

    // //////////
    // UI
    //
    private void bindViews() {
    }

    private void showProgress() {
//        dialogProgress = new ProgressDialog(this);
//        dialogProgress.setMessage(getString(R.string.consumer_progress_create));
//        dialogProgress.setCancelable(false);
//        dialogProgress.show();
    }

    private void hideProgress() {
//        if (dialogProgress != null) {
//            dialogProgress.dismiss();
//        }
    }

    // //////////
    // In-app billing
    //
    private void getSubscriptions(List<Purchase> purchases) {
        // TODO: Use purchases to mark owned skus
        billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, SubscriptionsHelper.getSkuList(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode != BillingClient.BillingResponse.OK) {
                            // TODO: Handle error retrieving subscriptions list
                        }
                        else {
                            if (skuDetailsList != null) {
                                List<SubscriptionItem> items = new ArrayList<>();
                                for (SkuDetails sku : skuDetailsList) {
                                    SubscriptionItem item = new SubscriptionItem();
                                    item.title = sku.getTitle();
                                    item.description = sku.getDescription();
                                    item.price = sku.getPriceAmountMicros() / 1000000;
                                    item.sku = sku.getSku();
                                    items.add(item);
                                }
                                adapter.setData(items);
                            }
                        }
                    }
                }
        );
    }

    private void purchaseSubscription(SubscriptionItem item) {
        billingManager.initiatePurchaseFlow(item.sku, BillingClient.SkuType.SUBS);
    }

    //
    // 'BillinggManager' listener implementation
    //
    @Override
    public void onBillingClientSetupFinished() {
        Logger.d("onBillingClientSetupFinished(): ");
        getSubscriptions(null);
    }

    @Override
    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        SubscriptionsHelper.updateSubscriptionCount(purchases);
        if (purchases != null && !purchases.isEmpty()) {
            setResult(RESULT_OK);
            finish();
        }
        else {
            getSubscriptions(purchases);
        }
    }

    // //////////
    // Data
    //
    public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {
        private List<SubscriptionItem> items;

        public SubscriptionsAdapter() {
            items = new ArrayList<>();
        }

        public void setData(List<SubscriptionItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscriptions_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.item = items.get(position);
            holder.textTitle.setText(holder.item.title);
            holder.textPrice.setText(String.valueOf(holder.item.price));
            holder.textDescription.setText(holder.item.description);
            holder.buttonContinue.setText(String.format(getString(R.string.subscription_item_button_continue), holder.item.title));
            holder.buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchaseSubscription(holder.item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public SubscriptionItem item;
            public TextView textTitle;
            public TextView textPrice;
            public TextView textDescription;
            public Button buttonContinue;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textTitle = (TextView) view.findViewById(R.id.textTitle);
                textPrice = (TextView) view.findViewById(R.id.textPrice);
                textDescription = (TextView) view.findViewById(R.id.textDescription);
                buttonContinue = (Button) view.findViewById(R.id.buttonContinue);
            }
        }
    }

    // //////////
    // Event bus listeners
    //
}
