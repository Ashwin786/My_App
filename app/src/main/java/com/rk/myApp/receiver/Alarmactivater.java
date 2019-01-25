package com.rk.myApp.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rk.myApp.callscreenblocker.Common;

/**
 * Created by user1 on 10/11/16.
 */
public class Alarmactivater {
    public static final int REQUEST_CODE = 1001;
    private static final String ACTION_ALARM_RECEIVER = "call_blocker_alarm";

    public static void addBlockerAlarm(Context context) {
        AlarmReceiver.blocker = true;
        scheduleAlarm(context);
    }

    public static void scheduleAlarm(Context context) {
        if (!isAlarmRunning(context)) {
            Log.e("alarm", "started");
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(ACTION_ALARM_RECEIVER);
            final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long start_time = System.currentTimeMillis() + 4000;
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, start_time, Common._interval_time, pIntent);
        }
    }
    public static void cancelAlarm(Context context) {
//        if (Common.getInstance(context).checkIsMyPhone()) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_ALARM_RECEIVER);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        pIntent.cancel();//important
        Log.e("alarm", "cancelled");
//        } else {
//            if (Common.getInstance(context).isMyServiceRunning(IncomingService.class)) {
//                context.stopService(new Intent(context, IncomingService.class));
//                Log.e("service status", "stopped");
//            } else
//                Log.e("service status", "not running");
//        }


    }

    public static boolean isAlarmRunning(Context context) {
        //checking if alarm is working with pendingIntent
        Intent intent = new Intent(context, AlarmReceiver.class);//the same as up
        intent.setAction(ACTION_ALARM_RECEIVER);//the same as up
        boolean isWorking = (PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.e(ACTION_ALARM_RECEIVER, "alarm is " + (isWorking ? "" : "not") + " working...");
        return isWorking;
    }

    public static void oneTimeAlarm(Context context) {

//        if (Common.getInstance(context).isMyServiceRunning(IncomingService.class))
//            context.stopService(new Intent(context, IncomingService.class));
        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.setAction(ACTION_ALARM_RECEIVER);
        Log.e("Alarm", "onetime");
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long start_time = System.currentTimeMillis() + 5000;
        alarm.setExact(AlarmManager.RTC_WAKEUP, start_time, pIntent);
    }


    public static void addSyncAlarm(Context context) {
        AlarmReceiver.sync_toggle = true;
        scheduleAlarm(context);
    }
}
