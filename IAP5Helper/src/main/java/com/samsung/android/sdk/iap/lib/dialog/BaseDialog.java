package com.samsung.android.sdk.iap.lib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.samsung.android.sdk.iap.lib.R;


public class BaseDialog extends AlertDialog implements 
                              DialogInterface.OnCancelListener,
                              DialogInterface.OnClickListener
                              
{
    // ------------------------------------------------------------------------
    // 구성
    //
    // ----------------------------------
    //  아이콘  |  타이틀
    // ----------------------------------
    //
    //
    //   1)  Message 영역
    //
    //
    // ----------------------------------
    //
    //   2)  Custom Content 영역
    //
    // ----------------------------------
    // 5) 버튼 영역
    // ----------------------------------
    //
    // ------------------------------------------------------------------------

    // 1)  Message 영역
    // ========================================================================
    private TextView        mTvCustomMessage        = null;
    // ========================================================================

    // 2)  Custom Content 영역
    // ========================================================================
    private FrameLayout     mFlCustomContent        = null;
    // ========================================================================

    private ViewGroup       mContentView            = null;
    
    private OnDialogClickListener  mOnClickListener = null;
    
    public interface OnDialogClickListener 
    {
        void onClick(int which);
    }
    
    public static BaseDialog newInstance( Context _context )
    {
        BaseDialog dialog = new BaseDialog( _context,
                   android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth );
        
        return dialog; 
    }
    
    
    protected BaseDialog(Context _context)
    {
        super(_context);
    }

    public BaseDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        int colorResId = getContext()
                             .getResources()
                                 .getColor( R.color.text_accent );

        getButton( AlertDialog.BUTTON_POSITIVE ).setTextColor( colorResId );
        getButton( AlertDialog.BUTTON_NEGATIVE ).setTextColor( colorResId );

        // 1. 다이얼로그의 취소 리스너를 설정한다.
        // ====================================================================
        setOnCancelListener(this);
        // ====================================================================
    }

    private ViewGroup initContentView()
    {
        if ( mContentView == null )
        {
            mContentView = (ViewGroup)getLayoutInflater().inflate(
                                                  R.layout.base_dialog, null );

            // 1. 사용자 정의 레이아웃을 영역을 설정한다.
            // ================================================================
            setUserContentLayout( mContentView );
            // ================================================================

            // 2. 전체 Layout 을 로딩한다.
            // ================================================================
            setView( mContentView );
            // ================================================================
        }

        return mContentView;
    }

    private void setUserContentLayout( View v )
    {
        // 1. Dialog 사용자 뷰를 참조한다.
        // ====================================================================
        mFlCustomContent = (FrameLayout)v.findViewById( R.id.base_dialog_custom_view );
        // ====================================================================

        // 1. Dialog 사용자 메시지 영역 텍스트뷰를 참조한다.
        // ====================================================================
        mTvCustomMessage = (TextView)v.findViewById( R.id.base_dialog_custom_message );
        // ====================================================================
    }



    public BaseDialog setDialogOnClickListener( OnDialogClickListener _listener )
    {
        mOnClickListener = _listener;

        return this;
    }


    public BaseDialog setDialogTitle( String _title )
    {
        if( _title != null )
        {
            setTitle( _title );
        }

        return this;
    }
    public BaseDialog setDialogTitle( int _titleId )
    {
        setTitle( (String)getContext().getText( _titleId ) );

        return this;
    }

    public BaseDialog setDialogMessageText( String _contentText )
    {
        if( _contentText != null )
        {
            setMessage( _contentText );
        }

        return this;
    }
    public BaseDialog setDialogMessageText( int _messageTextId )
    {
        setMessage( getContext().getString( _messageTextId ) );

        return this;
    }

    public BaseDialog setDialogContentView( View _view )
    {
        if( _view != null )
        {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                         ViewGroup.LayoutParams.MATCH_PARENT,
                                         ViewGroup.LayoutParams.WRAP_CONTENT );

            initContentView().addView(_view, params);
            /*initContentView();
            mFlCustomContent.addView(_view, params);
            mFlCustomContent.setVisibility(View.VISIBLE);*/
        }

        return this;
    }

    public BaseDialog setDialogContentView( View _view,
                                            ViewGroup.LayoutParams _lp )
    {
        if( _view != null )
        {
            initContentView();
            mFlCustomContent.addView(_view, _lp);
            mFlCustomContent.setVisibility(View.VISIBLE);
        }

        return this;
    }


    public BaseDialog setDialogPositiveButton( String _positiveBtnText )
    {
        if( _positiveBtnText != null )
        {
            setButton(DialogInterface.BUTTON_POSITIVE,
                    _positiveBtnText,
                    this);
        }
        return this;
    }
    public BaseDialog setDialogPositiveButton( int _positiveBtnTextId )
    {
        setDialogPositiveButton(
                          (String)getContext().getText( _positiveBtnTextId ) );

        return this;
    }
    public BaseDialog setDialogPositiveButton( )
    {
        setDialogPositiveButton("OK"
                             /*(String)getContext().getText( R.string.MIDS_OTS_BUTTON_OK ) */);

        return this;
    }
    public BaseDialog setDialogNegativeButton( String _netativeBtnText )
    {
        if( _netativeBtnText != null )
        {
            setButton( DialogInterface.BUTTON_NEGATIVE, 
                       _netativeBtnText, 
                       this );
        }
        return this;
    }
    public BaseDialog setDialogNegativeButton( int _netativeBtnTextId )
    {
        setDialogNegativeButton( 
                             (String)getContext().getText( _netativeBtnTextId ) );
        
        return this;
    }
    public BaseDialog setDialogNegativeButton( )
    {
        setDialogNegativeButton( "CANCEL"
                         /*(String)getContext().getText( R.string.MIDS_OTS_BUTTON_CANCEL_ABB2 ) */);
        
        return this;
    }


    @Override
    public void onClick( DialogInterface _dialog, int _which )
    {
        switch( _which )
        {
            case DialogInterface.BUTTON_NEGATIVE:
                
                cancel();
               
                break;
                
            case DialogInterface.BUTTON_POSITIVE:

                if( mOnClickListener != null )
                {
                    dismiss();
                    mOnClickListener.onClick( BUTTON_POSITIVE );
                }

                break;
        }
        
    }
    public BaseDialog setDialogCancelable( boolean flag )
    {
        super.setCancelable( flag );

        return this;
    }

    @Override
    public void onCancel( DialogInterface dialog )
    {
        if( mOnClickListener != null )
        {
            mOnClickListener.onClick( BUTTON_NEGATIVE );
        }
    }

    public void setDialogPositiveButtonEnabled(boolean _state)
    {
        getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled(_state);
    }

    public void setDialoNegativeButtonEnabled(boolean _state)
    {
        getButton( AlertDialog.BUTTON_NEGATIVE ).setEnabled(_state);
    }

    // 콜백을 전달하지 않고, 조용히 다이얼로그만 종료해준다.
    public void noAlertdismiss()
    {
        dismiss();
    }
}