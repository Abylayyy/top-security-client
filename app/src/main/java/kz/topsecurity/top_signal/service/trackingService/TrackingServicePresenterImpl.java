package kz.topsecurity.top_signal.service.trackingService;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.model.alert.AlertResponse;
import kz.topsecurity.top_signal.model.alert.CancelAlertResponse;
import kz.topsecurity.top_signal.model.device.SaveDeviceDataResponse;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.service.trackingService.managers.BarometricAltitudeListenerManager;
import kz.topsecurity.top_signal.service.trackingService.managers.BatteryListenerManager;
import kz.topsecurity.top_signal.service.trackingService.managers.DataProvider;
import kz.topsecurity.top_signal.service.trackingService.managers.LocationListener;
import kz.topsecurity.top_signal.service.trackingService.managers.LocationListenerManager;
import kz.topsecurity.top_signal.service.trackingService.managers.TelephonyListenerManager;
import kz.topsecurity.top_signal.service.trackingService.managers.VolumeListenerManager;
import kz.topsecurity.top_signal.service.trackingService.managers.VolumeServiceManager;
import kz.topsecurity.top_signal.service.trackingService.model.DeviceData;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingServicePresenterImpl implements LocationListener {

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

    private LocationListenerManager mLocationListenerManager = new LocationListenerManager(this);
    private BatteryListenerManager mBatteryListenerManager = new BatteryListenerManager();
    private TelephonyListenerManager mTelephonyListenerManager = new TelephonyListenerManager();
    private BarometricAltitudeListenerManager mBarometricAltitudeListenerManager = new BarometricAltitudeListenerManager();
    private VolumeListenerManager mVolumeListenerManager = new VolumeListenerManager();
    private VolumeServiceManager mVolumeServiceManager = new VolumeServiceManager() ;

    public void setupLocationReceiver(Context context) {
        setupLocationReceiver(
                (LocationManager)(context).getSystemService( Context.LOCATION_SERVICE ),
                getFusedLocationProviderClient(context),
                new Geocoder(context, Locale.ENGLISH)
        );
    }

    public void setupBatteryReceiver(Context context){
        if(!mBatteryListenerManager.isActive())
            mBatteryListenerManager.setupBatteryManager(context);
    }

    public void setupTelephonyReceiver(Context context){
        if(!mTelephonyListenerManager.isActive())
            mTelephonyListenerManager.setupTelephonyListener(context);
    }

    public void setupBarometricAltitudeTracker(Context context){
        if(!mBarometricAltitudeListenerManager.isActive())
            mBarometricAltitudeListenerManager.setupBarometricAltitude(context);
    }

    public void setupVolumeReceiver(Context context) {
        if (mVolumeListenerManager.isActive()) {
            mVolumeListenerManager.setupVolumeReceiver(context);
        }
    }
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
    }

    private void sendDataToServer(DeviceData data) {
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
        Log.i(TAG, "TIMER FIRED");
    }

    public void stopTimer() {
        if(handler!=null && timer_fired!=null)
            handler.removeCallbacks(timer_fired);
        isTimerActive = false;
        isAlertTimerActive = false;
        isIdleTimerActive = false;
    }

    private void setupLocationReceiver(LocationManager locationManager, FusedLocationProviderClient client, Geocoder geocoder) {
        if(!mLocationListenerManager.isActive())
            mLocationListenerManager.setupLocationReceiver(locationManager,client, geocoder);
    }

    @Override
    public void onLocationChanged(Double lat, Double lng, Double alt, String street) {
        onTimerFired(false);
    }

    boolean isGPSenabled(){
        if(mLocationListenerManager!=null && mLocationListenerManager.isActive())
            return mLocationListenerManager.isGpsEnabled();
        return false;
    }

    //TODO: STOP SERVICE MEMORY LEAKS
    void stop(Context context){
        stopTimer();
        compositeDisposable.clear();
        if(mLocationListenerManager!=null)
            mLocationListenerManager.stop();
        if(mBatteryListenerManager!=null)
            mBatteryListenerManager.stop(context);
        if(mTelephonyListenerManager!=null)
            mTelephonyListenerManager.stop();
        if(mBarometricAltitudeListenerManager!=null)
            mBarometricAltitudeListenerManager.stop();
        if(mVolumeListenerManager!=null)
            mVolumeListenerManager.stop(context);
        if(mVolumeServiceManager!=null)
            mVolumeServiceManager.stop(context);
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    void callAlert(DeviceData data){
        if(data==null)
        {
            setAlertSendError();
            return;
        }

        Disposable success = new RequestService<>(new RequestService.RequestResponse<AlertResponse>() {
            @Override
            public void onSuccess(AlertResponse data) {
                setAlertIsActiveStatus();
            }

            @Override
            public void onFailed(AlertResponse data, int error_message) {
                if(data.getStatusCode()==4102){
                    setAlertIsActiveStatus();
                }
                else
                    setAlertSendError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setAlertSendError();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .sendAlert(RetrofitClient.getRequestToken(), data.getLat(), data.getLng(), data.getTimestamp()));


        compositeDisposable.add(success);
    }

    private void setAlertIsActiveStatus() {
        isAlertActive = true;
        setupAlertTimer();
        Constants.is_service_sending_alert(true);
        view.onBroadcastMessage(TrackingService.ACTION_STATUS_ALERT_SEND);
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
}
