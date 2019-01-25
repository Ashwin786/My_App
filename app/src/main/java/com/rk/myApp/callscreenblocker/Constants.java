package com.rk.myApp.callscreenblocker;

import android.content.Context;

/**
 * Created by user1 on 1/8/18.
 */
public class Constants {
    static Context context;
    static Constants constant;

    public Constants(Context context) {
        this.context = context;
    }

    public static Constants getInstance(Context con) {
        context = con;
        if (constant == null)
            constant = new Constants(context);

        return constant;
    }


}
