package com.samsung.android.sdk.iap.lib.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.samsung.android.sdk.iap.lib.R;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.HelperUtil;

/**
 * Created by sangbum7.kim on 2018-03-05.
 */

public class DialogActivity extends Activity {
    private static final String TAG = DialogActivity.class.getSimpleName();
    private String mExtraString = "";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int DialogType = extras.getInt("DialogType");
                switch (DialogType) {
                    case HelperDefine.DIALOG_TYPE_NOTIFICATION: {
                        String title = extras.getString("Title");
                        String message = extras.getString("Message");
                        HelperUtil.showIapDialogIfNeeded(this,
                                title,
                                message,
                                true,
                                null,
                                true);
                    }
                    break;
                    case HelperDefine.DIALOG_TYPE_UPGRADE: {
                        String message = extras.getString("Message");
                        mExtraString = extras.getString("ExtraString");
                        if(mExtraString == null)
                            mExtraString = "";

                        // a) When user click the OK button on the dialog,
                        //    go to SamsungApps IAP Detail page.
                        // --------------------------------------------------------
                        Runnable OkBtnRunnable = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if( true == TextUtils.isEmpty(
                                        mExtraString ) )
                                {
                                    return;
                                }

                                Intent intent = new Intent(Intent.ACTION_VIEW);

                                intent.setData(
                                        Uri.parse( mExtraString ) );

                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

                                try
                                {
                                    startActivity( intent );
                                }
                                catch( ActivityNotFoundException e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        };
                        // --------------------------------------------------------

                        // b) Pop-up shows that the IAP package needs to be updated.
                        // --------------------------------------------------------
                        HelperUtil.showIapDialogIfNeeded( this,
                                getString( R.string.mids_sapps_header_samsung_in_app_purchase_abb ),
                                message,
                                true,
                                OkBtnRunnable,
                                true );
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }
}
