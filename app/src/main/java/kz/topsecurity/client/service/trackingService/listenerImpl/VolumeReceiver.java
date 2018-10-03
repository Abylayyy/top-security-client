package kz.topsecurity.client.service.trackingService.listenerImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.locks.ReentrantLock;

import kz.topsecurity.client.R;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.service.trackingService.managers.DataProvider;

public class VolumeReceiver extends BroadcastReceiver {

    private static final String TAG = VolumeReceiver.class.getSimpleName();
    int MAX_CLICK = 3;
    int click_rate = 0;
    private static final int THRESHOLD = 800;
    long last_click_time = 0;
    private ReentrantLock sleepLocker = new ReentrantLock();
    private ReentrantLock locker = new ReentrantLock();
    private boolean sleepRunning = false;

    private static final int MAX_VOLUME_STATUS = 540;
    private static final int MIN_VOLUME_STATUS = 85;
    private static final int VOLUME_DOWN_STATUS = 277;
    private static final int VOLUME_UP_STATUS = 746;

    boolean isAlertActive = false;
    boolean isReceiverRegistered = false;
    int last_status = 0;
    int last_new_volume = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        locker.lock();
        MAX_CLICK = Integer.parseInt(Constants.click_type_rate[SharedPreferencesManager.getVolumeButtonClickRate(getApplicationContext())]);

        if(!isReceiverRegistered){
            isReceiverRegistered = true;
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            boolean alertStatus = intent.getBooleanExtra(DataProvider.EXTRA_ALERT,false);
                            if(!isAlertActive && alertStatus){
                                isAlertCalled = false;
                            }
                            isAlertActive = alertStatus;
                        }
                    }, new IntentFilter(DataProvider.FILTER_ACTION_LOCATION_BROADCAST));
        }

        if(Constants.is_service_sending_alert())
            return;
        //TODO: MAKE CHECK TO ALL CASES
        if(intent==null||intent.getAction()==null) {
            locker.unlock();
            return;
        }

        if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            //TODO: FETCH FROM SETTINGS
            boolean allowService = SharedPreferencesManager.getVolumeTrackingServiceActiveState(getApplicationContext());
            if(allowService) {
                StartStandbyService();
            }
            else {
                StopStandbyService();
            }
            return;
        }
        else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
            StartStandbyService();
            return;
        }
        else if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
            StopStandbyService();
            return;
        }
        else if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
            AudioManager am = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
//            lastChangeTime = System.currentTimeMillis();
//            previousRingerMode = currentRingerMode;
//            currentRingerMode = am.getRingerMode();
//            if (previousRingerMode == 2) {
//                if (currentRingerMode == 0) {
//                    strangRingerMode = 1;
//                } else {
//                    strangRingerMode = 0;
//                }
//            }
//            Log.i(TAG, "Ring mode changed from [" + previousRingerMode + "] to [" + currentRingerMode + "]");
            Log.i(TAG, "Ring mode changed from [ ] to [ ]");
        }
        else if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
            int type = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
            int maxVolume = audioManager.getStreamMaxVolume(type);

            int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
            int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

            if(last_new_volume==-1){
                last_new_volume = newVolume;
            }
            else if(last_new_volume == newVolume){
                locker.unlock();
                return;
            }
            else{
                last_new_volume = newVolume;
            }

            if(newVolume == oldVolume && newVolume!=0 &&  newVolume!=maxVolume) {
                locker.unlock();
                return;
            }

            if (newVolume == oldVolume) {
                if(last_status == 0) {
                    if(newVolume == 0)
                        last_status = MIN_VOLUME_STATUS;
                    if(maxVolume == newVolume)
                        last_status = MAX_VOLUME_STATUS;
                }
                else if(last_status == MIN_VOLUME_STATUS ||  last_status == VOLUME_DOWN_STATUS)
                {
                    proccessClick();
                }
                else if(last_status == MAX_VOLUME_STATUS || last_status == VOLUME_UP_STATUS)
                {
                    proccessClick();
                }
                else{
                    last_status = 0;
                }
            }
            else{
                if(newVolume < oldVolume)
                {
                    if(last_status == 0){
                        last_status = VOLUME_DOWN_STATUS;
                    }
                    else if(last_status == VOLUME_DOWN_STATUS){
                        proccessClick();
                    }
                    else{
                        last_status = 0;
                    }
                    Log.d(TAG,"Volume Down");
                }
                else if(newVolume> oldVolume)
                {
                    if(last_status == 0) {
                        last_status = VOLUME_UP_STATUS;
                    }
                    else if(last_status == VOLUME_UP_STATUS){
                        proccessClick();
                    }
                    else{
                        last_status = 0;
                    }
                    Log.d(TAG,"Volume Up");
                }
            }
        }

        if (locker.isHeldByCurrentThread()) {
            locker.unlock();
        }
    }

    boolean checkStatus(){
        boolean direction = SharedPreferencesManager.getVolumeDirection(getApplicationContext());
        return (last_status == MAX_VOLUME_STATUS && direction) ||
                (last_status == MIN_VOLUME_STATUS && !direction) ||
                (last_status == VOLUME_DOWN_STATUS && !direction ) ||
                (last_status == VOLUME_UP_STATUS && direction);

    }

    void proccessClick(){
        if(!checkStatus()){
            click_rate =0;
            last_status = 0;
            locker.unlock();
            return;
        }
        Log.d(TAG,"Volume changed");

        long current_time = System.currentTimeMillis() ;
        if(current_time - last_click_time > THRESHOLD){
            //If new action is not in combination then consider it as new click
            click_rate =0;
            last_status = 0;
//            Log.d(TAG,"Clearing clicks");
        }
        else if(click_rate == MAX_CLICK-1){
//            Log.d(TAG,"Click rate reach max value");
            click_rate = 0;
            callAlert();
            last_status = 0;
        }

        click_rate++;
        last_click_time = current_time;
        Log.d(TAG,"CLICK RATE  "+ click_rate);
        Toast.makeText(getApplicationContext(),"Click rate : " + click_rate,Toast.LENGTH_SHORT).show();
    }

    boolean delayActive = false;
    boolean handleSuccess = false;
    void handleDelay(){
        if(delayActive){
            return;
        }
        delayActive = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(handleSuccess)
                    return;
                delayActive = false;
                click_rate = 0;
                last_click_time = 0;
                last_status = 0;
            }
        },THRESHOLD);
    }

    boolean isScreenOn(){
        return ((PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private Context getApplicationContext(){
        return TopSecurityClientApplication.getInstance();
    }

    private void StartStandbyService() {
        this.sleepLocker.lock();
        Log.d(TAG, "StartStadbyService.");
        if (!this.sleepRunning) {
            Log.d(TAG, "Starting sleep thread.");
            this.sleepRunning = true;
            sleepSound();
        }
        this.sleepLocker.unlock();
    }

    private void StopStandbyService() {
        this.sleepLocker.lock();
        Log.d(TAG, "StopStandbyService");
        this.sleepRunning = false;
        stopSleepSound();
        this.sleepLocker.unlock();
    }

    private MediaPlayer sleepPlayer = null;
    MediaPlayer.OnCompletionListener sleepSoundListener = new SleepSoundListener();
    class SleepSoundListener implements MediaPlayer.OnCompletionListener {
        SleepSoundListener() {
        }

        public void onCompletion(MediaPlayer arg0) {
            Log.d(TAG, "Sleep sound finished.");
            try {
                sleepPlayer.release();
                sleepPlayer = null;
            } catch (Exception e) {

            }
        }
    }

    //THIS is fucked up
    public void sleepSound() {
        Log.d(TAG, "Sleep sound started.");
        try {
            sleepPlayer = MediaPlayer.create(getApplicationContext(), R.raw.twice);
            sleepPlayer.setOnCompletionListener(this.sleepSoundListener);
            sleepPlayer.setLooping(true);
            sleepPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start the sleep sound.");
        }
    }

    public void stopSleepSound() {
        Log.d(TAG, "Sleep sound stopped.");
        try {
            if (this.sleepPlayer != null) {
                if (this.sleepPlayer.isPlaying()) {
                    this.sleepPlayer.stop();
                }
                this.sleepPlayer.reset();
                this.sleepPlayer.release();
                this.sleepPlayer = null;
            }
        } catch (Exception e) {
        }
    }

    boolean isAlertCalled = false;

    void callAlert(){
        if(Constants.is_service_sending_alert() || isAlertCalled)
            return;
        isAlertCalled = true;
        Log.d(TAG,"Alert will called");
        if(getApplicationContext()!=null)
            Toast.makeText(getApplicationContext(),"Тревога", Toast.LENGTH_LONG).show();
        Intent newIntent = new Intent(getApplicationContext(),TrackingService.class);
        newIntent.setAction(Constants.ALERT_ACTION);
        getApplicationContext().startService(newIntent);
    }
}
