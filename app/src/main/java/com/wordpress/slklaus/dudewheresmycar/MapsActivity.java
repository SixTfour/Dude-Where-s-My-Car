package com.wordpress.slklaus.dudewheresmycar;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final String TAG = MapsActivity.class.getSimpleName();
    List<GoogleMap> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if(!mGoogleApiClient.isConnected()) {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Please turn on phone Location Service")
                    .create();
        }

        Typeface lobster = Typeface.createFromAsset(getAssets(), "LobsterTwo-BoldItalic.ttf");
        TextView title = (TextView)findViewById(R.id.Title);
        title.setTypeface(lobster);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
        if(!mGoogleApiClient.isConnected()) {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            myAlert.setMessage("Please turn on phone Location Service")
                    .create();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        } else {
            handleNewLocation(location);
        }
    }

    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Create the Location Request object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)       //10 milliseconds
                .setFastestInterval(1000); //1 millisecond
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
    }

    private void setUpMapIfNeeded() {
        //Check to see if the map has been created
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //Shows you location as a marker
        mMap.setMyLocationEnabled(true);
        //Sets map type to terrain
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void dropPin(View v) {
        switch (v.getId()) {
            case R.id.button:
                Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                double currentLatitude = mLocation.getLatitude();
                double currentLongitude = mLocation.getLongitude();
                //Creates LatLng from you current position by combining currentLatitude and currentLongitude
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                //Sets the Marker Location, color of the marker, and the title of the marker
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("My Car");
                //Limits the number of markers to one
                if (arr.size() < 1) {
                    //Adds the marker to the map
                    mMap.addMarker(options.position(latLng));
                    //Moves camera to position of the car with animation
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(currentLatitude, currentLongitude)).zoom(18).build();
                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                    //Adds mMap to the arr array
                    arr.add(0, mMap);
                }

                break;
        }
    }

    public void deletePin(View v) {
        //Deletes the Marker off of the screen by clearing the map and the array
        switch (v.getId()) {
            case R.id.button2:
                mMap.clear();
                arr.clear();
                break;
        }
    }
}