package kz.topsecurity.client.service.trackingService;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.SplashScreen.SplashScreen;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

//import com.example.sample.trackingapp.services.actionService.ActionService;

public class TrackingService extends Service implements TrackingServiceView {

    private static final String TAG = TrackingService.class.getSimpleName();
    private TrackingServicePresenterImpl presenter;
    private DeviceData data = new DeviceData();

    public static final String FILTER_ACTION_SERVICE_BROADCAST = TrackingService.class.getName() + "ActionBroadcast";
    public static final String EXTRA_ACTION_TYPE = "extra_alert";

    public static int currentState = 0;
    public static final int ACTION_NEW_SERVICE = 0;
    public static final int ACTION_GPS = 1;
    public static final int ACTION_NETWORK = 2;
    public static final int ACTION_STATUS_ALERT_CALLED = 705;
    public static final int ACTION_STATUS_ALERT_SEND = 217;
    public static final int ACTION_STATUS_ALERT_FAILED = 175;
    public static final int ACTION_STATUS_ALERT_CANCEL = 254;
    public static final int ACTION_STATUS_ALERT_CANCEL_FAILED = 421;
    public static final int ACTION_STATUS_ALERT_CANCEL_SEND = 852;
    public static final int ACTION_GPS_NOT_AVAILABLE = 470;
    public static final int ACTION_OPERATOR_ACCEPTED =342 ;
    public static final int ACTION_MRRT_ACCEPTED = 534;
    public static final int ACTION_OPERATOR_CANCELLED = 563;

    NotificationCompat.Builder notificationBuilder;
    NotificationManager mNotifyManager;
    boolean isAlertActive = false;
    public static final int locationNotAvailable =197;

    public TrackingService() {

    }

    public TrackingService(Context applicationContext) {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"SERVICE IS STARTED");
        setupData();
        if(presenter==null)
            presenter = new TrackingServicePresenterImpl(this);
        setupReceivers();
        setupIntent(intent);
        Constants.is_service_active(true);
        // SETUP ACTION LOGIC
        //setupForeground();
        return START_STICKY;
    }

    void setupData(){
        //Restore last data service recreated
        //TODO: TEST this method
        if(dataBaseManager==null)
            return;

        if(data==null){
                data = dataBaseManager.getDeviceData();
        }
        else{
            if(data.getCharge()==null){
                data = dataBaseManager.getDeviceData();
            }
        }
    }

    void setupReceivers(){
        setupLocationReceiver();
        presenter.setupBatteryReceiver(this);
//        setupTelephonyReceiver();
        presenter.setupBarometricAltitudeTracker(this);
        presenter.setupVolumeServiceReceiver( this);
        presenter.setupTimer();
        presenter.setupFirebaseMessagesReceiver(this);
    }

    private void setupIntent(Intent intent){
        Log.d(TAG,"SERVICE IS STARTED");
        if(intent == null){
            Log.d(TAG,"INTENT is null");
            return;
        }
        String intent_action_type = intent.getAction();
        processAction(intent_action_type,intent);
    }

    private void processAction(String intent_action_type , Intent intent){
        switch (intent_action_type){
            case Constants.START_FOREGROUND_ACTION:{
                setIdleStatus();
                break;
            }
            case Constants.RESTART_FOREGROUND_SERVICE:{
                restoreState();
                break;
            }
            case Constants.ALERT_ACTION: {
                setAlertStatus();
                presenter.callAlert(data);
                Constants.is_service_sending_alert(true);
                break;
            }
            case Constants.ALERT_ACTIVE_ACTION:{
                setAlertSendStatus();
                break;
            }
            case Constants.ALERT_FAILED_ACTION:{
                setAlertFailedStatus();
                break;
            }
            case Constants.ALERT_CANCEL_ACTION:{
                setAlertCancelStatus();
                Constants.is_service_sending_alert(false);
                presenter.cancelAlert(data,intent.getStringExtra("password"));
                break;
            }
            case Constants.ALERT_CANCEL_FAILED_ACTION:{
                setAlertCancelFailedStatus();
                break;
            }
            case Constants.ALERT_CANCELED_ACTION:{
                setAlertCanceledStatus();
                break;
            }
            case Constants.ASK_TO_TURN_ON_GPS_ACTION:{
                setGPSnotActiveStatus();
                break;
            }
            case Constants.ASK_TO_TURN_ON_NETWORK_ACTION:{
                setNetworkNotActiveStatus();
                break;
            }
            default:{
                try {
                    throw new Throwable("UNKNOWN ACTION TYPE");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
            }
        }
    }

    private void restoreState() {
        isAlertActive = SharedPreferencesManager.getAlertActive(this);
        currentState = SharedPreferencesManager.getBackgroundServiceState(this);
        sendNotification("Restore state", currentState);
//        broadcastMessage(SharedPreferencesManager.getBackgroundServiceState(this));
    }

    private void setIdleStatus(){
        sendNotification("Click to call alert", ACTION_NEW_SERVICE);
        broadcastMessage(ACTION_NEW_SERVICE);
    }

    private void setAlertStatus(){
        sendNotification("Alert is called", ACTION_STATUS_ALERT_CALLED);
        broadcastMessage(ACTION_STATUS_ALERT_CALLED);
    }

    @Override
    public void setAlertFailedStatus(){
        sendNotification("Alert is failed", ACTION_STATUS_ALERT_FAILED);
        broadcastMessage(ACTION_STATUS_ALERT_FAILED);
    }

    @Override
    public void setAlertSendStatus(){
        sendNotification("Alert is send", ACTION_STATUS_ALERT_SEND);
        broadcastMessage(ACTION_STATUS_ALERT_SEND);
    }

    private void setAlertCancelStatus(){
        sendNotification("Click to call alert", ACTION_NEW_SERVICE);
        broadcastMessage(ACTION_STATUS_ALERT_CANCEL);
    }

    @Override
    public void setAlertCancelFailedStatus(){
        sendNotification("Click to call alert", ACTION_NEW_SERVICE);
        broadcastMessage(ACTION_STATUS_ALERT_CANCEL_FAILED);
    }

    @Override
    public void setAlertCanceledStatus(){
        sendNotification("Click to call alert", ACTION_NEW_SERVICE);
        broadcastMessage(ACTION_STATUS_ALERT_CANCEL_SEND);
    }

    @Override
    public void onLocationNotAvailable() {
        broadcastMessage(ACTION_GPS_NOT_AVAILABLE);
    }

    @Override
    public void checkLocationRequest(LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        presenter.checkLocationRequest(builder,settingsClient);
    }

    private void setGPSnotActiveStatus(){
        sendNotification("GPS is turned off",ACTION_GPS);
        broadcastMessage(ACTION_GPS);
    }

    private void setNetworkNotActiveStatus(){
        sendNotification("Network is unavailable",ACTION_NETWORK);
        broadcastMessage(ACTION_NETWORK);
    }

    private void broadcastMessage( int type) {
        Log.d(TAG, "Sending info...");
        currentState = type;
        SharedPreferencesManager.setBackgroundServiceState(this,type);
        if(currentState == ACTION_NEW_SERVICE)
            return;
        Intent intent = new Intent(FILTER_ACTION_SERVICE_BROADCAST);
        intent.putExtra(EXTRA_ACTION_TYPE, type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void setupLocationReceiver(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== Error On onConnected() Permission not granted");
            return;
        }
        presenter.setupLocationReceiver( this );
    }

    private void setupTelephonyReceiver() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            return;
        }
        presenter.setupTelephonyReceiver(this);
    }

    private void sendNotification(String msg, int action_type) {
//        //TODO: NavigationLogic
        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.putExtra(SplashScreen.START_MAIN_SCREEN_KEY,true);
        notificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        String channelId = "channel_02";

        //notificationIntent.setAction(Constants.MAIN_ACTION);// ??
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        //TODO: notification view
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.notification_alert);

        setupView(views, msg, action_type);
        //
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(mNotifyManager, channelId);
        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_vector_app_icon)
                .setColor(ContextCompat.getColor(this, R.color.white))
                .setChannelId(channelId)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true);

        notificationBuilder.setCustomContentView(views);
        notificationBuilder.setContentIntent(pendingIntent);
        startForeground(2, notificationBuilder.build());
    }

    private void placeholderNotification(String msg) {
        String channelId = "channel_02";
        Intent resultIntent = new Intent(this, SplashScreen.class);
        resultIntent.putExtra(SplashScreen.START_MAIN_SCREEN_KEY , true);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(mNotifyManager, channelId);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this , channelId)
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("TOP SIGNAL")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(msg);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(2, mBuilder.build());
    }

    int darkBlue = android.graphics.Color.rgb(56, 69, 79);
    int lightPink = android.graphics.Color.rgb(255, 0, 92);
    int lightGrey = android.graphics.Color.rgb(183, 183, 183);
    int colorWhite = android.graphics.Color.rgb(255, 255, 255);
    int darkGrey = android.graphics.Color.rgb(148, 148, 148);

    private void setupView(RemoteViews views, String msg, int action_type){
        String set_action = "";
        int bg_color_info_container = 0;
        int bg_btn = 0;
        int btn_image = 0;
        int notif_icon = 0;
        int topTextColor = 0;
        int bottomTextColor = 0;
        String topText = "";
        String bottomText = "";
        String btn_text = "";
        int bottomIcon = R.drawable.ic_active;

        if(action_type== ACTION_STATUS_ALERT_CANCEL ||
                action_type == ACTION_NEW_SERVICE ||
                action_type == ACTION_STATUS_ALERT_FAILED) {

            views.setOnClickPendingIntent(R.id.rl_alert, null);
            if (action_type == ACTION_NEW_SERVICE  || !isPossibleToSendAlert()) {
                isAlertActive = false;
                SharedPreferencesManager.setAlertActive(this,false);
                set_action = Constants.ALERT_ACTION;
                bg_color_info_container = colorWhite;
                bg_btn = lightPink;
                btn_image = R.drawable.ic_call_alert;
                notif_icon = R.drawable.ic_notification_pink_bg;
                topTextColor = darkGrey;
                topText = "трекер активен";
                bottomTextColor = darkGrey;
                bottomText = "Слежка включена";
                bottomIcon = R.drawable.ic_active;
                btn_text = getString(R.string.alert);
            }else{
                //TODO: IT IS JUST AN PLACEHOLDER
                set_action = Constants.ALERT_ACTION;
                bg_color_info_container = colorWhite;
                bg_btn = lightPink;
                btn_image = R.drawable.ic_call_alert;
                notif_icon = R.drawable.ic_notification_pink_bg;
                topTextColor = darkGrey;
                topText = "трекер активен";
                bottomTextColor = darkGrey;
                bottomText = "Слежка включена";
                bottomIcon = R.drawable.ic_active;
                btn_text = getString(R.string.alert);
            }
        }
        else if(action_type==ACTION_STATUS_ALERT_CALLED || action_type == ACTION_STATUS_ALERT_SEND)
        {
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            if (isPossibleToSendAlert()) {
                isAlertActive = true;
                SharedPreferencesManager.setAlertActive(this,true);
                set_action = Constants.ALERT_CANCEL_ACTION;
                bg_color_info_container = lightPink;
                bg_btn = darkBlue;
                btn_image = R.drawable.ic_cancel_alert;
                notif_icon = R.drawable.ic_notification_white_bg;
                topTextColor = colorWhite;
                topText = "тревога отправлена";
                bottomTextColor = colorWhite;
                bottomText = "Тревога активна";
                bottomIcon = R.drawable.ic_active;
                btn_text = getString(R.string.cancel);
            }else{
                //TODO: IT IS JUST AN PLACEHOLDER
                set_action = Constants.ALERT_CANCEL_ACTION;
                bg_color_info_container = lightPink;
                bg_btn = darkBlue;
                btn_image = R.drawable.ic_cancel_alert;
                notif_icon = R.drawable.ic_notification_white_bg;
                topTextColor = colorWhite;
                topText = "тревога отправлена";
                bottomTextColor = colorWhite;
                bottomText = "Тревога активна";
                bottomIcon = R.drawable.ic_active;
                btn_text = getString(R.string.cancel);
            }
        }
        else if(action_type == ACTION_GPS){
            views.setOnClickPendingIntent(R.id.rl_alert, null);
                bg_color_info_container = colorWhite;
                bg_btn = darkGrey;
                btn_image = R.drawable.ic_no_geolocation;
                notif_icon = R.drawable.ic_notification_pink_bg;
                topTextColor = darkGrey;
                topText = "не активно";
                bottomTextColor = darkGrey;
                bottomText = "Включите геолокацию";
                bottomIcon = R.drawable.ic_not_active;
                btn_text = getString(R.string.turn_on);
        }
        else if(action_type == ACTION_NETWORK){
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            bg_color_info_container = colorWhite;
            bg_btn = darkGrey;
            btn_image = R.drawable.ic_no_connection;
            notif_icon = R.drawable.ic_notification_pink_bg;
            topTextColor = darkGrey;
            topText = "не активно";
            bottomTextColor = darkGrey;
            bottomText = "Включите сеть";
            bottomIcon = R.drawable.ic_not_active;
            btn_text = getString(R.string.turn_on);
        }else{
            //TODO: IT IS JUST AN PLACEHOLDER
            set_action = Constants.ALERT_ACTION;
            bg_color_info_container = colorWhite;
            bg_btn = lightPink;
            btn_image = R.drawable.ic_call_alert;
            notif_icon = R.drawable.ic_notification_pink_bg;
            topTextColor = darkGrey;
            topText = "трекер активен";
            bottomTextColor = darkGrey;
            bottomText = "Слежка включена";
            bottomIcon = R.drawable.ic_active;
            btn_text = getString(R.string.alert);
        }
        views.setInt(R.id.rl_info_container, "setBackgroundColor",
                bg_color_info_container);
        views.setInt(R.id.rl_alert, "setBackgroundColor",
                bg_btn);
        views.setImageViewResource(R.id.iv_alert ,btn_image);
        views.setImageViewResource(R.id.iv_app_icon, notif_icon);


        views.setTextViewText(R.id.tv_service_status, topText);
        views.setTextViewText(R.id.tv_service_msg, bottomText);

        views.setTextColor(R.id.tv_service_status ,topTextColor);
        views.setTextColor(R.id.tv_service_msg, bottomTextColor);
        views.setImageViewResource(R.id.iv_status,bottomIcon);

        views.setTextViewText(R.id.tv_alert_btn,btn_text);
        if(!set_action.equals("") && set_action.equals(Constants.ALERT_ACTION)) {
            Intent alertIntent = new Intent(this, TrackingService.class);
            alertIntent.setAction(set_action);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pAlertIntent = PendingIntent.getService(this, 0,
                    alertIntent, 0);


            views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
        }
        else if(!set_action.equals("")){
            Intent cancelAlertIntent = new Intent(this, MainActivity.class);
            cancelAlertIntent.putExtra(MainActivity.CANCEL_ALERT_EXTRA,true);
            cancelAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pAlertIntent = PendingIntent.getActivity(this, 0,
                    cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
        }
        else{
            Intent cancelAlertIntent = new Intent(this, MainActivity.class);
            cancelAlertIntent.putExtra(MainActivity.CANCEL_ALERT_EXTRA,true);
            cancelAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pAlertIntent = PendingIntent.getActivity(this, 0,
                    cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
        }
    }

    boolean isPossibleToSendAlert(){
        return !isAlertActive ;
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager, String CHANNEL_ID) {
        String channel_name = "Tracker";
        String channel_description = "Notifications for tracker";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            channel.setSound(null,null);
            channel.setDescription(channel_description);

            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDataPrepared(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    @Override
    public void onDataPrepared(DeviceData deviceData) {
        data = deviceData;
        dataBaseManager.saveDeviceData(deviceData);
        checkServiceStatus(deviceData);
    }

    @Override
    public void onShowToast(int no_devices) {
        Toast.makeText(this,no_devices ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowToast(String s) {
        Toast.makeText(this,s ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBroadcastMessage(int actionStatusAlertSend) {
        broadcastMessage(actionStatusAlertSend);
    }

    Disposable subscribe;

    //TODO: MAKE THIS METHOD WORK
    public void checkServiceStatus(DeviceData data) {
        if(currentState == ACTION_NETWORK && !isNetworkOnline())
            return;
        if(currentState == ACTION_GPS && !data.getIs_gps_active())
            return;
        if(currentState != ACTION_NETWORK && !isNetworkOnline()){
            Intent newIntent = new Intent(this, TrackingService.class);
            newIntent.setAction(Constants.ASK_TO_TURN_ON_NETWORK_ACTION);
            runService(newIntent);
        }
        else if(currentState != ACTION_GPS && !data.getIs_gps_active()){
            Intent newIntent = new Intent(this, TrackingService.class);
            newIntent.setAction(Constants.ASK_TO_TURN_ON_GPS_ACTION);
            runService(newIntent);
        }
        else if((currentState == ACTION_NETWORK || currentState == ACTION_GPS) && isNetworkOnline() && data.getIs_gps_active()){
            Intent newIntent = new Intent(this, TrackingService.class);
            if(!data.getIs_urgent())
                newIntent.setAction(Constants.START_FOREGROUND_ACTION);
            else
                newIntent.setAction(Constants.ALERT_ACTIVE_ACTION);
            runService(newIntent);
        }
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm!=null) {
                NetworkInfo netInfo = cm.getNetworkInfo(0);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    netInfo = cm.getNetworkInfo(1);
                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                        status = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }

    void  runService(Intent newIntent){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(newIntent);
        }
        else{
            startForegroundService(newIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.is_service_active(false);
        if(subscribe!=null)
            subscribe.dispose();
        presenter.stop(this);
    }
}