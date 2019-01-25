package com.rk.myApp.sync_toggle;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.rk.myApp.R;

public class Network_service extends Service {

    private NetworkChangeReceiver net;
    private Incomingsms_receiver incomingsms_receiver;

    public Network_service() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "oncreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service", "onStartCommand");
        try {
            net = new NetworkChangeReceiver();
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(net, intentFilter);
//            start_foreground();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Network_service error", e.toString());
        }
        try {
            incomingsms_receiver = new Incomingsms_receiver();
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(incomingsms_receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Network_service error", e.toString());
        }
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    private void start_foreground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("service", "onDestroy");
        if (net != null)
            unregisterReceiver(net);
        if (incomingsms_receiver != null)
            unregisterReceiver(incomingsms_receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
