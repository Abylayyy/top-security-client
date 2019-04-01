package kz.topsecurity.client.service.trackingService.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;

public class LocationListenerManager {

    private static final String TAG = LocationListenerManager.class.getSimpleName();

    private static final int FROM_GPS = 428;
    private static final int FROM_NETWORK = 146;
    private static final int FROM_KALMAN = 417;

    List<Location> storedLocationData;
    /**
     * Request location updates with the highest possible frequency on gps.
     * Typically, this means one update per second for gps.
     */

    private static final long GPS_TIME = 5000;

    /**
     * For the network provider, which gives locations with less accuracy (less reliable),
     * request updates every 5 seconds.
     */
    private static final long NET_TIME = 5000;

    /**
     * For the filter-time argument we use a "real" value: the predictions are triggered by a timer.
     * Lets say we want 5 updates (estimates) per second = update each 200 millis.
     */
    private static final long FILTER_TIME = 1000;

    private Double mLastAltitude = 0.0;
    private Double mLastLatitude = 0.0;
    private Double mLastLongitude = 0.0;
    private String mLastLocationStreet = "";

    public Double getAltitude() {
        return mLastAltitude;
    }

    public Double getLatitude() {
        return mLastLatitude;
    }

    public Double getLongitude() {
        return mLastLongitude;
    }

    public String getCurrentLocationStreet() {
        return mLastLocationStreet;
    }

    LocationListener mLocationListener;
    Geocoder geocoder ;
    LocationManager mLocationManager;
    LocationRequest mLocationRequest = new LocationRequest();
    FusedLocationProviderClient mFusedLocationProviderClient;
    // private KalmanLocationManager mKalmanLocationManager;

    boolean mIsActive = false;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation(), FROM_GPS);
        }
    };

    public LocationListenerManager( LocationListener locationListener){
        this.mLocationListener = locationListener;
    }

    public void setupLocationReceiver(Context context,LocationManager locationManager, FusedLocationProviderClient client, Geocoder geocoder) {
        //mKalmanLocationManager = new KalmanLocationManager(context);
        this.geocoder = geocoder;
        mLocationManager = locationManager;
//        //mKalmanLocationManager.requestLocationUpdates(
//                KalmanLocationManager.UseProvider.GPS_AND_NET, FILTER_TIME, GPS_TIME, NET_TIME, new android.location.LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        if(location!=null)
//                            LocationListenerManager.this.onLocationChanged(location.getLatitude(),location.getLongitude(), location.getAltitude(), FROM_KALMAN , location.getAccuracy());
//                    }
//
//                    @Override
//                    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String s) {
//
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String s) {
//
//                    }
//                }, true);

        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);

//        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        //other values are PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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



    public void onLocationChanged(Location location, int type ){
        if (location == null)
            return;

        Double lat,lng,alt;
        float acc;
//            Log.d(TAG, "== location != null");
//            if(hcKalmanFilter == null){
//                if(location.getAccuracy()<50)
//                    hcKalmanFilter = new HCKalmanAlgorithm( location);
//            }
//            else{
//                CLLocation clLocation = hcKalmanFilter.processState(location);
//                if(clLocation.getLongitude()!=null && clLocation.getLongitude()!=null && clLocation.getAltitude()!=null &&  clLocation.getAccuracy()!=null )
//                    onLocationChanged(clLocation.getLatitude(),clLocation.getLongitude(), clLocation.getAltitude(), 400 , clLocation.getAccuracy());
//            }
        if(storedLocationData == null){
            storedLocationData = new ArrayList<>();
        }
        storedLocationData.add(location);
        lat = location.getLatitude();
        lng = location.getLongitude();
        alt = location.getAltitude();
        acc = location.getAccuracy();

        if(storedLocationData == null){
            storedLocationData = new ArrayList<>();
        }
        storedLocationData.add(location);
        Log.d(TAG, "Location changed");

        if(alt >0.0){
            mLastAltitude = alt;
        }
        if((mLastLongitude == 0 || mLastLatitude == 0) && acc<120 ){
            mLastLatitude = lat;
            mLastLongitude = lng;
//            getAddressFromLocation(mLastLatitude,mLastLongitude);
        }
        if(acc<50) {
            mLastLatitude = lat;
            mLastLongitude = lng;
            Log.d(TAG, " ACCURACY lower 50");
            getAddressFromLocation(mLastLatitude, mLastLongitude);
        }
        else {
            Location loc = getMostAccurateLocation(storedLocationData);
            mLastLatitude = loc.getLatitude();
            mLastLongitude = loc.getLongitude();
        }
        mLocationListener.onLocationChanged(mLastLatitude, mLastLongitude, mLastAltitude, mLastLocationStreet);
    }

    private Location getMostAccurateLocation(List<Location> storedLocationData) {
        Location mostAccurate = storedLocationData.get(0);
        for (Location loc: storedLocationData) {
            if(loc.getAccuracy()<mostAccurate.getAccuracy() ){
                mostAccurate = loc;
            }
        }
        return mostAccurate;
    }

    long lastAddressRequestInMilis = 0;

    private void getAddressFromLocation(double latitude, double longitude) {
        if(lastAddressRequestInMilis == 0)
            lastAddressRequestInMilis = System.currentTimeMillis();
        else if(System.currentTimeMillis() - lastAddressRequestInMilis<30*1000)
            return;
        lastAddressRequestInMilis = System.currentTimeMillis();
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
                mLastLocationStreet = strAddress.toString();
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
        SharedPreferencesManager.setGpsStatus(TopSecurityClientApplication.getInstance(),isGpsEnabled);
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


    public void clearStoredData() {

        if(storedLocationData!=null)
            storedLocationData.clear();
    }
}