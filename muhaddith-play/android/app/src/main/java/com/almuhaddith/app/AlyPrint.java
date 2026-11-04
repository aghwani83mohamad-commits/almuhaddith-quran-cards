package com.almuhaddith.app;

import android.app.Activity;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AlyPrint {
    public static WebView keep;
    private final Activity a;

    public class C extends WebViewClient {
        private final Activity act;
        private boolean done;
        private final String title;

        public C(Activity activity, String str) {
            this.act = activity;
            this.title = str;
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            if (this.done) {
                return;
            }
            this.done = true;
            try {
                PrintManager printManager = (PrintManager) this.act.getSystemService("print");
                String str2 = this.title;
                printManager.print(str2, webView.createPrintDocumentAdapter(str2), new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build());
            } catch (Exception e) {
            }
        }
    }

    public class R implements Runnable {
        private final Activity act;
        private final String html;
        private final String title;

        public R(Activity activity, String str, String str2) {
            this.act = activity;
            this.html = str;
            this.title = str2;
        }

        @Override // java.lang.Runnable
        public void run() {
            Activity activity = this.act;
            String str = this.title;
            WebView webView = new WebView(activity);
            webView.setWebViewClient(new C(activity, str));
            AlyPrint.keep = webView;
            webView.loadDataWithBaseURL("https://almuhaddith.local/", this.html, "text/html", "UTF-8", null);
        }
    }

    public AlyPrint(Activity activity) {
        this.a = activity;
    }

    public static void attach(Activity activity, WebView webView) {
        webView.addJavascriptInterface(new AlyPrint(activity), "AlyPrint");
    }

    @JavascriptInterface
    public void printHtml(String str, String str2) {
        Activity activity = this.a;
        activity.runOnUiThread(new R(activity, str, str2));
    }

    @JavascriptInterface
    public String saveFile(String str, String str2, String str3) {
        return AlySave.save(this.a, str, str2, str3);
    }
}
