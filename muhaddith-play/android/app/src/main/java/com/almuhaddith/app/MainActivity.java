package com.almuhaddith.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        WebSettings settings = getBridge().getWebView().getSettings();
        settings.setTextZoom(100);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        WebView webView = getBridge().getWebView();
        AlyPrint.attach(this, webView);
        AlyAdhan.attach(this, webView);
    }
}
