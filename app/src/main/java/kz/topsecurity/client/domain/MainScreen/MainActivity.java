package kz.topsecurity.client.domain.MainScreen;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.topsecurity.client.domain.FeedbackScreen.FeedbackActivity;
import kz.topsecurity.client.domain.AlertHistoryScreen.AlertHistoryActivity;
import kz.topsecurity.client.domain.PaymentScreen.PaymentActivity;
import kz.topsecurity.client.domain.PlaceScreen.PlaceActivity;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.SettingsScreen.SettingsActivity;
import kz.topsecurity.client.domain.SplashScreen.SplashScreen;
import kz.topsecurity.client.domain.TrustedNumbersScreen.TrustedNumbersActivity;
import kz.topsecurity.client.domain.informationScreen.InformationActivity;
import kz.topsecurity.client.fragments.tutorial.TutorialFragment;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.PhoneHelper;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.mainPresenter.MainPresenterImpl;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.service.trackingService.interfaces.TrackingServiceBroadcastReceiverListener;
import kz.topsecurity.client.service.trackingService.listenerImpl.TrackingServiceBroadcastReceiver;
import kz.topsecurity.client.service.trackingService.managers.DataProvider;
import kz.topsecurity.client.service.trackingService.model.DeviceData;
import kz.topsecurity.client.ui_widgets.customDialog.CustomDialog;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.view.mainView.MainView;

public class MainActivity extends ServiceControlActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        View.OnClickListener ,
        MainView ,
        TrackingServiceBroadcastReceiverListener  {

    public static final String CANCEL_ALERT_EXTRA = "CANCEL_ALERT_EXTRA";
    public static final String TRACKING_SERVICE_STATUS_RESULT = "IS_MADE_CHANGES";
    public static final String EXIT_FROM_APPLICATION = "EXIT_FROM_APPLICATION";
    public static final String SHOULD_FINISH = "SHOULD_FINISH";
    private static final String ANIMATION_STATE_KEY = "ANIMATION_STATE";

    @BindView(R.id.ll_profile) LinearLayout ll_profile;
    @BindView(R.id.ll_places) LinearLayout ll_places;
    @BindView(R.id.ll_contacts) LinearLayout ll_contacts;
    @BindView(R.id.ll_settings) LinearLayout ll_settings;
    @BindView(R.id.ll_payment) LinearLayout ll_payment;
    @BindView(R.id.ll_feedback) LinearLayout ll_feedback;
    @BindView(R.id.ll_info) LinearLayout ll_info;
    @BindView(R.id.ll_alert_history) LinearLayout ll_alert_history;
    @BindView(R.id.btn_alert) Button btn_alert;
    @BindView(R.id.btn_cancel_alert) Button btn_cancel_alert;
    @BindView(R.id.btn_minimize_app) Button btn_minimize_app;
    @BindView(R.id.iv_user_avatar) CircleImageView iv_user_avatar;
    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.tv_user_phone) TextView tv_user_phone;
    @BindView(R.id.tv_user_email) TextView tv_user_email;
    @BindView(R.id.iv_circle) ImageView iv_circle;
    @BindView(R.id.iv_circle2) ImageView iv_circle2;
    @BindView(R.id.iv_dash_circle) ImageView iv_dash_circle;
    @BindView(R.id.iv_icon) ImageView iv_icon;

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean isAlertViewVisible = false;
    private static final int PROFILE_REQUEST_CODE = 723;
    private static final int SETTINGS_REQUEST_CODE = 486;
    private static final int PAYMENT_REQUEST_CODE = 236;
    private static final int ALERT_HISTORY_CODE = 175;
    private static final int ABOUT_CODE = 818;

    AnimatorSet rippleAnimatorSet, circleAnimatorSet;
    RotateAnimation carAnimation  ;
    int currentAnimationState = -1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PROFILE_REQUEST_CODE:{
                presenter.updateDrawerData(this);
//                if(data.hasExtra(SHOULD_FINISH) && data.getBooleanExtra(SHOULD_FINISH,false)){
//                    finish();
//                }
                break;
            }
            case SETTINGS_REQUEST_CODE:{
                if(data.getBooleanExtra(MainActivity.EXIT_FROM_APPLICATION,false)) {
                    presenter.exitFromApplication(this);
                }
                break;
            }
            case  REQUEST_CHECK_SETTINGS :{
                if (resultCode == Activity.RESULT_OK) {
                    if(!isGpsAlertShown) {
                        isGpsAlertShown = true;
                        restartService();
                    }
                }
                break;
            }
            case PAYMENT_REQUEST_CODE:{
                if (resultCode == Activity.RESULT_OK) {
//                    if(checkIfUserPhotoIsNessesarry()){
////                        showAddYourPhotoDialog();
//                    }
                }
                if(!SharedPreferencesManager.getUserPaymentIsActive(this)){
                    presenter.checkPlan();
                }
            }
        }
    }

    CustomSimpleDialog needAvatarToWorkProperlyDialog;

    private boolean checkIfUserPhotoIsNessesarry(){
        String imageStringUri = SharedPreferencesManager.getAvatarUriValue(this);
        boolean isUserMadePayment = SharedPreferencesManager.getUserPaymentIsActive(this);
        return (isUserMadePayment &&( imageStringUri==null || imageStringUri.isEmpty()));
    }

//    private void showAddYourPhotoDialog() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//        needAvatarToWorkProperlyDialog = new CustomSimpleDialog();
//
//        Bundle arg = new Bundle();
//        arg.putString(CustomSimpleDialog.DIALOG_MESSAGE, getString(R.string.need_avatar_to_work_properly));
//
//        needAvatarToWorkProperlyDialog.setArguments(arg);
//        needAvatarToWorkProperlyDialog.setCancelable(false);
//        needAvatarToWorkProperlyDialog.setListener(new CustomSimpleDialog.Callback() {
//            @Override
//            public void onCancelBtnClicked() {
//                if(needAvatarToWorkProperlyDialog!=null)
//                    needAvatarToWorkProperlyDialog.dismiss();
//            }
//
//            @Override
//            public void onPositiveBtnClicked() {
//                ll_profile.performClick();
//                if(needAvatarToWorkProperlyDialog!=null)
//                    needAvatarToWorkProperlyDialog.dismiss();
//            }
//        });
//        needAvatarToWorkProperlyDialog.show(ft, "dialog");
//    }

    @Override
    public void exitFromMainScreen(){
        startActivity(new Intent(MainActivity.this, SplashScreen.class));
        finish();
    }

    @Override
    public void userPlanChanged(boolean isActive) {
        showCallAlertView();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        currentAnimationState = savedInstanceState.getInt(ANIMATION_STATE_KEY);
        if (currentAnimationState != -1) {
            isRippleAnimationActive = false;
            startRippleAnimation(currentAnimationState);
        }
        else
            stopRippleAnimation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ANIMATION_STATE_KEY, currentAnimationState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAlertBtn();
        presenter.checkStatus();
    }

    private void checkAlertBtn() {
        if(SharedPreferencesManager.getIsServiceActive(this))
            btn_cancel_alert.setEnabled(true);
        else
            btn_alert.setEnabled(true);
    }

    @Override
    protected void setAlertActiveView() {
        if(!isAlertViewVisible){
            runOnUiThread(()->{
                if(presenter!=null) {
                    presenter.setAlertActive(true);
                    showCancelAlertView();
                }
            });
        }
    }

    @Override
    protected void blockAlertButton(boolean state){
        btn_alert.setEnabled(!state);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(Constants.is_service_sending_alert()){
            showCancelAlertView();
            presenter.setAlertActive(true);
        }
        boolean isCancelAlertAction = intent.getBooleanExtra(CANCEL_ALERT_EXTRA,false);
        if (!SharedPreferencesManager.getUserPaymentIsActive(this))
            return;
        if(isCancelAlertAction){
//            presenter.actionWithCheck();
            presenter.actionCancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Тревога");
        if (savedInstanceState != null) {
           currentAnimationState = savedInstanceState.getInt(ANIMATION_STATE_KEY);
        }
        setDrawerSettings(toolbar);

        ll_profile.setOnClickListener(this);
        ll_places.setOnClickListener(this);
        ll_contacts.setOnClickListener(this);
        ll_settings.setOnClickListener(this);
        ll_payment.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_info.setOnClickListener(this);
        ll_alert_history.setOnClickListener(this);
        btn_alert.setOnClickListener(this);
        btn_cancel_alert.setOnClickListener(this);
        btn_minimize_app.setOnClickListener(this);
        iv_user_avatar.setOnClickListener(this);
        initPresenter(new MainPresenterImpl(this));
        presenter.logToken();

        setupBroadcastReceiver();
        if(Constants.is_service_sending_alert()){
            showCancelAlertView();
            presenter.setAlertActive(true);
        }
        else{
            showCallAlertView();
        }

        if(getIntent().getBooleanExtra(CANCEL_ALERT_EXTRA,false)){
            presenter.actionCancel();
        }
        presenter.updateDrawerData(this);
        btn_alert.setEnabled(false);
        presenter.checkStatus();
        checkTutsStatus(savedInstanceState);
//        if(checkIfUserPhotoIsNessesarry() && SharedPreferencesManager.getIsTutsShown(this))
//            showAddYourPhotoDialog();

    }

    private void setDrawerSettings(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!drawer.isDrawerOpen(GravityCompat.START)) {
                        // starts opening
                        if(isAlertViewVisible)
                            stopRippleAnimation();
                    } else {
                        if( isAlertViewVisible)
                            startRippleAnimation(currentAnimationState);
                    }
                    invalidateOptionsMenu();
                }
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    int waitingColor = R.color.colorPrimary;
    int alertColor = R.color.colorAccent;
    int troubleColor = R.color.colorSecondaryRed;

    ArrayList<Animator> animatorList = new ArrayList<>();
    boolean isRippleAnimationActive = false;

    private void startRippleAnimation(int type) {
        if(type==-1) {
            stopRippleAnimation();
            return;
        }
        iv_circle.setVisibility(View.VISIBLE);
        iv_circle2.setVisibility(View.VISIBLE);
        iv_icon.setVisibility(View.VISIBLE);
        iv_dash_circle.setVisibility(View.VISIBLE);
        if(currentAnimationState==type && isRippleAnimationActive)
            return;
        currentAnimationState = type;
        animatorList = new ArrayList<>();
        changeRippleType(type);
        int rippleDurationTime = 1800;
        rippleAnimatorSet = new AnimatorSet();
        rippleAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleAnimatorSet.setDuration(rippleDurationTime);
        setAnimator(iv_circle,rippleDurationTime/2,0);
        setAnimator(iv_circle2,rippleDurationTime/2,rippleDurationTime/2);
        rippleAnimatorSet.playTogether(animatorList);
        rippleAnimatorSet.start();
        isRippleAnimationActive = true;
        startCircleAnimation();
    }

    public static final int ALERT_SEND = 836;
    public static final int ORDER_CREATED = 465;
    public static final int ORDER_ACCEPTED = 529;
    public static final int MRRT_CHANGED_POSIITION = 651;
    public static final int TROUBLES = 324;

    private void changeRippleType(int type) {
        stopCarAnimation();
        if (type == ALERT_SEND) {
            setColorToImageView(waitingColor);
            iv_icon.setVisibility(View.GONE);
        }
        if (type == ORDER_CREATED) {
            setColorToImageView(waitingColor);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.ic_operator);
        }
        if (type == ORDER_ACCEPTED) {
            setColorToImageView(alertColor);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.ic_mrrt_car);
        }
        if (type == MRRT_CHANGED_POSIITION) {
            setColorToImageView(alertColor);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.ic_mrrt_car);
            startCarAnimation();
        }
        if (type == TROUBLES) {
            setColorToImageView(troubleColor);
            iv_icon.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(R.drawable.ic_error);
        }
    }

    private void setColorToImageView(int color) {
        iv_circle.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.MULTIPLY);
        iv_circle2.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void setAnimator(ImageView imageView, int anim_time, int delay) {
        final ObjectAnimator alphaAnimator1= ObjectAnimator.ofFloat(imageView, "ScaleX", 1.0f, 6.0f);
        alphaAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator1.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator1.setStartDelay(delay);
        alphaAnimator1.setDuration(anim_time);
        animatorList.add(alphaAnimator1);
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(imageView, "ScaleY", 1.0f, 6.0f);
        alphaAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator2.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator2.setStartDelay(delay);
        alphaAnimator2.setDuration(anim_time);
        animatorList.add(alphaAnimator2);
        final ObjectAnimator alphaAnimator3= ObjectAnimator.ofFloat(imageView, "Alpha", 1.0f, 0.0f);
        alphaAnimator3.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator3.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator3.setStartDelay(delay);
        alphaAnimator3.setDuration(anim_time);
        animatorList.add(alphaAnimator3);
    }

    private void stopRippleAnimation() {
        if(rippleAnimatorSet!=null)
            rippleAnimatorSet.cancel();
        iv_circle.setVisibility(View.GONE);
        iv_circle2.setVisibility(View.GONE);
        iv_icon.setVisibility(View.GONE);
        iv_dash_circle.setVisibility(View.GONE);
        isRippleAnimationActive = false;
        stopCircleAnimation();
        stopCarAnimation();
    }

    private void stopCircleAnimation() {
        if(circleAnimatorSet!=null)
            circleAnimatorSet.cancel();
    }

    private void startCircleAnimation(){
        circleAnimatorSet = new AnimatorSet();
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(iv_dash_circle, "rotation", 0f, 360f);
        alphaAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator2.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator2.setDuration(10*1000);
        circleAnimatorSet.play(alphaAnimator2);
        circleAnimatorSet.start();
    }

    private void startCarAnimation() {//iv_dash_circle
        int width = iv_icon.getWidth();
        int width2 = iv_dash_circle.getWidth();
//        carAnimation = new RotateAnimation(0.0f, 360.0f,
//                Animation.ABSOLUTE, x/2, Animation.ABSOLUTE,
//                y/2);
        carAnimation = new RotateAnimation(0.0f, 360.0f,
                Animation.ABSOLUTE, width/2, Animation.ABSOLUTE,
                (width+width2/2));
        carAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        carAnimation.setRepeatMode(ObjectAnimator.RESTART);
        carAnimation.setDuration(15*1000);
        iv_icon.startAnimation(carAnimation);
//        carAnimation.start();
    }

    private void stopCarAnimation() {
        if(carAnimation!=null)
            carAnimation.cancel();
    }

    private void checkTutsStatus(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showTutorials(TutorialFragment.MAIN_ACTIVITY);
            }
        },100);
    }

    @Override
    public void onAlertNotActive() {
        showCallAlertView();
        hideProgressDialog();
        btn_alert.setEnabled(true);
        restoreStatus();
    }

    @Override
    public void onStoppingAlert() {
        onCancelAlert();
    }

    @Override
    public void onCallingAlert() {
        if(!checkGPS() ) {
            isGpsAlertShown = false;
            createAndCheckLocationProvider();
            return;
        }
        presenter.callAlert();
    }

    @Override
    public void setAnimationStatus(int status) {
        startRippleAnimation(status);
    }

    private void restoreStatus() {

    }

    @Override
    public void onAlertIsActive() {
        hideProgressDialog();
//        Constants.is_service_sending_alert(true);
        presenter.setAlertActive(true);
        setTrackerAlertActiveStatus();
        showCancelAlertView();
        restoreStatus();
    }

    private void setTrackerAlertActiveStatus() {
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_ACTIVE_ACTION);
        broadcastToService(intent);
    }

    @Override
    public void setDrawerData(Client clientData) {
        String userAvatar = null;
        if (clientData != null) {
            tv_user_name.setText(clientData.getUsername());
            String phone = clientData.getPhone();
            tv_user_phone.setText(PhoneHelper.getFormattedPhone(phone));
            tv_user_email.setText(clientData.getEmail());
            userAvatar = clientData.getPhoto();
        }
        if (iv_user_avatar != null)
            setAvatar(iv_user_avatar,userAvatar);
        WeakReference data = new WeakReference<String>(userAvatar);
        data.clear();
    }

    private void setupBroadcastReceiver() {
        setupTrackerBroadcastReceiver(new TrackingServiceBroadcastReceiver(this), new IntentFilter(DataProvider.FILTER_ACTION_LOCATION_BROADCAST));
        setupAlertBroadcastReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int actionType = intent.getIntExtra(TrackingService.EXTRA_ACTION_TYPE,-1);
                switch (actionType){
                    case TrackingService.ACTION_STATUS_ALERT_CALLED:{
                        presenter.setAlertActive(true);
                        btn_alert.setEnabled(false);
                        btn_cancel_alert.setEnabled(false);
                        showCancelAlertView();
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_SEND:{
                        btn_cancel_alert.setEnabled(true);
//                        rippleBackground./setRippleColor(getResources().getColor(R.color.colorAccent));
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_FAILED:{
                        presenter.setAlertActive(false);
                        btn_alert.setEnabled(true);
                        btn_cancel_alert.setEnabled(true);
                        showCallAlertView();
                        showToast(R.string.failed_to_send_alert);
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL:{
                        presenter.setAlertActive(false);
                        btn_alert.setEnabled(false);
                        btn_cancel_alert.setEnabled(false);
                        showCallAlertView();
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL_SEND:{
                        btn_alert.setEnabled(true);
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL_FAILED:{
                        presenter.setAlertActive(true);
                        btn_alert.setEnabled(true);
                        btn_cancel_alert.setEnabled(true);
                        showCancelAlertView();
                        showToast(R.string.failed_to_cancel_alert);
                        break;
                    }
                    case TrackingService.ACTION_GPS_NOT_AVAILABLE:{
                        createAndCheckLocationProvider();
                        break;
                    }
                    case TrackingService.ACTION_OPERATOR_CREATED:{
                        startRippleAnimation(ORDER_CREATED);
                        break;
                    }
                    case TrackingService.ACTION_MRRT_ACCEPTED:{
                        startRippleAnimation(ORDER_ACCEPTED);
                        break;
                    }
                    case TrackingService.ACTION_OPERATOR_CANCELLED:{
                        presenter.cancelAlert();
                        break;
                    }
                    case TrackingService.ACTION_MRRT_CHANGED_POSITION:{
                        startRippleAnimation(MRRT_CHANGED_POSIITION);
                        break;
                    }
                }
            }
        }, new IntentFilter(TrackingService.FILTER_ACTION_SERVICE_BROADCAST));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_places) {
            if(!Constants.is_service_sending_alert())
                startActivity(new Intent(this, PlaceActivity.class));
            else
                onShowToast(R.string.you_cant_when_alert_active);
            return true;
        }
        else if (item.getItemId() == R.id.action_contacts) {
            if(!Constants.is_service_sending_alert())
                startActivity(new Intent(this, TrustedNumbersActivity.class));
            else
                onShowToast(R.string.you_cant_when_alert_active);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        boolean ifNavButtons = false;
        switch(view.getId()){
            case R.id.iv_user_avatar:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(this,ProfileActivity.class),PROFILE_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_profile:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(this,ProfileActivity.class),PROFILE_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_places:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivity(new Intent(this, PlaceActivity.class));
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_contacts : {
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivity(new Intent(this, TrustedNumbersActivity.class));
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_settings:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),SETTINGS_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_payment:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, PaymentActivity.class),PAYMENT_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_alert_history:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, AlertHistoryActivity.class),ALERT_HISTORY_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_feedback:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, FeedbackActivity.class), ABOUT_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.ll_info:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, InformationActivity.class), ABOUT_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.btn_alert:{
                if(!checkNetwork()){
                    return;
                }
                else if(!mAlreadyStartedService && !Constants.is_service_active() && !SharedPreferencesManager.getTrackingServiceActiveState(this)){
                    promptTurnOnService();
                }
                else {
                    presenter.actionAlert();
                }
                break;
            }
            case R.id.btn_cancel_alert:{
                if(!checkNetwork()){
                    return;
                }
                else {
                    presenter.actionCancel();
                }
                break;
            }
            case R.id.btn_minimize_app:{
                minimizeApp();
                break;
            }
            default:{
                showToast("Not exits");
                break;
            }
        }

        if(ifNavButtons) {
            closeDrawer();
        }
    }

    private void closeDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void minimizeApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onAlert() {
        btn_cancel_alert.setEnabled(false);
        sendAlert();
    }

    void sendAlert(){
        if(!SharedPreferencesManager.getUserPaymentIsActive(this)) {
            showAreYouSureDialog(getString(R.string.user_do_not_make_the_payment_to_call_alert), new CustomSimpleDialog.Callback() {
                @Override
                public void onCancelBtnClicked() {
                    dissmissAreYouSureDialog();
                }

                @Override
                public void onPositiveBtnClicked() {
                    startActivity(new Intent(MainActivity.this,PaymentActivity.class));
                    dissmissAreYouSureDialog();
                }
            });
            return;
        }
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_ACTION);
        broadcastToService(intent);
    }

    void cancelAlert(){
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_CANCEL_ACTION);
        broadcastToService(intent);
    }

    void showCallAlertView() {
        if (!SharedPreferencesManager.getUserPaymentIsActive(this)) {
            isAlertViewVisible = false;
            runOnUiThread(()->{
                stopRippleAnimation();
                btn_alert.setVisibility(View.VISIBLE);
                btn_cancel_alert.setVisibility(View.GONE);
                iv_dash_circle.setImageResource(R.drawable.ic_dash_circle_red);
                iv_dash_circle.setVisibility(View.VISIBLE);
                iv_icon.setImageResource(R.drawable.ic_blocked);
                iv_icon.setVisibility(View.VISIBLE);
                btn_alert.setBackgroundResource(R.drawable.alert_btn_disabled_bg);
            });
            return;
        }
        btn_alert.setBackgroundResource(R.drawable.alert_btn_bg);
        iv_dash_circle.setImageResource(R.drawable.ic_dashed_circle);
        isAlertViewVisible = false;
        runOnUiThread(()->{
                stopRippleAnimation();
                btn_alert.setVisibility(View.VISIBLE);
                btn_cancel_alert.setVisibility(View.GONE);
        });

    }

    void showCancelAlertView(){
        if(!SharedPreferencesManager.getUserPaymentIsActive(this))
            return;
        isAlertViewVisible = true;
        runOnUiThread(()->{
//            rippleBackground.setRippleColor(getResources().getColor(R.color.colorPrimary));
            startRippleAnimation(ALERT_SEND);
            btn_alert.setVisibility(View.GONE);
            btn_cancel_alert.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void onCancelAlert() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        final CustomDialog dialogFragment = new CustomDialog();
        dialogFragment.setCancelable(false);
        dialogFragment.setListener(new CustomDialog.Callback() {
            @Override
            public void onCancelBtnClicked() {
                dialogFragment.dismiss();
                startRippleAnimation(currentAnimationState);
            }

            @Override
            public void onPositiveBtnClicked() {
                dialogFragment.dismiss();
                presenter.cancelAlert();
            }
        });
        dialogFragment.show(ft, "dialog");
        stopRippleAnimation();
    }

    @Override
    public void onStopAlert() {
        btn_alert.setEnabled(false);
        cancelAlert();
    }


    @Override
    public void onDataReceived(DeviceData data) {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
