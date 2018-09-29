package kz.topsecurity.top_signal.service.trackingService.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;


import kz.topsecurity.top_signal.service.trackingService.listenerImpl.NewVolumeReceiver;
import kz.topsecurity.top_signal.service.trackingService.listenerImpl.VolumeReceiver;

public class VolumeListenerManager {

    BroadcastReceiver volumeReceiver;
    boolean mIsActive = false;


    public VolumeListenerManager() {
    }

    public void setupVolumeReceiver(Context context){
        volumeReceiver = new NewVolumeReceiver();
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.android.music.musicservicecommand.togglepause");
        iF.addAction("com.android.music.musicservicecommand.pause");
        iF.addAction("com.android.music.musicservicecommand.previous");
        iF.addAction("com.android.music.musicservicecommand.next");
        iF.addAction("com.android.music.musicservicecommand");
        iF.addAction("com.android.music.togglepause");
        iF.addAction("com.android.music.pause");
        iF.addAction("com.android.music.previous");
        iF.addAction("com.android.music.next");
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.htc.music.metachanged");
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");

        iF.addAction("android.intent.action.CAMERA_BUTTON");
        iF.addAction("android.intent.action.USER_PRESENT");
        iF.addAction("android.media.RINGER_MODE_CHANGED");
        iF.addAction("android.intent.action.SCREEN_OFF");
        iF.addAction("android.intent.action.SCREEN_ON");
        iF.addAction("android.media.VOLUME_CHANGED_ACTION");
        context.registerReceiver(volumeReceiver,iF);
        mIsActive = true;
    }

    public void stop(Context context){
        if(volumeReceiver!=null)
            context.unregisterReceiver(volumeReceiver);
        mIsActive = false;
    }

    public boolean isActive(){
        return mIsActive;
    }
}
