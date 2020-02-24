package kz.topsecurity.client.fragments.main_fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.CompositeDisposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.domain.PlaceScreen.PlaceActivity;
import kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen.CropPictureActivity;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.fragments.main_fragment.private_info.InfoFragment;
import kz.topsecurity.client.fragments.main_fragment.private_info.SecurityFragment;
import kz.topsecurity.client.fragments.main_fragment.private_info.SettingsFragment;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.PhoneHelper;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.utils.GlideApp;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

@RuntimePermissions
public class ProfileMainFragment extends Fragment {

    private ProfileMainCallBack mCallback;

    private String stringUri;
    private boolean isMadeChanges = false;
    Fragment currentFragment;
    private boolean isTerminate = false;
    DataBaseManager dataBaseManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MainActivityNew mActivity;
    Dialog dialog;

    public ProfileMainFragment() {}

    @BindView(R.id.profPageImage) CircleImageView profImage;
    @BindView(R.id.changeProfInfo) TextView changeInfo;
    @BindView(R.id.profName) EditText profName;
    @BindView(R.id.profSurnameTxt) EditText surName;
    @BindView(R.id.profIyn) EditText profZhsn;
    @BindView(R.id.profPhoneNum) RoundCorneredEditTextWithPhoneMask profPhone;
    @BindView(R.id.profEmail) EditText profEmail;

    @BindView(R.id.view) View v1;
    @BindView(R.id.view3) View v3;
    @BindView(R.id.view4) View v4;
    @BindView(R.id.view5) View v5;

    @BindView(R.id.errName) TextView errName;
    @BindView(R.id.errSurname) TextView errSurname;
    @BindView(R.id.errIyn) TextView errIyn;
    @BindView(R.id.errEmail) TextView errEmail;

    @BindView(R.id.profFavor) TextView profFavor;
    @BindView(R.id.profSecur) TextView profSecure;
    @BindView(R.id.profSetting) TextView profSetting;
    @BindView(R.id.profInf) TextView profInf;
    @BindView(R.id.profLogout) TextView profLogout;
    @BindView(R.id.errImage) ImageView errImage;

    @BindView(R.id.placeProf)
    ImageView profPlace;

    View[] array, eArray;
    EditText[] arrayEdit;

    private static final int SELECT_PHOTO = 1;
    private static final int READ_STORAGE = 22;
    public static final int CROP_PHOTO = 3;
    private static final int IMAGE_CAPTURE = 2;

    public static final String FORCED_LOAD_AVATAR = "FORCED_LOAD_AVATAR_EXTRA";
    public static final String CROPPED_IMAGE_PATH = "cropped_image_path_extra";
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";
    private Task<List<FirebaseVisionFace>> task;

    public interface ProfileMainCallBack {
        void showToast(String msg);
        void onNotSaved();
        void onSaved();
        void onUpdateInfoCorrect(String token, String username, String email, String name, String surname, String iin);
        void onUpdate();
        void onStopService();
        void onCameraRequest(CircleImageView imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        array = new View[]{v1, v5, v4, v3};
        eArray = new View[] {errName, errSurname, errIyn, errEmail};
        arrayEdit = new EditText[]{profName, surName, profZhsn, profEmail};
        dataBaseManager = new DataBaseManagerImpl(getActivity());

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.loading_layout);

        Client clientData = (new DataBaseManagerImpl(getActivity())).getClientData();
        WeakReference data = new WeakReference<>(clientData);

        changeInfo.setOnClickListener(v -> {
            if (changeInfo.getText().equals("Изменить")) {
                mCallback.onNotSaved();
                changeProfInfo();
            } else {
                saveAllInfo();
            }
        });

        profImage.setEnabled(false);
        profImage.setOnClickListener(v -> showChooser());

        if (dataBaseManager.getClientData() != null) {
            setUserData();
        }

        setUserAvatar();

        profFavor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PlaceActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        profSecure.setOnClickListener(v -> {
            currentFragment = new SecurityFragment();
            replaceFragment(currentFragment, "secure", true);
        });
        profSetting.setOnClickListener(v -> {
            currentFragment = new SettingsFragment();
            replaceFragment(currentFragment, "setting", true);
        });
        profInf.setOnClickListener(v -> {
            currentFragment = new InfoFragment();
            replaceFragment(currentFragment, "info", true);
        });
        profLogout.setOnClickListener(v -> {
            backToMain();
        });

        for (int i = 0; i < arrayEdit.length; i++) {
            int finalI = i;
            arrayEdit[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setColor(array[finalI], "b6b6b6");
                    setGone(eArray[finalI]);
                }
                @Override
                public void afterTextChanged(Editable s) { }
            });
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityNew){
            mActivity =(MainActivityNew) context;
        }
        mCallback = (ProfileMainFragment.ProfileMainCallBack) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void backToMain() {
        mCallback.onStopService();
        clearData();
        isTerminate = true;
        startActivity(new Intent(getActivity(), StartActivity.class));
        getActivity().finishAffinity();
    }


    private void setUserAvatar() {
        Client client = dataBaseManager.getClientData();
        String avatar = null;
        if (client != null) {
            avatar = client.getPhoto();
        }
        checkAvatar(avatar);
    }

    private void setUserData() {
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();
        if (clientData != null) {
            userAvatar = clientData.getPhoto();
        }
        String finalUserAvatar = userAvatar;
        checkAvatar(finalUserAvatar);
        new Handler().postDelayed(()-> {
            if (clientData.getEmail() != null) {
                profEmail.setText(clientData.getEmail());
            }
            profZhsn.setText(clientData.getIin());
            if (clientData.getPatronymic() == null) {
                profName.setText(clientData.getFirstname());
                surName.setText(clientData.getLastname());
            }
            profPhone.setText(PhoneHelper.getFormattedPhone(clientData.getPhone()));
        }, 300);
        GlideApp.with(getActivity())
                .load(finalUserAvatar)
                .placeholder(R.drawable.big_prof_image)
                .into(profImage);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    void clearData(){
        SharedPreferencesManager.clearData(getActivity());
        dataBaseManager.dropClientData();
        dataBaseManager.dropDeviceDataTable();
        ProfileActivity.savedBitmap = null;
    }

    private void showChooser() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.fragment_chooser_bottom_sheet, null);
        LinearLayout camera = sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_camera);
        LinearLayout gallery = sheetView.findViewById(R.id.fragment_chooser_bottom_sheet_gallery);
        camera.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            mBottomSheetDialog.dismiss();
        });

        gallery.setOnClickListener(v -> {
            loadImage();
            mBottomSheetDialog.dismiss();
        });
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setOnDismissListener(dialog -> {
            String test = "";
        });
        mBottomSheetDialog.show();
    }

    private void loadImage() {
        startActivityForResult((new Intent(getActivity(), CropPictureActivity.class)),CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    if(imageUri==null)
                        return;
                    if (Build.VERSION.SDK_INT >= 19) {
                        final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getActivity().getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                    }

                    stringUri = imageUri.toString();
                    SharedPreferencesManager.setAvatarUriValue(getActivity(), stringUri);
                    setImage(stringUri, profImage);
                    isMadeChanges = true;
                    Bitmap bitmap = null;
                    WeakReference list = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        list = new WeakReference<>(bitmap);
                        String path = saveImage(bitmap);
                        startActivityForResult((new Intent(getActivity(), CropPictureActivity.class).putExtra(CropPictureActivity.IMAGE_SOURCE, path)), CROP_PHOTO);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        list.clear();
                    }
                }
                break;

            case CROP_PHOTO:{
                if (resultCode == RESULT_OK) {
                    String stringExtra = data.getStringExtra(CROPPED_IMAGE_PATH);
                    Bitmap bitmap = getBitmap(stringExtra);
                    WeakReference list = new WeakReference<>(bitmap);
                    checkAndUploadAvatar(stringExtra, bitmap);
                    list.clear();
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

    public void setImage(String imagePath, ImageView iv_user_avatar){
        iv_user_avatar.setImageResource(R.drawable.placeholder_avatar);
        if (imagePath!=null && !imagePath.isEmpty()){
            Bitmap bitmap = getBitmap(imagePath);
            WeakReference data = new WeakReference<>(bitmap);
            if(bitmap!=null){
                setImage(bitmap,iv_user_avatar);
                data.clear();
                return;
            }
            else{
                try {
                    setImage(Uri.parse(imagePath),iv_user_avatar);
                    data.clear();
                }
                catch (Exception ex){

                }
            }
        }
    }

    public void setImage(Uri imageUri, ImageView iv_user_avatar){
        WeakReference data =null;
        try {
            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
            data = new WeakReference<>(selectedImage);
            iv_user_avatar.setImageBitmap(selectedImage);
            data.clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if(data!=null)
                data.clear();
        }
    }

    public void setImage(Bitmap bmp, ImageView iv_user_avatar){
        try {
            iv_user_avatar.setImageBitmap(bmp);
        }
        catch (Exception ex){

            ex.printStackTrace();
        }
    }


    public Bitmap getBitmap(String path) {
        WeakReference fileData =null;
        WeakReference data =null;
        Bitmap bitmap;
        try {
            File f= new File(path);
            fileData = new WeakReference<>(f);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            fileData.clear();
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(fileData!=null)
                fileData.clear();
        }
        return bitmap;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(getActivity().getFilesDir(),
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
            MediaScannerConnection.scanFile(getActivity(),
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

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationaleForPick(PermissionRequest request) {
        showRationaleDialog(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ProfileMainFragmentPermissionsDispatcher.onRequestPermissionsResult(ProfileMainFragment.this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void capturePhoto() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            ProfileMainFragment.this.startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void showDeniedForCamera() {
        Toast.makeText(getActivity(), R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void showNeverAskForCamera() {
        Toast.makeText(getActivity(), R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.button_allow,
                (dialog, which) -> request.proceed()).setNegativeButton(R.string.button_deny,
                (dialog, which) -> request.cancel()).setCancelable(false).setMessage(R.string.permission_camera).show();
    }

    private void prepareAvatarToSave(String stringExtra, Bitmap bitmap) {
        SharedPreferencesManager.setCheckClientAvatar(getActivity(),true);
        SharedPreferencesManager.setAvatarUriValue(getActivity(), stringExtra);
        setImage(bitmap, profImage);
        new Handler().postDelayed(()-> uploadMultipart(getActivity(),stringExtra), 300);

        isMadeChanges = true;
    }

    public void uploadMultipart(final Context context, String imagePath) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, Constants.ACTIVE_DOMAIN+"/api/client/photo")
                            .addHeader("Authorization", RetrofitClient.getRequestToken())
                            // starting from 3.1+, you can also use content:// URI string instead of absolute file
                            .addFileToUpload(imagePath, "file")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();

        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    private void checkAndUploadAvatar(final String stringExtra,final Bitmap bitmap) {
        dialog.show();
        OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener = firebaseVisionFaces -> {

            if(firebaseVisionFaces.size()==0){
                dialog.dismiss();
                showToast(getString(R.string.face_not_detected));
            }
            else if(firebaseVisionFaces.size()==1) {
                dialog.dismiss();
                prepareAvatarToSave(stringExtra, bitmap);
                showToast(getString(R.string.success));
            }
            else {
                dialog.dismiss();
                showToast(getString(R.string.more_than_one_face_in_picture));
            }
        };
        OnFailureListener onFailureListener = e -> showToast("Error");
        detectFace(bitmap,onSuccessListener,onFailureListener );
    }

    private void showToast(String face_not_detected) {
        Toasty.info(getActivity(), face_not_detected, Toast.LENGTH_SHORT).show();
    }

    private void detectFace(Bitmap bitmap,
                            OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener,
                            OnFailureListener onFailureListener) {

        FirebaseApp.initializeApp(getActivity());
        FirebaseVisionFaceDetector faceDetector =
                FirebaseVision.getInstance().getVisionFaceDetector();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        task = faceDetector.detectInImage(image);
        task.addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

    ArrayList<Animator> animatorList = new ArrayList<>();
    AnimatorSet scaleAnimSet;
    private void animateLoadImageButton(int anim_time){
        scaleAnimSet = new AnimatorSet();
        scaleAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimSet.setDuration(anim_time);
        final ObjectAnimator alphaAnimator1= ObjectAnimator.ofFloat(errImage, "ScaleX", 1.0f, 0.8f);
        alphaAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator1.setDuration(anim_time/2);
        animatorList.add(alphaAnimator1);
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(errImage, "ScaleY", 1.0f, 0.8f);
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

    void checkAvatar(String userAvatar){
        String imageStringUri;
        imageStringUri = SharedPreferencesManager.getAvatarUriValue(getContext());
        if(((userAvatar==null || userAvatar.isEmpty()) || userAvatar.contains("no-avatar")) && (imageStringUri==null || imageStringUri.isEmpty())){
            errImage.setVisibility(View.VISIBLE);
            showToast("Загрузите свою фотографию");
            mCallback.onNotSaved();
            changeProfInfo();
            animateLoadImageButton(800);
        }
        else{
            SharedPreferencesManager.setCheckClientAvatar(getActivity(), true);
            errImage.setVisibility(View.GONE);
            stopAnimLoadImageButton();
            mCallback.onSaved();
        }
    }

    private void saveAllInfo() {
        if (checkEditInfo(
                profName.getText().toString().trim(),
                surName.getText().toString().trim(),
                profZhsn.getText().toString().trim(),
                profEmail.getText().toString().trim())) {
            mCallback.onSaved();
            changeInfo.setText("Изменить");
            stopAnimLoadImageButton();
            profPlace.setVisibility(View.INVISIBLE);
            changeInfo.setTextColor(Color.parseColor("#1F9900"));
            profImage.setEnabled(false);
            for (View view : array) {
                view.setVisibility(View.INVISIBLE);
            }
            for (EditText text : arrayEdit) {
                text.setEnabled(false);
            }
        }
    }

    private void changeProfInfo() {
        changeInfo.setText("Сохранить");
        changeInfo.setTextColor(Color.parseColor("#EF3B39"));
        profPlace.setVisibility(View.VISIBLE);
        profImage.setEnabled(true);
        for (View view : array) {
            view.setVisibility(View.VISIBLE);
        }
        for (EditText text : arrayEdit) {
            text.setEnabled(true);
        }
    }

    private boolean checkEditInfo(String name, String surname, String iyn, String email) {
        boolean check = false;
        String[] results = {"error", "error", "error", "error"};
        if (TextUtils.isEmpty(name)) { setVisible(errName); setColor(v1, "EF3B39");} else {
            results[0] = "correct"; setGone(errName); setColor(v1, "b6b6b6");
        }
        if (TextUtils.isEmpty(surname)) { setVisible(errSurname); setColor(v5, "EF3B39");} else {
            results[1] = "correct"; setGone(errSurname); setColor(v5, "b6b6b6");
        }
        if (iyn.length() != 12) { setVisible(errIyn); setColor(v4, "EF3B39");} else {
            results[2] = "correct"; setGone(errIyn); setColor(v4, "b6b6b6");
        }
        if (TextUtils.isEmpty(email) || !email.contains("@")) { setVisible(errEmail); setColor(v3, "EF3B39");} else {
            results[3] = "correct"; setGone(errEmail); setColor(v3, "b6b6b6");
        }
        if (results[0].equals("correct") && results[1].equals("correct") && results[2].equals("correct") &&
                results[3].equals("correct")){
            check = true;
            mCallback.onUpdateInfoCorrect(RetrofitClient.getRequestToken(), name, email, name, surname, iyn);
            mCallback.onCameraRequest(profImage);
            mCallback.onUpdate();

        }
        return check;
    }

    private void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    private void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setColor(View view, String color) {
        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    public void replaceFragment(Fragment fragment, String fragmentTag, boolean withBackStack){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction  = getActivity().getSupportFragmentManager().beginTransaction();
        if(withBackStack){
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.addToBackStack(BACK_STACK_ROOT_TAG);
        }
        transaction.replace(R.id.mainBottomContainer, fragment,fragmentTag);
        transaction.commit();
    }
}
