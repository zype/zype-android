package com.zype.android.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.BuildConfig;
import com.zype.android.R;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.AuthParamsBuilder;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.auth.AccessTokenInfoEvent;
import com.zype.android.webapi.events.auth.RefreshAccessTokenEvent;
import com.zype.android.webapi.events.auth.RetrieveAccessTokenEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.model.auth.RefreshAccessToken;
import com.zype.android.webapi.model.auth.RetrieveAccessToken;
import com.zype.android.webapi.model.auth.TokenInfo;
import com.zype.android.webapi.model.consumers.Consumer;

public class LoginActivity extends BaseActivity {

    //    public static final String EXTRA_AUTH_ERROR = "extra_auth_error";
//    public static final String EXTRA_LOGOUT = "logout";
    public final static String PARAMETERS_FORCE_LOGIN = "ForceLogin";

    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;

    private boolean forceLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initParameters(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        passwordWrapper.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        emailWrapper.setHint(getString(R.string.prompt_email));
        passwordWrapper.setHint(getString(R.string.prompt_password));

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (forceLogin) {
            emailWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_EMAIL));
            passwordWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_PASSWORD));
            attemptLogin();
        }
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
            forceLogin = args.getBoolean(PARAMETERS_FORCE_LOGIN);
        }
    }

    public void attemptLogin() {
        hideKeyboard();
        String email = emailWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();
        if (BuildConfig.DEBUG) {
            if (email.length() == 0 && password.length() == 0) {
                email = "brian@zypemedia.com";
                password = "Password1";
            }
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordWrapper.setError(getString(R.string.error_invalid_password));
        }

        if (TextUtils.isEmpty(email)) {
            emailWrapper.setError(getString(R.string.error_field_required));
        } else if (!isEmailValid(email)) {
            emailWrapper.setError(getString(R.string.error_invalid_email));
        } else {
            emailWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);
            showProgress(true);

            AuthParamsBuilder builder = new AuthParamsBuilder();
            builder.addUsername(email);
            builder.addPassword(password);
            builder.addClientId();
            builder.addClientSecret();
            builder.addGrandType("password");
            getApi().executeRequest(WebApiManager.Request.AUTH_RETRIEVE_ACCESS_TOKEN, builder.build());
        }

    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Subscribe
    public void handleRetrieveAccessToken(RetrieveAccessTokenEvent event) {

        RetrieveAccessToken.RetrieveAccessTokenData data = event.getEventData().getModelData();
        SettingsProvider.getInstance().saveAccessToken(data.getAccessToken());
        SettingsProvider.getInstance().saveExpiresIn(data.getExpiresIn());
        SettingsProvider.getInstance().saveRefreshToken(data.getRefreshToken());
        SettingsProvider.getInstance().saveScope(data.getScope());
        SettingsProvider.getInstance().saveTokenType(data.getTokenType());
        Logger.d("handleRetrieveAccessToken");

        AuthParamsBuilder authParamsBuilder = new AuthParamsBuilder();
        authParamsBuilder.addToken(data.getAccessToken());
        getApi().executeRequest(WebApiManager.Request.TOKEN_INFO, authParamsBuilder.build());

    }

    @Subscribe
    public void handleRefreshAccessTokenEvent(RefreshAccessTokenEvent event) {

        RefreshAccessToken.RefreshAccessTokenData data = event.getEventData().getModelData();
        SettingsProvider.getInstance().saveAccessToken(data.getAccessToken());
        SettingsProvider.getInstance().saveExpiresIn(data.getExpiresIn());
        SettingsProvider.getInstance().saveRefreshToken(data.getRefreshToken());
        SettingsProvider.getInstance().saveScope(data.getScope());
        SettingsProvider.getInstance().saveTokenType(data.getTokenType());
        Logger.d("handleRetrieveAccessToken");

        attemptLogin();
    }

    @Subscribe
    public void handleAccessTokenInfo(AccessTokenInfoEvent event) {
        Logger.d("handleAccessTokenInfo");
        showProgress(false);
        TokenInfo data = event.getEventData().getModelData();
        SettingsProvider.getInstance().saveAccessTokenApplication(data.getApplicationData());
        SettingsProvider.getInstance().saveAccessTokenCreatedAt(data.getCreatedAt());
        SettingsProvider.getInstance().saveAccessTokenExpiration(data.getExpiresInSeconds());
        SettingsProvider.getInstance().saveAccessTokenResourceOwnerId(data.getResourceOwnerId());
        SettingsProvider.getInstance().saveAccessTokenScopes(data.getScopes());

        ConsumerParamsBuilder builder = new ConsumerParamsBuilder()
                .addAccessToken();
        getApi().executeRequest(WebApiManager.Request.CONSUMER_GET, builder.build());
    }

    @Subscribe
    public void handleConsumer(ConsumerEvent event) {
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
        String consumerId = data.getConsumerData().getId();
        SettingsProvider.getInstance().saveConsumerId(consumerId);
        SettingsProvider.getInstance().setString(SettingsProvider.CONSUMER_EMAIL, data.getConsumerData().getEmail());
        showProgress(false);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void handleAuthorizationError(AuthorizationErrorEvent event) {
        SettingsProvider.getInstance().logout();
        getApi().cancelPendingRequests(true);
        showProgress(false);
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), event.getErrMessage());
        showDialog();
    }

    @Override
    protected String getActivityName() {
        return getString(R.string.activity_name_login);
    }

    void showDialog() {
        DialogFragment newFragment = CustomAlertDialog.newInstance(
                R.string.alert_dialog_title_auth_failed, R.string.alert_dialog_message_auth_failed);
//        newFragment.show(getSupportFragmentManager(), "dialog");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(newFragment, "Error credentials dialog");
        transaction.commitAllowingStateLoss();
    }

    @Subscribe
    public void handleError(ErrorEvent event) {
        SettingsProvider.getInstance().logout();
        showProgress(false);
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), event.getErrMessage());
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
