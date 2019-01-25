package com.rk.myApp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Boot_receiver extends BroadcastReceiver {
    public Boot_receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.e("Boot_receiver", "started");
        Alarmactivater.scheduleAlarm(context);
        Log.e("Boot_receiver", "done");
    }
}
