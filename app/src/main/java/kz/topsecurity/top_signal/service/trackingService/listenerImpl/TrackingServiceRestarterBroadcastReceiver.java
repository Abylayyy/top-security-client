package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.helper.SharedPreferencesManager;
import kz.topsecurity.top_signal.service.trackingService.TrackingService;

public class TrackingServiceRestarterBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = TrackingServiceRestarterBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Restart is called");

        if(SharedPreferencesManager.getTrackingServiceActiveState(context)){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(new Intent(context, TrackingService.class));
                intent.setAction(Constants.RESTART_FOREGROUND_SERVICE);
            }
            else{
                context.startForegroundService(new Intent(context, TrackingService.class));
            }
        }
    }
}

