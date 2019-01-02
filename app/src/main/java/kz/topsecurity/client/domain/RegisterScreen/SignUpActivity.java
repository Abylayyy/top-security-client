package kz.topsecurity.client.domain.RegisterScreen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.InputCodeScreen.SmsCodeActivity;
import kz.topsecurity.client.domain.LoginScreen.LoginActivity;
import kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen.CropPictureActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.base.BaseFragmentActivity;
import kz.topsecurity.client.fragments.register.additionalFields.AdditionalRegistrationFieldsFragment;
import kz.topsecurity.client.fragments.register.avatarField.AvatarFieldFragment;
import kz.topsecurity.client.fragments.register.loadingField.LoadingFragment;
import kz.topsecurity.client.fragments.register.signUpFields.RegisterSignFieldsFragment;
import kz.topsecurity.client.fragments.register.usernameFields.UserNameFieldsFragment;
import kz.topsecurity.client.helper.PhoneHelper;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.photo.PhotoResponse;
import kz.topsecurity.client.presenter.registerPresenter.SignUpPresenter;
import kz.topsecurity.client.presenter.registerPresenter.SignUpPresenterImpl;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.RegisterSignFieldsPresenter;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.view.registerView.SignUpView;
import kz.topsecurity.client.view.registerView.fragmentsView.UserNameFieldsView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SignUpActivity extends BaseFragmentActivity implements RegisterSignFieldsFragment.RegisterSignFieldsFragmentCallback,
        AdditionalRegistrationFieldsFragment.AdditionalRegistrationFieldsCallback,
        AvatarFieldFragment.AvatarFieldFragmentCallback,
        UserNameFieldsFragment.UserNameFieldsCallback,
        SignUpView {


    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView iv_back;

    String currentFragment;

    String userPhone;
    String userPassword;
    String userEmail;
    String userIIN;
    String userPhoto;
    String userFirstName;
    String userLastName;
    String userPatronymic;

    private static final int IMAGE_CAPTURE = 98;
    private static final int CROP_PHOTO = 710;
    public static final String CROPPED_IMAGE_PATH = "cropped_image_path_extra";
    public SignUpPresenter presenter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            WeakReference weakdata = null;
            weakdata = new WeakReference<Bitmap>(bitmap);
            Intent intent = new Intent(SignUpActivity.this, CropPictureActivity.class).putExtra(CropPictureActivity.BITMAP_IMAGE, bitmap);
            intent.putExtra("BitmapImage", bitmap);
            startActivityForResult(intent, CROP_PHOTO);
            weakdata.clear();
        } else if (requestCode == CROP_PHOTO && resultCode == RESULT_OK) {
            String stringExtra = data.getStringExtra(CROPPED_IMAGE_PATH);
            Bitmap bitmap = getBitmap(stringExtra);
            WeakReference weakdata = new WeakReference<Bitmap>(bitmap);
            checkAndUploadAvatar(stringExtra, bitmap);
            weakdata.clear();
        }
    }

    private void checkAndUploadAvatar(final String stringExtra, final Bitmap bitmap) {
        showLoadingDialog();
        OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener = new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                hideProgressDialog();
                if (firebaseVisionFaces.size() == 0) {
                    showToast(R.string.face_not_detected);
                } else if (firebaseVisionFaces.size() == 1) {
                    showToast(R.string.success);
                    prepareAvatarToSave(stringExtra, bitmap);
                } else {
                    showToast(R.string.more_than_one_face_in_picture);
                }
            }
        };
        OnFailureListener onFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showToast("FAILED");
            }
        };
        detectFace(bitmap, onSuccessListener, onFailureListener);
    }

    private void prepareAvatarToSave(String stringExtra, Bitmap bitmap) {
        SharedPreferencesManager.setAvatarUriValue(this, stringExtra);
        try {
            AvatarFieldFragment fragment = (AvatarFieldFragment) getSupportFragmentManager().findFragmentByTag(AvatarFieldFragment.class.getSimpleName());
            fragment.setImage(bitmap);
        } catch (Exception ex) {

        }
        //TODO : setImageLogic
        uploadMultipart(this, bitmap);
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void uploadMultipart(final Context context, Bitmap imagePath) {

        File file = getCreateFile(imagePath);
        if (imagePath == null) {
            showToast(R.string.file_not_found);
            return;
        }
        if (file == null)
            return;
        showLoadingDialog();
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",
                file.getName(),
                RequestBody.create(MediaType.parse("image/*"),
                        file));
        Disposable success = new RequestService<>(new RequestService.RequestResponse<PhotoResponse>() {
            @Override
            public void onSuccess(PhotoResponse r) {
                hideProgressDialog();
                String src = r.getSrc();
                userPhoto = src;
                showToast(R.string.photo_successful_saved_code);
                //registerUser();
            }

            @Override
            public void onFailed(PhotoResponse data, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                hideProgressDialog();
                showToast(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .uploadPhotoForRegistration(RetrofitClient.getRequestToken(), filePart).subscribeOn(Schedulers.newThread()));

        compositeDisposable.add(success);
    }

    private void registerUser() {
        Log.d(TAG, userEmail);
        Log.d(TAG, userPassword);
        Log.d(TAG, userPhone);
        Log.d(TAG, userPhoto);
        Log.d(TAG, userFirstName);
    }

    private File getCreateFile(Bitmap imagePath) {
        String filename = "image_" + String.valueOf(System.currentTimeMillis());
        File f = new File(getCacheDir(), filename);
        boolean newFile = false;
        boolean isError = false;
        try {
            newFile = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!newFile)
            return null;
        Bitmap bitmap = imagePath;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            isError = true;
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            isError = true;
        }
        if (isError)
            return null;
        return f;
    }

    private Task<List<FirebaseVisionFace>> task;

    private void detectFace(Bitmap bitmap,
                            OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener,
                            OnFailureListener onFailureListener) {

        FirebaseApp.initializeApp(this);
        FirebaseVisionFaceDetector faceDetector =
                FirebaseVision.getInstance().getVisionFaceDetector();
//        FirebaseVisionFaceDetectorOptions build = new FirebaseVisionFaceDetectorOptions.Builder()
//                .setModeType(ACCURATE_MODE)
//                .setLandmarkType(ALL_LANDMARKS)
//                .setClassificationType(ALL_CLASSIFICATIONS)
//                .setMinFaceSize(0.15f)
//                .setTrackingEnabled(true)
//                .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        task = faceDetector.detectInImage(image);
        task.addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

    public Bitmap getBitmap(String path) {
        WeakReference fileData = null;
        WeakReference data = null;
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            fileData = new WeakReference<File>(f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            fileData.clear();
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileData != null)
                fileData.clear();
        }
        return bitmap;
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
            goBack();
        });
    }

    private void goBack() {
        showAreYouSureDialog(getString(R.string.are_you_sure_what_exit_from_activation), new CustomSimpleDialog.Callback() {
            @Override
            public void onCancelBtnClicked() {
                dissmissAreYouSureDialog();
            }

            @Override
            public void onPositiveBtnClicked() {
                dissmissAreYouSureDialog();
                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                intent.putExtra(StartActivity.SKIP_LOADING_KEY, true);
                startActivity(intent);
                System.gc();
                finish();
            }
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


    private void setNextFragment() {
        if (currentFragment.equals(RegisterSignFieldsFragment.class.getSimpleName())) {
            setAdditionalFieldsFragment();
        } else if (currentFragment.equals(AdditionalRegistrationFieldsFragment.class.getSimpleName()))
            setAvatarFieldFragment();
    }

    private void setSignFieldsFragment() {
        RegisterSignFieldsFragment registerSignFieldsFragment = RegisterSignFieldsFragment.newInstance();
        currentFragment = replaceFragment(registerSignFieldsFragment, RegisterSignFieldsFragment.class.getSimpleName(), false);
    }

    private void setAdditionalFieldsFragment() {
        AdditionalRegistrationFieldsFragment additionalRegistrationFieldsFragment = AdditionalRegistrationFieldsFragment.newInstance();
        currentFragment = replaceFragment(additionalRegistrationFieldsFragment, AdditionalRegistrationFieldsFragment.class.getSimpleName(), false);
    }

    private void setAvatarFieldFragment() {
        AvatarFieldFragment additionalRegistrationFieldsFragment = AvatarFieldFragment.newInstance();
        currentFragment = replaceFragment(additionalRegistrationFieldsFragment, AvatarFieldFragment.class.getSimpleName(), false);
    }

    private void setUserNameFieldFragment() {
        UserNameFieldsFragment userNameFieldsFragment = UserNameFieldsFragment.newInstance();
        currentFragment = replaceFragment(userNameFieldsFragment, UserNameFieldsFragment.class.getSimpleName(), false);
    }

    @Override
    public void onMainFieldsCorrect(String phone, String password) {
        this.userPhone = phone;
        this.userPassword = password;
        setAdditionalFieldsFragment();
    }

    @Override
    public void showToast(int msg) {
        showToast(getString(msg));
    }

    @Override
    public void onSignUp() {
//        Log.d(TAG,userEmail);
//        Log.d(TAG,userPassword);
//        Log.d(TAG,userPhone);
//        Log.d(TAG,userPhoto);
//        Log.d(TAG,userName);
        presenter.register(userPhone, userEmail, userIIN, userPassword, userPhoto, userFirstName, userLastName, userPatronymic);
    }

    @Override
    public void onAdditionalFieldsCorrect(String userEmail, String IIN) {
        this.userEmail = userEmail;
        this.userIIN = IIN;
        setUserNameFieldFragment();
    }

    @Override
    public void onUserNameFieldsCorrect(String firstname, String lastname, String patronymic) {
        this.userFirstName = firstname;
        this.userLastName = lastname;
        this.userPatronymic = patronymic;
        setAvatarFieldFragment();
    }

    @Override
    public void onClosed() {
        if (currentFragment.equals(AvatarFieldFragment.class.getSimpleName())) {
            currentFragment = AdditionalRegistrationFieldsFragment.class.getSimpleName();
        } else if (currentFragment.equals(AdditionalRegistrationFieldsFragment.class.getSimpleName())) {
            currentFragment = RegisterSignFieldsPresenter.class.getSimpleName();
        }
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
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(R.string.button_deny,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                }).setCancelable(false).setMessage(messageResId).show();
    }

    private void showChooser() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.fragment_chooser_bottom_sheet, null);
        LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_camera);
        LinearLayout gallery = (LinearLayout) sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_gallery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivityPermissionsDispatcher.capturePhotoWithCheck(SignUpActivity.this);
                mBottomSheetDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String test = "";
            }
        });
        mBottomSheetDialog.show();
    }

    private void loadImage() {
        startActivityForResult((new Intent(SignUpActivity.this, CropPictureActivity.class)), CROP_PHOTO);
    }

    @Override
    public void onLoadAvatarRequest() {
        showChooser();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        goBack();
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
