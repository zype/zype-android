package com.samsung.android.sdk.iap.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.samsung.android.sdk.iap.lib.R;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.HelperUtil;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;

/**
 * Created by sangbum7.kim on 2018-03-07.
 */

public class CheckPackageActivity extends Activity
{
    private static final String TAG = CheckPackageActivity.class.getSimpleName();
    private static boolean mFinishFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        mFinishFlag = true;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int DialogType = extras.getInt("DialogType");
                switch (DialogType) {
                    case HelperDefine.DIALOG_TYPE_INVALID_PACKAGE:{
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
                        mFinishFlag = false;
                    }
                    break;
                    case HelperDefine.DIALOG_TYPE_DISABLE_APPLICATION:{
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
                                true,
                                runnable,
                                true);
                        mFinishFlag = false;
                    }
                    break;
                    case HelperDefine.DIALOG_TYPE_APPS_DETAIL:{
                        // 1. When user click the OK button on the dialog,
                        //    go to SamsungApps IAP Detail page
                        // ====================================================================
                        Runnable OkBtnRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // Link of SamsungApps for IAP install
                                // ------------------------------------------------------------
                                Uri appsDeepLink = Uri.parse(
                                        "samsungapps://StoreVersionInfo/");
                                // ------------------------------------------------------------

                                Intent intent = new Intent();
                                intent.setData( appsDeepLink );

                                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 )
                                {
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_INCLUDE_STOPPED_PACKAGES );
                                }
                                else
                                {
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                }

                                if(intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        };
                        // ====================================================================

                        // 2. Show information dialog
                        // ====================================================================
                        HelperUtil.showIapDialogIfNeeded( this,
                                getString( R.string.mids_sapps_header_update_galaxy_apps ),
                                getString( R.string.mids_sapps_pop_a_new_version_is_available_galaxy_apps_will_be_updated_to_the_latest_version_to_complete_this_purchase ),
                                true,
                                OkBtnRunnable,
                                true );
                        // ====================================================================
                        mFinishFlag = false;
                    }
                    break;
                }
            }
        }
        if(mFinishFlag)
            finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
        IapHelper.getInstance(getApplicationContext()).dispose();
    }
}
