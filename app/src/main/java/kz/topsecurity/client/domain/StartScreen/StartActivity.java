package kz.topsecurity.client.domain.StartScreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.BuildConfig;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.InputCodeScreen.SmsCodeActivity;
import kz.topsecurity.client.domain.MainScreen.AlertActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.domain.RegisterScreen.SignUpActivity;
import kz.topsecurity.client.domain.RestorePasswordScreen.RestorePasswordActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.loginPresenter.LoginPresenter;
import kz.topsecurity.client.presenter.loginPresenter.LoginPresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.loginView.LoginView;

public class StartActivity extends BaseActivity<LoginView, LoginPresenter, LoginPresenterImpl>
        implements View.OnClickListener, StatusListener, LoginView  {

    @BindView (R.id.tv_phone_number_error) TextView tv_phone_number_error;
    @BindView (R.id.tv_password_error) TextView tv_password_error;
    @BindView(R.id.cl_login_loading_layer) ConstraintLayout cl_loading_layer;
    @BindView(R.id.cl_login_layer) ConstraintLayout cl_login_layer;
    @BindView(R.id.btn_sign_in) Button btn_sign_in;
    @BindView(R.id.btn_sign_up) TextView btn_sign_up;
    @BindView(R.id.tv_forget_password) TextView tv_forget_password;
    @BindView(R.id.ed_tel_number) RoundCorneredEditTextWithPhoneMask ed_tel_number;
    @BindView(R.id.ed_password) RoundCorneredEditText ed_password;
    @BindView(R.id.phone_number_hide) RoundCorneredEditText number_hide;

    public static final String START_MAIN_SCREEN_KEY = "START_MAIN_SCREEN";
    public static final String SKIP_LOADING_KEY = "SKIP_LOADING";
    RoundCorneredEditTextHelper telephone_helper,password_helper;
    String imei = "";
    private static final int REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE = 35;
    public static final String PHONE_EXTRA = "extra_phone";
    public static boolean isUserLoggedIn;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        btn_sign_in.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);

        number_hide.setOnFocusChangeListener((v, hasFocus) -> {
            number_hide.setVisibility(View.INVISIBLE);
        });

        ed_password.setOnFocusChangeListener((v, hasFocus)-> {
            if (ed_tel_number.getText().length() <= 3) {
                number_hide.setVisibility(View.VISIBLE);
            }
        });
        initPresenter(new LoginPresenterImpl(this));
        setIMEI();
        telephone_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_phone_number_error);
        password_helper = new RoundCorneredEditTextHelper(this,ed_password,tv_password_error);
        telephone_helper.init(this);
        password_helper.init(this);
        String phone = getIntent().getStringExtra(PHONE_EXTRA);
        phone = phone!=null && !phone.isEmpty() ? phone :  SharedPreferencesManager.getUserPhone(this);
        WeakReference<String> data = new WeakReference<>(phone);
        if(phone!=null && !phone.isEmpty()){
            ed_tel_number.setText(phone);
        }
        data.clear();

        checkDataBase();
        isUserLoggedIn = SharedPreferencesManager.getUserData(this) && SharedPreferencesManager.getUserAuthToken(this)!=null;
        boolean db_state = checkDatabaseForTables();
        isUserLoggedIn = isUserLoggedIn && db_state;

        if(isUserLoggedIn){
            startWithLogin();
        }
    }


    private void checkDataBase() {
        dataBaseManager.updateDatabase(DataBaseManagerImpl.REASON_VERSION);
    }

    private boolean checkDatabaseForTables() {
        boolean state = true;
        try{
            dataBaseManager.getClientData();
        }
        catch (SQLiteException exception){
            state = false;
            dataBaseManager.updateDatabase(DataBaseManagerImpl.REASON_FORCE);
        }
        return state;
    }

    private void startWithLogin() {
        Disposable success = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse r) {
                if (r.getClient() == null)
                    onLoginFailed();
                else {
                    //TODO : CHECK IF USER ACC NOT ACTIVATED
                    boolean isPaymentActive = r.getClient().getPlan()!= null && !r.getClient().getPlan().getIsExpired();
                    SharedPreferencesManager.setUserPaymentIsActive( StartActivity.this, isPaymentActive);
                    onSuccessLogin(r.getClient());
                }
            }
            @Override
            public void onFailed(GetClientResponse data, int error_message) {
                onLoginFailed();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                onLoginFailed();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .getClientData(RetrofitClient.getRequestToken()));

        compositeDisposable.add(success);
    }

    private void onLoginFailed() {
    }

    private void onSuccessLogin(Client client) {
        dataBaseManager.saveClientData(client);
        if(client.getPhoto().contains("no-avatar") || client.getPhoto()==null || client.getPhoto().isEmpty())
            SharedPreferencesManager.setCheckClientAvatar( StartActivity.this, false);
        else
            SharedPreferencesManager.setCheckClientAvatar(StartActivity.this,true);
//        if(Constants.IS_DEBUG || (client.checkPlan()!=null && !client.checkPlan().getIsExpired())) {
            startMainActivity();
//        }
//        else{
//            Intent intent = new Intent(this, PaymentActivity.class);
//            intent.putExtra(PaymentActivity.FORCED,true);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE){
            if (grantResults.length <= 0)return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setIMEI();
            } else {
                onShowSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_sign_in:{
                login();
                number_hide.setVisibility(View.INVISIBLE);
                break;
            }
            case R.id.btn_sign_up:{
                register();
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
        SharedPreferencesManager.setUserPassword(getApplication(), ed_password.getText().toString());
        presenter.login(ed_tel_number.getRawText(),ed_tel_number.getText().toString(), ed_password.getText().toString(),imei);
    }


    private void register() {
        startActivity(new Intent(this,SignUpActivity.class));
    }


    private void startRestorePasswordActivity() {
        startActivity(new Intent(this, RestorePasswordActivity.class));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
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
        runOnUiThread(() -> {
            if (SharedPreferencesManager.getCheckClientAvatar(getApplication())) {
                startActivity(new Intent(StartActivity.this, AlertActivity.class));
            } else {
                startActivity(new Intent(StartActivity.this, MainActivityNew.class));
            }
            System.gc();
            finish();
        });
    }

    @Override
    public void onErrorDeactivated() {
        runOnUiThread(() -> btn_sign_in.setEnabled(true));
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
                    android.R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(StartActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
                    });
        } else {
            ActivityCompat.requestPermissions(StartActivity.this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
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

        if(client.getPhoto().contains("no-avatar") || client.getPhoto()==null || client.getPhoto().isEmpty()) {
            SharedPreferencesManager.setCheckClientAvatar(StartActivity.this, false);
        }
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
        runOnUiThread(() -> {
            cl_login_layer.setVisibility(View.GONE);
            cl_loading_layer.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onHideLoading() {
        runOnUiThread(() -> {
            cl_login_layer.setVisibility(View.VISIBLE );
            cl_loading_layer.setVisibility(View.GONE);
        });
    }

}
