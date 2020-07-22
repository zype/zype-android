package com.zype.android.ui.Subscription;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.squareup.otto.Subscribe;
import com.zype.android.Billing.BillingManager;
import com.zype.android.Billing.Subscription;
import com.zype.android.Billing.SubscriptionsHelper;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.marketplaceconnect.MarketplaceConnectEvent;

import org.threeten.bp.Period;

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
                List<Subscription> subscriptionList = new ArrayList<>();
                for (Map.Entry<String, Subscription> entry : subscriptions.entrySet()) {
                    if (entry.getValue().getMarketplace() != null) {
                        subscriptionList.add(entry.getValue());
                    }
                }
                adapter.setData(subscriptionList);
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

    private void showProgress(String message) {
        if (!isFinishing()) {
            dialogProgress = new ProgressDialog(this);
            dialogProgress.setMessage(message);
            dialogProgress.setCancelable(false);
            dialogProgress.show();
        }
    }

    private void hideProgress() {
        if (!isFinishing()) {
            if (dialogProgress != null) {
                dialogProgress.dismiss();
            }
        }
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
                if (selectedSubscription != null) {
                    showProgress(getString(R.string.subscription_verify));
                    ZypeApp.marketplaceGateway.verifySubscription(selectedSubscription).observe(this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean result) {
                            hideProgress();
                            if (result) {
                                setResult(RESULT_OK);
                                finish();
                            }
                            else {
                                DialogHelper.showErrorAlert(SubscriptionActivity.this,
                                        getString(R.string.subscribe_or_login_error_validation));
                            }
                        }
                    });
                }
            }
        }
        if (result) {
            if (selectedSubscription != null) {
                setResult(RESULT_OK);
                finish();
            }
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
            if (holder.item.getMarketplace() != null) {
                holder.textTitle.setText(holder.item.getMarketplace().getTitle());
                String periodText = getPeriodText(holder.item.getMarketplace().getSubscriptionPeriod());
                String price;
                if (TextUtils.isEmpty(periodText)) {
                    price = String.valueOf(holder.item.getMarketplace().getPrice());
                }
                else {
                    price = String.format("%1$s/%2$s", holder.item.getMarketplace().getPrice(), periodText);
                }
                holder.textPrice.setText(price);
                String trialText = getTrialText(holder.item.getMarketplace().getFreeTrialPeriod());
                holder.textTrial.setText(trialText);
                holder.textDescription.setText(holder.item.getMarketplace().getDescription());
                holder.buttonContinue.setText(String.format(getString(R.string.subscription_item_button_continue), holder.item.getMarketplace().getTitle()));
                holder.buttonContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedSubscription = holder.item;
                        purchaseSubscription(selectedSubscription);
                    }
                });
            }
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
            public TextView textTrial;
            public TextView textDescription;
            public Button buttonContinue;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textTitle = view.findViewById(R.id.textTitle);
                textPrice = view.findViewById(R.id.textPrice);
                textTrial = view.findViewById(R.id.textTrial);
                textDescription = view.findViewById(R.id.textDescription);
                buttonContinue = view.findViewById(R.id.buttonContinue);
            }
        }

        private String getPeriodText(String subscriptionPeriod) {
            switch (subscriptionPeriod) {
                case "P1M":
                    return "month";
                case "P1Y":
                    return "year";
                default:
                    return "";
            }
        }

        private String getTrialText(String trialPeriod) {
            if (TextUtils.isEmpty(trialPeriod)) {
                return "";
            }
            int freeTrialDays = Period.parse(trialPeriod).getDays();
            return String.format(getString(R.string.subscription_trial_days), freeTrialDays);
        }
    }

}
