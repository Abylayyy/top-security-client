package kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import kz.topsecurity.client.R;

@RuntimePermissions
public class CropPictureActivity extends BaseActivity {

    private static final String TAG = CropPictureActivity.class.getSimpleName();

    @BindView(R.id.crop_view) public CropImageView cropView;

    public static final String IMAGE_SOURCE = "image_source_extra";

    private RectF mFrameRect = null;
    private Uri mSourceUri = null;

    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;

    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";

    private CropImageView.OnCropImageCompleteListener listener = (view, result) -> {
        hideProgressDialog();
        if(result.isSuccessful())
            returnImage(saveImage(result.getBitmap()));
    };

    @Override public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_OK) {
            mFrameRect = null;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    mSourceUri = result.getData();
                    cropView.setImageUriAsync(mSourceUri);
                    break;
                case REQUEST_SAF_PICK_IMAGE:
                    mSourceUri = result.getData();
                    cropView.setImageUriAsync(mSourceUri);
                    break;
            }
        }
        else{
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CropPictureActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}) public void cropImage() {
        showLoadingDialog();
        cropView.getCroppedImageAsync();
    }

    private void returnImage(String s) {
        if(s!=null && !s.isEmpty()){
            Intent intent = new Intent();
            intent.putExtra(ProfileActivity.CROPPED_IMAGE_PATH,s);
            setResult(RESULT_OK,intent);
        }
        finish();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForCrop(PermissionRequest request) {
        showRationaleDialog(R.string.permission_crop_rationale, request);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForPick(PermissionRequest request) {
        showRationaleDialog(R.string.permission_crop_rationale, request);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE) public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Log.i(TAG,"getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_picture);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cropView.setAspectRatio(1,1);
        cropView.setOnCropImageCompleteListener(listener);
        CropPictureActivityPermissionsDispatcher.pickImageWithCheck(CropPictureActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crop_picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        if (itemId == R.id.crop_image_menu_rotate_right){
            cropView.rotateImage(90);
        }
        if(itemId == R.id.crop_image_menu_crop){
            cropImage();
        }

        return super.onOptionsItemSelected(item);
    }

    public String saveImage(Bitmap myBitmap) {
        showLoadingDialog();
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
        finally {
            hideProgressDialog();
        }
        return "";
    }
}
