package com.zype.android.ui.main.fragments.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zype.android.R;
import com.zype.android.utils.BundleConstants;

public class WebActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras == null) {
            throw new RuntimeException("Bundle can not be empty");
        }
        setContentView(R.layout.activity_web);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setUserAgentString(WebApiManager.CUSTOM_HEADER_VALUE);
        webView.loadUrl(extras.getString(BundleConstants.WEB_ACTIVITY_URL));
        webView.setWebViewClient(new WebViewClient());
        setTitle(extras.getString(BundleConstants.WEB_ACTIVITY_TITLE));
    }
}
