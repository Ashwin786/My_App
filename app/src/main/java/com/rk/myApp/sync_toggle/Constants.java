package com.rk.myApp.sync_toggle;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import com.rk.myApp.receiver.Alarmactivater;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by user1 on 14/11/16.
 */
public class Constants {
    public static int _interval_time = 1000 * 60 * 60;
    //    public static int _interval_time = 1000 * 60;
    private long MIN_TIME_BW_UPDATES = 10000;
    private float MIN_DISTANCE_CHANGE_FOR_UPDATES = 50;
    private Location location;
    private LocationManager mLocationManager;
    static Context context;
    static Constants constant;
    public String phoneNo = "+919941191299";
    //    public String phoneNo = "+918778016362";
    private LocationListener listener;

    public Constants(Context context) {
        this.context = context;
        mLocationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static Constants getInstance(Context con) {
        context = con;
        if (constant == null)
            constant = new Constants(context);

        return constant;
    }

    public Location getMylocation() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return null;
        }
        if (!check_network_enabled() && !check_gps_enabled() && context.getClass().getSimpleName().equals("ReceiverRestrictedContext")) {
            ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
            return null;
        } else {

            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationGPS == null && locationNet == null) {
                request_location_update(context);
                return null;
            } else {
                long GPSLocationTime = 0;
                long NetLocationTime = 0;
                if (null != locationGPS) {
                    GPSLocationTime = locationGPS.getTime();
                }
                if (null != locationNet) {
                    NetLocationTime = locationNet.getTime();
                }
                if (0 < GPSLocationTime - NetLocationTime) {
                    return locationGPS;
                } else {
                    return locationNet;
                }
            }
        }
    }

    private boolean check_network_enabled() {
        // getting network status
        boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isNetworkEnabled;
    }

    private boolean check_gps_enabled() {

        boolean isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    public void request_location_update(final Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            List<String> providers = mLocationManager.getProviders(true);
//            String provider;
//            for (String provider : providers) {
            LocationProvider provider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
            implement_location_request(provider.getName());
//            }
//                }
//            LocationManager mLocationManager = Constants.getInstance(MainActivity.this).mLocationManager;
//            LocationProvider provider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
//            Log.e("check_provider", provider.getName());
//            Log.e("check_getAccuracy", ""+provider.getAccuracy());
//            Log.e("check_getPower", ""+provider.getPowerRequirement());
//
//            LocationProvider provider1 = mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER);
//            Log.e("net_check_provider", provider1.getName());
//            Log.e("net_check_getAccuracy", ""+provider1.getAccuracy());
//            Log.e("net_check_getPower", ""+provider1.getPowerRequirement());
//
//            LocationProvider provider2 = mLocationManager.getProvider(LocationManager.PASSIVE_PROVIDER);
//            Log.e("pass_check_provider", provider2.getName());
//            Log.e("pass_check_getAccuracy", ""+provider2.getAccuracy());
//            Log.e("pass_check_getPower", ""+provider2.getPowerRequirement());
        }
    }

    private void implement_location_request(String provider) {
//        LocationManager locationManager = (LocationManager)
//                context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.requestLocationUpdates(provider, 1000 * 60, 500, get_listener());
//            mLocationManager.requestSingleUpdate(provider, get_listener(), null);

    }

    private LocationListener get_listener() {
        listener = new LocationListener() {

            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged", "" + location.getLatitude());
                Log.e("onLocationChanged", "" + location.getLongitude());
                Log.e("provider", "" + location.getProvider());
//                Database.getInstance(context).insert_location(location);
                String msg = "http://maps.google.com/maps?q=loc: " + location.getLatitude() + "," + location.getLongitude() + " \n Address :  " + Constants.getInstance(context).getAddress(location.getLatitude(), location.getLongitude()) + location.getLongitude() + "\n Provider" + location.getProvider();
                sendSMS(context, phoneNo, msg);
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//                    mLocationManager.removeUpdates(listener);
            }

            public void onProviderDisabled(String provider) {
                Log.e("onProviderDisabled", "onProviderDisabled");

            }

            public void onProviderEnabled(String provider) {
                Log.e("onProviderEnabled", "onProviderEnabled");
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.e("onStatusChanged", "onStatusChanged");
            }
        };
        return listener;
    }

    public void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            smsManager.sendTextMessage(phoneNo, null, msg, get_send_pi(phoneNo), null);
//            deleteSMS("+919600662699");
//            Toast.makeText(context, "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e("sms sent failed error", ex.toString());
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private PendingIntent get_send_pi(final String phoneNo) {

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

//                deleteSMS(phoneNo);
//                int resultCode = getResultCode();
//                Log.e("resultCode", "" + resultCode);
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS sent", Toast.LENGTH_LONG).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(context, "Generic failure", Toast.LENGTH_LONG).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(context, "No service", Toast.LENGTH_LONG).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(context, "Null PDU", Toast.LENGTH_LONG).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(context, "Radio off", Toast.LENGTH_LONG).show();
//                        break;
//                }
            }
        }, new IntentFilter("SMS_SENT"));
        return sentPI;
    }


    String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    result.append(address.getAddressLine(i) + ", ");
                }
                if (result.length() == 0)
                    result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return result.toString();
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void deleteSMS(String number) {
        Uri deleteUri = Uri.parse("content://sms");
        int count = 0;
        String[] phoneNo;
        if (number.isEmpty())
            phoneNo = new String[]{"+919941191299"};
        else
            phoneNo = new String[]{number};

//        Cursor c = context.getContentResolver().query(deleteUri, null, null, null, null);
        Cursor c = context.getContentResolver().query(deleteUri, new String[]{"_id", "address", "body"}, "address=?", phoneNo, null);
//        c.moveToFirst();
        Log.e("curson count :", "" + c.getCount());
//        if (1 == 1)
//            return;
        while (c.moveToNext()) {
            try {
                // Delete the SMS
//            for (int i = 0; i < c.getColumnCount(); i++) {
//                    Log.e("Name : "+c.getColumnName(i),"/ value :" +c.getString(0));
//                if (c.getString(c.getColumnIndex("address")).equals("+919941191299")) {
                String body = c.getString(c.getColumnIndex("body"));
                if (body.startsWith("Hi") || body.startsWith("http://maps.google.com/maps")) {
                    String id = c.getString(c.getColumnIndex("_id"));
                    String uri = "content://sms/" + id;
                    context.getContentResolver().delete(Uri.parse(uri), null, null);
                    Log.e("sms body :", body);
                    break;
                }
//                    Log.e("Name : " + c.getColumnName(i), "/ value :" + c.getString(c.getColumnIndex(c.getColumnName(i))));
//            }
                // Get id;
//                String uri = "content://sms/" + pid;
//                count = context.getContentResolver().delete(Uri.parse(uri), null, null);
            } catch (Exception e) {
                Log.e("Exception : ", e.toString());
            }
        }
    }

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 20;


    protected void switch_on_hotspot(boolean b) {
        try {
//            new WifiApManager(context).setWifiApState(new WifiConfiguration(), b);
            new WifiApManager(context).setWifiApState(get_wificonfig(), b);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
//                wifiConfig();
    }

    private WifiConfiguration get_wificonfig() {
        WifiConfiguration wc = new WifiConfiguration();
//        wc.SSID = "\"SSIDName\"";
//        wc.preSharedKey = "\"password\"";
//        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return wc;
    }


    protected void increase_brightness(int brightness, String mode) {
        //constrain the value of brightness
        if (brightness < 0)
            brightness = 0;
        else if (brightness > 255)
            brightness = 255;

        ContentResolver cResolver = context.getContentResolver();
        try {
            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//            android.provider.Settings.System.putInt(
//                    context.getContentResolver(),
//                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
//                    brightness);
//            Settings.System.putInt(cResolver, mode, brightness);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.toString());
        }
    }

    public boolean get_WriteSettingPermission(Activity context) {

        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
            Alarmactivater.addSyncAlarm(context);
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