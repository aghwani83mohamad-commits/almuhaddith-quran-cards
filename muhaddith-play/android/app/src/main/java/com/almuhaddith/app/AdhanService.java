package com.almuhaddith.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class AdhanService extends Service {
    public static final String ACTION_PLAY = "com.almuhaddith.app.ADHAN_PLAY";
    public static final String ACTION_STOP = "com.almuhaddith.app.ADHAN_STOP";
    static final String CHANNEL = "aly_adhan_channel2";
    static final int NOTIF_ID = 4713;
    public static volatile boolean playing = false;
    static volatile long startedAt = 0;
    private AudioManager audio;
    private final AudioManager.OnAudioFocusChangeListener focusL = new AudioManager.OnAudioFocusChangeListener() {
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int i) {
        }
    };
    private MediaPlayer mp;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    static String arName(String str) {
        return "Fajr".equals(str) ? "الفجر" : "Dhuhr".equals(str) ? "الظهر" : "Asr".equals(str) ? "العصر" : "Maghrib".equals(str) ? "المغرب" : "Isha".equals(str) ? "العشاء" : "";
    }

    private Notification buildNotification(String str) {
        Notification.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                Class<?> cls = Class.forName("android.app.NotificationChannel");
                NotificationChannel notificationChannel = (NotificationChannel) cls.getConstructor(String.class, CharSequence.class, Integer.TYPE).newInstance(CHANNEL, "الأذان", 4);
                notificationChannel.setSound(null, null);
                notificationChannel.enableVibration(false);
                NotificationManager.class.getMethod("createNotificationChannel", cls).invoke(notificationManager, notificationChannel);
                builder = (Notification.Builder) Notification.Builder.class.getConstructor(Context.class, String.class).newInstance(this, CHANNEL);
            } else {
                builder = new Notification.Builder(this);
            }
        } catch (Throwable th) {
            builder = new Notification.Builder(this);
        }
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (launchIntentForPackage == null) {
            launchIntentForPackage = new Intent();
        }
        PendingIntent.getActivity(this, 4714, launchIntentForPackage, 201326592);
        Intent intent = new Intent(this, (Class<?>) AdhanService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent service = PendingIntent.getService(this, 4715, intent, 201326592);
        Notification.Builder ongoing = builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("حان وقت أذان " + arName(str)).setContentText("اضغط هنا أو على «إيقاف» لإيقاف الأذان").setContentIntent(service).setOngoing(true);
        ongoing.setDeleteIntent(service);
        ongoing.setPriority(1);
        ongoing.setVisibility(1);
        ongoing.addAction(android.R.drawable.ic_media_pause, "⏹ إيقاف الأذان", service);
        return builder.build();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (ACTION_STOP.equals(intent == null ? null : intent.getAction())) {
            try {
                startForeground(NOTIF_ID, buildNotification(""));
            } catch (Throwable th) {
            }
            stopAll();
        } else {
            String stringExtra = intent == null ? null : intent.getStringExtra("key");
            String str = stringExtra == null ? "Dhuhr" : stringExtra;
            try {
                startForeground(NOTIF_ID, buildNotification(str));
            } catch (Throwable th2) {
            }
            boolean z = intent != null && intent.getBooleanExtra("force", false);
            if (!playing || z || System.currentTimeMillis() - startedAt >= 90000) {
                play(str);
            }
        }
        return 2;
    }

    private void play(String str) {
        releasePlayer();
        try {
            String str2 = "Fajr".equals(str) ? "adhan_fajr" : "adhan_main";
            int identifier = getResources().getIdentifier(str2, "raw", getPackageName());
            if (identifier == 0) {
                identifier = getResources().getIdentifier(str2, "raw", getPackageName());
            }
            String path = AdhanFiles.path(this, str2);
            if (path != null || identifier != 0) {
                this.audio = (AudioManager) getSystemService("audio");
                try {
                    if (this.audio != null) {
                        this.audio.requestAudioFocus(this.focusL, 4, 2);
                    }
                } catch (Throwable th) {
                }
                this.mp = new MediaPlayer();
                this.mp.setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(2).build());
                this.mp.setWakeMode(getApplicationContext(), 1);
                if (path != null) {
                    this.mp.setDataSource(path);
                } else {
                    AssetFileDescriptor openRawResourceFd = getResources().openRawResourceFd(identifier);
                    this.mp.setDataSource(openRawResourceFd.getFileDescriptor(), openRawResourceFd.getStartOffset(), openRawResourceFd.getLength());
                    openRawResourceFd.close();
                }
                this.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        AdhanService.this.stopAll();
                    }
                });
                this.mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override // android.media.MediaPlayer.OnErrorListener
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                        AdhanService.this.stopAll();
                        return true;
                    }
                });
                this.mp.prepare();
                this.mp.start();
                playing = true;
                startedAt = System.currentTimeMillis();
                return;
            }
            stopAll();
        } catch (Throwable th2) {
            stopAll();
        }
    }

    private void releasePlayer() {
        try {
            if (this.mp != null) {
                try {
                    this.mp.stop();
                } catch (Throwable th) {
                }
                this.mp.release();
            }
        } catch (Throwable th2) {
        }
        this.mp = null;
        try {
            if (this.audio != null) {
                this.audio.abandonAudioFocus(this.focusL);
            }
        } catch (Throwable th3) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAll() {
        playing = false;
        releasePlayer();
        try {
            stopForeground(true);
        } catch (Throwable th) {
        }
        stopSelf();
    }

    @Override // android.app.Service
    public void onDestroy() {
        playing = false;
        releasePlayer();
        super.onDestroy();
    }
}
