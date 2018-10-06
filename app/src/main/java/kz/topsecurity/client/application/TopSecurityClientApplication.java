package kz.topsecurity.client.application;

import android.app.Application;
import android.content.res.Configuration;

import com.google.firebase.FirebaseApp;

import net.gotev.uploadservice.UploadService;

import kz.topsecurity.client.BuildConfig;


public class TopSecurityClientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = "kz.topsecurity.client";
        trackingApp = this;
    }

    private static TopSecurityClientApplication trackingApp;

    public static TopSecurityClientApplication getInstance(){
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
