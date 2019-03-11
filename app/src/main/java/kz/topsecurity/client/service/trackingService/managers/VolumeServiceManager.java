package kz.topsecurity.client.service.trackingService.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import kz.topsecurity.client.service.trackingService.listenerImpl.VolumeServiceReceiver;
import kz.topsecurity.client.service.trackingService.volumeService.VolumeService;

public class VolumeServiceManager {

    BroadcastReceiver volumeServiceReceiver;
    boolean mIsActive = false;

    public void setupVolumeReceiver(Context context){
        if(mIsActive && volumeServiceReceiver!=null )
            return;
        mIsActive = true;
        volumeServiceReceiver = new VolumeServiceReceiver();
        IntentFilter iF = new IntentFilter(VolumeService.FILTER_ACTION_SERVICE_BROADCAST);
        context.registerReceiver(volumeServiceReceiver,iF);
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
