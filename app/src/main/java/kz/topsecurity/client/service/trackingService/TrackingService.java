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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.lang.ref.WeakReference;

import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.application.TopSecurityClientApplication;
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
    public static final int ACTION_MRRT_ACCEPTED = 534;
    public static final int ACTION_OPERATOR_CANCELLED = 563;
    public static final int ACTION_MRRT_CHANGED_POSITION = 643;
    public static final int ACTION_OPERATOR_CREATED = 908;
    public static final int ACTION_UNDEFINED_ALERT_SEND = 765;
    public static final int ACTION_AMBULANCE_CALLED = 324;

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
                if(SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance())) {
                    setAlertStatus();
                    Constants.is_service_sending_alert(true);
                }
                presenter.callAlert(data);
                break;
            }
            case Constants.CALL_AMBULANCE_ACTION:{
                if(SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance())) {
                    setAlertStatus();
                    Constants.is_service_sending_alert(true);
                }
                presenter.callAlert(data,2);
                break;
            }
            case Constants.REQUEST_CALL_ACTION:{
                if(SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance())) {
                    setAlertStatus();
                    Constants.is_service_sending_alert(true);
                }
                presenter.callAlert(data,1);
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
                if(SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance())) {
                    setAlertCancelStatus();
                    Constants.is_service_sending_alert(false);
                }
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
                finally {
                    setIdleStatus();
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
        if(!SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance()))
            return;
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
        if(!SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance()))
            return;
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
        WeakReference data = new WeakReference< LocationSettingsRequest.Builder >(builder);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        WeakReference data2 = new WeakReference< SettingsClient >(settingsClient);
        presenter.checkLocationRequest(builder,settingsClient);
        data.clear();
        data2.clear();
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

//    private void setupTelephonyReceiver() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            Log.d(TAG, "== Error On onConnected() Permission not granted");
//            return;
//        }
//        presenter.setupTelephonyReceiver(this);
//    }

    static final String channelId = "channel_02";
    private void sendNotification(String msg, int action_type) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.putExtra(SplashScreen.START_MAIN_SCREEN_KEY,true);
        notificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);

        //notificationIntent.setAction(Constants.MAIN_ACTION);// ??
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.notification_alert);
        WeakReference data = new WeakReference<RemoteViews>(views);
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
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                notificationIntent, 0));
        startForeground(2, notificationBuilder.build());
        data.clear();
    }

    private void placeholderNotification(String msg) {
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
        WeakReference data = new WeakReference< NotificationCompat.Builder>(mBuilder);
        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                resultIntent, 0));
        startForeground(2, mBuilder.build());
        data.clear();
    }

    int darkBlue = android.graphics.Color.rgb(56, 69, 79);
    int lightPink = android.graphics.Color.rgb(255, 0, 92);
    int lightGrey = android.graphics.Color.rgb(183, 183, 183);
    int colorWhite = android.graphics.Color.rgb(255, 255, 255);
    int darkGrey = android.graphics.Color.rgb(148, 148, 148);

    static class CustomNotificationViewHolder {
        static class Builder{
            private int bg_color_info_container;
            private int bg_btn;
            private int btn_image;
            private int notif_icon;
            private int topTextColor;
            private int bottomTextColor;
            private String set_action="" , topText="" , bottomText="" , btn_text="" ;
            private int bottomIcon;

            public int getBg_color_info_container() {
                return bg_color_info_container;
            }

            public Builder setBg_color_info_container(int bg_color_info_container) {
                this.bg_color_info_container = bg_color_info_container;
                return this;
            }

            public int getBg_btn() {
                return bg_btn;
            }

            public Builder setBg_btn(int bg_btn) {
                this.bg_btn = bg_btn;
                return this;
            }

            public int getBtn_image() {
                return btn_image;
            }

            public Builder setBtn_image(int btn_image) {
                this.btn_image = btn_image;
                return this;
            }

            public int getNotif_icon() {
                return notif_icon;
            }

            public Builder setNotif_icon(int notif_icon) {
                this.notif_icon = notif_icon;
                return this;
            }

            public int getTopTextColor() {
                return topTextColor;
            }

            public Builder setTopTextColor(int topTextColor) {
                this.topTextColor = topTextColor;
                return this;
            }

            public int getBottomTextColor() {
                return bottomTextColor;
            }

            public Builder setBottomTextColor(int bottomTextColor) {
                this.bottomTextColor = bottomTextColor;
                return this;
            }

            public String getSet_action() {
                return set_action;
            }

            public Builder setSet_action(String set_action) {
                this.set_action = set_action;
                return this;
            }

            public String getTopText() {
                return topText;
            }

            public Builder setTopText(String topText) {
                this.topText = topText;
                return this;
            }

            public String getBottomText() {
                return bottomText;
            }

            public Builder setBottomText(String bottomText) {
                this.bottomText = bottomText;
                return this;
            }

            public String getBtn_text() {
                return btn_text;
            }

            public Builder setBtn_text(String btn_text) {
                this.btn_text = btn_text;
                return this;
            }

            public int getBottomIcon() {
                return bottomIcon;
            }

            public Builder setBottomIcon(int bottomIcon) {
                this.bottomIcon = bottomIcon;
                return this;
            }

        }
    }

    private void setupView(RemoteViews views, String msg, int action_type){
        CustomNotificationViewHolder.Builder customNotificationViewHolder = new CustomNotificationViewHolder.Builder();

        if(action_type== ACTION_STATUS_ALERT_CANCEL ||
                action_type == ACTION_NEW_SERVICE ||
                action_type == ACTION_STATUS_ALERT_FAILED) {

            views.setOnClickPendingIntent(R.id.rl_alert, null);
            if (action_type == ACTION_NEW_SERVICE  || !isPossibleToSendAlert()) {
                isAlertActive = false;
                SharedPreferencesManager.setAlertActive(this,false);
                customNotificationViewHolder
                        .setSet_action(Constants.ALERT_ACTION)
                        .setBg_btn(lightPink)
                        .setBg_color_info_container(colorWhite)
                        .setBtn_image(R.drawable.ic_call_alert)
                        .setNotif_icon(R.drawable.ic_notification_pink_bg)
                        .setTopText("трекер активен")
                        .setTopTextColor(darkGrey)
                        .setBottomText("Онлайн")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text(getString(R.string.alert));
            }else{
                customNotificationViewHolder
                        .setSet_action(Constants.ALERT_ACTION)
                        .setBg_btn(lightPink)
                        .setBg_color_info_container(colorWhite)
                        .setBtn_image(R.drawable.ic_call_alert)
                        .setNotif_icon(R.drawable.ic_notification_pink_bg)
                        .setTopText("трекер активен")
                        .setTopTextColor(darkGrey)
                        .setBottomText("Онлайн")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text(getString(R.string.alert));
            }
        }
        else if(action_type==ACTION_STATUS_ALERT_CALLED || action_type == ACTION_STATUS_ALERT_SEND)
        {
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            if (isPossibleToSendAlert()) {
                isAlertActive = true;
                SharedPreferencesManager.setAlertActive(this,true);

                customNotificationViewHolder
                        .setSet_action(Constants.ALERT_CANCEL_ACTION)
                        .setBg_btn(darkBlue)
                        .setBg_color_info_container(lightPink)
                        .setBtn_image(R.drawable.ic_cancel_alert)
                        .setNotif_icon(R.drawable.ic_notification_white_bg)
                        .setTopText("тревога отправлена")
                        .setTopTextColor(colorWhite)
                        .setBottomText("Тревога активна")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text(getString(R.string.cancel));
            }else{
                //TODO: IT IS JUST AN PLACEHOLDER

                customNotificationViewHolder
                        .setSet_action(Constants.ALERT_CANCEL_ACTION)
                        .setBg_btn(darkBlue)
                        .setBg_color_info_container(lightPink)
                        .setBtn_image(R.drawable.ic_cancel_alert)
                        .setNotif_icon(R.drawable.ic_notification_white_bg)
                        .setTopText("тревога отправлена")
                        .setTopTextColor(colorWhite)
                        .setBottomText("Тревога активна")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text(getString(R.string.cancel));
            }
        }
        else if(action_type==ACTION_AMBULANCE_CALLED || action_type == ACTION_UNDEFINED_ALERT_SEND)
        {
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            if (isPossibleToSendAlert()) {
                isAlertActive = true;
                SharedPreferencesManager.setAlertActive(this,true);

                customNotificationViewHolder
                        .setBg_btn(darkBlue)
                        .setBg_color_info_container(lightPink)
                        .setBtn_image(0)
                        .setNotif_icon(R.drawable.ic_notification_white_bg)
                        .setTopText("тревога отправлена")
                        .setTopTextColor(colorWhite)
                        .setBottomText("Тревога активна")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text("");
            }else{
                //TODO: IT IS JUST AN PLACEHOLDER
                customNotificationViewHolder
                        .setBg_btn(darkBlue)
                        .setBg_color_info_container(lightPink)
                        .setBtn_image(0)
                        .setNotif_icon(R.drawable.ic_notification_white_bg)
                        .setTopText("тревога отправлена")
                        .setTopTextColor(colorWhite)
                        .setBottomText("Тревога активна")
                        .setBottomTextColor(darkGrey)
                        .setBottomIcon(R.drawable.ic_active)
                        .setBtn_text("");
            }
        }
        else if(action_type == ACTION_GPS){
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            customNotificationViewHolder
                    .setBg_btn(darkGrey)
                    .setBg_color_info_container(colorWhite)
                    .setBtn_image(R.drawable.ic_no_geolocation)
                    .setNotif_icon(R.drawable.ic_notification_pink_bg)
                    .setTopText("не активно")
                    .setTopTextColor(darkGrey)
                    .setBottomText("Включите геолокацию")
                    .setBottomTextColor(darkGrey)
                    .setBottomIcon(R.drawable.ic_not_active)
                    .setBtn_text(getString(R.string.turn_on));
        }
        else if(action_type == ACTION_NETWORK){
            views.setOnClickPendingIntent(R.id.rl_alert, null);
            customNotificationViewHolder
                    .setBg_btn(darkGrey)
                    .setBg_color_info_container(colorWhite)
                    .setBtn_image(R.drawable.ic_no_connection)
                    .setNotif_icon(R.drawable.ic_notification_pink_bg)
                    .setTopText("не активно")
                    .setTopTextColor(darkGrey)
                    .setBottomText("Включите сеть")
                    .setBottomTextColor(darkGrey)
                    .setBottomIcon(R.drawable.ic_not_active)
                    .setBtn_text(getString(R.string.turn_on));

        }else{
            //TODO: IT IS JUST AN PLACEHOLDER
            customNotificationViewHolder
                    .setSet_action(Constants.ALERT_ACTION)
                    .setBg_btn(lightPink)
                    .setBg_color_info_container(colorWhite)
                    .setBtn_image(R.drawable.ic_call_alert)
                    .setNotif_icon(R.drawable.ic_notification_pink_bg)
                    .setTopText("трекер активен")
                    .setTopTextColor(darkGrey)
                    .setBottomText("Онлайн")
                    .setBottomTextColor(darkGrey)
                    .setBottomIcon(R.drawable.ic_active)
                    .setBtn_text(getString(R.string.alert));
        }
        views.setInt(R.id.rl_info_container, "setBackgroundColor",
                customNotificationViewHolder.bg_color_info_container);
        views.setInt(R.id.rl_alert, "setBackgroundColor",
                customNotificationViewHolder.bg_btn);
        views.setImageViewResource(R.id.iv_alert ,customNotificationViewHolder.btn_image);
        views.setImageViewResource(R.id.iv_app_icon, customNotificationViewHolder.notif_icon);


//        views.setTextViewText(R.id.tv_service_status, customNotificationViewHolder.topText);
        views.setTextViewText(R.id.tv_service_msg, customNotificationViewHolder.bottomText);

//        views.setTextColor(R.id.tv_service_status ,customNotificationViewHolder.topTextColor);
        views.setTextColor(R.id.tv_service_msg, customNotificationViewHolder.bottomTextColor);
//        views.setImageViewResource(R.id.iv_status,customNotificationViewHolder.bottomIcon);

        views.setTextViewText(R.id.tv_alert_btn,customNotificationViewHolder.btn_text);
//        if(SharedPreferencesManager.getUserPaymentIsActive(TopSecurityClientApplication.getInstance())) {
            if (customNotificationViewHolder.set_action!=null&& !customNotificationViewHolder.set_action.equals("") && customNotificationViewHolder.set_action.equals(Constants.ALERT_ACTION)) {
                Intent alertIntent = new Intent(this, TrackingService.class);
                alertIntent.setAction(customNotificationViewHolder.set_action);
                alertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pAlertIntent = PendingIntent.getService(this, 0,
                        alertIntent, 0);

                views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
            } else if (customNotificationViewHolder.set_action!=null&& !customNotificationViewHolder.set_action.equals("")&& customNotificationViewHolder.set_action.equals(Constants.ALERT_CANCEL_ACTION)) {
                Intent cancelAlertIntent = new Intent(this, MainActivity.class);
                cancelAlertIntent.putExtra(MainActivity.CANCEL_ALERT_EXTRA, true);
                cancelAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pAlertIntent = PendingIntent.getActivity(this, 0,
                        cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
            } else {
                //Todo : test it
//                Intent cancelAlertIntent = new Intent(this, MainActivity.class);
//                cancelAlertIntent.putExtra(MainActivity.CANCEL_ALERT_EXTRA, true);
//                cancelAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                PendingIntent pAlertIntent = PendingIntent.getActivity(this, 0,
//                        cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                views.setOnClickPendingIntent(R.id.rl_alert, pAlertIntent);
            }

            WeakReference data = new WeakReference<CustomNotificationViewHolder.Builder>(customNotificationViewHolder);
            data.clear();
    }

    boolean isPossibleToSendAlert(){
        return !isAlertActive ;
    }

    String channel_name = "Tracker";
    String channel_description = "Notifications for tracker";

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager, String CHANNEL_ID) {
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

    //TODO: MAKE THIS METHOD WORK PROPERLY
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
        WeakReference data = null;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            data = new WeakReference<ConnectivityManager>(cm);
            if(cm!=null) {
                NetworkInfo netInfo = cm.getNetworkInfo(0);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    netInfo = cm.getNetworkInfo(1);
                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                        status = true;
                }
                data.clear();
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(data!=null)
                data.clear();
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
        if(presenter!=null)
            presenter.stop(this);
    }
}