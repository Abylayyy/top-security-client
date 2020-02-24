package kz.topsecurity.client.domain.MainScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.PaymentScreen.PaymentActivity;
import kz.topsecurity.client.domain.SplashScreen.SplashScreen;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.helper.Constants;
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

public class AlertActivity extends ServiceControlActivity implements MainView,
        TrackingServiceBroadcastReceiverListener, View.OnClickListener {

    private static final int PROFILE_REQUEST_CODE = 333;
    @BindView(R.id.topConstLayout) ConstraintLayout topLayout;
    @BindView(R.id.bottomConstLayout) ConstraintLayout bottomLayout;
    @BindView(R.id.alertBtn) ImageView alertBtn;
    @BindView(R.id.profImage) RelativeLayout profImage;
    @BindView(R.id.pokupkaImage) RelativeLayout pokupkaImg;
    @BindView(R.id.callPopup) RelativeLayout callPopup;
    @BindView(R.id.skoryiPopup) RelativeLayout skoryiPopup;
    @BindView(R.id.cancelAlert) ImageView cancelAlert;
    @BindView(R.id.animRect) ImageView rect5;
    @BindView(R.id.redAlertBtn) ImageView redBtn;

    @BindView(R.id.background_image) ImageView backGr;
    @BindView(R.id.krugPriniat) ImageView krugPriniat;
    @BindView(R.id.krugVPuti) ImageView krugVputi;
    @BindView(R.id.krugUspeh) ImageView krugUspeh;

    @BindView(R.id.priniatImage) ImageView imgPriniat;
    @BindView(R.id.rrt_edet) ImageView imgEdet;
    @BindView(R.id.success) ImageView imgSuccess;

    @BindView(R.id.successImage) ImageView rectSuccess;
    @BindView(R.id.acceptedImg) ImageView rectAccept;
    @BindView(R.id.mgorVputi) ImageView rectVputi;

    ImageView [] krugs, images, rects;

    int count = 0;
    boolean isUp;

    public static final String CANCEL_ALERT_EXTRA = "CANCEL_ALERT_EXTRA";
    private static final String ANIMATION_STATE_KEY = "ANIMATION_STATE";

    boolean isAlertViewVisible = false;
    private static final int PAYMENT_REQUEST_CODE = 236;
    int currentAnimationState = -1;
    private boolean checkClientAvatarExist;

    public static final int ALERT_SEND = 333;
    public static final int ORDER_CREATED = 11;
    public static final int ORDER_ACCEPTED = 111;
    public static final int MRRT_CHANGED_POSIITION = 222;

    public static final int TROUBLES = 324;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        ButterKnife.bind(this);
        isUp = false;

        krugs = new ImageView[] {krugPriniat, krugVputi, krugUspeh};
        images = new ImageView[] {imgPriniat, imgEdet, imgSuccess};
        rects = new ImageView[] {rectVputi, rectVputi, rectSuccess};

        if (!SharedPreferencesManager.getUserData(getApplicationContext())) {
            backToLogin();
        }

        if (savedInstanceState != null) {
            currentAnimationState = savedInstanceState.getInt(ANIMATION_STATE_KEY);
        }
        initPresenter(new MainPresenterImpl(this));
        presenter.logToken();

        setupBroadcastReceiver();

        if(Constants.is_service_sending_alert()){
            showCancelAlertView();
            presenter.setAlertActive(true);
        }
        else{
            showCallAlertView();
            presenter.setAlertActive(false);
        }

        Log.d("SENDING ALERT::", String.valueOf(Constants.is_service_sending_alert()));

        checkClientAvatarExist();

        if(getIntent().getBooleanExtra(CANCEL_ALERT_EXTRA,false)){
            presenter.actionCancel();
        }

        presenter.updateDrawerData(this);
        presenter.checkStatus();

        alertBtn.setOnClickListener(this);

        alertBtn.setOnLongClickListener(v -> {
            presenter.actionAlert();
            return true;
        });

        pokupkaImg.setOnClickListener(this);

        cancelAlert.setOnClickListener(this);

        profImage.setOnClickListener(this);

        animateElements(rectAccept);
        transitionAnimation(90, rectAccept.getHeight() - 30);
        startRedAlertAnimation(redBtn);
    }

    private void backToLogin() {
        startActivity(new Intent(getApplication(), StartActivity.class));
        finishAffinity();
    }

    private void showCallAlertView() {
        if (!SharedPreferencesManager.getUserPaymentIsActive(this)) {
            isAlertViewVisible = false;
            return;
        }
        isAlertViewVisible = false;
        runOnUiThread(this::startCancelAlertAction);
    }

    private void startCancelAlertAction() {
        topLayout.animate().translationY(0).setDuration(1000).start();
        bottomLayout.animate().translationY(0).setDuration(1000).start();
        alertBtn.animate().translationY(0).setDuration(1000).start();

        for (int i = 0; i < images.length; i++) {
            stopAnimations(krugs[i], rects[i], images[i]);
        }
    }

    private void startAlertAction() {
        topLayout.animate().translationY(-3000).setDuration(1000).start();
        bottomLayout.animate().translationY(3000).setDuration(1000).start();
        alertBtn.animate().translationY(3000).setDuration(1000).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("EVERYTHING::", "GOOD");
            }
            if (!SharedPreferencesManager.getUserPaymentIsActive(this)) {
                presenter.checkPlan();
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        currentAnimationState = savedInstanceState.getInt(ANIMATION_STATE_KEY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ANIMATION_STATE_KEY, currentAnimationState);
        super.onSaveInstanceState(outState);
    }

    boolean firstTimeShow = true;
    private void checkClientAvatarExist(){
        checkClientAvatarExist = SharedPreferencesManager.getCheckClientAvatar( this);
        if(!checkClientAvatarExist && firstTimeShow ){
            firstTimeShow = false;
            startActivityForResult(new Intent(this, MainActivityNew.class),PROFILE_REQUEST_CODE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkAlertBtn();
        checkClientAvatarExist();
    }

    private void checkAlertBtn() {
        if(SharedPreferencesManager.getIsServiceActive(this)) {
            cancelAlert.setEnabled(true);
        }
        else
            alertBtn.setEnabled(true);
    }

    private void transitionAnimation(int from, int to) {
            slideUp(rect5, from, to);
    }

    private void delivered(ImageView krug, ImageView img) {
        krug.setColorFilter(Color.parseColor("#EF3B39"));
        img.setColorFilter(Color.parseColor("#EF3B39"));
    }

    private void animateElements(ImageView rectAccept) {
        rectAccept.setVisibility(View.VISIBLE);
    }

    private void stopAnimations(ImageView krug, ImageView rect, ImageView img) {
        krug.setColorFilter(Color.parseColor("#b6b6b6"));
        rect.setVisibility(View.INVISIBLE);
        img.setColorFilter(Color.parseColor("#b6b6b6"));
        count = 0;
        animateElements(rectAccept);
        transitionAnimation(0, rectAccept.getHeight()-150);
    }

    private void startRedAlertAnimation(ImageView view) {
        ScaleAnimation animation = new ScaleAnimation(
                0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(700);
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }

    public void slideUp(View view, int from_height, int to_height){
        TranslateAnimation animate = new TranslateAnimation(0,0, from_height,  to_height);
        animate.setDuration(700);
        animate.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animate);
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

    void showCancelAlertView(){
        if(!SharedPreferencesManager.getUserPaymentIsActive(this))
            return;
        runOnUiThread(this::startAlertAction);
    }

    @Override
    protected void blockAlertButton(boolean state) {
        alertBtn.setEnabled(!state);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(Constants.is_service_sending_alert()){
            showCancelAlertView();
            presenter.setAlertActive(true);
        }
        boolean isCancelAlertAction = intent.getBooleanExtra(CANCEL_ALERT_EXTRA,false);
        if (!SharedPreferencesManager.getUserPaymentIsActive(this))
            return;
        if(isCancelAlertAction){
            presenter.actionCancel();
        }
    }

    @Override
    public void onAlertNotActive() {
        hideProgressDialog();
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
    public void onDataReceived(DeviceData data) {

    }

    @Override
    public void onAlertIsActive() {
        hideProgressDialog();
        presenter.setAlertActive(true);
        setTrackerAlertActiveStatus();
        showCancelAlertView();
    }

    private void setTrackerAlertActiveStatus() {
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_ACTIVE_ACTION);
        broadcastToService(intent);
    }

    @Override
    public void onAlert(int type) {
        if(type == 0) {
            sendAlert(type);
        }
        else if(type == 1){
            requestCall();
        }
        else if (type == 2){
            callAmbulance();
        }

    }
    private void requestCall() {
        sendAlert(1);
    }

    private void callAmbulance() {
        sendAlert(2);
    }

    void sendAlert(int type){
        if(!SharedPreferencesManager.getUserPaymentIsActive(this)) {
            showAreYouSureDialog(getString(R.string.user_do_not_make_the_payment_to_call_alert), new CustomSimpleDialog.Callback() {
                @Override
                public void onCancelBtnClicked() {
                    dissmissAreYouSureDialog();
                }

                @Override
                public void onPositiveBtnClicked() {
                    startActivity(new Intent(AlertActivity.this, PaymentActivity.class));
                    dissmissAreYouSureDialog();
                }
            });
            return;
        }
        Intent intent = new Intent(this, TrackingService.class);
        if(type == 0)
            intent.setAction(Constants.ALERT_ACTION);
        else if(type == 1)
            intent.setAction(Constants.REQUEST_CALL_ACTION);
        else if(type == 2)
            intent.setAction(Constants.CALL_AMBULANCE_ACTION);
        broadcastToService(intent);
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
            }

            @Override
            public void onPositiveBtnClicked() {
                dialogFragment.dismiss();
                presenter.cancelAlert();
                startCancelAlertAction();
            }
        });
        dialogFragment.show(ft, "dialog");
    }

    @Override
    public void onStopAlert() {
        cancelAlert();
    }

    void cancelAlert(){
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_CANCEL_ACTION);
        broadcastToService(intent);
    }

    @Override
    public void setAnimationStatus(int status) {
        startAnimations(status);
    }

    private void startAnimations(int status) {
        switch (status) {
            case ORDER_CREATED: {
                break;
            }
            case ORDER_ACCEPTED: {
                statusAccepted();
                break;
            }
            case MRRT_CHANGED_POSIITION: {
                statusVputi();
                break;
            }
            case ALERT_SEND: {
                statusSuccess();
                break;
            }
        }
    }

    private void statusAccepted() {
        delivered(krugPriniat, imgPriniat);
        animateElements(rectVputi);
        transitionAnimation(- rectAccept.getHeight(), - rectVputi.getHeight());
    }

    private void statusVputi() {
        delivered(krugVputi, imgEdet);
        animateElements(rectSuccess);
        transitionAnimation(- 80 - (rectAccept.getHeight() + rectVputi.getHeight()), - rectSuccess.getHeight() - 150);
    }

    private void statusSuccess() {
        delivered(krugUspeh, imgSuccess);
        showCallAlertView();
    }

    @Override
    public void setDrawerData(Client clientData) {

    }

    @Override
    public void exitFromMainScreen() {
        startActivity(new Intent(AlertActivity.this, SplashScreen.class));
        finish();
    }

    @Override
    public void userPlanChanged(boolean isActive) {
        startCancelAlertAction();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.removeHandlerCallbacks();
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
                        showCancelAlertView();
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_SEND:{
                        startAlertAction();
                        break;
                    }
                    case TrackingService.ACTION_AMBULANCE_CALLED:{
                        showCancelAlertView();
                        break;
                    }
                    case TrackingService.ACTION_UNDEFINED_ALERT_SEND:{
                        showCallAlertView();
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_FAILED:{
                        showCallAlertView();
                        showToast(R.string.failed_to_send_alert);
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL:{
                        showCallAlertView();
                        Log.d("ALERT::", "ALERT_CANCELLED");
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL_SEND:{
                        showCallAlertView();
                        Log.d("ALERT::", "CANCEL_ALERT_SEND");
                        break;
                    }
                    case TrackingService.ACTION_STATUS_ALERT_CANCEL_FAILED:{
                        presenter.setAlertActive(true);
                        showCancelAlertView();
                        showToast(R.string.failed_to_cancel_alert);
                        break;
                    }
                    case TrackingService.ACTION_GPS_NOT_AVAILABLE:{
                        createAndCheckLocationProvider();
                        break;
                    }
                    case TrackingService.ACTION_OPERATOR_CREATED:{
                        Log.d("STATUS::", "created");
                        break;
                    }
                    case TrackingService.ACTION_MRRT_ACCEPTED:{
                        statusAccepted();
                        Log.d("STATUS::", "accepted");
                        break;
                    }
                    case TrackingService.ACTION_OPERATOR_CANCELLED:{
                        presenter.cancelAlert();
                        showCallAlertView();
                        break;
                    }
                    case TrackingService.ACTION_MRRT_CHANGED_POSITION:{
                        statusVputi();
                        break;
                    }
                }
            }
        }, new IntentFilter(TrackingService.FILTER_ACTION_SERVICE_BROADCAST));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alertBtn:
                if(!checkNetwork()){
                    return;
                }
                else if(!mAlreadyStartedService && !Constants.is_service_active() && !SharedPreferencesManager.getTrackingServiceActiveState(this)){
                    promptTurnOnService();
                }
                else if(!checkClientAvatarExist)
                    onShowToast(R.string.you_cant_when_your_photo_not_exist_on_server);
                else {
                    presenter.actionAlert();
                }
                break;

            case R.id.cancelAlert:
                if(!checkNetwork()){
                    return;
                }
                else {
                    presenter.actionCancel();
                }
                break;

            case R.id.pokupkaImage:
                if(Constants.is_service_sending_alert())
                    onShowToast(R.string.you_cant_when_alert_active);
                else if(!checkClientAvatarExist)
                    onShowToast(R.string.you_cant_when_your_photo_not_exist_on_server);
                else
                    startActivityForResult(new Intent(this,PaymentActivity.class),PAYMENT_REQUEST_CODE);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.profImage:
                Intent intent2 = new Intent(getApplication(), MainActivityNew.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                break;

            case R.id.callPopup:
                if(!checkNetwork()){
                    return;
                }else if(!checkClientAvatarExist)
                    onShowToast(R.string.you_cant_when_your_photo_not_exist_on_server);
                else
                    presenter.callMeBack();
                break;

            case R.id.skoryiPopup:
                if(!checkNetwork()){
                    return;
                }else if(!checkClientAvatarExist)
                    onShowToast(R.string.you_cant_when_your_photo_not_exist_on_server);
                else
                    presenter.callAmbulance();
                break;
        }
    }
}
