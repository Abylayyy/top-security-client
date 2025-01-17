package kz.topsecurity.client.domain.InputCodeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.LoginScreen.LoginActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;

public class SmsCodeActivity extends BaseActivity implements View.OnClickListener {

    public static final String FOR_LOGIN = "FOR_LOGIN";
    public static final String PASSWORD = "PASS";
    public static final String IMEI = "IMEI";
    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.cl_code) ConstraintLayout cl_code;
    @BindView(R.id.tv_sms_code_label) TextView tv_sms_code_label;
    @BindView(R.id.ed_sms_code) RoundCorneredEditText ed_sms_code;
    @BindView(R.id.tv_sms_code_error) TextView tv_sms_code_error;
    @BindView(R.id.tv_send_again) TextView tv_send_again;
    @BindView(R.id.btn_confirm) Button btn_confirm;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isSuccess = false;
    public static final String ON_BACK_EXTRA = "on_back_extra";
    public static final String ON_FORWARD_EXTRA = "on_forward_extra";
    public static final String GET_PHONE_NUMB = "get_phone_numb";

    public static final int TO_LOGIN = 681;
    public static final int TO_MAIN = 694;
    boolean isShouldLogin = false;

    int onBackAction = -1;
    int onForwardAction = -1;
    private String sendedPhone,password,imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_code);
        ButterKnife.bind(this);
        btn_confirm.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_send_again.setOnClickListener(this);
        setupView();
        onBackAction = getIntent().getIntExtra(ON_BACK_EXTRA,-1);
        onForwardAction = getIntent().getIntExtra(ON_FORWARD_EXTRA , -1);
        sendedPhone = getIntent().getStringExtra(GET_PHONE_NUMB);
        if(sendedPhone!=null){
            SharedPreferencesManager.setTmpSendedCode(this,sendedPhone);
            if(getIntent().getIntExtra(FOR_LOGIN,-1)!=-1) {
                isShouldLogin = true;
                password = getIntent().getStringExtra(PASSWORD);
                imei = getIntent().getStringExtra(IMEI);
                requestVerificationCode(sendedPhone);
            }
        }
    }

    private void requestVerificationCode(String phone) {
        showLoadingDialog();

        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                sendedPhone = phone;
                restartTimer();
                hideProgressDialog();
                showToast(R.string.sms_code_has_been_send);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().requesteCode("register",phone));
        compositeDisposable.add(disposable);
    }

    private void restartTimer() {
        tv_send_again.setClickable(false);
        tv_send_again.setTextColor(getResources().getColor(R.color.gray));
        countDownTimer.start();
    }

    CountDownTimer countDownTimer =  new CountDownTimer(60000, 1000) {

        public void onTick(long millisUntilFinished) {
            tv_send_again.setText(String.format("%s (%d)",getString(R.string.send_again) , millisUntilFinished / 1000));
        }

        public void onFinish() {
            tv_send_again.setClickable(true);
            tv_send_again.setText(getString(R.string.send_again));
            tv_send_again.setTextColor(getResources().getColor(android.R.color.black));
        }
    };

    private void setupView() {
        tv_send_again.setClickable(false);
        tv_send_again.setTextColor(getResources().getColor(R.color.gray));
        countDownTimer.start();
    }

    @Override
    public void onClick(View view) {
        switch( view.getId()){
            case R.id.btn_confirm:{
                sendCode();
                break;
            }
            case R.id.tv_send_again:{
                if(sendedPhone==null)
                    sendedPhone = SharedPreferencesManager.getTmpSendedCode(this);
                requestVerificationCode(sendedPhone);
                break;
            }
            case R.id.iv_back:{
                onBackPressed();
                break;
            }
        }
    }

    private void sendCode() {
        String code = ed_sms_code.getText().toString();
        boolean is_contain_error = false;
        if(code.length()<4){
            is_contain_error = true;
           showToast(R.string.code_length_error);
        }
        if (!is_contain_error)
            sendRequest(code);
    }

    private void sendRequest(String code) {
        if(sendedPhone==null)
            sendedPhone = SharedPreferencesManager.getTmpSendedCode(this);
        showLoadingDialog();
        Disposable disposable = new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                hideProgressDialog();
                onConfirmCode(code);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().verificateCode("register",sendedPhone, code));

        compositeDisposable.add(disposable);
    }

    private void onConfirmCode(String code) {
        showLoadingDialog();
        Disposable disposable = new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                hideProgressDialog();
                onCodeCorrect();
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().confirmCode(code));

        compositeDisposable.add(disposable);
    }

    private void onCodeCorrect() {
        if(isShouldLogin)
            loginUser();
        else{
            isSuccess = true;
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.PHONE_EXTRA,sendedPhone);
            startActivity(intent);
            System.gc();
            finish();
        }
    }

    private void loginUser() {
        Disposable success = new RequestService<>(new RequestService.RequestResponse<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse r) {
                onLoginSuccess(r.getClient() , r.getToken().getAccessToken());
                hideProgressDialog();
            }

            @Override
            public void onFailed(AuthResponse data, int error_message) {
                isSuccess = true;
                finish();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                isSuccess = true;
                finish();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .authorize(sendedPhone, password, Constants.CLIENT_DEVICE_TYPE,Constants.CLIENT_DEVICE_PLATFORM_TYPE, imei));

        compositeDisposable.add(success);
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    public void onLoginSuccess(Client client, String token) {
        dataBaseManager.saveClientData(client);
        SharedPreferencesManager.setUserData(this,true);
        SharedPreferencesManager.setUserAuthToken(this,token);
        boolean isPaymentActive = client.getPlan()!=null && !client.getPlan().getIsExpired();
        SharedPreferencesManager.setUserPaymentIsActive(this, isPaymentActive);
        onSuccessLogin();
    }

    private void onSuccessLogin(){
        isSuccess = true;
        Class activityToClass = MainActivity.class;
//        if(onForwardAction==TO_MAIN)
//            activityToClass = MainActivity.class;
        startActivity(new Intent(this,activityToClass));
        System.gc();
        finish();
    }

    @Override
    public void finish() {
        if(isSuccess)
            super.finish();
        else {
            showAreYouSureDialog(getString(R.string.are_you_sure_what_exit_from_activation), new CustomSimpleDialog.Callback() {
                @Override
                public void onCancelBtnClicked() {
                    dissmissAreYouSureDialog();
                }

                @Override
                public void onPositiveBtnClicked() {
                    dissmissAreYouSureDialog();
                    startActivity(new Intent(SmsCodeActivity.this, LoginActivity.class));
                    System.gc();
                    SmsCodeActivity.super.finish();
                }
            });
        }
    }


}
