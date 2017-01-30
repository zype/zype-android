package com.zype.android.ui.video_details.fragments.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.zype.android.R;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.webapi.WebApiManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vasya
 * @version 1
 *          date 9/24/15
 */
@Deprecated
public class WebViewVideoFragment extends BaseFragment implements MediaControlInterface {
    private static final String ARG_PARAM_URL = "url";

    private String mParamUrl;

    //    private OnWebViewListener mListener;
    private WebView mWebView;

    public WebViewVideoFragment() {
        // Required empty public constructor
    }

    public static WebViewVideoFragment newInstance(String url) {
        WebViewVideoFragment fragment = new WebViewVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamUrl = getArguments().getString(ARG_PARAM_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_web_view_video, container, false);
        mWebView = (WebView) v.findViewById(R.id.webview_video);
        return v;
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.activity_name_live);
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            mListener = (OnWebViewListener) getActivity();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement OnWebViewListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public void seekToMillis(int ms) {
        //IGNORE
    }

    @Override
    public int getCurrentTimeStamp() {
        return -1;
    }

    @Override
    public void play() {
        // IGNORE
    }

    @Override
    public void stop() {
        mWebView.stopLoading();
    }

//    @Override
//    public void showMediaControl() {
//        //IGNORE
//    }


    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(WebApiManager.CUSTOM_HEADER_KEY, WebApiManager.CUSTOM_HEADER_KEY);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mParamUrl, headers);

    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
//            mListener.openVideoFragment(url);
//            if (url.contains("m3u8")) {
//                mListener.openVideoFragment(url);
//                mVideoView.setVisibility(View.VISIBLE);
//                mWebView.setVisibility(View.GONE);
//                mVideoView.setVideoURI(Uri.parse(url));
//                mVideoView.start();
//            }
            return true;
        }

//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//            //You can add some custom functionality here
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Crashlytics.log("MyWebViewClient onReceivedError request:" + request + " error:" + error );
        }

//        @Override
//        public void onReceivedError(WebView view, int errorCode,
//                                    String description, String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            Crashlytics.log("MyWebViewClient onReceivedError errorCode:" + errorCode + " description:" + description + " failingUrl:" + failingUrl);
//        }
    }
}