package com.almuhaddith.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import org.json.JSONObject;

public class AlyAdhan {
    private final Activity act;
    private final Context app;

    @JavascriptInterface
    public boolean hasFile(String str) {
        return AdhanFiles.has(this.app, str);
    }

    @JavascriptInterface
    public String readFile(String str) {
        return AdhanFiles.read(this.app, str);
    }

    @JavascriptInterface
    public boolean saveFile(String str, String str2) {
        return AdhanFiles.save(this.app, str, str2);
    }

    private AlyAdhan(Activity activity) {
        this.act = activity;
        this.app = activity.getApplicationContext();
    }

    public static void attach(Activity activity, WebView webView) {
        try {
            webView.addJavascriptInterface(new AlyAdhan(activity), "AlyAdhan");
            try {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("aly_adhan", 0);
                if (Build.VERSION.SDK_INT >= 33 && !sharedPreferences.getBoolean("asked_notif", false) && activity.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != 0) {
                    sharedPreferences.edit().putBoolean("asked_notif", true).commit();
                    activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 7711);
                }
            } catch (Throwable th) {
            }
            AdhanScheduler.scheduleNext(activity.getApplicationContext());
        } catch (Throwable th2) {
        }
    }

    @JavascriptInterface
    public boolean setSchedule(String str) {
        try {
            new JSONObject(str);
            AdhanScheduler.save(this.app, str);
            AdhanScheduler.scheduleNext(this.app);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    @JavascriptInterface
    public void play(String str) {
        playInternal(str, false);
    }

    @JavascriptInterface
    public void playTest(String str) {
        playInternal(str, true);
    }

    private void playInternal(String str, boolean z) {
        try {
            Intent intent = new Intent(this.app, (Class<?>) AdhanService.class);
            intent.setAction(AdhanService.ACTION_PLAY);
            if (str == null) {
                str = "Dhuhr";
            }
            intent.putExtra("key", str);
            intent.putExtra("force", z);
            AdhanScheduler.startSvc(this.app, intent);
        } catch (Throwable th) {
        }
    }

    @JavascriptInterface
    public void stop() {
        try {
            Intent intent = new Intent(this.app, (Class<?>) AdhanService.class);
            intent.setAction(AdhanService.ACTION_STOP);
            AdhanScheduler.startSvc(this.app, intent);
        } catch (Throwable th) {
        }
    }

    @JavascriptInterface
    public boolean isPlaying() {
        return AdhanService.playing;
    }

    @JavascriptInterface
    public String status() {
        boolean z;
        PowerManager powerManager;
        boolean z2 = true;
        try {
            JSONObject jSONObject = new JSONObject();
            if (Build.VERSION.SDK_INT >= 33) {
                z = this.act.checkSelfPermission("android.permission.POST_NOTIFICATIONS") == 0;
            } else {
                z = true;
            }
            if (Build.VERSION.SDK_INT >= 23 && ((powerManager = (PowerManager) this.app.getSystemService("power")) == null || !powerManager.isIgnoringBatteryOptimizations(this.app.getPackageName()))) {
                z2 = false;
            }
            jSONObject.put("notif", z);
            jSONObject.put("battery", z2);
            SharedPreferences sharedPreferences = this.app.getSharedPreferences("aly_adhan", 0);
            jSONObject.put("nextMs", sharedPreferences.getLong("next_ms", 0L));
            jSONObject.put("nextKey", sharedPreferences.getString("next_key", ""));
            return jSONObject.toString();
        } catch (Throwable th) {
            return "{}";
        }
    }

    @JavascriptInterface
    public void setup() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT >= 33 && AlyAdhan.this.act.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != 0) {
                        AlyAdhan.this.act.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 7711);
                    }
                } catch (Throwable th) {
                }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        PowerManager powerManager;
                        try {
                            if (Build.VERSION.SDK_INT >= 23 && (powerManager = (PowerManager) AlyAdhan.this.app.getSystemService("power")) != null && !powerManager.isIgnoringBatteryOptimizations(AlyAdhan.this.app.getPackageName())) {
                                Intent intent = new Intent("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
                                AlyAdhan.this.act.startActivity(intent);
                            }
                        } catch (Throwable th2) {
                        }
                    }
                }, 2500L);
            }
        });
    }
}
