package com.rk.myApp.sync_toggle;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by user1 on 5/1/19.
 */

public class Location_Constants implements LocationListener{
    private long MIN_TIME_BW_UPDATES = 10000;
    private float MIN_DISTANCE_CHANGE_FOR_UPDATES = 50;
    private Location location;
    private LocationManager mLocationManager;
    static Context context;
    static Constants constant;
    public String phoneNo = "+919941191299";
    private LocationListener listener;

    public Location_Constants(Context context) {
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

    protected Location getMylocation() {
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
            LocationProvider provider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
            implement_location_request(provider.getName());
        }
    }

    private void implement_location_request(String provider) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.requestLocationUpdates(provider, 1000 * 60, 500, get_listener());

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




    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}