package com.zype.android.ui.settings;

import android.os.Bundle;
import android.webkit.WebView;

import com.squareup.otto.Subscribe;
import com.zype.android.R;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ContentSettingsParamsBuilder;
import com.zype.android.webapi.events.settings.ContentSettingsEvent;
import com.zype.android.webapi.model.settings.ContentSettingsData;

import java.util.List;

public class TermsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Contents settings
        getApi().executeRequest(WebApiManager.Request.CONTENT_SETTINGS, new ContentSettingsParamsBuilder().build());

//        ((WebView) findViewById(R.id.terms_webview)).loadUrl("file:///android_asset/terms.html");
    }

    @Override
    protected String getActivityName() {
        return TermsActivity.class.getSimpleName();
    }

    @Subscribe
    public void handleContentSettingsEvent(ContentSettingsEvent event) {
        Logger.d("handleContentSettingsEvent()");
        List<ContentSettingsData> data = event.getEventData().getModelData().getData();
        for (ContentSettingsData item : data) {
            if (item.getFriendlyTitle().equals("privacy_policy")) {
                ((WebView) findViewById(R.id.terms_webview)).loadData(item.getDescription(), "text/html", "UTF-8");
                return;
            }
        }
    }




}


