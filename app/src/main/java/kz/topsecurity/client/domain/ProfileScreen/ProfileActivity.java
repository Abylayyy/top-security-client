package kz.topsecurity.client.domain.ProfileScreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen.CropPictureActivity;
import kz.topsecurity.client.domain.ProfileScreen.EditEmailScreen.EditEmailActivity;
import kz.topsecurity.client.domain.ProfileScreen.EditPasswordScreen.EditPasswordActivity;
import kz.topsecurity.client.domain.SetSecretCancelCodeScreen.SetSecretCancelCodeActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.FileHelper;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.photo.PhotoResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    String stringUri;

    boolean isMadeChanges = false;

    private static final int SELECT_PHOTO = 1;
    private static final int CROP_PHOTO = 3;
    private static final int READ_STORAGE = 22;
    private static final int CHANGE_EMAIL = 41;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        String path = saveImage(bitmap);
                        startActivityForResult((new Intent(ProfileActivity.this, CropPictureActivity.class).putExtra(CropPictureActivity.IMAGE_SOURCE,path)),CROP_PHOTO);
                        //uploadImage(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
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
                    String stringExtra = imageReturnedIntent.getStringExtra(CROPPED_IMAGE_PATH);
                    SharedPreferencesManager.setAvatarUriValue(this, stringExtra);
                    Bitmap bitmap = getBitmap(stringExtra);
                    setImage(bitmap, iv_user_avatar);
                    uploadMultipart(this,stringExtra);
                   // detectFace(bitmap);
                    isMadeChanges = true;
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

    private void detectFace(Bitmap bitmap) {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        if(faces!=null){
            showToast("FaCE found");
        }
        else{
            showToast("No faces");
        }
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
                    new MultipartUploadRequest(context, "http://gpstracking.muratov.kz/api/client/photo")
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
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    private void setUserData() {
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();
        if(clientData!=null){
            userAvatar = clientData.getPhoto();
        }
        setAvatar(iv_user_avatar,userAvatar);
        ((EditText)findViewById(R.id.ed_edit_user_email)).setHint(clientData.getEmail());
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
                loadImage();
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
        super.finish();
    }
}
