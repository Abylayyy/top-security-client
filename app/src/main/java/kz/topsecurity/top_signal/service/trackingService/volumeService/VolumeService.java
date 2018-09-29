package kz.topsecurity.top_signal.service.trackingService.volumeService;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import kz.topsecurity.top_signal.application.TopSignalApplication;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.helper.SharedPreferencesManager;
import kz.topsecurity.top_signal.service.trackingService.TrackingService;

import static kz.topsecurity.top_signal.service.trackingService.TrackingService.EXTRA_ACTION_TYPE;

public class VolumeService extends AccessibilityService {

    public static final String FILTER_ACTION_SERVICE_BROADCAST = VolumeService.class.getName() + "ActionBroadcast";
    public static final int ACTION_KEY_DOWN_VOLUME_UP = 547;
    public static final int ACTION_KEY_DOWN_VOLUME_DOWN = 951;
    public static final int ACTION_KEY_UP_VOLUME_UP= 121;
    public static final int ACTION_KEY_UP_VOLUME_DOWN = 443;
    public static final String EXTRA_VOLUME_ACTION_TYPE = "extra_volume_track";
    private static final String TAG = VolumeService.class.getSimpleName();

    @Override
    public boolean onKeyEvent(final KeyEvent event) {
        Log.d(TAG, "onKeyEvent(KeyEvent=" + event + ")");
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d(TAG, "KEYCODE_VOLUME_UP released");
                broadcastMessage(ACTION_KEY_UP_VOLUME_UP);
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d(TAG, "KEYCODE_VOLUME_DOWN released");
                broadcastMessage(ACTION_KEY_UP_VOLUME_DOWN);
            }
            return super.onKeyEvent(event);
        }

        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    broadcastMessage(ACTION_KEY_DOWN_VOLUME_UP);
                    return super.onKeyEvent(event);
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    broadcastMessage(ACTION_KEY_DOWN_VOLUME_DOWN);
                    return super.onKeyEvent(event);
            }
            Log.d(TAG, KeyEvent.keyCodeToString(keyCode) + " pressed");
            return super.onKeyEvent(event);
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected()");
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt()");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, "onAccessibilityEvent(AccessibilityEvent=" + event + ")");
    }

    private void broadcastMessage( int type) {
        if(!SharedPreferencesManager.getVolumeTrackingServiceActiveState(this))
            return;
        Log.d(TAG, "Sending info...");
        Intent intent = new Intent(FILTER_ACTION_SERVICE_BROADCAST);
        intent.putExtra(EXTRA_VOLUME_ACTION_TYPE, type);
        sendBroadcast(intent);
    }
}