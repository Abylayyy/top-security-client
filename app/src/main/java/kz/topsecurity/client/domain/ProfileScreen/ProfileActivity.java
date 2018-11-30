package kz.topsecurity.client.domain.ProfileScreen;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen.CropPictureActivity;
import kz.topsecurity.client.domain.ProfileScreen.EditEmailScreen.EditEmailActivity;
import kz.topsecurity.client.domain.ProfileScreen.EditPasswordScreen.EditPasswordActivity;
import kz.topsecurity.client.domain.SetSecretCancelCodeScreen.SetSecretCancelCodeActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RetrofitClient;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ProfileActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.tv_delete_account) TextView tv_delete_account;
    @BindView(R.id.cl_profile_view) ConstraintLayout cl_profile_view;
    @BindView(R.id.cl_delete_view) ConstraintLayout cl_delete_view;
    @BindView(R.id.btn_ok) Button btn_ok;
    @BindView(R.id.btn_cancel) Button btn_cancel;
    @BindView(R.id.rl_user_avatar) RelativeLayout rl_user_avatar;
    @BindView(R.id.iv_edit_user_email) ImageView iv_edit_user_email;
    @BindView(R.id.iv_edit_user_password) ImageView iv_edit_user_password;
    @BindView(R.id.iv_user_avatar) CircleImageView iv_user_avatar;
    @BindView(R.id.tv_add_secret_code) TextView tv_add_secret_code;
    @BindView(R.id.iv_upload_avatar) ImageView iv_upload_avatar;

    String stringUri;

    boolean isMadeChanges = false;

    private static final int SELECT_PHOTO = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final int CROP_PHOTO = 3;
    private static final int READ_STORAGE = 22;
    private static final int CHANGE_EMAIL = 41;

    public static final String FORCED_LOAD_AVATAR = "FORCED_LOAD_AVATAR_EXTRA";
    public static final String CROPPED_IMAGE_PATH = "cropped_image_path_extra";
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:{
                if (resultCode == RESULT_OK) {
                    Uri imageUri = imageReturnedIntent.getData();
                    if(imageUri==null)
                        return;
                    if (Build.VERSION.SDK_INT >= 19) {
                        final int takeFlags = imageReturnedIntent.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                    }

                    stringUri = imageUri.toString();
                    SharedPreferencesManager.setAvatarUriValue(this, stringUri);
                    setImage(stringUri, iv_user_avatar);
                    isMadeChanges = true;
                    Bitmap bitmap = null;
                    WeakReference data = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        data = new WeakReference<Bitmap>(bitmap);
                        String path = saveImage(bitmap);
                        startActivityForResult((new Intent(ProfileActivity.this, CropPictureActivity.class).putExtra(CropPictureActivity.IMAGE_SOURCE,path)),CROP_PHOTO);
                        //uploadImage(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        if(data!=null)
                            data.clear();
                    }
                }
                break;
            }
            case IMAGE_CAPTURE:{
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    WeakReference data = null;
                    data = new WeakReference<Bitmap>(bitmap);
                    Intent intent = new Intent(ProfileActivity.this, CropPictureActivity.class).putExtra(CropPictureActivity.BITMAP_IMAGE, bitmap);
                    intent.putExtra("BitmapImage", bitmap);
                    startActivityForResult(intent,CROP_PHOTO);
                    data.clear();
                }
                else{

                }
            }
            case CHANGE_EMAIL:{
                if(resultCode == RESULT_OK){
                    isMadeChanges = true;
                    setUserData();
                }
                break;
            }
            case CROP_PHOTO:{
                if (resultCode == RESULT_OK) {
//                    String stringExtra = imageReturnedIntent.getStringExtra(CROPPED_IMAGE_PATH);
//                    SharedPreferencesManager.setAvatarUriValue(this, stringExtra);
//                    Bitmap bitmap = getBitmap(stringExtra);
//                    setImage(bitmap, iv_user_avatar);
//                    uploadMultipart(this,stringExtra);
//                    checkAndUploadAvatar(stringExtra, bitmap );
//                    detectFace(bitmap);
//                    isMadeChanges = true;

                    String stringExtra = imageReturnedIntent.getStringExtra(CROPPED_IMAGE_PATH);
                    Bitmap bitmap = getBitmap(stringExtra);
                    WeakReference data = new WeakReference<Bitmap>(bitmap);
                    checkAndUploadAvatar(stringExtra, bitmap );
                    data.clear();
                }
                break;
            }
            case READ_STORAGE:{
                if (resultCode == RESULT_OK) {
                    openImagePicker();
                }
                break;
            }
        }
    }

    ArrayList<Animator> animatorList = new ArrayList<>();
    AnimatorSet scaleAnimSet;
    private void animateLoadImageButton(int anim_time){
        scaleAnimSet = new AnimatorSet();
        scaleAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimSet.setDuration(anim_time);
        final ObjectAnimator alphaAnimator1= ObjectAnimator.ofFloat(iv_upload_avatar, "ScaleX", 1.0f, 0.8f);
        alphaAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator1.setDuration(anim_time/2);
        animatorList.add(alphaAnimator1);
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(iv_upload_avatar, "ScaleY", 1.0f, 0.8f);
        alphaAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator2.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator2.setDuration(anim_time/2);
        animatorList.add(alphaAnimator2);
        scaleAnimSet.playTogether(animatorList);
        scaleAnimSet.start();
    }

    void stopAnimLoadImageButton(){
        if(scaleAnimSet!=null)
            scaleAnimSet.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ProfileActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void checkAndUploadAvatar(final String stringExtra,final Bitmap bitmap) {
        showLoadingDialog();
        OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener = new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                hideProgressDialog();
                if(firebaseVisionFaces.size()==0){
                    showToast(R.string.face_not_detected);
                }
                else if(firebaseVisionFaces.size()==1) {
                    showToast(R.string.success);
                    prepareAvatarToSave(stringExtra, bitmap);
                    stopAnimLoadImageButton();
                }
                else {
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
        detectFace(bitmap,onSuccessListener,onFailureListener );
    }

    private void prepareAvatarToSave(String stringExtra, Bitmap bitmap) {
        SharedPreferencesManager.setAvatarUriValue(this, stringExtra);
        setImage(bitmap, iv_user_avatar);
        uploadMultipart(this,stringExtra);
        setFinishResult(false);
        isMadeChanges = true;
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

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(getFilesDir(),
               IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");

            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void uploadImage(String imagePath) {
//        File file = new File(imagePath);
//        if(file==null) {
//            showToast(R.string.file_not_found);
//            return;
//        }
//
//        showLoadingDialog();
//
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
//        Disposable success = new RequestService<>(new RequestService.RequestResponse<PhotoResponse>() {
//            @Override
//            public void onSuccess(PhotoResponse r) {
//                hideProgressDialog();
//                showToast(R.string.photo_successful_saved_code);
//            }
//
//            @Override
//            public void onFailed(PhotoResponse data, int error_message) {
//                hideProgressDialog();
//                showToast(error_message);
//            }
//
//            @Override
//            public void onError(Throwable e, int error_message) {
//                hideProgressDialog();
//                showToast(error_message);
//            }
//        }).makeRequest(RetrofitClient.getClientApi()
//                .uploadPhoto(RetrofitClient.getRequestToken(), filePart).subscribeOn(Schedulers.newThread()));
//
//        compositeDisposable.add(success);
    }

    public void uploadMultipart(final Context context,String imagePath) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, Constants.ACTIVE_DOMAIN+"/api/client/photo")
                            .addHeader("Authorization",RetrofitClient.getRequestToken())
                            // starting from 3.1+, you can also use content:// URI string instead of absolute file
                            .addFileToUpload(imagePath, "file")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Профиль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tv_delete_account.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        rl_user_avatar.setOnClickListener(this);
        iv_edit_user_email.setOnClickListener(this);
        iv_edit_user_password.setOnClickListener(this);
        tv_add_secret_code.setOnClickListener(this);
        setUserData();

        if(!Constants.BlockedFunctions.isTwoCodeEnabled)
            tv_add_secret_code.setVisibility(View.GONE);

        boolean booleanExtra = getIntent().getBooleanExtra(FORCED_LOAD_AVATAR, false);
        if(booleanExtra){
            setFinishResult(true);
        }
    }

    boolean onGoBackMainActShouldFinish = false;

    private void setFinishResult(boolean shouldFinish) {
        onGoBackMainActShouldFinish = shouldFinish;
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    private void setUserData() {
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();
        if(clientData!=null){
            userAvatar = clientData.getPhoto();
        }
        setAvatar(iv_user_avatar,userAvatar);
        checkAvatar(userAvatar);
        ((EditText)findViewById(R.id.ed_edit_user_email)).setHint(clientData.getEmail());
    }

    void checkAvatar(String userAvatar){
        String imageStringUri = null;
        imageStringUri = SharedPreferencesManager.getAvatarUriValue(this);
        if(((userAvatar==null || userAvatar.isEmpty()) || userAvatar.contains("no-avatar"))&&(imageStringUri==null || imageStringUri.isEmpty())){
            animateLoadImageButton(800);
        }
        else{
            stopAnimLoadImageButton();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case(R.id.tv_delete_account):{
                toggleView();
                break;
            }
            case(R.id.btn_ok):{
                finish();
                break;
            }
            case(R.id.btn_cancel):{
                toggleView();
                break;
            }
            case (R.id.rl_user_avatar):{
                showChooser();
                break;
            }
            case (R.id.iv_edit_user_email):{
                startActivityForResult((new Intent(this,EditEmailActivity.class)),CHANGE_EMAIL);
                break;
            }
            case (R.id.iv_edit_user_password):{
                startActivity(new Intent(ProfileActivity.this, EditPasswordActivity.class));
                break;
            }
            case (R.id.tv_add_secret_code):{
                startActivity(new Intent(ProfileActivity.this, SetSecretCancelCodeActivity.class));
                break;
            }
            default:{
                Toast.makeText(this,"Not implemented",Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void showChooser() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.fragment_chooser_bottom_sheet, null);
        LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_camera);
        LinearLayout gallery = (LinearLayout) sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_gallery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivityPermissionsDispatcher.capturePhotoWithCheck(ProfileActivity.this);
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
                    @Override public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(R.string.button_deny,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                }).setCancelable(false).setMessage(messageResId).show();
    }

    private void loadImage() {
        startActivityForResult((new Intent(ProfileActivity.this, CropPictureActivity.class)),CROP_PHOTO);
    }

    void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        if(Build.VERSION.SDK_INT>=19) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        else{
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    boolean isProfileViewActive = true;

    private void toggleView(){
        if(isProfileViewActive){
            cl_delete_view.setVisibility(View.VISIBLE);
            cl_profile_view.setVisibility(View.GONE);
        }
        else{
            cl_delete_view.setVisibility(View.GONE);
            cl_profile_view.setVisibility(View.VISIBLE);
        }
        isProfileViewActive = !isProfileViewActive;
    }

    @Override
    public void finish(){
        if(isMadeChanges)
            setResult(RESULT_OK);
        if(onGoBackMainActShouldFinish)
        {
           Intent resIntent = new Intent();
           resIntent.putExtra(MainActivity.SHOULD_FINISH,true);
           setResult(RESULT_OK, resIntent);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
//        if(task!=null)
        super.onDestroy();
    }


}
