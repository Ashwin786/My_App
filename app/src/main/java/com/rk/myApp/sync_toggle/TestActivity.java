package com.rk.myApp.sync_toggle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.rk.myApp.R;


public class TestActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//        intent.setData(Uri.parse("package:" + getPackageName()));
//        startActivityForResult(intent, 1);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_SETTINGS}, 0);
//        if (get_WriteSettingPermission(this)) {
//            increase_brightness(50);
//        }
//        switch_on_hotspot();
    }

    private void increase_brightness(int brightness) {
        //constrain the value of brightness
        if(brightness < 0)
            brightness = 0;
        else if(brightness > 255)
            brightness = 255;

        ContentResolver cResolver = this.getApplicationContext().getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private void launch_scanner() {
//        Intent i = new Intent(TestActivity.this,QrCodeActivity.class);
//        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }
    public static boolean youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
            return true;
        }  else {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestCode", ""+requestCode);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                        Log.e("QR CODE result", result);
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TestActivity.class.getSimpleName(), "Scan cancelled");
                }

                break;

            default:
                break;
        }
    }
}
