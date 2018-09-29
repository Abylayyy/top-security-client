package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


import kz.topsecurity.top_signal.application.TopSignalApplication;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.helper.SharedPreferencesManager;
import kz.topsecurity.top_signal.service.trackingService.TrackingService;
import kz.topsecurity.top_signal.service.trackingService.volumeService.VolumeService;

public class VolumeServiceReceiver extends BroadcastReceiver {

    private static final int LONG_CLICK_TIME_OUT = 5*1000;
    private static final String TAG = VolumeService.class.getSimpleName();
//    private static long lastClickTime = 0;
    private Context mContext;
    Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        int actionType = intent.getIntExtra(VolumeService.EXTRA_VOLUME_ACTION_TYPE,-1);
        switch (actionType){
            case VolumeService.ACTION_KEY_DOWN_VOLUME_DOWN:{
                Log.d(TAG,"VOLUME ACTION_KEY_DOWN_VOLUME_DOWN");
                processClick(VolumeService.ACTION_KEY_DOWN_VOLUME_DOWN);
                break;
            }
            case VolumeService.ACTION_KEY_DOWN_VOLUME_UP:{
                Log.d(TAG,"VOLUME ACTION_KEY_DOWN_VOLUME_UP");
                processClick(VolumeService.ACTION_KEY_UP_VOLUME_UP);
                break;
            }
            case VolumeService.ACTION_KEY_UP_VOLUME_DOWN:{
                Log.d(TAG,"VOLUME ACTION_KEY_UP_VOLUME_DOWN");
                checkClick();
                break;
            }
            case VolumeService.ACTION_KEY_UP_VOLUME_UP:{
                Log.d(TAG,"VOLUME ACTION_KEY_UP_VOLUME_UP");
                checkClick();
                break;
            }
        }
    }

    private void checkClick() {
//        long currentTimeMillis = System.currentTimeMillis();
//        if(currentTimeMillis-lastClickTime<LONG_CLICK_TIME_OUT && handler!=null){
//            handler.removeCallbacks(clickEndCallback);
//        }
        if(handler!=null){
            handler.removeCallbacks(clickEndCallback);
        }
    }

    private void processClick(int type) {
        if(handler!=null)
            handler.removeCallbacks(clickEndCallback);
        if(type != (SharedPreferencesManager.getVolumeDirection(mContext) ? VolumeService.ACTION_KEY_UP_VOLUME_UP :  VolumeService.ACTION_KEY_UP_VOLUME_DOWN )){
            return;
        }
        handler = new Handler();
        handler.postDelayed(clickEndCallback,LONG_CLICK_TIME_OUT);
//        lastClickTime = System.currentTimeMillis();
    }

    final Runnable clickEndCallback = new Runnable() {
        @Override
        public void run() {
            if(handler!=null){
                handler.removeCallbacks(clickEndCallback);
            }
            callAlert();
        }
    };

    private void callAlert() {
        if(!SharedPreferencesManager.getAlertActive(TopSignalApplication.getInstance().getApplicationContext())){
            Log.d(TAG,"CALL ALERT");
            Intent intent = new Intent(mContext, TrackingService.class);
            intent.setAction(Constants.ALERT_ACTION);
            startServiceWithCheck(intent);
        }
    }

    private void startServiceWithCheck(Intent intent){
        assert intent.getComponent().getClass()==null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mContext.startService(intent);
        }
        else{
            mContext.startForegroundService(intent);
        }
    }

}
