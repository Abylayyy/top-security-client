package kz.topsecurity.client.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;


import kz.topsecurity.client.service.trackingService.interfaces.BatteryLevelListener;

public class PowerConnectionReceiver extends BroadcastReceiver {

    BatteryLevelListener listener;
    public PowerConnectionReceiver(){super();};
    public PowerConnectionReceiver(BatteryLevelListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null)
            listener.onBatteryLevelChanged(evaluateBatteryLevel(intent));
    }

    int evaluateBatteryLevel(Intent batteryStatus) {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if(level != -1 && scale != -1)
            return (level* 100 /  scale) ;
        return 1;
    }
}