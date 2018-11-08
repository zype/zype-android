package com.samsung.android.sdk.iap.lib.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.samsung.android.sdk.iap.lib.R;
import com.samsung.android.sdk.iap.lib.activity.AccountActivity;
import com.samsung.android.sdk.iap.lib.activity.DialogActivity;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

/**
 * Created by sangbum7.kim on 2018-02-28.
 */

public abstract class BaseService {
    private static final String  TAG = BaseService.class.getSimpleName();

    protected ErrorVo mErrorVo          = new ErrorVo();
    protected IapHelper mIapHelper      = null;
    protected Context   mContext        = null;
    public BaseService(IapHelper _iapHelper, Context _context)
    {
        mIapHelper = _iapHelper;
        mContext = _context;
        mErrorVo.setError(HelperDefine.IAP_ERROR_INITIALIZATION, mContext.getString(R.string.mids_sapps_pop_unknown_error_occurred));
    }

    public ErrorVo getErrorVo() {
        return mErrorVo;
    }

    public void setErrorVo(ErrorVo mErrorVo) {
        this.mErrorVo = mErrorVo;
    }

    public abstract void runServiceProcess();

    public void onEndProcess( ) {
        Log.v(TAG, "BaseService.onEndProcess");

        if( mErrorVo.getErrorCode() == HelperDefine.IAP_ERROR_NEED_APP_UPGRADE )
        {
            Intent intent = new Intent( mContext, DialogActivity.class );
            intent.putExtra( "Title", mContext.getString( R.string.mids_sapps_header_samsung_in_app_purchase_abb ) );
            intent.putExtra( "Message", mErrorVo.getErrorString() );
            intent.putExtra( "ExtraString", mErrorVo.getExtraString() );
            intent.putExtra( "DialogType", HelperDefine.DIALOG_TYPE_UPGRADE );
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            mContext.startActivity(intent);
        }
        else if( mErrorVo.getErrorCode() == HelperDefine.IAP_ERROR_NEED_SA_LOGIN )
        {
                Intent intent = new Intent(mContext, AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return;
        }
        else if( mErrorVo.getErrorCode() != HelperDefine.IAP_ERROR_NONE )
        {
            if(mErrorVo.isShowDialog()) {
                Intent intent = new Intent(mContext, DialogActivity.class);
                intent.putExtra("Title", mContext.getString(R.string.mids_sapps_header_samsung_in_app_purchase_abb));
                intent.putExtra("Message", mErrorVo.getErrorString());
                intent.putExtra("DialogType", HelperDefine.DIALOG_TYPE_NOTIFICATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }

        if(mIapHelper != null) {
            BaseService baseService = mIapHelper.getServiceProcess(true);
            if (baseService != null)
                baseService.runServiceProcess();
            else
                mIapHelper.dispose();
        }
        onReleaseProcess();
    }

    abstract void onReleaseProcess();
}
