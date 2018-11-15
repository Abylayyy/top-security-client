package kz.topsecurity.client.service.trackingService.managers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import kz.topsecurity.client.service.trackingService.interfaces.BatteryLevelListener;
import kz.topsecurity.client.service.trackingService.listenerImpl.PowerConnectionReceiver;

public class BatteryListenerManager implements BatteryLevelListener {

    private static final String TAG = BatteryListenerManager.class.getSimpleName();
    private PowerConnectionReceiver mPowerConnectionReceiver;

    private int batteryPct = 0;
    private boolean mIsActive = false;

    public int getBatteryPct() {
        return batteryPct;
    }

    public void setupBatteryManager(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mPowerConnectionReceiver = new PowerConnectionReceiver(this);
        context.registerReceiver(mPowerConnectionReceiver, ifilter);
        // Are we charging / charged?
        mIsActive = true;
        Log.d(TAG, "BATTERY TRACKER CONNECTED");
    }

    @Override
    public void onBatteryLevelChanged(int batteryPercentage) {
        if(batteryPercentage>0)
            batteryPct = batteryPercentage;
    }

    public void stop(Context context){
        if(mPowerConnectionReceiver!=null)
            context.unregisterReceiver(mPowerConnectionReceiver);
        mIsActive = false;
    }

    public boolean isActive(){
        if(mPowerConnectionReceiver == null)
            return false;
        return mIsActive;
    }
}
