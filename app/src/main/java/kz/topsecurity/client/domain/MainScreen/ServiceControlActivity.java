package kz.topsecurity.client.domain.MainScreen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import kz.topsecurity.client.BuildConfig;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.presenter.mainPresenter.MainPresenter;
import kz.topsecurity.client.presenter.mainPresenter.MainPresenterImpl;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.view.mainView.MainView;

public abstract class ServiceControlActivity extends BaseActivity<MainView,MainPresenter,MainPresenterImpl> {

    private static final String TAG = ServiceControlActivity.class.getSimpleName();
    public static final int REQUEST_CHECK_SETTINGS = 717;
    boolean mAlreadyStartedService = false;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE = 35;
    private static final int PHONE_PERMISSION = 333;
    private static final int GEOLOCATION_PERMISSION = 123;

    boolean networkCheckedOnce = false;
    boolean gpsCheckedOnce = false;

    AlertDialog networkDialog;
    AlertDialog gpsDialog;



    @Override
    protected void onResume() {
        super.onResume();
        if(!SharedPreferencesManager.getTrackingServiceActiveState(this))
            clearTrackingService();
        else {
            startStep1();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                onShowSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
        if(requestCode == REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE){
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");

            } else {
                onShowSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    public void onRequestPermission(int type) {
        switch (type){
            case PHONE_PERMISSION:{
                requestPhoneStatePermission();
                break;
            }
            case GEOLOCATION_PERMISSION:{
                requestPermissions();
                break;
            }
        }
    }

    void clearTrackingService(){
        mAlreadyStartedService = false;
    }

    private void startStep1() {
        if (isGooglePlayServicesAvailable()) {
            startStep2(null);
        } else {
            onShowToast("Google services are unavailable");
        }
    }


    private Boolean startStep2(DialogInterface dialog) {
        if (checkPermissions()) {
            startStep3();
        } else {
            onRequestPermission(GEOLOCATION_PERMISSION);
        }
        return true;
    }

    protected void promptTurnOnService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceControlActivity.this);
        builder.setTitle(R.string.title_service_not_active);
        builder.setMessage(R.string.msg_service_not_active);
        String positiveText = getString(R.string.turn_on);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkPermissions()) {
                            SharedPreferencesManager.setTrackingServiceActiveState(ServiceControlActivity.this,true);
                            startStep1();
                        } else if (!checkPermissions()) {
                            onRequestPermission(GEOLOCATION_PERMISSION);
                        }
                        dialog.dismiss();
                    }
                });
        String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void startStep3() {
        if (!mAlreadyStartedService ||  !Constants.is_service_active()) {
            Intent intent = new Intent(this, TrackingService.class);
            intent.setAction(Constants.START_FOREGROUND_ACTION);
            checkAndStartService(intent,TrackingService.class);
            mAlreadyStartedService = true;
            runOnUiThread(()->{
                //blockAlertButton(false);
            });
        }
        else if(Constants.is_service_sending_alert() ){
            setAlertActiveView();
        }
        createAndCheckLocationProvider();
        checkServices();
    }

    protected void forceStartService(){
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.START_FOREGROUND_ACTION);
        checkAndStartService(intent,TrackingService.class);
        mAlreadyStartedService = true;
    }

    protected abstract void setAlertActiveView();
    protected abstract void blockAlertButton(boolean state);

    private void checkServices() {
        if(!networkCheckedOnce){
            networkCheckedOnce = true;
            if(checkNetwork()){
                checkServices();
            }
            return;
        }
        if (!gpsCheckedOnce){
            gpsCheckedOnce = true;
            if(checkGPS()){
                checkServices();
            }
            return;
        }
    }


    protected boolean checkNetwork() {
        if (!isNetworkOnline()) {
            promptInternetConnect();
            return false;
        }
        return true;
    }

    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceControlActivity.this);
        builder.setTitle(R.string.title_alert_no_internet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        networkDialog.dismiss();
                        checkServices();
                    }
                });

        networkDialog = builder.create();
        networkDialog.show();
    }


    protected boolean checkGPS() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        boolean isGpsEnabled = provider.contains("gps");
        boolean isGpsActive = isGpsEnabled || (lm!=null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER));

        if(!isGpsActive){
            promptGeolocationConnect();
            return false;
        }
        else{
            checkServices();
        }
        return true;
    }

    private void promptGeolocationConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceControlActivity.this);
        builder.setTitle(R.string.title_alert_no_gps);
        builder.setMessage(R.string.msg_alert_no_gps);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gpsDialog.dismiss();
                        checkServices();
                    }
                });

        gpsDialog = builder.create();
        gpsDialog.show();
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPhoneStatePermission() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_PHONE_STATE);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            onShowSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ServiceControlActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(ServiceControlActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            onShowSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ServiceControlActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(ServiceControlActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    void createAndCheckLocationProvider(){
        LocationRequest locationRequest = new LocationRequest();
        // 2
        locationRequest.setInterval(10000);
        // 3
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // 4
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build());

        // 5
        locationSettingsResponseTask.addOnSuccessListener (r->{
            restartService();
        });
        locationSettingsResponseTask.addOnFailureListener ( error ->{
            if (error instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ((ResolvableApiException) error).startResolutionForResult(this,REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    protected void restartService() {
//        fusedLocationClient.removeLocationUpdates(locationCallback);
        Intent newIntent = new Intent(this, TrackingService.class);
        stopService(newIntent);
        forceStartService();
    }

}
