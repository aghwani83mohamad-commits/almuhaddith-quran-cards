package com.almuhaddith.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONObject;

public class AdhanScheduler {
    public static final String ACTION_ALARM = "com.almuhaddith.app.ADHAN_ALARM";
    static final String KEY_SCHEDULE = "schedule";
    static final String NEXT_CHANNEL = "aly_adhan_next";
    static final int NEXT_ID = 4716;
    static final String[] ORDER = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
    static final String PREFS = "aly_adhan";

    static void noNext(Context context) {
        try {
            if (new JSONObject(context.getSharedPreferences(PREFS, 0).getString(KEY_SCHEDULE, null)).optBoolean("off", false)) {
                postOff(context);
                return;
            }
        } catch (Throwable th) {
        }
        clearNext(context);
    }

    static String offText(Context context) {
        try {
            String optString = new JSONObject(context.getSharedPreferences(PREFS, 0).getString(KEY_SCHEDULE, null)).optString("offList", "");
            if (optString.length() == 0) {
                return null;
            }
            return optString;
        } catch (Throwable th) {
            return null;
        }
    }

    static void postOff(Context context) {
        Notification.Builder builder;
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                notificationManager.createNotificationChannel(new NotificationChannel(NEXT_CHANNEL, "الأذان القادم", 2));
                builder = new Notification.Builder(context, NEXT_CHANNEL);
            } else {
                builder = new Notification.Builder(context);
            }
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (launchIntentForPackage == null) {
                launchIntentForPackage = new Intent();
            }
            notificationManager.notify(NEXT_ID, builder.setSmallIcon(R.drawable.ic_adhan_bell_off).setContentTitle("الأذان معطّل").setContentText("اضغط لفتح التطبيق وتفعيل الأذان").setContentIntent(PendingIntent.getActivity(context, 4717, launchIntentForPackage, 201326592)).setOngoing(true).build());
        } catch (Throwable th) {
        }
    }

    static void postNext(Context context, long j, String str, TimeZone timeZone) {
        Notification.Builder builder;
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    Class<?> cls = Class.forName("android.app.NotificationChannel");
                    NotificationManager.class.getMethod("createNotificationChannel", cls).invoke(notificationManager, cls.getConstructor(String.class, CharSequence.class, Integer.TYPE).newInstance(NEXT_CHANNEL, "الأذان القادم", 2));
                    builder = (Notification.Builder) Notification.Builder.class.getConstructor(Context.class, String.class).newInstance(context, NEXT_CHANNEL);
                } else {
                    builder = new Notification.Builder(context);
                }
                Calendar calendar = Calendar.getInstance(timeZone);
                calendar.setTimeInMillis(j);
                String format = String.format(Locale.US, "%02d:%02d", Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12)));
                Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (launchIntentForPackage == null) {
                    launchIntentForPackage = new Intent();
                }
                PendingIntent activity = PendingIntent.getActivity(context, 4717, launchIntentForPackage, 201326592);
                int i = R.drawable.ic_adhan_bell;
                if (offText(context) != null) {
                    i = R.drawable.ic_adhan_bell_off;
                }
                builder.setSmallIcon(i).setContentTitle("الأذان القادم: " + AdhanService.arName(str) + " — " + format).setContentIntent(activity).setOngoing(true).setOnlyAlertOnce(true).setWhen(j).setShowWhen(true);
                String offText = offText(context);
                if (offText != null) {
                    builder.setContentText(offText);
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        builder.setUsesChronometer(true);
                        Notification.Builder.class.getMethod("setChronometerCountDown", Boolean.TYPE).invoke(builder, Boolean.TRUE);
                    } catch (Throwable th) {
                    }
                } else {
                    builder.setContentText("الساعة " + format);
                }
                notificationManager.notify(NEXT_ID, builder.build());
            }
        } catch (Throwable th2) {
        }
    }

    static void clearNext(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.cancel(NEXT_ID);
            }
        } catch (Throwable th) {
        }
    }

    public static void startSvc(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                Context.class.getMethod("startForegroundService", Intent.class).invoke(context, intent);
            } else {
                context.startService(intent);
            }
        } catch (Throwable th) {
            try {
                context.startService(intent);
            } catch (Throwable th2) {
            }
        }
    }

    public static void save(Context context, String str) {
        context.getSharedPreferences(PREFS, 0).edit().putString(KEY_SCHEDULE, str).commit();
    }

    static PendingIntent alarmIntent(Context context, String str, int i) {
        Intent intent = new Intent(context, (Class<?>) AdhanReceiver.class);
        intent.setAction(ACTION_ALARM);
        if (str == null) {
            str = "Dhuhr";
        }
        intent.putExtra("key", str);
        return PendingIntent.getBroadcast(context, 4711, intent, 67108864 | i);
    }

    public static void scheduleNext(Context context) {
        String str;
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
            if (alarmManager != null) {
                alarmManager.cancel(alarmIntent(context, "Dhuhr", 134217728));
                String string = context.getSharedPreferences(PREFS, 0).getString(KEY_SCHEDULE, null);
                if (string != null) {
                    JSONObject jSONObject = new JSONObject(string);
                    JSONObject jSONObject2 = jSONObject.getJSONObject("days");
                    TimeZone timeZone = TimeZone.getTimeZone(jSONObject.optString("tz", "Europe/Istanbul"));
                    long currentTimeMillis = System.currentTimeMillis() + 5000;
                    long j = -1;
                    String str2 = null;
                    int i = 0;
                    while (i < 60 && j < 0) {
                        Calendar calendar = Calendar.getInstance(timeZone);
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(5, i);
                        JSONObject optJSONObject = jSONObject2.optJSONObject(String.format(Locale.US, "%04d-%02d-%02d", Integer.valueOf(calendar.get(1)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(5))));
                        if (optJSONObject != null) {
                            int i2 = 0;
                            while (true) {
                                if (i2 >= ORDER.length) {
                                    str = str2;
                                    break;
                                }
                                String optString = optJSONObject.optString(ORDER[i2], "");
                                if (optString.length() >= 4) {
                                    String[] split = optString.split(":");
                                    if (split.length >= 2) {
                                        Calendar calendar2 = (Calendar) calendar.clone();
                                        calendar2.set(11, Integer.parseInt(split[0].trim()));
                                        calendar2.set(12, Integer.parseInt(split[1].trim()));
                                        calendar2.set(13, 0);
                                        calendar2.set(14, 0);
                                        long timeInMillis = calendar2.getTimeInMillis();
                                        if (timeInMillis > currentTimeMillis) {
                                            str = ORDER[i2];
                                            j = timeInMillis;
                                            break;
                                        }
                                    } else {
                                        continue;
                                    }
                                }
                                i2++;
                            }
                        } else {
                            str = str2;
                        }
                        i++;
                        str2 = str;
                    }
                    if (j >= 0) {
                        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        if (launchIntentForPackage == null) {
                            launchIntentForPackage = new Intent();
                        }
                        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(j, PendingIntent.getActivity(context, 4712, launchIntentForPackage, 201326592)), alarmIntent(context, str2, 134217728));
                        context.getSharedPreferences(PREFS, 0).edit().putLong("next_ms", j).putString("next_key", str2).commit();
                        postNext(context, j, str2, timeZone);
                        return;
                    }
                    noNext(context);
                    return;
                }
                clearNext(context);
            }
        } catch (Throwable th) {
        }
    }
}
