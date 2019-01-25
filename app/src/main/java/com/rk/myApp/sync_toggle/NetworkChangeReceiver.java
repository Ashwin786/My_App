package com.rk.myApp.sync_toggle;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by user1 on 5/11/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private ConnectivityManager cm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("service", "NetworkChangeReceiver");
//        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,500);
        if (isOnline(context)) {
            ContentResolver.setMasterSyncAutomatically(true);
        } else
            ContentResolver.setMasterSyncAutomatically(false);
    }

    public boolean isOnline(Context context) {

        NetworkInfo netInfo = null;
        try {
            if (cm == null)
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
