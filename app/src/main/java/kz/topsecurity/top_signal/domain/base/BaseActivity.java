package kz.topsecurity.top_signal.domain.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.helper.SharedPreferencesManager;
import kz.topsecurity.top_signal.presenter.base.BasePresenter;
import kz.topsecurity.top_signal.service.trackingService.TrackingService;
import kz.topsecurity.top_signal.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.top_signal.utils.GlideApp;
import kz.topsecurity.top_signal.view.base.BaseView;

public abstract class BaseActivity
        <V extends  BaseView,
        S extends BasePresenter ,
        U extends  S >
        extends AppCompatActivity
        implements BaseView{

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Intent mTrackingService;
    private static final String TRACKING_SERVICE = TrackingService.class.getSimpleName();

    ProgressDialog pg_loader;

    protected S presenter;

    protected void hideSoftKeyboard ( View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    protected void initPresenter(U presenterImpl){
        presenter = presenterImpl;
        presenterImpl.attach();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"OnResume fired");
        if(mBroadcastReceiver!=null && !isReceiverRegistered) {
            setupTrackerBroadcastReceiver(mBroadcastReceiver, mAction);
        }
        if(mAlarmBroadcastReceiver!=null && !isAlarmReceiverRegistered) {
            setupAlertBroadcastReceiver(mAlarmBroadcastReceiver, mAlarmAction);
        }
    }

    protected void showLoadingDialog()  {
        pg_loader = ProgressDialog.show(this,"","Loading, please wait ..",true);
    }

    protected void hideProgressDialog() {
        if(pg_loader!=null )
            pg_loader.cancel();
    }

    protected void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }
    protected void showToast(int textMsgResId){
        showToast(getString(textMsgResId));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(presenter!=null)
            presenter.detach();
        super.onDestroy();
    }

    @Override
    public void onShowToast(String msg) {
        showToast(msg);
    }

    @Override
    public void onShowToast(int textMsgResId) {
        showToast(getString(textMsgResId));
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

    @Override
    public void onShowSnackbar(int resMsg, int resActionTxt, View.OnClickListener listener) {
        onShowSnackbar(
                getString(resMsg),
                getString(resActionTxt),
                listener);
    }

    @Override
    public void onShowSnackbar(final String resMsg,final String resActionTxt, final View.OnClickListener listener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        resMsg,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(resActionTxt, listener).show();
            }});
    }


    public void checkAndStartService(Intent intent, Class<?> serviceClass){
        if(!isMyServiceRunning(serviceClass)) {
            startServiceWithCheck(intent);
        }
        else{
            Log.d(TAG,serviceClass.getSimpleName()+" is RUNNING");
        }
    }

    public void broadcastToService(Intent intent){
        startServiceWithCheck(intent);
    }

    private void startServiceWithCheck(Intent intent){
        assert intent.getComponent().getClass()==null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(defineService(intent, intent.getComponent().getClass()));
        }
        else{
            startForegroundService(defineService(intent, intent.getComponent().getClass()));
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) throws NullPointerException{
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private Intent defineService(Intent newIntent, Class<?> serviceClass){
        if(TRACKING_SERVICE.equalsIgnoreCase(serviceClass.getSimpleName())){
            mTrackingService = newIntent;
            return mTrackingService;
        }
        return newIntent;
    }


    boolean isReceiverRegistered =false;
    BroadcastReceiver mBroadcastReceiver;
    IntentFilter mAction;

    boolean isAlarmReceiverRegistered =false;
    BroadcastReceiver mAlarmBroadcastReceiver;
    IntentFilter mAlarmAction;

    protected void setupTrackerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter action){
        mBroadcastReceiver = receiver;
        mAction = action;
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mAction);
        isReceiverRegistered = true;
        Log.d(TAG,"RECEIVER REGISTERED");
    }

    protected void setupAlertBroadcastReceiver(BroadcastReceiver receiver, IntentFilter action){
        mAlarmBroadcastReceiver = receiver;
        mAlarmAction = action;
        LocalBroadcastManager.getInstance(this).registerReceiver(mAlarmBroadcastReceiver, mAlarmAction);
        isAlarmReceiverRegistered = true;
        Log.d(TAG,"Alarm RECEIVER REGISTERED");
    }

    protected void unregisterTrackerBroadcastReceiver(BroadcastReceiver receiver){
        if(isReceiverRegistered && mBroadcastReceiver!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
            isReceiverRegistered = false;
            Log.d(TAG,"RECEIVER UNREGISTERED");
        }
    }

    protected void unregisterAlarmBroadcastReceiver(BroadcastReceiver receiver){
        if(isAlarmReceiverRegistered && mAlarmBroadcastReceiver!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mAlarmBroadcastReceiver);
            isAlarmReceiverRegistered = false;
            Log.d(TAG,"RECEIVER UNREGISTERED");
        }
    }

    public void onPause() {
        Log.d(TAG,"OnPause fired");
        super.onPause();
        unregisterTrackerBroadcastReceiver(mBroadcastReceiver);
        unregisterAlarmBroadcastReceiver(mAlarmBroadcastReceiver);
    }

    public void setAvatar(ImageView iv_user_avatar,String userAvatar){
        String imageStringUri = SharedPreferencesManager.getAvatarUriValue(this);
        if(imageStringUri!=null) {
            setImage(imageStringUri,iv_user_avatar);
        }
        else if(userAvatar!=null){

            GlideApp.with(this)
                    .load("http://gpstracking.muratov.kz"+userAvatar)
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(iv_user_avatar);
        }
    }

    public void setImage(String imagePath, ImageView iv_user_avatar){
        iv_user_avatar.setImageResource(R.drawable.placeholder_avatar);
        if (imagePath!=null && !imagePath.isEmpty()){
            Bitmap bitmap = getBitmap(imagePath);
            if(bitmap!=null){
                setImage(bitmap,iv_user_avatar);
                return;
            }
            else{
                try {
                    setImage(Uri.parse(imagePath),iv_user_avatar);
                }
                catch (Exception ex){

                }
            }
        }
    }

    public void setImage(Uri imageUri, ImageView iv_user_avatar){
        try {
//            imageStream = getContentResolver().openInputStream(imageUri);
//            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            iv_user_avatar.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap=null;
        try {
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public void setImage(Bitmap bmp, ImageView iv_user_avatar){
        try {
//            imageStream = getContentResolver().openInputStream(imageUri);
//            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            iv_user_avatar.setImageBitmap(bmp);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = "kz.topsecurity.top_signal/kz.topsecurity.top_signal.service.trackingService.volumeService.VolumeService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG,"accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG,"Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d(TAG,"***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.d(TAG,"-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG,"We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG,"***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    CustomSimpleDialog customSimpleDialog;

    public void showAreYouSureDialog(String message, CustomSimpleDialog.Callback listener){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        customSimpleDialog = new CustomSimpleDialog();

        Bundle arg = new Bundle();
        arg.putString(CustomSimpleDialog.DIALOG_MESSAGE, message);

        customSimpleDialog.setArguments(arg);
        customSimpleDialog.setCancelable(false);
        customSimpleDialog.setListener(listener);
        customSimpleDialog.show(ft, "dialog");
    }

    public void dissmissAreYouSureDialog(){
        customSimpleDialog.dismiss();
    }
}
