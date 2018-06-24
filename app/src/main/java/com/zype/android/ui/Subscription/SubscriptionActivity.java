package com.zype.android.ui.Subscription;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.Subscription;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Gallery.HeroImagesViewModel;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.Subscription.Model.SubscriptionItem;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.events.bifrost.BifrostEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.model.bifrost.Bifrost;
import com.zype.android.webapi.model.bifrost.BifrostData;
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.webapi.model.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evgeny Cherkasov on 27.06.2017.
 */
public class SubscriptionActivity extends BaseActivity implements BillingManager.BillingUpdatesListener {
    private static final String TAG = SubscriptionActivity.class.getSimpleName();

    private RecyclerView listSubscriptions;
    private FrameLayout layoutLogin;
    private Button buttonLogin;
    private LinearLayout layoutLoggedIn;
    private TextView textUsername;

    private ProgressDialog dialogProgress;

    private SubscriptionsAdapter adapter;
    private Subscription selectedSubscription = null;

    // In-app billing
    private BillingManager billingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.subscription_title);

        initParameters(savedInstanceState);

        listSubscriptions = findViewById(R.id.listSubscriptions);
        adapter = new SubscriptionsAdapter();
        listSubscriptions.setAdapter(adapter);

        layoutLogin = findViewById(R.id.layoutLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper.getInstance(SubscriptionActivity.this).switchToLoginScreen(SubscriptionActivity.this);
            }
        });
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn);
        textUsername = findViewById(R.id.textUsername);

        ZypeApp.marketplaceGateway.getSubscriptions().observe(this, new Observer<Map<String, Subscription>>() {
            @Override
            public void onChanged(@Nullable Map<String, Subscription> subscriptions) {
                adapter.setData((List<Subscription>) subscriptions.values());
            }
        });

        billingManager = new BillingManager(this, this);
        hideProgress();
        updateViews();
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
    private void updateViews() {
//        if (ZypeSettings.NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED) {
//            layoutLogin.setVisibility(View.VISIBLE);
//            if (SettingsProvider.getInstance().isLoggedIn()) {
//                buttonLogin.setVisibility(View.GONE);
//                layoutLoggedIn.setVisibility(View.VISIBLE);
//            }
//            else {
//                buttonLogin.setVisibility(View.VISIBLE);
//                layoutLoggedIn.setVisibility(View.GONE);
//            }
//        }
//        else {
//            layoutLogin.setVisibility(View.GONE);
//        }
        bindViews();
    }

    private void bindViews() {
//        if (ZypeSettings.NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED) {
//            if (SettingsProvider.getInstance().isLoggedIn()) {
//                textUsername.setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_EMAIL));
//            }
//        }
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
    // Actions
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BundleConstants.REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    if (selectedSubscription != null) {
                        purchaseSubscription(selectedSubscription);
                    }
                    else {
                        updateViews();
                    }
                }
                break;
            case BundleConstants.REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (SettingsProvider.getInstance().getSubscriptionCount() > 0) {
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        updateViews();
                    }
                }
                break;
        }
    }

    // //////////
    // In-app billing
    //
//    private void getSubscriptions(List<Purchase> purchases) {
//        // TODO: Use purchases to mark owned skus
//        billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, SubscriptionsHelper.getSkuList(),
//                new SkuDetailsResponseListener() {
//                    @Override
//                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
//                        if (responseCode != BillingClient.BillingResponse.OK) {
//                            // TODO: Handle error retrieving subscriptions list
//                        }
//                        else {
//                            if (skuDetailsList != null) {
//                                List<SubscriptionItem> items = new ArrayList<>();
//                                for (SkuDetails sku : skuDetailsList) {
//                                    SubscriptionItem item = new SubscriptionItem();
//                                    item.title = sku.getTitle();
//                                    item.description = sku.getDescription();
//                                    item.price = sku.getPriceAmountMicros() / 1000000;
//                                    item.sku = sku.getSku();
//                                    items.add(item);
//                                }
//                                adapter.setData(items);
//                            }
//                        }
//                    }
//                }
//        );
//    }

    private void purchaseSubscription(Subscription item) {
        billingManager.initiatePurchaseFlow(this, item.getMarketplace().getSku(), BillingClient.SkuType.SUBS);
    }

    //
    // 'BillinggManager' listener implementation
    //
    @Override
    public void onBillingClientSetupFinished() {
        Logger.d("onBillingClientSetupFinished(): ");
//        getSubscriptions(null);
    }

    @Override
    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        boolean result = false;
        if (ZypeConfiguration.isNativeSubscriptionEnabled(this)) {
            if (purchases != null && !purchases.isEmpty()) {
                result = true;
            }
            SubscriptionsHelper.updateSubscriptionCount(purchases);
        }
        else if (ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(this)) {
            if (purchases != null && !purchases.isEmpty()) {
                // TODO: Change validation request
//                if (selectedSubscription != null) {
//                    SubscriptionsHelper.validateSubscription(purchases, selectedSubscription.sku, getApi());
//                }
            }
        }
        if (result) {
            setResult(RESULT_OK);
            finish();
        }
        else {
//            getSubscriptions(purchases);
        }
    }

    // //////////
    // Data
    //
    public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {
        private List<Subscription> items;

        public SubscriptionsAdapter() {
            items = new ArrayList<>();
        }

        public void setData(List<Subscription> items) {
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
            holder.textTitle.setText(holder.item.getMarketplace().getTitle());
            holder.textPrice.setText(String.valueOf(holder.item.getMarketplace().getPrice()));
            holder.textDescription.setText(holder.item.getMarketplace().getDescription());
            holder.buttonContinue.setText(String.format(getString(R.string.subscription_item_button_continue), holder.item.getMarketplace().getTitle()));
            holder.buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSubscription = holder.item;
//                    if (ZypeSettings.NATIVE_TO_UNIVERSAL_SUBSCRIPTION_ENABLED) {
//                        // Create consumer before making purchase in case user is not logged in
//                        if (!SettingsProvider.getInstance().isLoggedIn()) {
//                            NavigationHelper.getInstance(SubscriptionActivity.this).switchToConsumerScreen(SubscriptionActivity.this);
//                            return;
//                        }
//                    }
                    purchaseSubscription(selectedSubscription);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public Subscription item;
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
    @Subscribe
    public void handleBifrost(BifrostEvent event) {
        BifrostData data = event.getEventData().getModelData().data;
        if (data.success) {
            if (data.isValid) {
                hideProgress();
                setResult(RESULT_OK);
                finish();
            }
            else {
                // TODO: Show subscription not valid message
            }
        }
    }
}
