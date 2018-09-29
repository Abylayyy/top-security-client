package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import kz.topsecurity.top_signal.service.trackingService.TrackingService;
import kz.topsecurity.top_signal.service.trackingService.interfaces.TrackingServiceBroadcastReceiverListener;
import kz.topsecurity.top_signal.service.trackingService.managers.DataProvider;
import kz.topsecurity.top_signal.service.trackingService.model.DeviceData;

public class TrackingServiceBroadcastReceiver extends BroadcastReceiver {

    TrackingServiceBroadcastReceiverListener mListener;
    DeviceData data = new DeviceData();
    String TAG = TrackingServiceBroadcastReceiver.class.getSimpleName();

    public TrackingServiceBroadcastReceiver(TrackingServiceBroadcastReceiverListener listener){
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Broadcast receiver fired");
        String latitude = intent.getStringExtra(DataProvider.EXTRA_LATITUDE);
        String longitude = intent.getStringExtra(DataProvider.EXTRA_LONGITUDE);
        String altitude = intent.getStringExtra(DataProvider.EXTRA_ALTITUDE);
        String barometricAltitude = intent.getStringExtra(DataProvider.EXTRA_BAROMETRIC_ALTITUDE);
        String battery = intent.getStringExtra(DataProvider.EXTRA_BATTERY_PERCENTAGE);
        String signalLevel = intent.getStringExtra(DataProvider.EXTRA_SIGNAL_LEVEL);
        String streetLocation = intent.getStringExtra(DataProvider.EXTRA_STREET);
        boolean isAlertActive = intent.getBooleanExtra(DataProvider.EXTRA_ALERT,false);
        boolean isGpsActive = intent.getBooleanExtra(DataProvider.EXTRA_GPS,false);

        data.setLat(Double.parseDouble(latitude));
        data.setLng(Double.parseDouble(longitude));
        data.setAlt(Double.parseDouble(altitude));
        data.setAltBarometer(Double.parseDouble(barometricAltitude));
        data.setCharge(Integer.parseInt(battery));
        data.setSignalStrength(1); //signalLevel
        data.setStreetAddress(streetLocation);
        data.setIs_gps_active(isGpsActive);
        data.setIs_urgent(isAlertActive);
        long unixTime = System.currentTimeMillis() / 1000L;
        data.setTimestamp((int) unixTime);

        mListener.onDataReceived(data);
    }
}
