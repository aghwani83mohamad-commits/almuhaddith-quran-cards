package com.almuhaddith.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AdhanReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action;
        if (intent == null) {
            action = null;
        } else {
            try {
                action = intent.getAction();
            } catch (Throwable th) {
            }
        }
        if (AdhanScheduler.ACTION_ALARM.equals(action)) {
            String stringExtra = intent.getStringExtra("key");
            Intent intent2 = new Intent(context, (Class<?>) AdhanService.class);
            intent2.setAction(AdhanService.ACTION_PLAY);
            if (stringExtra == null) {
                stringExtra = "Dhuhr";
            }
            intent2.putExtra("key", stringExtra);
            AdhanScheduler.startSvc(context, intent2);
        }
        AdhanScheduler.scheduleNext(context);
    }
}
