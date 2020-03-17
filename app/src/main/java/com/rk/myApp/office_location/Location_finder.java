package com.rk.myApp.office_location;

import android.content.Context;
import android.location.Location;

import com.rk.myApp.sync_toggle.Constants;

/**
 * Created by user1 on 20/3/19.
 */

public class Location_finder {
    private final Context context;
    private float maxGeoFencingRange = 200;
    private String office_latitude="13.0816385";
    private String office_longitude="80.2650648";

    public Location_finder(Context context) {
        this.context = context;
        check_location();
    }

    private void check_location() {
        Location locationB = Constants.getInstance(context).getMylocation();
        if (locationB != null) {
            Location office_location = new Location("point A");
            office_location.setLatitude(Float.parseFloat(office_latitude));
            office_location.setLongitude(Float.parseFloat(office_longitude));
            float distance = office_location.distanceTo(locationB);
//            if (distance > maxGeoFencingRange)

        }

    }

}
