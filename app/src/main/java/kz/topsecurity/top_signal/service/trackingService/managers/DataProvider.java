package kz.topsecurity.top_signal.service.trackingService.managers;

import android.content.Intent;
import android.util.Log;

import kz.topsecurity.top_signal.service.trackingService.TrackingService;

public class DataProvider {

    private static final String TAG = DataProvider.class.getSimpleName();

    public static final String FILTER_ACTION_LOCATION_BROADCAST = TrackingService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_ALTITUDE = "extra_altitude";
    public static final String EXTRA_BATTERY_PERCENTAGE = "extra_battery";
    public static final String EXTRA_SIGNAL_LEVEL = "extra_signal_level";
    public static final String EXTRA_BAROMETRIC_ALTITUDE = "extra_barometric_altitude";
    public static final String EXTRA_STREET = "extra_location_street";
    public static final String EXTRA_ALERT = "extra_alert_is_possible";
    public static final String EXTRA_ALERT_LEFT_TIME = "extra_alert_left_time";
    public static final String EXTRA_GPS = "extra_gps_is_active";

    boolean is_GPS_enabled = false;

    public static Intent prepareIntentData(double mLatitude, double mLongitude, double mAltitude, String currentLocationStreet,
                                    double mPressureAltitudeValue, int batteryPct, int mSignalStrength,
                                    boolean isAlertActive , boolean is_GPS_enabled) {
        Log.d(TAG, "Sending info...");

        String lat =  String.valueOf(mLatitude);
        String lng =  String.valueOf(mLongitude);
        String alt =  String.valueOf(mAltitude);
        String streetLocation =  String.valueOf(currentLocationStreet);
        String barometricAltitude =  String.valueOf(mPressureAltitudeValue);
        String batteryPercentage =  String.valueOf(batteryPct);
        String signalLevel =  String.valueOf(mSignalStrength);

        Intent intent = new Intent(FILTER_ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        intent.putExtra(EXTRA_ALTITUDE, alt);
        intent.putExtra(EXTRA_STREET,streetLocation);
        intent.putExtra(EXTRA_BAROMETRIC_ALTITUDE,barometricAltitude);
        intent.putExtra(EXTRA_BATTERY_PERCENTAGE,batteryPercentage);
        intent.putExtra(EXTRA_SIGNAL_LEVEL,signalLevel);
        intent.putExtra(EXTRA_ALERT,isAlertActive);
        intent.putExtra(EXTRA_GPS,is_GPS_enabled);

        return intent;
    }

}
