package com.rk.myApp.sync_toggle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2/6/15.
 * Checking for network connection
 */
public class NetworkConnection {

//    private static Context context; //Context for activity
//
//    public NetworkConnection() {
//        this.context = context;
//    }

    //If network available it returns true else false
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
