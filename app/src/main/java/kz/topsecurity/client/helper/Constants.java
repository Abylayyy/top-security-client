package kz.topsecurity.client.helper;


import android.content.Context;

import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;

public class Constants {

    public static final boolean IS_DEBUG = true;

    public static final int LOCATION_INTERVAL = 30*1000;
    public static final int FASTEST_LOCATION_INTERVAL = 5*1000;
    public static final int TIMER_WAKE_UP_INTERVAL = 15*1000;
    public static final int TIMER_IDLE_WAKE_UP_INTERVAL = 20*60*1000;

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;
    public static final String PACKAGE_NAME =
            "kz.topsecurity.client";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String LOCATION_NAME_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";

    public static final String MAIN_ACTION = PACKAGE_NAME+".action.main";

    public static final String START_FOREGROUND_ACTION = PACKAGE_NAME+".action.start";
    public static final String RESTART_FOREGROUND_SERVICE = PACKAGE_NAME+".action.restart_service";;
    public static final String ALERT_ACTION = PACKAGE_NAME+".action.alert";
    public static final String ALERT_ACTIVE_ACTION = PACKAGE_NAME+".action.alert_active";
    public static final String ALERT_FAILED_ACTION = PACKAGE_NAME+".action.alert_failed";
    public static final String ALERT_CANCEL_ACTION = PACKAGE_NAME+".action.cancel_alert";
    public static final String ALERT_CANCEL_FAILED_ACTION = PACKAGE_NAME+".action.alert_cancel_failed";
    public static final String ALERT_CANCELED_ACTION = PACKAGE_NAME+".action.alert_stopped";
    public static final String ASK_TO_TURN_ON_GPS_ACTION = PACKAGE_NAME+".action.ask_gps";
    public static final String ASK_TO_TURN_ON_NETWORK_ACTION = PACKAGE_NAME+".action.ask_network";

    public static final String CLIENT_DEVICE_TYPE = "mobile";
    public static final String CLIENT_DEVICE_PLATFORM_TYPE = "android";
   // public static User userData = new User();

    //TODO: DEVELOP MODE
    public static final boolean DEVELOP_MODE = true;

    public static final String GoogleApiBaseURL = "https://www.googleapis.com/geolocation/v1/geolocate";
    public static final String WheatherApiBaseURL = "https://api.openweathermap.org/data/2.5/weather";

    public static final String[] click_type_rate = {"3", "4", "5", "6"};

    public static void clearData(Context ctx) {
//        deviceData = new DeviceData();
        DataBaseManager dataBaseManager = new DataBaseManagerImpl(ctx);
        dataBaseManager.dropClientData();
        dataBaseManager.dropDeviceDataTable();
        SharedPreferencesManager.setIsServiceActive(ctx,false);
        SharedPreferencesManager.setIsServiceSendingAlert(ctx,false);
        SharedPreferencesManager.setUserAuthToken(ctx,null);
    }

    public static boolean is_service_sending_alert(){
        return SharedPreferencesManager.getIsServiceSendingAlert(TopSecurityClientApplication.getInstance().getApplicationContext());
    }

    public static void is_service_sending_alert(boolean status){
        SharedPreferencesManager.setIsServiceSendingAlert(TopSecurityClientApplication.getInstance().getApplicationContext(),status);
    }

    public static boolean is_service_active(){
        return SharedPreferencesManager.getIsServiceActive(TopSecurityClientApplication.getInstance().getApplicationContext());
    }

    public static void is_service_active(boolean status){
        SharedPreferencesManager.setIsServiceActive(TopSecurityClientApplication.getInstance().getApplicationContext(),status);
    }

    public static String getGoogleMapKey() {
        return "AIzaSyCEXiQiHX7V8qJDuBvFTy6RW9WaGShPAO8";
    }

    public class BlockedFunctions{
        public static final boolean isTwoCodeEnabled = false;
    }

    public class ERROR_STATES {
        public static final int WRONG_PASSWORD_CODE = 101;
        public static final int NOT_AUTHENTICATED_CODE = 102;
        public static final int USER_NOT_FOUND_CODE = 103;
        public static final int ALREADY_AUTHENTICATE_CODE = 104;
        public static final int PHONE_NUMBER_EXISTS_CODE = 106 ;
        public static final int SUCCESSFUL_AUTHENTICATE_CODE = 1;
        public static final int SUCCESSFUL_REGISTERED_CODE = 2;
        public static final int PASSWORD_MISSMATCH_CODE = 6101;
        public static final int PASSWORD_SUCCESSFUL_CHANGED_CODE = 6001;
        public static final int PROFILE_SUCCESSFUL_SAVED_CODE = 6002;
        public static final int PHOTO_SUCCESSFUL_SAVED_CODE   = 6003;
        public static final int SECRET_SUCCESS_SAVED_CODE     = 6004;
        public static final int DEVICE_ALREADY_LINKED_CODE = 9101;
        public static final int CONCURRENCY_ERROR_CODE = 3101;
        public static final int SUCCESSFUL_CREATED_CODE = 3002;
        public static final int SUCCESSFUL_UPDATED_CODE = 3003;
        public static final int SUCCESSFUL_ACCEPTED_CODE = 3009;
        public static final int INTERNAL_ERROR_CODE = 0;
        public static final int VALIDATION_ERROR_CODE = 2101;
        public static final int ACTION_NOT_FOUND_CODE = 11101;
        public static final int ACTION_IS_EXPIRED_CODE = 11102;
        public static final int ALERT_NOT_EXISTS_CODE = 4101;
        public static final int ALERT_IS_EXISTS_CODE = 4102;
        public static final int ALERT_SUCCESSFUL_CREATED_CODE = 4001;
        public static final int ALERT_SUCCESSFUL_CHECKED_CODE = 4002;
        public static final int ALERT_SUCCESSFUL_CANCELLED_CODE = 4003;
    }
}
