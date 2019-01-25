package com.rk.myApp.sync_toggle;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rk.myApp.R;
import com.rk.myApp.receiver.Alarmactivater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn_hide;
    private NetworkChangeReceiver net;
    public Test_services testService;
    public boolean isBound = false;
    private Button btn_cancel;
    private Intent intent;
    private Button btn_check;

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(net);
    }

    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        unlockScreen();
        setContentView(R.layout.activity_main);
//        checkIs_sms_default_app();

//            Toast.makeText(this, "Kindly allow permission", Toast.LENGTH_SHORT).show();
//        Alarmactivater.addSyncAlarm(this, 1000 * 60 * 60);
        intent = new Intent(MainActivity.this, Test_services.class);
//        startService(intent);

        btn_check = (Button) findViewById(R.id.btn_check);
        btn_hide = (Button) findViewById(R.id.btn_hide);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_hide.setText("bind");
        btn_check.setText("Check");
        btn_cancel.setText("unbind");
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isBound)
//                    testService.make_sound();

            }
        });
        btn_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                getClientList();
                /*String msg = "wifi_on";
                Constants.getInstance(MainActivity.this).sendSMS(MainActivity.this, Constants.getInstance(MainActivity.this).phoneNo, msg);*/
//                Constants.getInstance(MainActivity.this).deleteSMS("");
//                Constants.getInstance(MainActivity.this).request_location_update(MainActivity.this);
//                finish();
//                hide_app();
//                start_play();
//                finish();
//                vibrate();


                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

//        btn_cancel.setVisibility(View.GONE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                unbindService(serviceConnection);
                stopService(intent);

            }
        });

        if (checkANDgetpermission() == 0) {
            Constants.getInstance(MainActivity.this).get_WriteSettingPermission(this);
            Log.e("hass all permission", "hass all permission");
            Alarmactivater.addSyncAlarm(MainActivity.this);
//            startActivity(new Intent(this,TestActivity.class));
        }


//        Constants.getInstance(this).switch_on_hotspot(true);
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                unbindService(serviceConnection);
//                Intent intent = new Intent(MainActivity.this,Network_service.class);
//                stopService(intent);
//            }
//        });
    }

    void make_sound() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
    }

    private int checkANDgetpermission() {
        String[] network = {Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS};
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
        }
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
                Constants.getInstance(MainActivity.this).get_WriteSettingPermission(this);
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Write permission", "" + resultCode);
        Alarmactivater.addSyncAlarm(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (isBound)
//            unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void checkIs_sms_default_app() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Log.e("sms package", Telephony.Sms.getDefaultSmsPackage(this));
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals("com.rk.myApp.callscreenblocker.sync_toggle")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("onServiceConnected", "onServiceConnected");
            Test_services.MyBinder binder = (Test_services.MyBinder) service;
            testService = binder.getServices();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("onServiceDisconnected", "onServiceDisconnected");
            testService = null;
            isBound = false;
        }
    };


    private void start_play() {

    }

    private void hide_app() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        int what = p.getComponentEnabledSetting(componentName);
        if (what == 2) {
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            btn_hide.setText("Hide");
        } else {
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            btn_hide.setText("un hide");
        }
    }

    public void getClientList() {
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null) {
                    // Basic sanity check
                    String mac = splitted[3];
                    System.out.println("Mac : Outside If " + mac);
                    if (mac.matches("..:..:..:..:..:..")) {
                        macCount++;
                   /* ClientList.add("Client(" + macCount + ")");
                    IpAddr.add(splitted[0]);
                    HWAddr.add(splitted[3]);
                    Device.add(splitted[5]);*/
                        System.out.println("Mac : " + mac + " IP Address : " + splitted[0]);
                        System.out.println("Mac_Count  " + macCount + " MAC_ADDRESS  " + mac);
                        Toast.makeText(
                                getApplicationContext(),
                                "Mac_Count  " + macCount + "   MAC_ADDRESS  "
                                        + mac, Toast.LENGTH_SHORT).show();

                    }
               /* for (int i = 0; i < splitted.length; i++)
                    System.out.println("Addressssssss     "+ splitted[i]);*/

                }
            }
        } catch (Exception e) {

        }
    }
}
