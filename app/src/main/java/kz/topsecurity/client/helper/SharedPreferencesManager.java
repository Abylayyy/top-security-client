package kz.topsecurity.client.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesManager {
    private static final String TAG = SharedPreferencesManager.class.getSimpleName();

    private static final String APP_SETTINGS = "APP_SETTINGS";


    // properties
    private static final String AVATAR_URI_PATH = "AVATAR_URI_PATH";
    private static final String IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN";
    private static final String TRACKING_SERVICE_STATE_KEY = "IS_TRACKING_SERVICE_ACTIVE_KEY";
    private static final String VOLUME_TRACKING_SERVICE_STATE_KEY = "IS_VOLUME_TRACKING_SERVICE_ACTIVE_KEY";
    private static final String VOLUME_DIRECTION_UP_KEY = "IS_VOLUME_DIRECTION_UP_KEY";
    private static final String VOLUME_BUTTON_CLICK_RATE_KEY = "VOLUME_BUTTON_CLICK_RATE_KEY";
    private static final String USER_AUTH_TOKEN_KEY = "USER_AUTH_TOKEN_KEY";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_IMEI_KEY = "USER_IMEI_KEY";
    private static final String IS_SERVICE_SENDING_ALERT = "IS_SERVICE_SENDING_ALERT";
    private static final String IS_SERVICE_ACTIVE_KEY = "IS_SERVICE_ACTIVE_KEY";
    private static final String IS_ALERT_ACTIVE_KEY = "IS_ALERT_ACTIVE_KEY";
    private static final String SERVICE_STATE_KEY = "SERVICE_STATE_KEY";
    private static final String IS_FIRST_START_KEY = "IS_FIRST_START_KEY";
    // other properties...


    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean getUserData(Context context) {
        return getSharedPreferences(context).getBoolean(IS_USER_LOGGED_IN, false);
    }

    public static void setUserData(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_USER_LOGGED_IN, newValue);
        editor.commit();
    }

    public static String getAvatarUriValue(Context context) {
        return getSharedPreferences(context).getString(AVATAR_URI_PATH, null);
    }

    public static void setAvatarUriValue(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(AVATAR_URI_PATH, newValue);
        editor.commit();
    }

    public static boolean getTrackingServiceActiveState(Context context) {
        return getSharedPreferences(context).getBoolean(TRACKING_SERVICE_STATE_KEY, true);
    }

    public static void setTrackingServiceActiveState(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(TRACKING_SERVICE_STATE_KEY, newValue);
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static boolean getVolumeTrackingServiceActiveState(Context context) {
        return getSharedPreferences(context).getBoolean(VOLUME_TRACKING_SERVICE_STATE_KEY, true);
    }

    public static void setVolumeTrackingServiceActiveState(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(VOLUME_TRACKING_SERVICE_STATE_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static boolean getVolumeDirection(Context context) {
        return getSharedPreferences(context).getBoolean(VOLUME_DIRECTION_UP_KEY, true);
    }

    public static void setVolumeDirection(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(VOLUME_DIRECTION_UP_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static int getVolumeButtonClickRate(Context context){
        return getSharedPreferences(context).getInt(VOLUME_BUTTON_CLICK_RATE_KEY, 0);
    }

    public static void setVolumeButtonClickRate(Context context, int newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(VOLUME_BUTTON_CLICK_RATE_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static String getUserAuthToken(Context context){
        return getSharedPreferences(context).getString(USER_AUTH_TOKEN_KEY, null);
    }

    public static void setUserAuthToken(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_AUTH_TOKEN_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static String getUserPhone(Context context){
        return getSharedPreferences(context).getString(USER_PHONE_KEY, null);
    }

    public static void setUserPhone(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_PHONE_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static void clearData(Context context){
        setUserData(context , false);
        setAvatarUriValue(context , null);
        setTrackingServiceActiveState(context , true);
        setVolumeTrackingServiceActiveState(context , true);
        setVolumeDirection(context , true);
        setVolumeButtonClickRate(context ,0  );
        setUserAuthToken(context , null);
        setUserPhone(context,null);
        setBackgroundServiceState(context, 0);
        setAlertActive(context,false);
    }

    public static String getPhoneImei(Context context){
        return getSharedPreferences(context).getString(USER_IMEI_KEY, null);
    }

    public static void setPhoneImei(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_IMEI_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }


    public static boolean getIsServiceSendingAlert(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SERVICE_SENDING_ALERT, false);
    }

    public static void setIsServiceSendingAlert(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SERVICE_SENDING_ALERT, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static boolean getIsServiceActive(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SERVICE_ACTIVE_KEY, false);
    }

    public static void setIsServiceActive(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SERVICE_ACTIVE_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static boolean getAlertActive(Context context) {
        return getSharedPreferences(context).getBoolean(IS_ALERT_ACTIVE_KEY, false);
    }


    public static void setAlertActive(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_ALERT_ACTIVE_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

    public static int getBackgroundServiceState(Context context){
        return getSharedPreferences(context).getInt(SERVICE_STATE_KEY,0);
    }

    public static void setBackgroundServiceState(Context context , int state){
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SERVICE_STATE_KEY, state);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }


    public static boolean getIsFirstStart(Context context) {
        return getSharedPreferences(context).getBoolean(IS_FIRST_START_KEY, true);
    }

    public static void setIsFirstStart(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_FIRST_START_KEY, newValue);
        //editor.apply();
        boolean isSuccessful = editor.commit();
        if(!isSuccessful){
            Log.e(TAG,"Put value failed");
        }
    }

}
