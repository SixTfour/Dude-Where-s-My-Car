package com.wordpress.slklaus.dudewheresmycar;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by sheld_000 on 11/14/2015.
 */

// Will be used later to draw lines to point from location
public interface RoutingListener {
    void onRoutingSuccess(PolylineOptions mPolyOptions);
}
