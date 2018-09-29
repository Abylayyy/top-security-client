package kz.topsecurity.top_signal.application;

import android.app.Application;
import android.content.res.Configuration;

import net.gotev.uploadservice.UploadService;

import kz.topsecurity.top_signal.BuildConfig;


public class TopSignalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = "kz.topsecurity.topsignal";
        trackingApp = this;
    }

    private static TopSignalApplication trackingApp;

    public static TopSignalApplication getInstance(){
        return trackingApp;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
