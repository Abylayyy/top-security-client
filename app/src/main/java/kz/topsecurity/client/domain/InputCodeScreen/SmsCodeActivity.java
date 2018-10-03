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
import kz.topsecurity.client.domain.RegisterScreen.RegisterActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;

public class SmsCodeActivity extends BaseActivity implements View.OnClickListener {

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
    public static final String PHONE_EXTRA= "phone_extra";

    private static final int TO_LOGIN = 681;
    private static final int TO_REGISTER = 156;

    int onBackAction = -1;
    int onForwardAction = -1;
    String userPhone= "";

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
        userPhone = getIntent().getStringExtra(PHONE_EXTRA);
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
        int id = view.getId();

        switch(id){
            case R.id.btn_confirm:{
                sendCode();
                break;
            }
            case R.id.tv_send_again:{
                showToast(R.string.not_implemented);
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
        }).makeRequest(RetrofitClient.getClientApi().verificateCode("register",code));

        compositeDisposable.add(disposable);
    }

    private void onCodeCorrect() {
        isSuccess = true;
        Class activityToClass = LoginActivity.class;
        if(onForwardAction == TO_REGISTER)
            activityToClass = RegisterActivity.class;
        startActivity(new Intent(this,activityToClass));
        finish();
    }

    @Override
    public void finish() {
        if(isSuccess)
            super.finish();
        else {
            showAreYouSureDialog(getString(R.string.are_you_sure_what_exit), new CustomSimpleDialog.Callback() {
                @Override
                public void onCancelBtnClicked() {
                    dissmissAreYouSureDialog();
                }

                @Override
                public void onPositiveBtnClicked() {
                    dissmissAreYouSureDialog();
                    goToPreviousActivity();
                    SmsCodeActivity.super.finish();
                }
            });
        }
    }

    private void goToPreviousActivity() {
        switch (onBackAction){
            case TO_LOGIN:{
                startActivity(new Intent(SmsCodeActivity.this, LoginActivity.class));
                break;
            }
            case TO_REGISTER:{
                startActivity(new Intent(SmsCodeActivity.this, RegisterActivity.class));
                break;
            }
        }
    }
}
