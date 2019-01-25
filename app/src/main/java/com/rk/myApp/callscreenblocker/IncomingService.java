package com.rk.myApp.callscreenblocker;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class IncomingService extends Service {
    private Call_Receiver callReceiver;
    private NotificationUtils mNotificationUtils;

    public IncomingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service status", "Service started");
        create_sound();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationUtils = new NotificationUtils(this);
            Notification.Builder nb = mNotificationUtils.
                    getAndroidChannelNotification("Screen Blocker", "By Ramesh");

            mNotificationUtils.getManager().notify(101, nb.build());
            startForeground(1, nb.build());
        }
        callReceiver = new Call_Receiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(callReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        Log.e("Service status", "Service onDestroy");
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }
//        Alarmactivater.oneTimeAlarm(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.e("Service status", "onTaskRemoved");
//        stopSelf();
//        Alarmactivater.oneTimeAlarm(this);
    }

    private void create_sound() {
        try {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
