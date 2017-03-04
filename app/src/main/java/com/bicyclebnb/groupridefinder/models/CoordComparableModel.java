package com.bicyclebnb.groupridefinder.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by admin on 2/21/17.
 */

public abstract class CoordComparableModel {
    public LatLng coordinate = null;
    public abstract String coordTitle();
    public abstract String coordSnippet();


    public float getProximityInMeter(Location locFrom) {
        if(coordinate == null || locFrom == null)
        {
            return Float.MAX_VALUE;
        }
        Location locTo = new Location("");
        locTo.setLatitude(coordinate.latitude);
        locTo.setLongitude(coordinate.longitude);

        return locFrom.distanceTo(locTo);
    }

    public String getProximityString(Location locFrom) {
        float proximityInMeter = getProximityInMeter(locFrom);

        if(proximityInMeter == Float.MAX_VALUE) {
            return "N/A";
        } else {
            double proximityInMI = proximityInMeter * 0.621371 / 1000;
            return String.format("%.2f mi", proximityInMI);
        }
    }
}
