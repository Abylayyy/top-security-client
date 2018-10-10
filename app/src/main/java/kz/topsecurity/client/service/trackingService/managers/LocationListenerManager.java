package kz.topsecurity.client.service.trackingService.managers;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;

import java.io.IOException;
import java.util.List;

import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;

public class LocationListenerManager {

    private static final String TAG = LocationListenerManager.class.getSimpleName();

    private static final int FROM_GPS = 428;
    private static final int FROM_NETWORK = 146;

    private Double mAltitude = 0.0;
    private Double mLatitude  = 0.0;
    private Double mLongitude  = 0.0;
    private String mCurrentLocationStreet = "";

    public Double getAltitude() {
        return mAltitude;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public String getCurrentLocationStreet() {
        return mCurrentLocationStreet;
    }

    LocationListener mLocationListener;

    Geocoder geocoder ;
    LocationManager mLocationManager;
    LocationRequest mLocationRequest = new LocationRequest();
    FusedLocationProviderClient mFusedLocationProviderClient;

    boolean mIsActive = false;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation(), FROM_GPS);
        }
    };

    public LocationListenerManager(LocationListener locationListener){
        this.mLocationListener = locationListener;
    }

    public void setupLocationReceiver(LocationManager locationManager, FusedLocationProviderClient client, Geocoder geocoder) {
        this.geocoder = geocoder;
        mLocationManager = locationManager;

        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        //other values are PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER
        mLocationRequest.setPriority(priority);
        Log.d(TAG, "LOCATION TRACKER CONNECTING");
        mFusedLocationProviderClient= client;
        checkSettings();
        mIsActive = true;
    }

    private void checkSettings() {
        mLocationListener.onLocationRequestCheck(mLocationRequest);
    }

    @SuppressLint("MissingPermission")
    private void connectToFusedLocation(FusedLocationProviderClient client) {
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        getLastLocation();
        Log.d(TAG, "Connected to Google API");
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        onLocationChanged(location,FROM_GPS);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                    e.printStackTrace();
                });
    }


    public void onLocationChanged(Location location, int type) {
        if (location != null) {
            Log.d(TAG, "== location != null");
            onLocationChanged(location.getLatitude(),location.getLongitude(), location.getAltitude(), type , location.getAccuracy());
        }
    }

    public void onLocationChanged(double lat , double lng, double alt, int type , double acc){
        String from = "";
        if(type == FROM_GPS)
            from = "GPS";
        else if(type == FROM_NETWORK)
            from = "NETWORK";
        Log.d(TAG,"FROM: "+from);
        Log.d(TAG, "Location changed");

        if(alt >0.0){
            mAltitude = alt;
        }
        if(acc<50) {
            mLatitude = lat;
            mLongitude = lng;
            getAddressFromLocation(mLatitude, mLongitude);
        }
        mLocationListener.onLocationChanged(mLatitude,mLongitude,mAltitude,mCurrentLocationStreet);
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        //TODO: LANG dynamic
        if(geocoder==null)
            return;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                if(fetchedAddress.getMaxAddressLineIndex()>0) {
                    for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                    }
                }
                else{
                    strAddress.append(fetchedAddress.getAddressLine(0));
                }
                mCurrentLocationStreet = strAddress.toString();
                String street = fetchedAddress.getThoroughfare();
                Log.e(TAG,String.format("Current address is %s",street));
            } else {
                Log.e(TAG,"Searching address");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Unable to get address");
        }
    }


    public boolean isGpsEnabled() {
        String provider = Settings.Secure.getString(TopSecurityClientApplication.getInstance().getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        boolean isGpsEnabled = provider.contains("gps");

        return isGpsEnabled || mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void stop(){
        if(mFusedLocationProviderClient!=null)
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mIsActive = false;
    }

    public boolean isActive(){
        return mIsActive;
    }

    public void startLocationUpdates() {
        connectToFusedLocation(mFusedLocationProviderClient);
    }
}
