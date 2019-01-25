package com.rk.myApp.sync_toggle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by user1 on 16/7/18.
 */
public class ActivateBySms {
    private final Context context;

    public ActivateBySms(Context context) {
        this.context=context;
    }

    private void switch_on_hotspot() {
        try {
            new WifiApManager(context).setWifiApState(new WifiConfiguration(), true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
//                wifiConfig();
    }

    private void wifiConfig() {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"SSIDName\"";
        wc.preSharedKey = "\"password\"";
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int res = wifi.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean b = wifi.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);
    }

    private void increase_brightness(int brightness) {
        //constrain the value of brightness
        if (brightness < 0)
            brightness = 0;
        else if (brightness > 255)
            brightness = 255;

        ContentResolver cResolver = context.getApplicationContext().getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    public boolean youDesirePermissionCode(Activity context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
            return true;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 0);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_SETTINGS}, 0);
            }
        }
        return false;
    }
}
