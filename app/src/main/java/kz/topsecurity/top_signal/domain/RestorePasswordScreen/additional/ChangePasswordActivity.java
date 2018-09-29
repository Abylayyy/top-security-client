package kz.topsecurity.top_signal.domain.RestorePasswordScreen.additional;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.domain.LoginScreen.LoginActivity;
import kz.topsecurity.top_signal.domain.LoginScreen.LoginActivity_ViewBinding;
import kz.topsecurity.top_signal.domain.base.BaseActivity;
import kz.topsecurity.top_signal.model.other.BasicResponse;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.StatusListener;

public class ChangePasswordActivity extends BaseActivity implements StatusListener, View.OnClickListener {

    boolean isSuccess = false;
    public static final String SMS_CODE = "sms_code_extra";
    @BindView(R.id.tv_change_password) TextView tv_change_password;
    @BindView(R.id.tv_new_password) TextView tv_new_password;
    @BindView(R.id.ed_new_password) RoundCorneredEditText ed_new_password;
    @BindView(R.id.tv_new_password_error) TextView tv_new_password_error;
    @BindView(R.id.tv_repeat_new_password) TextView tv_repeat_new_password;
    @BindView(R.id.ed_repeat_new_password) RoundCorneredEditText ed_repeat_new_password;
    @BindView(R.id.tv_repeat_new_password_error) TextView tv_repeat_new_password_error;
    @BindView(R.id.btn_confirm) Button btn_confirm;

    @BindView(R.id.iv_back) ImageView iv_back;

    RoundCorneredEditTextHelper userNewPassword_helper;
    RoundCorneredEditTextHelper userPasswordConfirmation_helper;

    CompositeDisposable compositeDisposable =new CompositeDisposable();
    boolean isError = false;
    String smsCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        userNewPassword_helper = new RoundCorneredEditTextHelper(this, ed_new_password, tv_new_password , tv_new_password_error);
        userPasswordConfirmation_helper = new RoundCorneredEditTextHelper(this, ed_repeat_new_password, tv_repeat_new_password , tv_repeat_new_password_error);
        smsCode = getIntent().getStringExtra(SMS_CODE);
        btn_confirm.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void changePassword() {
        if(isError){
            showToast(R.string.correct_errors);
            return;
        }
        if(!isNetworkOnline())
        {
            showToast(R.string.msg_alert_no_internet);
            return;
        }

        String new_password = ed_new_password.getText().toString();
        String password_confirmation = ed_repeat_new_password.getText().toString();

        boolean contains_error = false;

        if(new_password.isEmpty() || new_password.length()<6){
            contains_error = true;
            userNewPassword_helper.setError(getString(R.string.password_length_error));
        }
        if(password_confirmation.isEmpty() || password_confirmation.length()<6){
            contains_error = true;
            userPasswordConfirmation_helper.setError(getString(R.string.password_length_error));
        }
        else if(!contains_error && !new_password.equals(password_confirmation)){
            contains_error = true;
            userPasswordConfirmation_helper.setError(getString(R.string.password_do_not_match));
        }
        if(contains_error)
            isError = true;
        else {
            isError = false;
            makeChangePassword(smsCode, new_password, password_confirmation);
        }
    }

    private void makeChangePassword(String smsCode, String new_password, String password_confirmation) {
        showLoadingDialog();
        Disposable disposable = new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                hideProgressDialog();
                onChangePasswordSuccess();
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                failStatus(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                failStatus(error_message);
            }

            void failStatus(int error_message){
                hideProgressDialog();
                onChangePasswordFailed(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().changePassword(new_password, password_confirmation, smsCode));
        compositeDisposable.add(disposable);
    }

    private void onChangePasswordSuccess(){
        isSuccess = true;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void onChangePasswordFailed(int error_msg){
        showToast(error_msg);
    }

    @Override
    public void onErrorDeactivated() {
        btn_confirm.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_back:{
                onBackPressed();
                break;
            }
            case R.id.btn_confirm:{
                changePassword();
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
        super.onBackPressed();
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
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    ChangePasswordActivity.super.finish();
                }
            });
        }
    }
}
