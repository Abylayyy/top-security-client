package kz.topsecurity.client.domain.RegisterScreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.InputCodeScreen.SmsCodeActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.registerPresenter.RegisterPresenter;
import kz.topsecurity.client.presenter.registerPresenter.RegisterPresenterImpl;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithMask;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.registerView.RegisterView;

public class RegisterActivity extends BaseActivity<RegisterView,RegisterPresenter,RegisterPresenterImpl> implements View.OnClickListener, StatusListener,RegisterView {

    @BindView(R.id.cl_register_view) ConstraintLayout cl_register_view;
    @BindView(R.id.cl_loading_layer) ConstraintLayout cl_loading_layer;

    @BindView(R.id.iv_back) ImageView iv_back;
    @BindView(R.id.btn_sign_up) Button btn_sign_up;

    @BindView(R.id.tv_telephone_number_label) TextView tv_telephone_number_label;
    @BindView(R.id.ed_tel_number) RoundCorneredEditTextWithMask ed_tel_number;
    @BindView(R.id.tv_phone_number_error) TextView tv_phone_number_error;

    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.ed_username) RoundCorneredEditText ed_username;
    @BindView(R.id.tv_user_name_error) TextView tv_user_name_error;

    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.ed_email) RoundCorneredEditText ed_email;
    @BindView(R.id.tv_email_error) TextView tv_email_error;

    @BindView(R.id.tv_password) TextView tv_password;
    @BindView(R.id.ed_password) RoundCorneredEditText ed_password;
    @BindView(R.id.tv_password_error) TextView tv_password_error;

    @BindView(R.id.tv_privacy_policy) TextView tv_privacy_policy;
    @BindView(R.id.cb_privacy_policy) CheckBox cb_privacy_policy;

    RoundCorneredEditTextHelper phoneNumber_helper,userName_helper,userEmail_helper,userPassword_helper;

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initPresenter(new RegisterPresenterImpl(this));
        iv_back.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);
        phoneNumber_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_telephone_number_label,tv_phone_number_error);
        userName_helper = new RoundCorneredEditTextHelper(this , ed_username , tv_user_name, tv_user_name_error);
        userEmail_helper = new RoundCorneredEditTextHelper(this , ed_email , tv_email, tv_email_error);
        userPassword_helper = new RoundCorneredEditTextHelper(this , ed_password , tv_password , tv_password_error);
        phoneNumber_helper.setMandatory();
        userName_helper.setMandatory();
//        userEmail_helper.setMandatory();
        userPassword_helper.setMandatory();
        phoneNumber_helper.init(this);
        userName_helper.init(this);
        userEmail_helper.init(this);
        userPassword_helper.init(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_up:{
                register();
                break;
            }
            case R.id.iv_back:{
                goBack();
                break;
            }
            case R.id.tv_privacy_policy:{
                openPrivacyPolicyLink();
                break;
            }
            default:{
                break;
            }
        }
    }

    private void openPrivacyPolicyLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Constants.PRIVACY_POLICY_LINK));
        startActivity(i);
    }

    void register(){
        if(!isNetworkOnline())
        {
            showToast(R.string.msg_alert_no_internet);
            return;
        }
        if(!cb_privacy_policy.isChecked()){
            showToast(R.string.make_agreement_to_privacy_policy);
            return;
        }
        hideSoftKeyboard(btn_sign_up);
        presenter.register(ed_tel_number.getRawText(),ed_tel_number.getText().toString(),ed_email.getText().toString(),ed_username.getText().toString(),ed_password.getText().toString());
    }

    @Override
    public void onShowLoading(){
        runOnUiThread(()->{
            cl_loading_layer.setVisibility(View.VISIBLE);
            cl_register_view.setVisibility(View.GONE);
        });
    }

    @Override
    public void onHideLoading() {
        runOnUiThread(()->{
            cl_loading_layer.setVisibility(View.GONE);
            cl_register_view.setVisibility(View.VISIBLE);
        });
    }

    private void startApplication(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    public  void hideSoftKeyboard (View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    void goBack(){
        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
        intent.putExtra(StartActivity.SKIP_LOADING_KEY,true);
        startActivity(intent);
        System.gc();
        finish();
    }

    @Override
    public void onErrorDeactivated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_sign_up.setEnabled(true);
            }
        });
    }

    @Override
    public void onRegisterSuccess(Client client) {
        dataBaseManager.saveClientData(client);
        SharedPreferencesManager.setUserData(this,true);
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LoginActivity.GET_PHONE_NUMB , ed_tel_number.getRawText());
        Intent intent = new Intent(this, SmsCodeActivity.class);
        intent.putExtra(SmsCodeActivity.GET_PHONE_NUMB, ed_tel_number.getText().toString());
        intent.putExtra(SmsCodeActivity.ON_FORWARD_EXTRA,SmsCodeActivity.TO_LOGIN);
        startActivity(intent);
        System.gc();
        finish();
    }

    @Override
    public void onRegisterFailed(int error_msg) {
        showToast(error_msg);
    }

    @Override
    public void onPhoneFieldError(int textMsgResId) {
        phoneNumber_helper.setError(getString(textMsgResId));
    }

    @Override
    public void onEmailError(int textMsgResId) {
        userEmail_helper.setError(getString(textMsgResId));
    }

    @Override
    public void onUserNameError(int textMsgResId) {
        userName_helper.setError(getString(textMsgResId));
    }

    @Override
    public void onPasswordError(int textMsgResId) {
        userPassword_helper.setError(getString(textMsgResId));
    }

    @Override
    public void onLockRegisterButton() {
        btn_sign_up.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
