package kz.topsecurity.client.service.trackingService;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.model.alert.AlertResponse;
import kz.topsecurity.client.model.alert.CancelAlertResponse;
import kz.topsecurity.client.model.device.SaveDeviceDataResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.service.trackingService.managers.BarometricAltitudeListenerManager;
import kz.topsecurity.client.service.trackingService.managers.BatteryListenerManager;
import kz.topsecurity.client.service.trackingService.managers.DataProvider;
import kz.topsecurity.client.service.trackingService.managers.FirebaseMessageListener;
import kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager;
import kz.topsecurity.client.service.trackingService.managers.LocationListener;
import kz.topsecurity.client.service.trackingService.managers.LocationListenerManager;
import kz.topsecurity.client.service.trackingService.managers.VolumeServiceManager;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager.alert_cancelled;
import static kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager.order_accepted;
import static kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager.order_closed;
import static kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager.order_created;
import static kz.topsecurity.client.service.trackingService.managers.FirebaseMessagesListenerManager.tracking_changed;

public class TrackingServicePresenterImpl implements LocationListener , FirebaseMessageListener {

    private TrackingServiceView view;
    private Handler handler;
    private Runnable timer_fired;
    private boolean isTimerActive = false;
    private boolean isAlertActive = false;

    private static final String TAG = TrackingServicePresenterImpl.class.getSimpleName();

    public TrackingServicePresenterImpl() {}

    public TrackingServicePresenterImpl(TrackingServiceView view) {
        this.view = view;
    }

    //   private KalmanLocationManager mKalmanLocationManager = new KalmanLocationManager(this);
    private LocationListenerManager mLocationListenerManager = new LocationListenerManager(this);
    private BatteryListenerManager mBatteryListenerManager = new BatteryListenerManager();
//    private TelephonyListenerManager mTelephonyListenerManager = new TelephonyListenerManager();
    private BarometricAltitudeListenerManager mBarometricAltitudeListenerManager = new BarometricAltitudeListenerManager();
//    private VolumeListenerManager mVolumeListenerManager = new VolumeListenerManager();
    private VolumeServiceManager mVolumeServiceManager = new VolumeServiceManager() ;
    private FirebaseMessagesListenerManager mFirebaseMessagesListenerManager = new FirebaseMessagesListenerManager(this);

    public void setupLocationReceiver(Context context) {
        setupLocationReceiver(context,
                (LocationManager)(context).getSystemService( Context.LOCATION_SERVICE ),
                getFusedLocationProviderClient(context),
                new Geocoder(context, Locale.ENGLISH)
        );
    }

    public void setupBatteryReceiver(Context context){
        if(!mBatteryListenerManager.isActive())
            mBatteryListenerManager.setupBatteryManager(context);
    }

//    public void setupTelephonyReceiver(Context context){
//        if(!mTelephonyListenerManager.isActive())
//            mTelephonyListenerManager.setupTelephonyListener(context);
//    }

    public void setupBarometricAltitudeTracker(Context context){
        if(!mBarometricAltitudeListenerManager.isActive())
            mBarometricAltitudeListenerManager.setupBarometricAltitude(context);
    }

//    public void setupVolumeReceiver(Context context) {
//        if (mVolumeListenerManager.isActive()) {
//            mVolumeListenerManager.setupVolumeReceiver(context);
//        }
//    }
    public void setupVolumeServiceReceiver(Context context) {
        if(!mVolumeServiceManager.isActive())
            mVolumeServiceManager.setupVolumeReceiver(context);
    }

    void setupTimer(){
        if(isTimerActive && (isIdleTimerActive || isAlertTimerActive))
            return;
        if(isAlertActive)
            setupAlertTimer();
        else
            setupIdleTimer();
    }

    boolean isAlertTimerActive = false;
    boolean isIdleTimerActive = false;

    void setupAlertTimer(){
        if(isTimerActive && isAlertTimerActive)
            return;
        stopTimer();
        isAlertTimerActive =true;
        timer_fired = new Runnable() {
            @Override
            public void run() {
                onTimerFired(true);
                handler.postDelayed(this, Constants.TIMER_WAKE_UP_INTERVAL);
            }
        };
        handler.postDelayed(timer_fired,Constants.TIMER_WAKE_UP_INTERVAL);
        isTimerActive = true;
    }

    void setupIdleTimer(){
        if(isTimerActive && isIdleTimerActive)
            return;
        stopTimer();
        isIdleTimerActive = true;
        handler = new Handler();
        timer_fired = new Runnable() {
            @Override
            public void run() {
                onTimerFired(true);
                handler.postDelayed(this, Constants.TIMER_IDLE_WAKE_UP_INTERVAL);
            }
        };
        handler.postDelayed(timer_fired,Constants.TIMER_IDLE_WAKE_UP_INTERVAL);
        isTimerActive = true;
    }

    private void sendMessageToUI(boolean sendToServer){
        Double latitude = 0.0;
        Double longitude = 0.0;
        Double altitude = 0.0;
        String currentLocationStreet = "";
        boolean isGPSenabled = isGPSenabled();

        //*********************************************

        //IS checking gps Really important?
        if(isGPSenabled)
        {
//            latitude = mLocationListenerManager.getLatitude();
//            longitude = mLocationListenerManager.getLongitude();
//            altitude = mLocationListenerManager.getAltitude();
//            currentLocationStreet = mLocationListenerManager.getCurrentLocationStreet();
            //TODO: STOP TELEPHONY MANAGER
            //TODO: START LOCATION MANAGER
        }
//        else{
//            latitude = mTelephonyListenerManager.getLatitude();
//            longitude = mTelephonyListenerManager.getLongitude();
//            //TODO: START TELEPHONY MANAGER
//            //TODO: STOP LOCATION MANAGER
//        }

        latitude = mLocationListenerManager.getLatitude();
        longitude = mLocationListenerManager.getLongitude();
        altitude = mLocationListenerManager.getAltitude();
        currentLocationStreet = mLocationListenerManager.getCurrentLocationStreet();
        mLocationListenerManager.clearStoredData();
        //********************************************

        double pressureAltitudeValue = mBarometricAltitudeListenerManager.getPressureAltitudeValue();
        int batteryPct = mBatteryListenerManager.getBatteryPct();

        Intent intent = DataProvider.prepareIntentData(latitude,longitude,altitude,currentLocationStreet,
                pressureAltitudeValue,batteryPct,1,isAlertActive,isGPSenabled );

        view.onDataPrepared(intent);

        DeviceData data = new DeviceData();

        data.setStreetAddress(currentLocationStreet);
        data.setLat(latitude);
        data.setLng(longitude);
        data.setAlt(altitude);
        data.setAltBarometer(pressureAltitudeValue);
        data.setSignalStrength(1);
        data.setCharge(batteryPct);
        data.setIs_gps_active(isGPSenabled);
        data.setIs_urgent(isAlertActive);
        long unixTime = System.currentTimeMillis() / 1000L;
        data.setTimestamp((int) unixTime);
        view.onDataPrepared(data);
        if(sendToServer)
            sendDataToServer(data);
        boolean isAlertOnHold = SharedPreferencesManager.getIsAlertOnHold(TopSecurityClientApplication.getInstance());
        if(isAlertOnHold)
            callAlert(data);
    }

    private void sendDataToServer(DeviceData data) {
        Log.i("aibolscorpion","sendDataToServer: "+data.getLat()+" "+data.getLng());

        Disposable success = new RequestService<>(new RequestService.RequestResponse<SaveDeviceDataResponse>() {
            @Override
            public void onSuccess(SaveDeviceDataResponse data) {
                Log.e(TAG,"Data send successfully");

            }

            @Override
            public void onFailed(SaveDeviceDataResponse data, int error_message) {
                Log.e(TAG,"Unable to send data");
            }

            @Override
            public void onError(Throwable e, int error_message) {
                Log.e(TAG,"Unable to send data");
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .saveData(RetrofitClient.getRequestToken(),
                        data.getLat(),
                        data.getLng(),
                        data.getAlt(),
                        data.getAltBarometer(),
                        data.getCharge(),
                        data.getStreetAddress(),
                        data.getTimestamp()));


        compositeDisposable.add(success);
    }


    void onTimerFired(boolean sendToServer){
        try {
            mBarometricAltitudeListenerManager.getWeatherData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessageToUI(sendToServer);
        Log.i(TAG, "sendToServer "+sendToServer);
    }

    public void stopTimer() {
        if(handler!=null && timer_fired!=null)
            handler.removeCallbacks(timer_fired);
        isTimerActive = false;
        isAlertTimerActive = false;
        isIdleTimerActive = false;
    }

    private void setupLocationReceiver(Context context, LocationManager locationManager, FusedLocationProviderClient client, Geocoder geocoder) {
        if(!mLocationListenerManager.isActive())
            mLocationListenerManager.setupLocationReceiver(context , locationManager,client, geocoder);
//        if(!mLocationListenerManager.isActive())
//            mLocationListenerManager.setupLocationReceiver(context );
//        new LocationServiceWithFilter();
    }

    private long lastAddressRequestInMilis = 0;

    @Override
    public void onLocationChanged(Double lat, Double lng, Double alt, String street) {
        if(lastAddressRequestInMilis == 0)
            lastAddressRequestInMilis = System.currentTimeMillis();
        else if(System.currentTimeMillis() - lastAddressRequestInMilis<5*1000)
            return;
        lastAddressRequestInMilis = System.currentTimeMillis();
        onTimerFired(false);
        checkTimer();
    }

    private void checkTimer() {
        if(!isTimerActive){
            setupTimer();
        }
        else{
            handler.sendEmptyMessage(1);
            if(!handler.hasMessages(1)) {
                isTimerActive = false;
                setupTimer();
            }
        }
    }

    @Override
    public void onLocationRequestCheck(LocationRequest locationRequest) {
        view.checkLocationRequest(locationRequest);
    }

    boolean isGPSenabled(){
        if(  mLocationListenerManager!= null && mLocationListenerManager.isActive())
            mLocationListenerManager.isGpsEnabled();
        return SharedPreferencesManager.getGpsStatus(TopSecurityClientApplication.getInstance());
//        if(mLocationListenerManager!=null && mLocationListenerManager.isActive())
//            return mLocationListenerManager.isGpsEnabled();
//        return false;
    }

    void stop(Context context){
        stopTimer();
        compositeDisposable.clear();

        if(mLocationListenerManager!=null)
            mLocationListenerManager.stop();
        if(mBatteryListenerManager!=null)
            mBatteryListenerManager.stop(context);
//        if(mTelephonyListenerManager!=null)
//            mTelephonyListenerManager.stop();
        if(mBarometricAltitudeListenerManager!=null)
            mBarometricAltitudeListenerManager.stop();
//        if(mVolumeListenerManager!=null)
//            mVolumeListenerManager.stop(context);
        if(mVolumeServiceManager!=null)
            mVolumeServiceManager.stop(context);
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    void callAlert(DeviceData data){
//        if(data==null)
//        {
//            setAlertSendError();
//            return;
//        }
        callAlert(data,0);
    }

    void callAlert(DeviceData data, final int type){
//        if(data==null)
//        {
//            setAlertSendError();
//            return;
//        }
        String str_type = "alert";
        if(type==0)
            str_type = "alert";
        else if(type == 1)
            str_type = "undefined";
        else if (type == 2)
            str_type = "ambulance";
        Log.i("aibolscorpion","callAlert: "+data.getLat()+" "+data.getLng());
        if(!SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance()))
        {
            setAlertSendError();
            Toast.makeText(TopSecurityClientApplication.getInstance(), R.string.alert_do_not_work_without_payment,Toast.LENGTH_SHORT ).show();
            return;
        }
        if(data==null || data.getLat()==0.0 || data.getLng()==0.0)
        {
            setAlertOnHold(type);
            return;
        }
        Disposable success = new RequestService<>(new RequestService.RequestResponse<AlertResponse>() {
            @Override
            public void onSuccess(AlertResponse data) {
                setAlertIsActiveStatus(type);
                removeAlertFromHold();
            }

            @Override
            public void onFailed(AlertResponse data, int error_message) {
                if (data.getStatusCode() == 4102) {
                    setAlertIsActiveStatus(type);
                    removeAlertFromHold();
                } else {
                    setAlertSendError();
                    removeAlertFromHold();
                }
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setAlertSendError();
                removeAlertFromHold();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .sendAlert(RetrofitClient.getRequestToken(),
                        data.getLat(),
                        data.getLng(),
                        data.getAlt(),
                        data.getAltBarometer(),
                        data.getCharge(),
                        data.getStreetAddress(),
                        data.getTimestamp(),
                        str_type));


        compositeDisposable.add(success);
    }

    private void removeAlertFromHold() {
        SharedPreferencesManager.setIsAlertOnHold(TopSecurityClientApplication.getInstance(),false);
    }

    private void setAlertOnHold(int type) {
        SharedPreferencesManager.setIsAlertOnHold(TopSecurityClientApplication.getInstance(),true);
        setAlertIsActiveStatus(type );
    }

    private void setAlertIsActiveStatus(int type) {
        isAlertActive = true;
        setupAlertTimer();
        Constants.is_service_sending_alert(true);
        if(type==0)
            view.onBroadcastMessage(TrackingService.ACTION_STATUS_ALERT_SEND);
        else if(type == 1)
            view.onBroadcastMessage(TrackingService.ACTION_UNDEFINED_ALERT_SEND);
        else if (type ==2 )
            view.onBroadcastMessage(TrackingService.ACTION_AMBULANCE_CALLED);
        view.setAlertSendStatus();
    }

    void setAlertSendError(){
        Constants.is_service_sending_alert(false);
        view.onBroadcastMessage(TrackingService.ACTION_STATUS_ALERT_FAILED);
        view.setAlertFailedStatus();
    }

    public void cancelAlert(DeviceData data, String password){
        Disposable success = new RequestService<>(new RequestService.RequestResponse<CancelAlertResponse>() {
            @Override
            public void onSuccess(CancelAlertResponse data) {
                setAlertIsCanceledStatus();
            }

            @Override
            public void onFailed(CancelAlertResponse data, int error_message) {
                if(data.getStatusCode()==4101){
                    setAlertIsCanceledStatus();
                }
                else
                    setCancelError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setCancelError();
            }
        }).makeRequest(RetrofitClient
                .getClientApi()
                .cancelAlert(RetrofitClient.getRequestToken()));

        compositeDisposable.add(success);
    }

    private void setAlertIsCanceledStatus() {
        setupIdleTimer();
        isAlertActive = false;
        Constants.is_service_sending_alert(false);
        view.setAlertCanceledStatus();
        view.onBroadcastMessage(TrackingService.ACTION_STATUS_ALERT_CANCEL_SEND);
    }

    void setCancelError(){
        Constants.is_service_sending_alert(true);
        view.onBroadcastMessage(TrackingService.ACTION_STATUS_ALERT_CANCEL_FAILED);
        view.setAlertCancelFailedStatus();
    }

    public void checkLocationRequest(LocationSettingsRequest.Builder builder, SettingsClient settingsClient) {
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build());
        locationSettingsResponseTask.addOnSuccessListener(l->{
            mLocationListenerManager.startLocationUpdates();
        });
        locationSettingsResponseTask.addOnFailureListener( e ->{
            // 6
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    ((ResolvableApiException) e).startResolutionForResult(TrackingService.this,
//                            REQUEST_CHECK_SETTINGS );
//                } catch (IntentSender.SendIntentException ex) {
//                    // Ignore the error.
//                }

                view.onLocationNotAvailable();
            }
        });
    }

    public void setupFirebaseMessagesReceiver(Context context) {
        if(!mFirebaseMessagesListenerManager.isActive())
            mFirebaseMessagesListenerManager.setupFirebaseMessagesListenerManager(context);
    }

    @Override
    public void onOrderChanged(String type) {
        switch (type){
            case order_accepted:{
                view.onBroadcastMessage(TrackingService.ACTION_MRRT_ACCEPTED);
                break;
            }
            case order_closed:{
                view.onBroadcastMessage(TrackingService.ACTION_OPERATOR_CANCELLED);
                break;
            }
            case alert_cancelled:{
                view.onBroadcastMessage(TrackingService.ACTION_OPERATOR_CANCELLED);
                break;
            }
            case order_created:{
                view.onBroadcastMessage(TrackingService.ACTION_OPERATOR_CREATED);
                break;
            }
            case tracking_changed:{
                view.onBroadcastMessage(TrackingService.ACTION_MRRT_CHANGED_POSITION);
                break;
            }
        }
    }
}
