package com.samsung.android.sdk.iap.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.HelperUtil;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;

/**
 * Created by sangbum7.kim on 2018-03-06.
 */

public class AccountActivity extends Activity {
    private static final String  TAG = AccountActivity.class.getSimpleName();

    IapHelper                mIapHelper   = null;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        mIapHelper = IapHelper.getInstance( this );
        // ====================================================================
        // 1. If IAP package is installed and valid, start SamsungAccount
        //    authentication activity to start purchase.
        // ====================================================================
        Log.i( TAG, "Samsung Account Login..." );
        HelperUtil.startAccountActivity( this );
        // ====================================================================
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult>> requestCode : " + _requestCode + ", resultCode : " + _resultCode);
        switch (_requestCode) {
            case HelperDefine.REQUEST_CODE_IS_ACCOUNT_CERTIFICATION:
                Log.i(TAG, "REQUEST_CODE_IS_ACCOUNT_CERTIFICATION Result : " + _resultCode);
                // 1) If SamsungAccount authentication is succeed
                // ------------------------------------------------------------
                if (RESULT_OK == _resultCode) {
                    // bind to IAPService
                    // --------------------------------------------------------
                    Runnable runProcess = new Runnable() {
                        @Override
                        public void run() {
                            mIapHelper.bindIapService();
                        }
                    };
                    if(runProcess!=null)
                        runProcess.run();
                    else
                        mIapHelper.dispose();
                    finish();
                    overridePendingTransition(0,0);
                    // --------------------------------------------------------
                }
                // ------------------------------------------------------------
                // 2) If SamsungAccount authentication is cancelled
                // ------------------------------------------------------------
                else {
//                    Runnable runProcess = new Runnable() {
//                        @Override
//                        public void run() {
//                            if(mIapHelper.getServiceProcess() != null)
//                                mIapHelper.getServiceProcess().onEndProcess();
//                            else
//                                mIapHelper.dispose();
//                        }
//                    };
//                    if(runProcess!=null)
//                        runProcess.run();
//                    else
                    mIapHelper.dispose();
                    finish();
                }
                break;
        }
    }
}
