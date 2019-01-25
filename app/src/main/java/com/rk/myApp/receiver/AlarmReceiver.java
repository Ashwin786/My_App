package com.rk.myApp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.rk.myApp.callscreenblocker.Common;
import com.rk.myApp.callscreenblocker.IncomingService;
import com.rk.myApp.sync_toggle.Constants;
import com.rk.myApp.sync_toggle.Network_service;
import com.rk.myApp.sync_toggle.TTS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    public static boolean sync_toggle = false;
    public static boolean blocker = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receiver", "AlarmReceiver");
        this.context = context;
        /*sync_toggle*/
        if (sync_toggle) {
            sync_toggle();
            set_Auto_brightness(context);
        }

        /*Call screen blocker*/
        if (Common.getInstance(context).checkIsMyPhone()) {
            speak_time();
        }

        if (!Common.getInstance(context).isMyServiceRunning(IncomingService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, IncomingService.class));
            } else {
                context.startService(new Intent(context, IncomingService.class));
            }

        } else
            Log.e("service", "running");


    }

    private void sync_toggle() {
        if (!Constants.getInstance(context).isMyServiceRunning(Network_service.class))
            context.startService(new Intent(context, Network_service.class));
    }

    private void speak_time() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (!Constants.getInstance(context).isMyServiceRunning(TTS.class)) {
            if (hour > 6 && hour < 22) {
                context.startService(new Intent(context, TTS.class));
            }
        } else
            context.stopService(new Intent(context, TTS.class));

    }


    private void create_sound() {
        try {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_Auto_brightness(Context context) {
        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    private boolean is_hotspot_enabled() {
       /*int AP_STATE_DISABLING = 10;
       int AP_STATE_DISABLED = 11;
       int AP_STATE_ENABLING = 12;
       int AP_STATE_FAILED = 14;*/
        int AP_STATE_ENABLED = 13;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Method method = null;
        try {
            method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        int actualState = 0;
        try {
            actualState = (Integer) method.invoke(wifiManager, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (actualState == AP_STATE_ENABLED)
            return true;
        return false;

    }
}