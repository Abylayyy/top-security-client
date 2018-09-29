package kz.topsecurity.top_signal.domain.RestorePasswordScreen;

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
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.domain.LoginScreen.LoginActivity;
import kz.topsecurity.top_signal.domain.RestorePasswordScreen.additional.ChangePasswordActivity;
import kz.topsecurity.top_signal.domain.base.BaseActivity;
import kz.topsecurity.top_signal.presenter.restorePassPresenter.RestorePasswordPresenter;
import kz.topsecurity.top_signal.presenter.restorePassPresenter.RestorePasswordPresenterImpl;
import kz.topsecurity.top_signal.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithMask;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.top_signal.view.restorePassView.RestorePasswordView;

public class RestorePasswordActivity extends BaseActivity<RestorePasswordView,
        RestorePasswordPresenter,
        RestorePasswordPresenterImpl> implements RestorePasswordView, StatusListener , View.OnClickListener {

    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.cl_restore_pass_layer) ConstraintLayout cl_restore_pass_layer;
    @BindView(R.id.cl_telephone) ConstraintLayout cl_telephone;
    @BindView(R.id.cl_code) ConstraintLayout cl_code;
    @BindView(R.id.cl_restore_pass_loading_layer) ConstraintLayout cl_restore_pass_loading_layer;
    @BindView(R.id.tv_telephone_number_label) TextView tv_telephone_number_label;
    @BindView(R.id.tv_phone_number_error) TextView tv_phone_number_error;
    @BindView(R.id.tv_sms_code_label) TextView tv_sms_code_label;
    @BindView(R.id.tv_sms_code_error) TextView tv_sms_code_error;
    @BindView(R.id.tv_send_again) TextView tv_send_again;
    @BindView(R.id.ed_tel_number) RoundCorneredEditTextWithMask ed_tel_number;
    @BindView(R.id.ed_sms_code) RoundCorneredEditText ed_sms_code;
    @BindView(R.id.btn_confirm) Button btn_confirm;

    RoundCorneredEditTextHelper phoneNumber_helper;
    RoundCorneredEditTextHelper smsCode_helper;

    int currentViewState = -1;
    private static final int PHONE_VIEW = 41;
    private static final int CODE_VIEW = 43;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        ButterKnife.bind(this);
        initPresenter(new RestorePasswordPresenterImpl(this));
        phoneNumber_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_telephone_number_label,tv_phone_number_error);
        phoneNumber_helper.setMandatory();
        phoneNumber_helper.init(this);

        smsCode_helper = new RoundCorneredEditTextHelper(this,ed_sms_code,tv_sms_code_label,tv_sms_code_error);
        smsCode_helper.setMandatory();
        smsCode_helper.init(this);

        btn_confirm.setOnClickListener(this); tv_send_again.setOnClickListener(this);iv_back.setOnClickListener(this);
        setView(PHONE_VIEW);
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


    @Override
    public void onTelephoneNumberSendSuccessfully() {
        setView(CODE_VIEW);
    }

    @Override
    public void onTelephoneNumberSendFailed(int error_msg) {
        setView(PHONE_VIEW);
    }

    @Override
    public void onRestorePasswordSuccess(String success_sms_code) {
        isSuccess =true;
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra(ChangePasswordActivity.SMS_CODE, success_sms_code);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRestorePasswordFailed(int error_msg) {
        showToast(error_msg);

    }

    @Override
    public void onShowLoadingView() {
        cl_restore_pass_layer.setVisibility(View.GONE);
        cl_restore_pass_loading_layer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoadingView() {
        cl_restore_pass_loading_layer.setVisibility(View.GONE);
        cl_restore_pass_layer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowRestorePassView() {

        cl_telephone.setVisibility(View.VISIBLE);
        cl_code.setVisibility(View.GONE);
    }

    @Override
    public void onShowSMSCodeView() {
        cl_telephone.setVisibility(View.GONE);
        cl_code.setVisibility(View.VISIBLE);
        tv_send_again.setClickable(false);
        tv_send_again.setTextColor(getResources().getColor(R.color.gray));
        countDownTimer.start();
    }

    void setView(int state){
        setViewState(state);
        switch (state){
            case PHONE_VIEW:{
                onShowRestorePassView();
                break;
            }
            case CODE_VIEW:{
                onShowSMSCodeView();
                break;
            }
        }
    }

    void setViewState(int state){
        currentViewState = state;
    }

    @Override
    public void onPhoneNumberError(int error_msg) {
        btn_confirm.setEnabled(false);
        phoneNumber_helper.setError(getString(error_msg));
    }

    @Override
    public void onSmsCodeError(int error_msg) {
        btn_confirm.setEnabled(false);
        smsCode_helper.setError(getString(error_msg));
    }

    @Override
    public void onErrorDeactivated() {
        btn_confirm.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_confirm:{
                performClickAccordingToViewState();
                break;
            }
            case R.id.iv_back:{
                onBackPressed();
                if(currentViewState == PHONE_VIEW)
                break;
            }
            case R.id.tv_send_again:{
                setView(PHONE_VIEW);
                break;
            }
        }
    }

    private void performClickAccordingToViewState() {
        hideSoftKeyboard(btn_confirm);
        switch (currentViewState){
            case PHONE_VIEW:{
                if(!isNetworkOnline())
                {
                    showToast(R.string.msg_alert_no_internet);
                    return;
                }
                presenter.sendPhoneNumber(ed_tel_number.getRawText(),ed_tel_number.getText().toString());
                break;
            }
            case CODE_VIEW :{
                if(!isNetworkOnline())
                {
                    showToast(R.string.msg_alert_no_internet);
                    return;
                }
                presenter.sendCode(ed_sms_code.getText().toString());
                break;
            }
            default:{
                showToast(R.string.not_implemented);
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        switch (currentViewState) {
            case PHONE_VIEW: {
                super.onBackPressed();
                break;
            }
            case CODE_VIEW:{
                super.onBackPressed();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
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
                    startActivity(new Intent(RestorePasswordActivity.this, LoginActivity.class));
                    RestorePasswordActivity.super.finish();
                }
            });
        }
    }
}
