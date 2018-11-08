package com.samsung.android.sdk.iap.lib.service;

import android.content.Context;
import android.util.Log;

import com.samsung.android.sdk.iap.lib.R;
import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnGetOwnedListListener;
import com.samsung.android.sdk.iap.lib.vo.OwnedProductVo;

import java.util.ArrayList;

/**
 * Created by sangbum7.kim on 2018-02-28.
 */

public class OwnedProduct extends BaseService{
    private static final String TAG  = OwnedProduct.class.getSimpleName();

    private static OwnedProduct mInstance = null;
    private static OnGetOwnedListListener mOnGetOwnedListListener = null;
    private static String mProductType = "";
    protected ArrayList<OwnedProductVo> mOwnedList          = null;

    public OwnedProduct(IapHelper _iapHelper, Context _context, OnGetOwnedListListener _onGetOwnedListListener)
    {
        super(_iapHelper, _context);
        mOnGetOwnedListListener = _onGetOwnedListListener;
    }

    public static void setProductType(String _productType) {
        mProductType = _productType;
    }

    public void setOwnedList(ArrayList<OwnedProductVo> _ownedList) {
        this.mOwnedList = _ownedList;
    }

    @Override
    public void runServiceProcess(){
        Log.v(TAG, "runServiceProcess");
        if ( mIapHelper != null )
        {
            if(mIapHelper.safeGetOwnedList( OwnedProduct.this,
                    mProductType,
                    true ) == true) {
                return;
            }
        }
        mErrorVo.setError(HelperDefine.IAP_ERROR_INITIALIZATION,mContext.getString(R.string.mids_sapps_pop_unknown_error_occurred));
        onEndProcess();
    }

    @Override
    public void onReleaseProcess(){
        Log.v(TAG, "OwnedProduct.onEndProcess");
        try {
            if (mOnGetOwnedListListener != null)
                mOnGetOwnedListListener.onGetOwnedProducts(mErrorVo, mOwnedList);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}
