package kz.topsecurity.client.application;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import net.gotev.uploadservice.UploadService;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.fabric.sdk.android.Fabric;
import kz.topsecurity.client.BuildConfig;
import kz.topsecurity.client.di.components.app.DaggerAppComponent;


public class TopSecurityClientApplication extends MultiDexApplication implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
        Fabric.with(this , new Crashlytics());
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.NAMESPACE = "kz.topsecurity.client";
        trackingApp = this;
//        Stetho.initializeWithDefaults(this);
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

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }
}
