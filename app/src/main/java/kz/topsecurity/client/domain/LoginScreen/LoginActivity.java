package kz.topsecurity.client.domain.LoginScreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.BuildConfig;
import kz.topsecurity.client.domain.InputCodeScreen.SmsCodeActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.PaymentScreen.PaymentActivity;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.domain.RestorePasswordScreen.RestorePasswordActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.loginPresenter.LoginPresenter;
import kz.topsecurity.client.presenter.loginPresenter.LoginPresenterImpl;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.loginView.LoginView;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter, LoginPresenterImpl>
        implements View.OnClickListener, StatusListener , LoginView {

    @BindView(R.id.cl_login_loading_layer) ConstraintLayout cl_loading_layer;
    @BindView(R.id.cl_login_layer) ConstraintLayout cl_login_layer;
    @BindView(R.id.btn_sign_in) Button btn_sign_in;

    @BindView (R.id.tv_telephone_number_label) TextView tv_telephone_number_label;
    @BindView (R.id.ed_tel_number)
    RoundCorneredEditTextWithPhoneMask ed_tel_number;
    @BindView (R.id.tv_phone_number_error) TextView tv_phone_number_error;

    @BindView (R.id.tv_password_label) TextView tv_password_label;
    @BindView (R.id.ed_password) RoundCorneredEditText ed_password;
    @BindView (R.id.tv_password_error) TextView tv_password_error;
    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.ll_toolbar) RelativeLayout ll_toolbar;
    @BindView(R.id.tv_forget_password) TextView tv_forget_password;

    RoundCorneredEditTextHelper telephone_helper,password_helper;

    String imei = "";

    private static final int REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE = 35;
    public static final String PHONE_EXTRA = "extra_phone";

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE){
            if (grantResults.length <= 0)return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setIMEI();
            } else {
                onShowSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btn_sign_in.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
        telephone_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_telephone_number_label,tv_phone_number_error);
        password_helper = new RoundCorneredEditTextHelper(this,ed_password,tv_password_label,tv_password_error);
        telephone_helper.init(this);
        password_helper.init(this);
        initPresenter(new LoginPresenterImpl(this));
        setIMEI();
        String phone = getIntent().getStringExtra(PHONE_EXTRA);
        phone = phone!=null && !phone.isEmpty() ? phone :  SharedPreferencesManager.getUserPhone(this);
        WeakReference<String> data = new WeakReference<>(phone);
        if(phone!=null && !phone.isEmpty()){
            ed_tel_number.setText(phone);
        }
        data.clear();
    }

    public void setIMEI() {
        TelephonyManager TelephonyMgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPhoneStatePermission();
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = TelephonyMgr.getImei();
            }
            else{
                imei = TelephonyMgr.getDeviceId();
            }
        } else {
            imei = TelephonyMgr.getDeviceId();
        }

        SharedPreferencesManager.setPhoneImei(this,imei);
    }


    private void requestPhoneStatePermission() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_PHONE_STATE);

        if (shouldProvideRationale) {
            onShowSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn_sign_in:{
                login();
                break;
            }
            case R.id.iv_back:{
                goBack();
                break;
            }
            case R.id.tv_forget_password:{
                startRestorePasswordActivity();
                break;
            }
            default:{
                break;
            }
        }
    }

    private void startRestorePasswordActivity() {
        startActivity(new Intent(this, RestorePasswordActivity.class));
        finish();
    }

    private void login() {
        if(!isNetworkOnline())
        {
            showToast(R.string.msg_alert_no_internet);
            return;
        }

        if(imei==null || imei.isEmpty()){
            showToast("Произошла ошибка (возможно у вас отсутствует SIM карта)!");
            setIMEI();
            return;
        }
        hideSoftKeyboard(btn_sign_in);
        presenter.login(ed_tel_number.getRawText(),ed_tel_number.getText().toString(), ed_password.getText().toString(),imei);
    }

    @Override
    public void onLoginError(int msg) {
        showToast(getString(msg));
    }

    @Override
    public void onLoginSuccess(Client client, String token ) {
        dataBaseManager.saveClientData(client);
        SharedPreferencesManager.setUserPhone(this, ed_tel_number.getRawText());
        SharedPreferencesManager.setUserData(this,true);
        SharedPreferencesManager.setUserAuthToken(this,token);
        boolean isPaymentActive = client.getPlan()!=null && !client.getPlan().getIsExpired();
        SharedPreferencesManager.setUserPaymentIsActive(this, isPaymentActive);

        if(client.getPhoto().contains("no-avatar") || client.getPhoto()==null || client.getPhoto().isEmpty())
            SharedPreferencesManager.setCheckClientAvatar( LoginActivity.this, false);
        else
            SharedPreferencesManager.setCheckClientAvatar(LoginActivity.this,true);
            startMainActivity();
    }

    @Override
    public void onPhoneFieldError(int msg) {
        telephone_helper.setError(getString(msg));
    }

    @Override
    public void onPasswordFieldError(int msg) {
        password_helper.setError(getString(msg));
    }

    @Override
    public void onLockLoginButton() {
        btn_sign_in.setEnabled(false);
    }

    @Override
    public void onShowLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cl_login_layer.setVisibility(View.GONE);
                cl_loading_layer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onHideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cl_login_layer.setVisibility(View.VISIBLE );
                cl_loading_layer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onUserNotVerificatedPhone(String phone, String pass , String imei,  int error_message) {
        showToast(error_message);
        Intent intent = new Intent(this, SmsCodeActivity.class);
        intent.putExtra(SmsCodeActivity.GET_PHONE_NUMB,phone);
        intent.putExtra(SmsCodeActivity.ON_FORWARD_EXTRA,SmsCodeActivity.TO_MAIN);
        intent.putExtra(SmsCodeActivity.PASSWORD,pass);
        intent.putExtra(SmsCodeActivity.IMEI,imei);
        intent.putExtra(SmsCodeActivity.FOR_LOGIN,1);
        startActivity(intent);
        System.gc();
        finish();
    }

    void startMainActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                System.gc();
                finish();
            }
        });
    }

    void startProfileActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                System.gc();
                finish();
            }
        });
    }
    @Override
    public void onErrorDeactivated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_sign_in.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    void goBack(){
        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
        intent.putExtra(StartActivity.SKIP_LOADING_KEY,true);
        startActivity(intent);
        finish();
    }
}
