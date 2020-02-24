package kz.topsecurity.client.domain.RegisterScreen;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.InputCodeScreen.SmsCodeActivity;
import kz.topsecurity.client.domain.base.BaseFragmentActivity;
import kz.topsecurity.client.fragments.register.loadingField.LoadingFragment;
import kz.topsecurity.client.fragments.register.signUpFields.RegisterSignFieldsFragment;
import kz.topsecurity.client.helper.PhoneHelper;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.registerPresenter.SignUpPresenter;
import kz.topsecurity.client.presenter.registerPresenter.SignUpPresenterImpl;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.view.registerView.SignUpView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SignUpActivity extends BaseFragmentActivity implements SignUpView, RegisterSignFieldsFragment.RegisterSignFieldsFragmentCallback {


    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ConstraintLayout iv_back;

    String currentFragment;
    String userPhone;
    String userPassword;

    private static final int IMAGE_CAPTURE = 98;
    private static final int CROP_PHOTO = 710;
    public static final String CROPPED_IMAGE_PATH = "cropped_image_path_extra";
    public SignUpPresenter presenter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setupToolbar();
        setSignFieldsFragment();
        presenter = new SignUpPresenterImpl(this);
    }

    private void setupToolbar() {
        iv_back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    CustomSimpleDialog customSimpleDialog;

    public void showAreYouSureDialog(String message, CustomSimpleDialog.Callback listener) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        customSimpleDialog = new CustomSimpleDialog();

        Bundle arg = new Bundle();
        arg.putString(CustomSimpleDialog.DIALOG_MESSAGE, message);

        customSimpleDialog.setArguments(arg);
        customSimpleDialog.setCancelable(false);
        customSimpleDialog.setListener(listener);
        customSimpleDialog.show(ft, "dialog");
    }

    public void dissmissAreYouSureDialog() {
        customSimpleDialog.dismiss();
    }


    private void setSignFieldsFragment() {
        RegisterSignFieldsFragment registerSignFieldsFragment = RegisterSignFieldsFragment.newInstance();
        currentFragment = replaceFragment(registerSignFieldsFragment, RegisterSignFieldsFragment.class.getSimpleName(), false);
    }

    @Override
    public void onMainFieldsCorrect(String phone, String password) {
        this.userPhone = phone;
        this.userPassword = password;
    }

    @Override
    public void showToast(int msg) {
        showToast(getString(msg));
    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onSignUp() {
        presenter.register(userPhone, userPassword);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForPick(PermissionRequest request) {
        showRationaleDialog(R.string.permission_camera, request);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Toast.makeText(this, R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this).setPositiveButton(R.string.button_allow,
                (dialog, which) -> request.proceed()).setNegativeButton(R.string.button_deny,
                (dialog, which) -> request.cancel()).setCancelable(false).setMessage(messageResId).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRegisterError(int error) {
        showToast(error);
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    @Override
    public void onRegisterSuccess(Client client) {
        dataBaseManager.saveClientData(client);
        SharedPreferencesManager.setUserData(this, true);
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LoginActivity.GET_PHONE_NUMB , ed_tel_number.getRawText());
        Intent intent = new Intent(this, SmsCodeActivity.class);
        String formattedPhone = PhoneHelper.getFormattedPhone(client.getPhone());
        intent.putExtra(SmsCodeActivity.GET_PHONE_NUMB, formattedPhone);
        intent.putExtra(SmsCodeActivity.ON_FORWARD_EXTRA, SmsCodeActivity.TO_LOGIN);
        startActivity(intent);
        System.gc();
        finish();
    }

    @Override
    public void showLoading() {
        iv_back.setEnabled(false);
        addFragment(LoadingFragment.newInstance(), LoadingFragment.class.getSimpleName());
    }

    @Override
    public void hideLoading() {
        iv_back.setEnabled(true);
        removeFragment(LoadingFragment.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }
}
