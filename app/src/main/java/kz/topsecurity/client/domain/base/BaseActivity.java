package kz.topsecurity.client.domain.base;

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
import java.lang.ref.WeakReference;

import kz.topsecurity.client.R;
import kz.topsecurity.client.fragments.tutorial.TutorialFragment;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.presenter.base.BasePresenter;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.utils.GlideApp;
import kz.topsecurity.client.view.base.BaseView;

public abstract class BaseActivity
        <V extends  BaseView,
        S extends BasePresenter ,
        U extends  S >
        extends HelperActivity
        implements BaseView,
        TutorialFragment.OnFragmentInteractionListener{

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Intent mTrackingService;
    private static final String TRACKING_SERVICE = TrackingService.class.getSimpleName();


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

    public void showTutorials(int type) {
        if(SharedPreferencesManager.getIsTutsShown(this))
            return;
        if(findViewById(R.id.fl_tuts_fragment_container) == null)
            return;
        String tutsPage = "";
        switch (type){
            case TutorialFragment.MAIN_ACTIVITY:{
                tutsPage = SharedPreferencesManager.TutsPages.MAIN_PAGE;
                break;
            }
            case TutorialFragment.CONTACTS_ACTIVITY:{
                tutsPage = SharedPreferencesManager.TutsPages.CONTACTS_PAGE;
                break;
            }
            case TutorialFragment.PLACES_ACTIVITY:{
                tutsPage = SharedPreferencesManager.TutsPages.PLACES_PAGE;
                break;
            }
            case TutorialFragment.SETTINGS_ACTIVITY:{
                tutsPage = SharedPreferencesManager.TutsPages.SETTTINGS_PAGE;
                break;
            }
        }

        if(!tutsPage.isEmpty() && SharedPreferencesManager.getIsTutsShown(this,tutsPage))
            return;

        try {
            // Create a new Fragment to be placed in the activity layout
            findViewById(R.id.fl_tuts_fragment_container).setVisibility(View.VISIBLE);
            TutorialFragment firstFragment = TutorialFragment.newInstance( type );
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_tuts_fragment_container, firstFragment, TutorialFragment.class.getSimpleName()).commit();
        }
        catch (Exception ex){

        }
    }

    public void removeTutorial() {
        try {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TutorialFragment.class.getSimpleName());
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            if(findViewById(R.id.fl_tuts_fragment_container)!=null)
                findViewById(R.id.fl_tuts_fragment_container).setVisibility(View.GONE);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
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
        WeakReference data = new WeakReference<String>(imageStringUri);
        if(imageStringUri!=null) {
            setImage(imageStringUri,iv_user_avatar);
        }
        else if(userAvatar!=null){
            GlideApp.with(this)
                    .load(userAvatar)
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(iv_user_avatar);
        }
        data.clear();
    }

    public void setImage(String imagePath, ImageView iv_user_avatar){
        iv_user_avatar.setImageResource(R.drawable.placeholder_avatar);
        if (imagePath!=null && !imagePath.isEmpty()){
            Bitmap bitmap = getBitmap(imagePath);
            WeakReference data = new WeakReference<Bitmap>(bitmap);
            if(bitmap!=null){
                setImage(bitmap,iv_user_avatar);
                data.clear();
                return;
            }
            else{
                try {
                    setImage(Uri.parse(imagePath),iv_user_avatar);
                    data.clear();
                }
                catch (Exception ex){

                }
            }
        }
    }

    public void setImage(Uri imageUri, ImageView iv_user_avatar){
        WeakReference data =null;
        try {
//            imageStream = getContentResolver().openInputStream(imageUri);
//            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            data = new WeakReference<Bitmap>(selectedImage);
            iv_user_avatar.setImageBitmap(selectedImage);
            data.clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if(data!=null)
                data.clear();
        }
    }

    public Bitmap getBitmap(String path) {
        WeakReference fileData =null;
        WeakReference data =null;
        Bitmap bitmap=null;
        try {
            File f= new File(path);
            fileData = new WeakReference<File>(f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            fileData.clear();
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(fileData!=null)
                fileData.clear();
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

    final String serviceLink = "kz.topsecurity.client/kz.topsecurity.client.service.trackingService.volumeService.VolumeService";
    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
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
                    if (accessabilityService.equalsIgnoreCase(serviceLink)) {
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




    @Override
    public void onOkButtonClick() {
        removeTutorial();
    }
}
