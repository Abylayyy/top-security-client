package kz.topsecurity.client.domain.MainScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.topsecurity.client.domain.AlertHistoryScreen.AlertHistoryActivity;
import kz.topsecurity.client.domain.PaymentScreen.PaymentActivity;
import kz.topsecurity.client.domain.PlaceScreen.PlaceActivity;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.SettingsScreen.SettingsActivity;
import kz.topsecurity.client.domain.SplashScreen.SplashScreen;
import kz.topsecurity.client.domain.TrustedNumbersScreen.TrustedNumbersActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.mainPresenter.MainPresenterImpl;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.service.trackingService.interfaces.TrackingServiceBroadcastReceiverListener;
import kz.topsecurity.client.service.trackingService.listenerImpl.TrackingServiceBroadcastReceiver;
import kz.topsecurity.client.service.trackingService.managers.DataProvider;
import kz.topsecurity.client.service.trackingService.model.DeviceData;
import kz.topsecurity.client.ui_widgets.customDialog.CustomDialog;
import kz.topsecurity.client.view.mainView.MainView;

public class MainActivity extends ServiceControlActivity
        implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener , MainView , TrackingServiceBroadcastReceiverListener {

    public static final String CANCEL_ALERT_EXTRA = "CANCEL_ALERT_EXTRA";
    public static final String TRACKING_SERVICE_STATUS_RESULT = "IS_MADE_CHANGES";
    public static final String EXIT_FROM_APPLICATION = "EXIT_FROM_APPLICATION";

    @BindView(R.id.tv_profile) TextView tv_profile;
    @BindView(R.id.tv_places) TextView tv_places;
    @BindView(R.id.tv_contacts) TextView tv_contacts;
    @BindView(R.id.tv_settings) TextView tv_settings;
    @BindView(R.id.tv_payment) TextView tv_payment;
    @BindView(R.id.tv_alert_history) TextView tv_alert_history;
    @BindView(R.id.btn_alert) Button btn_alert;
    @BindView(R.id.btn_cancel_alert) Button btn_cancel_alert;
    @BindView(R.id.btn_minimize_app) Button btn_minimize_app;
    @BindView(R.id.rippleBackground) RippleBackground rippleBackground;
    @BindView(R.id.iv_user_avatar) CircleImageView iv_user_avatar;
    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.tv_user_phone) TextView tv_user_phone;
    @BindView(R.id.tv_user_email) TextView tv_user_email;

    private static final String TAG = MainActivity.class.getSimpleName();

//    boolean isAlertViewActive = false;
    boolean isAlertViewVisible = false;
    private static final int PROFILE_REQUEST_CODE = 723;
    private static final int SETTINGS_REQUEST_CODE = 486;
    private static final int PAYMENT_REQUEST_CODE = 236;
    private static final int ALERT_HISTORY_CODE = 175;


    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PROFILE_REQUEST_CODE:{
                    setDrawerData();
                break;
            }
            case SETTINGS_REQUEST_CODE:{
                if(data.getBooleanExtra(MainActivity.EXIT_FROM_APPLICATION,false)) {
                    Constants.clearData(this);
                    startActivity(new Intent(MainActivity.this, SplashScreen.class));
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAlertBtn();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!drawer.isDrawerOpen(GravityCompat.START)) {
                        // starts opening
                        if(rippleBackground!=null && isAlertViewVisible)
                            rippleBackground.stopRippleAnimation();
                    } else {
                        if(rippleBackground!=null && isAlertViewVisible)
                            rippleBackground.startRippleAnimation();
                    }
                    invalidateOptionsMenu();
                }
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tv_profile.setOnClickListener(this);
        tv_places.setOnClickListener(this);
        tv_contacts.setOnClickListener(this);
        tv_settings.setOnClickListener(this);
        tv_payment.setOnClickListener(this);
        tv_alert_history.setOnClickListener(this);
        btn_alert.setOnClickListener(this);
        btn_cancel_alert.setOnClickListener(this);
        btn_minimize_app.setOnClickListener(this);

        initPresenter(new MainPresenterImpl(this));

        setupBroadcastReceiver();
        if(Constants.is_service_sending_alert()){
            showCancelAlertView();
            presenter.setAlertActive(true);
        }
        boolean isCancelAlertAction = getIntent().getBooleanExtra(CANCEL_ALERT_EXTRA,false);
        if(isCancelAlertAction){
            presenter.actionCancel();
        }
        setDrawerData();
        btn_alert.setEnabled(false);
        presenter.checkStatus();
    }

    @Override
    public void onAlertNotActive() {
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
        if(!checkGPS())
            return;
        presenter.callAlert();
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

    private void setDrawerData() {
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();
        if (clientData != null) {
            tv_user_name.setText(clientData.getUsername());
            tv_user_phone.setText(clientData.getPhone());
            tv_user_email.setText(clientData.getEmail());
            userAvatar = clientData.getPhoto();
        }
        if (iv_user_avatar != null)
            setAvatar(iv_user_avatar,userAvatar);
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
        int id = item.getItemId();

        if (id == R.id.action_places) {
            if(!Constants.is_service_sending_alert())
                startActivity(new Intent(this, PlaceActivity.class));
            else
                onShowToast(R.string.you_cant_when_alert_active);
            return true;
        }
        else if (id == R.id.action_contacts) {
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
        int id = view.getId();
        boolean ifNavButtons = false;
        switch(id){
            case R.id.tv_profile:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(this,ProfileActivity.class),PROFILE_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.tv_places:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivity(new Intent(this, PlaceActivity.class));
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.tv_contacts : {
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivity(new Intent(this, TrustedNumbersActivity.class));
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.tv_settings:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),SETTINGS_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.tv_payment:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, PaymentActivity.class),PAYMENT_REQUEST_CODE);
                else
                    onShowToast(R.string.you_cant_when_alert_active);
                break;
            }
            case R.id.tv_alert_history:{
                ifNavButtons = true;
                if(!Constants.is_service_sending_alert())
                    startActivityForResult(new Intent(MainActivity.this, AlertHistoryActivity.class),ALERT_HISTORY_CODE);
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
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_ACTION);
        broadcastToService(intent);
    }

    void cancelAlert(){
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.ALERT_CANCEL_ACTION);
        broadcastToService(intent);
    }

    void showCallAlertView(){
        isAlertViewVisible = false;
        runOnUiThread(()->{
                rippleBackground.stopRippleAnimation();
                rippleBackground.clearAnimation();
                rippleBackground.setVisibility(View.GONE);
                btn_alert.setVisibility(View.VISIBLE);
                btn_cancel_alert.setVisibility(View.GONE);
        });

    }

    void showCancelAlertView(){
        isAlertViewVisible = true;
        runOnUiThread(()->{
            rippleBackground.setVisibility(View.VISIBLE);
            rippleBackground.startRippleAnimation();
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
                rippleBackground.startRippleAnimation();
            }

            @Override
            public void onPositiveBtnClicked() {
                dialogFragment.dismiss();
                presenter.cancelAlert();
            }
        });
        dialogFragment.show(ft, "dialog");
        rippleBackground.stopRippleAnimation();
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
