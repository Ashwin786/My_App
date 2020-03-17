package com.rk.myApp.callscreenblocker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rk.myApp.R;
import com.rk.myApp.receiver.Alarmactivater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Lifecycle Activity";
    private int OVERLAY_PERMISSION_CODE = 0;
    private Button btn, btn2;
    private SharedPreferences sp;
    private String btntext;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "on Start function");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on Resumes function");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "on Pause function");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on Stop function");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "on Restart function");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on Restart function");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_main);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            Log.e(TAG, "networkInfo type" + networkInfo.getType());
            Log.e(TAG, "networkInfo type" + networkInfo.getTypeName());
        }
        Log.e(TAG, "networkInfo : " + networkInfo);
//        if (networkInfo != null){
//            Log.e(TAG,"networkInfo : " +networkInfo.getExtraInfo());
//        }


//        getUIText22();
        /*TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String what = telephony.getNetworkOperatorName();*/
//        int what = telephony.get();
//        Log.e(TAG,"what"+what);
//        String optName = getOutput(this, "getCarrierName", 1);
//        Log.e(TAG,"optName"+optName);
        sp = getSharedPreferences("call_blocker", MODE_PRIVATE);
        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn.getText().toString().equals("Activate")) {
                    Alarmactivater.addBlockerAlarm(MainActivity.this);
                    sp.edit().putString("status", "Deactivate").commit();
                    btn.setText("Deactivate");
                } else {
                    if (sp.getBoolean("permission_status", false)) {
                        Alarmactivater.cancelAlarm(MainActivity.this);
                        sp.edit().putString("status", "Activate").commit();
                        btn.setText("Activate");
                    } else {
                        checkANDgetpermission();
                    }
                }
//                Common.getInstance(MainActivity.this).block_ui();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Alarmactivater.isAlarmRunning(MainActivity.this);
//                Log.e("Is service running", "" + Common.getInstance(MainActivity.this).isMyServiceRunning(IncomingService.class));

            }
        });
        btntext = sp.getString("status", "Activate");
        if (btntext.equals("Activate")) {
            if (Common.alarm) {
                if (Alarmactivater.isAlarmRunning(MainActivity.this))
                    btntext = "Deactivate";
            } else {
                if (Common.getInstance(this).isMyServiceRunning(IncomingService.class))
                    btntext = "Deactivate";
            }
        } else if (btntext.equals("Deactivate")) {
            if (Common.alarm) {
                if (!Alarmactivater.isAlarmRunning(MainActivity.this))
                    btntext = "Activate";
            } else {
                if (!Common.getInstance(this).isMyServiceRunning(IncomingService.class))
                    btntext = "Activate";
            }
        }
        btn.setText(btntext);
        sp.edit().putString("status", btntext).commit();
        if (!sp.getBoolean("permission_status", false)) {
            checkANDgetpermission();
        } else if (btntext.equals("Deactivate")) {
            Alarmactivater.addBlockerAlarm(MainActivity.this);
//            Log.e("activated","activated");
        }
        if (canDrawOverlays(this)) {
            sp.edit().putBoolean("permission_status", true).commit();
        }

//        Log.e("CanDrawOverlays",""+canDrawOverlays(this)) ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (sp.getBoolean("permission_status", false)) {
                SubscriptionManager manager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                int defaultSmsId = manager.getDefaultDataSubscriptionId();
                Log.e(TAG, "defaultSmsId" + defaultSmsId);
                SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultSmsId);

                Log.e(TAG, "info" + info);
                Log.e(TAG, "getIccId " + info.getIccId());
            }
            mSimSerialID();
        }

    }

    private void mSimSerialID() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subsInfoList != null) {
                Log.d("Test", "Current list = " + subsInfoList);
                for (int i = 0; i < subsInfoList.size(); i++) {
                    if (i == 0) {
//                        serialNoSIM1 = subsInfoList.get(i).getIccId();
                        Log.e(TAG, "sim 1 " + subsInfoList.get(i).getIccId());
                    }
                    if (i == 1) {
//                        serialNoSIM2 = subsInfoList.get(i).getIccId();
                        Log.e(TAG, "sim2 " + subsInfoList.get(i).getIccId());
                    }

                }
//                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
//                        String number1 = subscriptionInfo.getNumber();
//                        Log.d("Test", " Number is  " + number);
//                    }
            }
        }
    }

    public boolean addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);

                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    check_overlay();
                }
            }, 1500);

        }
    }

    private void check_overlay() {
        if (canDrawOverlays(this)) {
//               Common.getInstance(this).block_ui();
            sp.edit().putBoolean("permission_status", true).commit();
            check_autostart();
        } else {
            Toast.makeText(this, "Kindly switch on the permission button", Toast.LENGTH_SHORT).show();
            addOverlay();
        }
    }

    static boolean canDrawOverlays(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
            return true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //USING APP OPS MANAGER
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                try {
                    int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                    return result == AppOpsManager.MODE_ALLOWED;
                } catch (Exception ignore) {
                }
            }
        }

        try {
            //IF This Fails, we definitely can't do it
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (mgr == null) return false; //getSystemService might return null
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Exception ignore) {
        }
        return false;

    }

    private void check_autostart() {
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent1 = new Intent();
            intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent1);
        }
    }


    private int checkANDgetpermission() {
        String[] network = {Manifest.permission.READ_PHONE_STATE};
        ArrayList<String> list = new ArrayList();
        int j = 0;
        for (int i = 0; i < network.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, network[i]) != PackageManager.PERMISSION_GRANTED) {
                list.add(network[i]);
                j++;
            }
        }

        if (list.size() > 0) {
            String[] get = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                get[i] = list.get(i);
            }
            ActivityCompat.requestPermissions(MainActivity.this, get, 0);
        } else
            addOverlay();
        return j;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            boolean permission_granted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permission_granted = false;
                }
            }
            if (!permission_granted)
                checkANDgetpermission();
            else {
                addOverlay();
            }
        }


    }

    int getDefaultDataSubscriptionId(final SubscriptionManager subscriptionManager) {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            int nDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();

            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                return (nDataSubscriptionId);
            }
        }

        try {
            Class<?> subscriptionClass = Class.forName(subscriptionManager.getClass().getName());
            try {
                Method getDefaultDataSubscriptionId = subscriptionClass.getMethod("getDefaultDataSubId");

                try {
                    return ((int) getDefaultDataSubscriptionId.invoke(subscriptionManager));
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        return (SubscriptionManager.INVALID_SUBSCRIPTION_ID);
    }

    @TargetApi(22)
    public String getUIText22(final TelephonyManager telephonyManager) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        int nDataSubscriptionId = getDefaultDataSubscriptionId(subscriptionManager);

        if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            SubscriptionInfo si = subscriptionManager.getActiveSubscriptionInfo(nDataSubscriptionId);

            if (si != null) {
                return (si.getCarrierName().toString());
            }
        }
        return null;
    }

    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
//                if (name.contains(methodName)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length > 0) {
                    reflectionMethod = name;
                    Log.e("Name", reflectionMethod);
                }
//                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        //Log.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        //Log.i("Reflection", "Result: " + result);
        return result;
    }
}
