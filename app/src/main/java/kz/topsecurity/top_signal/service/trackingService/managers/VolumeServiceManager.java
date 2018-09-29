package kz.topsecurity.top_signal.service.trackingService.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import kz.topsecurity.top_signal.service.trackingService.listenerImpl.VolumeServiceReceiver;
import kz.topsecurity.top_signal.service.trackingService.volumeService.VolumeService;

public class VolumeServiceManager {

    BroadcastReceiver volumeServiceReceiver;
    boolean mIsActive = false;

    public void setupVolumeReceiver(Context context){
        volumeServiceReceiver = new VolumeServiceReceiver();
        IntentFilter iF = new IntentFilter(VolumeService.FILTER_ACTION_SERVICE_BROADCAST);
        context.registerReceiver(volumeServiceReceiver,iF);
        mIsActive = true;
    }

    public void stop(Context context){
        if(volumeServiceReceiver!=null)
            context.unregisterReceiver(volumeServiceReceiver);
        mIsActive = false;
    }

    public boolean isActive(){
        return mIsActive;
    }

}
