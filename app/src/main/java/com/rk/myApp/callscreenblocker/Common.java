package com.rk.myApp.callscreenblocker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rk.myApp.R;


/**
 * Created by user1 on 31/7/18.
 */
public class Common {
        public static int _interval_time = 1000 * 60 * 30;
//        public static int _interval_time = 1000 * 60;
    private String my_phoneId = "a654034904db8133";
//    public static int _interval_time = 1000 * 10 * 1;
    private static Context context;
    private static Common common;
    private final ImageView btnTag;
    private WindowManager wm;
    private RelativeLayout relativeLayout;
    WindowManager.LayoutParams params;
    private boolean lock;
    public static boolean alarm = true;

    protected void block_ui() {
        //add button to the blocker
        lock = true;
        try {
            wm.addView(relativeLayout, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void clear() {
        if (lock) {
//            wm.removeViewImmediate(relativeLayout);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.height = 200;
            params.width = 200;
            wm.updateViewLayout(relativeLayout, params);
            btnTag.setBackground(context.getDrawable(R.drawable.unlock));
            lock = false;
        } else {
//            wm.addView(relativeLayout, params);
            params.gravity = Gravity.TOP;
            params.height = wm.getDefaultDisplay().getHeight() - 500;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            wm.updateViewLayout(relativeLayout, params);
            btnTag.setBackground(context.getDrawable(R.drawable.lock));
            lock = true;
        }
    }

    public Common() {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
//        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
//        params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSPARENT);
////        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = wm.getDefaultDisplay().getHeight() - 500;
//        Log.e("height", String.valueOf();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.blocker, null);
        relativeLayout = (RelativeLayout) parent.findViewById(R.id.relative);
        btnTag = (ImageView) parent.findViewById(R.id.btn_clear);
        btnTag.setBackground(context.getDrawable(R.drawable.lock));
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
    }

    public static Common getInstance(Context mcontext) {
        context = mcontext;
        if (common == null)
            common = new Common();
        return common;
    }

    public void cancel() {
        wm.removeViewImmediate(relativeLayout);
        lock = false;
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

    private void openAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public boolean checkIsMyPhone() {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId.equals(my_phoneId))
            return true;
        else
            return false;
    }
}
