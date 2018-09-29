package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class NewVolumeReceiver extends BroadcastReceiver {
    private static final String TAG = NewVolumeReceiver.class.getSimpleName();
    public static boolean sIsReceived; // this is made true and false after each timer clock
    public static Timer sTimer=null;
    public static int i;
    final int MAX_ITERATION=15;
    public static boolean sIsAppWorkFinished=true;

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        sIsReceived=true; // Make this true whenever isReceived called
        if(sTimer==null && sIsAppWorkFinished){
            sTimer=new Timer();
            sTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(sIsReceived){  // if its true it means user is still pressing the button
                        i++;
                    }else{ //in this case user must has released the button so we have to reset the timer
                        cancel();
                        sTimer.cancel();
                        sTimer.purge();
                        sTimer=null;
                        i=0;
                    }
                    if(i>=MAX_ITERATION){ // In this case we had successfully detected the long press event
                        cancel();
                        sTimer.cancel();
                        sTimer.purge();
                        sTimer=null;
                        i=0;
                        callAlert();
                        //it is called after 3 seconds
                    }
                    sIsReceived=false; //Make this false every time a timer iterates
                }
            }, 0, 200);
        }
    }

    private void callAlert() {
        Log.i(TAG, "Volume Tracker fired");
    }
}