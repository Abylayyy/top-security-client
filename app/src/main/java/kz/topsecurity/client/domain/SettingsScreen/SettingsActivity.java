package kz.topsecurity.client.domain.SettingsScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.fragments.TutorialFragment;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.ui_widgets.CustomSwitch;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rl_exit) RelativeLayout rl_exit;
    @BindView(R.id.rl_tracking_service) RelativeLayout rl_tracking_service;
    @BindView(R.id.s_tracking_service) Switch s_tracking_service;

    @BindView(R.id.rl_volume_tracking_service) RelativeLayout rl_volume_tracking_service;
    @BindView(R.id.s_volume_track) Switch s_volume_track;
    @BindView(R.id.ll_volume_button_options) LinearLayout ll_volume_button_options;
    @BindView(R.id.cl_volume_direction) ConstraintLayout cl_volume_direction;

    @BindView(R.id.s_volume_direction) CustomSwitch s_volume_direction;
    @BindView(R.id.iv_volume_btn_image) CustomSwitch iv_volume_btn_image;
    @BindView(R.id.tv_volume_button_direction_up)
    TextView tv_volume_button_direction_up;
    @BindView(R.id.tv_volume_button_direction_down)
    TextView tv_volume_button_direction_down;

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Настройки");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
        checkTutsStatus(savedInstanceState);
    }

    private void checkTutsStatus(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showTutorials(TutorialFragment.SETTINGS_ACTIVITY);
            }
        },100);
    }

    View.OnClickListener volumeDirectionOnTopClick = v->{
          setVolumeIndicators(false);
    };

    View.OnClickListener volumeDirectionOnDownClick = v->{
        setVolumeIndicators( true);
    };

    private void initView() {
        rl_tracking_service.setOnClickListener(this);
        rl_volume_tracking_service.setOnClickListener(this);
        rl_exit.setOnClickListener(this);
        iv_volume_btn_image.setImageResources(R.drawable.ic_volume_btn_up_active, R.drawable.ic_volume_btn_down_active);
        s_volume_direction.setImageResources(R.drawable.btn_vertical_toggle_top,R.drawable.btn_vertical_toggle_bottom);
        setupSpinner();
        setCurrentState();
        updateVolumeOptionsView(s_volume_track.isChecked());
    }

    private void updateVolumeOptionsView(boolean state) {
        if(state){
            ll_volume_button_options.setVisibility(View.VISIBLE);
        }
        else{
            ll_volume_button_options.setVisibility(View.GONE);
        }
    }

    void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.click_type_rate);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spin_volume_click_rate.setAdapter(adapter);
//        spin_volume_click_rate.setPrompt("Click rate");
//        spin_volume_click_rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                SharedPreferencesManager.setVolumeButtonClickRate(SettingsActivity.this, i);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    void setCurrentState(){
        s_tracking_service.setChecked(SharedPreferencesManager.getTrackingServiceActiveState(this));
        s_volume_track.setChecked(SharedPreferencesManager.getVolumeTrackingServiceActiveState(this) && isAccessibilitySettingsOn(this));
        setVolumeIndicators(SharedPreferencesManager.getVolumeDirection(this));
//        spin_volume_click_rate.setSelection(SharedPreferencesManager.getVolumeButtonClickRate(this));
        updateVolumeOptionsView(SharedPreferencesManager.getVolumeTrackingServiceActiveState(this)&& isAccessibilitySettingsOn(this));
    }

    private void setVolumeIndicators(boolean volumeDirection) {
        s_volume_direction.setChecked(volumeDirection);
        iv_volume_btn_image.setChecked(volumeDirection);
        tv_volume_button_direction_down.setOnClickListener(volumeDirection ? volumeDirectionOnTopClick  : null);
        tv_volume_button_direction_down.setEnabled(volumeDirection);
        tv_volume_button_direction_down.setTextColor(!volumeDirection ? getResources().getColor(R.color.black):getResources().getColor(R.color.gray) );
        tv_volume_button_direction_up.setOnClickListener(!volumeDirection ? volumeDirectionOnDownClick : null);
        tv_volume_button_direction_up.setEnabled(!volumeDirection);
        tv_volume_button_direction_up.setTextColor(volumeDirection?  getResources().getColor(R.color.black):getResources().getColor(R.color.gray));

        s_volume_direction.setOnClickListener(volumeDirection ? volumeDirectionOnTopClick  : volumeDirectionOnDownClick);
        iv_volume_btn_image.setOnClickListener(volumeDirection ?volumeDirectionOnTopClick  : volumeDirectionOnDownClick);
        processVolumeButtonDirection(volumeDirection);
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewAction(viewId);
            }
        });
    }

    void viewAction(int id){
        if(Constants.is_service_sending_alert()){
            showErrorToast();
            finish();
            return;
        }
        switch(id){
            case R.id.rl_tracking_service:{
                toggleTrackingServiceState();
                setDelay(rl_tracking_service);
                break;
            }
            case R.id.rl_volume_tracking_service:{
                if(!SharedPreferencesManager.getTrackingServiceActiveState(this)){
                    showToast(R.string.msg_turn_on_tracking_service);
                    return;
                }
                if(!isAccessibilitySettingsOn(this)){
                    openAccessibilitySettings();
                    return;
                }
                s_volume_track.setChecked(!s_volume_track.isChecked());
                processVolumeTrackingService(s_volume_track.isChecked());
                setDelay(rl_volume_tracking_service);
                break;
            }
            case R.id.cl_volume_direction:{
                setVolumeIndicators(!s_volume_direction.isChecked());
                break;
            }
            case R.id.rl_exit:{
                rl_tracking_service.setEnabled(false);
                rl_volume_tracking_service.setEnabled(false);
                cl_volume_direction.setEnabled(false);

                if(SharedPreferencesManager.getTrackingServiceActiveState(this)){
                    toggleTrackingServiceState();
                }
                clearData();
                isTerminate = true;
                finish();
                break;
            }
        }
    }

    void showErrorToast(){
        runOnUiThread(()->{
            Toast.makeText(this , "Вы не можете изменить настройки при активной тревоге", Toast.LENGTH_LONG ).show();
        });
    }

    void openAccessibilitySettings(){
//        Intent intent = new Intent();
//        intent.setClassName("com.android.settings",
//                "com.android.settings.Settings");
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        startActivity(intent);
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    void toggleTrackingServiceState(){
        s_tracking_service.setChecked(!s_tracking_service.isChecked());
        processTrackingService(s_tracking_service.isChecked());
        if(s_volume_track.isChecked()){
            s_volume_track.setChecked(false);
            processVolumeTrackingService(false);
        }
    }

    void clearData(){
        SharedPreferencesManager.clearData(this);
        dataBaseManager.dropClientData();
        dataBaseManager.dropDeviceDataTable();
    }

    void setDelay(View view){
        view.setClickable(false);
        Disposable subscribe = Observable.timer(3 * 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> view.setClickable(true));
        compositeDisposable.add(subscribe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void processTrackingService(boolean state) {
        SharedPreferencesManager.setTrackingServiceActiveState(this, state);
        if (!state) {
            Intent newIntent = new Intent(this, TrackingService.class);
            stopService(newIntent);
        }
        else {
            startTrackingService();
        }
    }

    private void startTrackingService() {
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(Constants.START_FOREGROUND_ACTION);
        checkAndStartService(intent,TrackingService.class);
    }

    private void processVolumeTrackingService(boolean state){
        SharedPreferencesManager.setVolumeTrackingServiceActiveState(this,state);
        updateVolumeOptionsView(state);
    }

    private void processVolumeButtonDirection(boolean state){
        SharedPreferencesManager.setVolumeDirection(this,state);
    }

    boolean isTerminate = false;

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        if(!isTerminate) {
            returnIntent.putExtra(MainActivity.TRACKING_SERVICE_STATUS_RESULT, s_tracking_service.isChecked());
            setResult(Activity.RESULT_OK, returnIntent);
        }
        else{
            returnIntent.putExtra(MainActivity.EXIT_FROM_APPLICATION, true);
            setResult(Activity.RESULT_OK, returnIntent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
