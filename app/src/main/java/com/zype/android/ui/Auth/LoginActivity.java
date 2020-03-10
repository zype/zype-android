package com.zype.android.ui.Auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zype.android.Auth.AuthHelper;
import com.zype.android.Auth.AuthLiveData;
import com.zype.android.DataRepository;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.events.AuthorizationErrorEvent;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.utils.AdMacrosHelper;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.DialogHelper;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.AuthParamsBuilder;
import com.zype.android.webapi.builder.ConsumerForgotPasswordParamsBuilder;
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
    private TextView textForgotPassword;
    private TextView textSignUp;

    private LinearLayout layoutReset;
    private LinearLayout layoutResetCompleted;

    public final static String PARAMETERS_FORCE_LOGIN = "ForceLogin";

    private String deviceId;
    private String pin;

    private int mode;
    private static final int MODE_SELECT_METHOD = 0;
    private static final int MODE_DEVICE_LINKING = 1;
    private static final int MODE_SIGN_IN_WITH_EMAIL = 2;
    private static final int MODE_RESET_PASSWORD = 3;
    private static final int MODE_RESET_PASSWORD_COMPLETED = 4;

    private boolean forceLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.login_title);

        layoutAuthMethod = findViewById(R.id.layoutAuthMethod);
        buttonLinkDevice = findViewById(R.id.buttonLinkDevice);
        buttonLinkDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_DEVICE_LINKING;
                getDevicePin();
                updateViews();
            }
        });
        buttonEmail = findViewById(R.id.buttonEmail);
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_SIGN_IN_WITH_EMAIL;
                updateViews();
            }
        });

        layoutLinkDevice = findViewById(R.id.layoutLinkDevice);
        textDeviceLinkingUrl = findViewById(R.id.textDeviceLinkingUrl);
        textPin = findViewById(R.id.textPin);
        buttonDeviceLinked = findViewById(R.id.buttonDeviceLinked);
        buttonDeviceLinked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessTokenWithPin();
            }
        });

        layoutEmail = findViewById(R.id.layoutEmail);
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
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

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        textForgotPassword = findViewById(R.id.textForgotPassword);
        textSignUp = findViewById(R.id.textSignUp);

        layoutReset = findViewById(R.id.layoutReset);
        mLoginFormView = findViewById(R.id.login_form);
        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        layoutResetCompleted = findViewById(R.id.layoutResetCompleted);
        Button buttonResetCompleted = findViewById(R.id.buttonResetCompleted);
        buttonResetCompleted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        init(savedInstanceState);
        bindViews();

        if (forceLogin) {
            emailWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_EMAIL));
            passwordWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_PASSWORD));
            attemptLogin();
        }
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
            forceLogin = args.getBoolean(PARAMETERS_FORCE_LOGIN);
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

        // Set Forgot password link
        SpannableString spannableForgotPassword = new SpannableString(textForgotPassword.getText());
        ClickableSpan spanForgotPassword = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                switchToResetPasswordScreen();
            }
        };
        spannableForgotPassword.setSpan(spanForgotPassword, 0, textForgotPassword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textForgotPassword.setText(spannableForgotPassword);
        textForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        // Set Sign up link
        String signUp = getString(R.string.login_sign_up);
        String signUpLink = getString(R.string.login_sign_up_link);
        SpannableString spannableSignIn = new SpannableString(signUp);
        ClickableSpan spanSignUp = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                NavigationHelper.getInstance(LoginActivity.this).switchToConsumerScreen(LoginActivity.this);
                finish();
            }
        };
        int indexSignUp = signUp.indexOf(signUpLink);
        spannableSignIn.setSpan(spanSignUp, indexSignUp, indexSignUp + signUpLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSignUp.setText(spannableSignIn);
        textSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        updateViews();
    }

    private void updateViews() {
        layoutAuthMethod.setVisibility(View.GONE);
        layoutLinkDevice.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.GONE);
        layoutReset.setVisibility(View.GONE);
        layoutResetCompleted.setVisibility(View.GONE);
        switch (mode) {
            case MODE_SELECT_METHOD:
                layoutAuthMethod.setVisibility(View.VISIBLE);
                break;
            case MODE_DEVICE_LINKING:
                layoutLinkDevice.setVisibility(View.VISIBLE);
                break;
            case MODE_SIGN_IN_WITH_EMAIL:
                layoutEmail.setVisibility(View.VISIBLE);
                break;
            case MODE_RESET_PASSWORD:
                layoutReset.setVisibility(View.VISIBLE);
                break;
            case MODE_RESET_PASSWORD_COMPLETED:
                layoutResetCompleted.setVisibility(View.VISIBLE);
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

    private void switchToResetPasswordScreen() {
        mode = MODE_RESET_PASSWORD;
        updateViews();
    }

    private void switchToResetPasswordCompletedScreen() {
        mode = MODE_RESET_PASSWORD_COMPLETED;
        updateViews();
    }

    // //////////
    // Actions
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case BundleConstants.REQUEST_CONSUMER:
                if (resultCode == RESULT_OK) {
                    emailWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_EMAIL));
                    passwordWrapper.getEditText().setText(SettingsProvider.getInstance().getString(SettingsProvider.CONSUMER_PASSWORD));
                    attemptLogin();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void attemptLogin() {
        hideKeyboard();
        String email = emailWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailWrapper.setError(getString(R.string.error_field_required));
        }
        else if (!isEmailValid(email)) {
            emailWrapper.setError(getString(R.string.error_invalid_email));
        }
        else if (TextUtils.isEmpty(password)) {
            passwordWrapper.setError(getString(R.string.error_field_required));
        }
        else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordWrapper.setError(getString(R.string.error_invalid_password));
        }
        else {
            emailWrapper.setErrorEnabled(false);
            passwordWrapper.setErrorEnabled(false);
            showProgress(true);

            AuthParamsBuilder builder = new AuthParamsBuilder();
            builder.addUsername(email);
            builder.addPassword(password);
            builder.addClientId();
//            builder.addClientSecret();
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
//        builder.addClientSecret();
        builder.addGrandType("password");
        getApi().executeRequest(WebApiManager.Request.AUTH_RETRIEVE_ACCESS_TOKEN, builder.build());
    }

    private void resetPassword() {
        hideKeyboard();
        TextInputLayout layoutEmailReset = findViewById(R.id.layoutEmailReset);
        String email = layoutEmailReset.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            layoutEmailReset.setError(getString(R.string.error_field_required));
            return;
        }
        else if (!isEmailValid(email)) {
            layoutEmailReset.setError(getString(R.string.error_invalid_email));
            return;
        }

        showProgress(true);

        ConsumerForgotPasswordParamsBuilder builder = new ConsumerForgotPasswordParamsBuilder();
        builder.addEmail(email);
        getApi().executeRequest(WebApiManager.Request.CONSUMER_FORGOT_PASSWORD, builder.build());
    }

    // //////////
    // Subscriptions
    //
    @Subscribe
    public void handleRetrieveAccessToken(RetrieveAccessTokenEvent event) {
        Logger.d("handleRetrieveAccessToken");

        RetrieveAccessToken.RetrieveAccessTokenData data = event.getEventData().getModelData();
        SettingsProvider.getInstance().saveAccessToken(data.getAccessToken());
        SettingsProvider.getInstance().saveExpiresIn(data.getExpiresIn());
        SettingsProvider.getInstance().saveRefreshToken(data.getRefreshToken());
        SettingsProvider.getInstance().saveScope(data.getScope());
        SettingsProvider.getInstance().saveTokenType(data.getTokenType());

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
        showProgress(false);
        if (event.getRequest() == WebApiManager.Request.CONSUMER_FORGOT_PASSWORD) {
            switchToResetPasswordCompletedScreen();
            return;
        }
        Consumer data = event.getEventData().getModelData();
        int subscriptionCount = data.getConsumerData().getSubscriptionCount();
        SettingsProvider.getInstance().saveSubscriptionCount(subscriptionCount);
        String consumerId = data.getConsumerData().getId();
        SettingsProvider.getInstance().saveConsumerId(consumerId);
        SettingsProvider.getInstance().setString(SettingsProvider.CONSUMER_EMAIL, data.getConsumerData().getEmail());

        AuthHelper.onLoginStateChanged();

        DataRepository.getInstance(this.getApplication()).loadVideoFavorites(success -> {
            DataRepository.getInstance(this.getApplication()).loadVideoEntitlements(success1 -> {
                setResult(RESULT_OK);
                finish();
            });
        });
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
        showProgress(false);
        if (event.getEventData() == WebApiManager.Request.CONSUMER_FORGOT_PASSWORD) {
            DialogHelper.showErrorAlert(this, event.getErrMessage());
            return;
        }
        SettingsProvider.getInstance().logout();
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
