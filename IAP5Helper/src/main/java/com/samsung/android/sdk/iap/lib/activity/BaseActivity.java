package com.samsung.android.sdk.iap.lib.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.samsung.android.sdk.iap.lib.R;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.HelperUtil;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;


public abstract class BaseActivity extends Activity
{
    private static final String  TAG = BaseActivity.class.getSimpleName();

    protected ErrorVo                   mErrorVo            = new ErrorVo();
    private   Dialog                    mProgressDialog     = null;

    /**
     * Helper Class between IAPService and 3rd Party Application
     */
    IapHelper                mIapHelper   = null;

    /** Flag value to show successful pop-up. Error pop-up appears whenever it fails or not. */
    protected boolean mShowSuccessDialog  = true;
    protected boolean mShowErrorDialog    = true;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        // 1. Store IapMode passed by Intent
        // ====================================================================
        Intent intent = getIntent();

        // ====================================================================

        // 2. IapHelper Instance creation
        //    To test on development, set mode to test mode using
        //    use IapHelper.IAP_MODE_TEST_SUCCESS or
        //    IapHelper.IAP_MODE_TEST_FAIL constants.
        // ====================================================================
        mIapHelper = IapHelper.getInstance( this );
        // ====================================================================

        // 3. This activity is invisible excepting progress bar as default.
        // ====================================================================
        try
        {
            Toast.makeText( this,
                    R.string.dream_sapps_body_authenticating_ing,
                    Toast.LENGTH_LONG ).show();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        // ====================================================================

        super.onCreate( savedInstanceState );
    }

    public void setErrorVo( ErrorVo _errorVo )
    {
        mErrorVo = _errorVo;
    }

    public boolean checkAppsPackage()
    {
        // 1. If Billing Package is installed in your device
        // ====================================================================
        if(HelperUtil.isInstalledAppsPackage(this)) {
            // 1) If Billing package installed in your device is valid
            // ================================================================
            if (!HelperUtil.isEnabledAppsPackage(this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + HelperDefine.GALAXY_PACKAGE_NAME));
                        startActivityForResult(intent, HelperDefine.REQUEST_CODE_IS_ENABLE_APPS);
                    }
                };
                //// TODO: 2017-08-16 need to set the error string
                HelperUtil.showIapDialogIfNeeded(this,
                        getString(R.string.mids_sapps_header_samsung_in_app_purchase_abb),
                        getString(R.string.mids_sapps_pop_unable_to_open_samsung_in_app_purchase_msg),
                        false,
                        runnable,
                        true);

            } else if (HelperUtil.isValidAppsPackage(this)) {
                return true;

                // ================================================================
                // 2) If IAP package installed in your device is not valid
                // ================================================================
            } else {
                // Set error to notify result to third-party application
                // ------------------------------------------------------------
                //// TODO: 2017-08-16 need to set the error string
                mErrorVo.setError(HelperDefine.IAP_ERROR_COMMON,
                        getString(R.string.mids_sapps_pop_an_invalid_installation_of_in_app_purchase_has_been_detected_check_and_try_again));
                // ------------------------------------------------------------
                // show alert dialog if IAP Package is invalid
                // ------------------------------------------------------------
                HelperUtil.showIapDialogIfNeeded(
                        this,
                        getString(R.string.mids_sapps_header_samsung_in_app_purchase_abb),
                        getString(R.string.mids_sapps_pop_an_invalid_installation_of_in_app_purchase_has_been_detected_check_and_try_again),
                        true,
                        null,
                        true);
                // ------------------------------------------------------------
            }
            // ================================================================

            // ====================================================================
            // 2. If IAP Package is not installed in your device
            // ====================================================================
        } else {
            HelperUtil.installAppsPackage( this );
        }
        // ====================================================================
        return false;
    }

    /**
     * dispose IapHelper {@link PaymentActivity}
     * To do that, preDestory must be invoked at first in onDestory of each child activity
     */
    protected void preDestory()
    {
        // 1. Invoke dispose Method to unbind service and release inprogress flag
        // ====================================================================
        if( null != mIapHelper )
        {
            mIapHelper.dispose();
            mIapHelper = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        // 1. dismiss ProgressDialog
        // ====================================================================
        try
        {
            if( mProgressDialog != null )
            {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        // ====================================================================

        super.onDestroy();
    }
}