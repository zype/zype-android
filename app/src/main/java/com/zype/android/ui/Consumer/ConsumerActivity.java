package com.zype.android.ui.Consumer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.Consumer.Model.Consumer;
import com.zype.android.ui.Auth.LoginActivity;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.settings.TermsActivity;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ConsumerCreateParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.model.consumers.ConsumerData;

import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;

/**
 * Created by Evgeny Cherkasov on 27.06.2017.
 */
public class ConsumerActivity extends BaseActivity {
    private static final String TAG = ConsumerActivity.class.getSimpleName();

    public static final String PARAMETERS_CONSUMER = "Consumer";

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private Button buttonUpdate;
    private TextView textTerms;
    private TextView textSignIn;

    private ProgressDialog dialogProgress;

    private Consumer consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.consumer_title_create);

        initParameters(savedInstanceState);

//        TextView textTitle = findViewById(R.id.textTitle);
//        textTitle.setText(String.format(getString(R.string.consumer_create_form_subtitle),
//                getString(R.string.app_name)));
//        TextView textDescription = findViewById(R.id.textDescription);
//        textDescription.setText(String.format(getString(R.string.consumer_create_form_description),
//                getString(R.string.app_name)));

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                updateConsumer();
            }
        });
        textTerms = findViewById(R.id.textTerms);
        textSignIn = findViewById(R.id.textSignIn);

        hideProgress();
        bindViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARAMETERS_CONSUMER, consumer);
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
            consumer = args.getParcelable(PARAMETERS_CONSUMER);
        }
        else {
            consumer = null;
        }
    }

    // //////////
    // UI
    //
    private void bindViews() {
        if (consumer != null) {
            layoutEmail.getEditText().setText(consumer.email);
            layoutPassword.getEditText().setText(consumer.password);
        }
        // Set link to Terms screen
        String terms = getString(R.string.consumer_create_terms);
        String termsLink = getString(R.string.consumer_create_terms_link);
        SpannableString spannableTerms = new SpannableString(terms);
        ClickableSpan spanTerms = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                switchToTermsScreen();
            }
        };
        int index = terms.indexOf(termsLink);
        spannableTerms.setSpan(spanTerms, index, index + termsLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textTerms.setText(spannableTerms);
        textTerms.setMovementMethod(LinkMovementMethod.getInstance());
        // Set link to Sign in
        String signIn = getString(R.string.consumer_create_sign_in);
        String signInLink = getString(R.string.consumer_create_sign_in_link);
        SpannableString spannableSignIn = new SpannableString(signIn);
        ClickableSpan spanSignIn = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                switchToLoginScreen();
            }
        };
        int indexSignIn = signIn.indexOf(signInLink);
        spannableSignIn.setSpan(spanSignIn, indexSignIn, indexSignIn+ signInLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSignIn.setText(spannableSignIn);
        textSignIn.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showProgress() {
        dialogProgress = new ProgressDialog(this);
        dialogProgress.setMessage(getString(R.string.consumer_progress_create));
        dialogProgress.setCancelable(false);
        dialogProgress.show();
    }

    private void hideProgress() {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
    }

    // //////////
    // Actions
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
//                    NavigationHelper.getInstance(this).switchToSubscriptionScreen(this, null);
                    finish();
                }
                break;
        }
    }

    private void switchToLoginScreen() {
        NavigationHelper.getInstance(this).switchToLoginScreen(this);
    }

    private void switchToTermsScreen() {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    // //////////
    // Data
    //
    private Consumer getViewModel() {
        Consumer result = new Consumer();
        result.email = layoutEmail.getEditText().getText().toString().trim();
        result.password = layoutPassword.getEditText().getText().toString().trim();
        return result;
    }

    private boolean validate(Consumer consumer) {
        boolean result = true;
        if (TextUtils.isEmpty(consumer.email)) {
            result = false;
            layoutEmail.setError(getString(R.string.consumer_email_error_empty));
            layoutEmail.setErrorEnabled(true);
        }
        else {
            if (!isEmailValid(consumer.email)) {
                result = false;
                layoutEmail.setError(getString(R.string.consumer_email_error_invalid));
                layoutEmail.setErrorEnabled(true);
            }
            else {
                layoutEmail.setErrorEnabled(false);
            }
        }
        if (TextUtils.isEmpty(consumer.password)) {
            result = false;
            layoutPassword.setError(getString(R.string.consumer_password_error_empty));
            layoutPassword.setErrorEnabled(true);
        }
        else {
            if (!isPasswordValid(consumer.password)) {
                result = false;
                layoutPassword.setError(getString(R.string.error_invalid_password));
                layoutPassword.setErrorEnabled(true);
            }
            else {
                layoutPassword.setErrorEnabled(false);
            }
        }
        return result;
    }

    private boolean isEmailValid(String value) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void updateConsumer() {
        consumer = getViewModel();
        if (validate(consumer)) {
            requestCreateConsumer(consumer);
        }
        else {
            hideProgress();
        }
    }

    private void requestCreateConsumer(Consumer consumer) {
        ConsumerCreateParamsBuilder paramsBuilder = new ConsumerCreateParamsBuilder()
                .addAppKey()
                .addConsumerParams(consumer);
        getApi().executeRequest(WebApiManager.Request.CONSUMER_CREATE, paramsBuilder.build());

    }

    // //////////
    // Event bus listeners
    //
    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        Logger.d("handleConsumer()");
        hideProgress();
        ConsumerData data = event.getEventData().getModelData().getConsumerData();
        SettingsProvider.getInstance().saveSubscriptionCount(data.getSubscriptionCount());
        SettingsProvider.getInstance().setString(SettingsProvider.CONSUMER_EMAIL, consumer.email);
        SettingsProvider.getInstance().setString(SettingsProvider.CONSUMER_PASSWORD, consumer.password);

        Bundle extras = new Bundle();
        extras.putBoolean(LoginActivity.PARAMETERS_FORCE_LOGIN, true);
        NavigationHelper.getInstance(ConsumerActivity.this).switchToLoginScreen(ConsumerActivity.this, extras);
    }

    @Subscribe
    public void handleError(ErrorEvent err) {
        Logger.e("handleError");
        hideProgress();
        if (err.getError() != null) {
            if (err.getError().getResponse().getStatus() == 422) {
                DialogHelper.showErrorAlert(this, getString(R.string.consumer_error_create));
            } else {
                UiUtils.showErrorSnackbar(buttonUpdate, err.getErrMessage());
            }
        }
        else {
            UiUtils.showErrorSnackbar(buttonUpdate, err.getErrMessage());
        }
    }
}
