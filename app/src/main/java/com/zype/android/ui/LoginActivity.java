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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.BuildConfig;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.utils.AdMacrosHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.AuthParamsBuilder;
import com.zype.android.webapi.builder.ConsumerParamsBuilder;
import com.zype.android.webapi.builder.DevicePinParamsBuilder;
import com.zype.android.webapi.events.ErrorEvent;
import com.zype.android.webapi.events.auth.AccessTokenInfoEvent;
import com.zype.android.webapi.events.auth.RefreshAccessTokenEvent;
import com.zype.android.webapi.events.auth.RetrieveAccessTokenEvent;
import com.zype.android.webapi.events.consumer.ConsumerEvent;
import com.zype.android.webapi.events.linking.DevicePinEvent;
import com.zype.android.webapi.model.auth.RefreshAccessToken;
import com.zype.android.webapi.model.auth.RetrieveAccessToken;
import com.zype.android.webapi.model.auth.TokenInfo;
import com.zype.android.webapi.model.consumers.Consumer;
import com.zype.android.webapi.model.linking.DevicePinData;

public class LoginActivity extends BaseActivity {

    private LinearLayout layoutAuthMethod;
    private Button buttonLinkDevice;
    private Button buttonEmail;

    private LinearLayout layoutLinkDevice;
    private TextView textDeviceLinkingUrl;
    private TextView textPin;
    private Button buttonDeviceLinked;

    private LinearLayout layoutEmail;

    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;

    private String deviceId;
    private String pin;

    private int mode;
    private static final int MODE_SELECT_METHOD = 0;
    private static final int MODE_DEVICE_LINKING = 1;
    private static final int MODE_SIGN_IN_WITH_EMAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        layoutAuthMethod = (LinearLayout) findViewById(R.id.layoutAuthMethod);
        buttonLinkDevice = (Button) findViewById(R.id.buttonLinkDevice);
        buttonLinkDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_DEVICE_LINKING;
                getDevicePin();
                updateViews();
            }
        });
        buttonEmail = (Button) findViewById(R.id.buttonEmail);
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_SIGN_IN_WITH_EMAIL;
                updateViews();
            }
        });

        layoutLinkDevice = (LinearLayout) findViewById(R.id.layoutLinkDevice);
        textDeviceLinkingUrl = (TextView) findViewById(R.id.textDeviceLinkingUrl);
        textPin = (TextView) findViewById(R.id.textPin);
        buttonDeviceLinked = (Button) findViewById(R.id.buttonDeviceLinked);
        buttonDeviceLinked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessTokenWithPin();
            }
        });

        layoutEmail = (LinearLayout) findViewById(R.id.layoutEmail);
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

        init(savedInstanceState);
        bindViews();
    }

    private void init(Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState != null) {
            args = savedInstanceState;
        }
        else {
            args = getIntent().getExtras();
        }
        if (args != null) {
        }
        if (ZypeConfiguration.isDeviceLinkingEnabled(this)) {
            mode = MODE_SELECT_METHOD;
        }
        else {
            mode = MODE_SIGN_IN_WITH_EMAIL;
        }
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

    // //////////
    // UI
    //
    private void bindViews() {
        textDeviceLinkingUrl.setText(ZypeConfiguration.getDeviceLinkingUrl(this));
        textPin.setText(pin);

        updateViews();
    }

    private void updateViews() {
        switch (mode) {
            case MODE_SELECT_METHOD:
                layoutAuthMethod.setVisibility(View.VISIBLE);
                layoutLinkDevice.setVisibility(View.GONE);
                layoutEmail.setVisibility(View.GONE);
                break;
            case MODE_DEVICE_LINKING:
                layoutAuthMethod.setVisibility(View.GONE);
                layoutLinkDevice.setVisibility(View.VISIBLE);
                layoutEmail.setVisibility(View.GONE);
                break;
            case MODE_SIGN_IN_WITH_EMAIL:
                layoutAuthMethod.setVisibility(View.GONE);
                layoutLinkDevice.setVisibility(View.GONE);
                layoutEmail.setVisibility(View.VISIBLE);
                break;
        }
        if (TextUtils.isEmpty(pin)) {
            buttonDeviceLinked.setEnabled(false);
        }
        else {
            buttonDeviceLinked.setEnabled(true);
        }
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

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    // //////////
    // Actions
    //
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

    public void getDevicePin() {
        AdMacrosHelper.IDeviceIdListener listener = new AdMacrosHelper.IDeviceIdListener() {
            @Override
            public void onDeviceId(String id) {
                deviceId = id;
                // Create device pin
                DevicePinParamsBuilder builder = new DevicePinParamsBuilder();
                builder.addDeviceId(deviceId);
                getApi().executeRequest(WebApiManager.Request.DEVICE_PIN_CREATE, builder.build());
            }
        };
        AdMacrosHelper.fetchDeviceId(this, listener);
    }

    public void getAccessTokenWithPin() {
        showProgress(true);

        AuthParamsBuilder builder = new AuthParamsBuilder();
        builder.addLinkedDeviceId(deviceId);
        builder.addPin(pin);
        builder.addClientId();
        builder.addClientSecret();
        builder.addGrandType("password");
        getApi().executeRequest(WebApiManager.Request.AUTH_RETRIEVE_ACCESS_TOKEN, builder.build());
    }

    // //////////
    // Subscriptions
    //
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

    @Subscribe
    public void handleError(ErrorEvent event) {
        SettingsProvider.getInstance().logout();
        showProgress(false);
        UiUtils.showErrorSnackbar(findViewById(R.id.root_view), event.getErrMessage());
    }

    @Subscribe
    public void handleDevicePin(DevicePinEvent event) {
        DevicePinData data = event.getEventData().getModelData().data;
        pin = data.pin;
        if (data.linked) {
            getAccessTokenWithPin();
        }
        else {
            bindViews();
        }
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


}
