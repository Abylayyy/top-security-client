package kz.topsecurity.client.domain.MainScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.fragments.main_fragment.MedicineFragment;
import kz.topsecurity.client.fragments.main_fragment.ProfileMainFragment;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.presenter.health_card_presenter.HealthCardPresenter;
import kz.topsecurity.client.presenter.health_card_presenter.HealthCardPresenterImpl;
import kz.topsecurity.client.presenter.update_info_presenter.UpdateInfoPresenterImpl;
import kz.topsecurity.client.presenter.update_info_presenter.UpdatePresenter;
import kz.topsecurity.client.service.trackingService.TrackingService;
import kz.topsecurity.client.service.trackingService.interfaces.TrackingServiceBroadcastReceiverListener;
import kz.topsecurity.client.service.trackingService.model.DeviceData;


public class MainActivityNew extends BaseActivity
        implements View.OnClickListener,
        TrackingServiceBroadcastReceiverListener,
        ProfileMainFragment.ProfileMainCallBack, MedicineFragment.MedicineCallBack {

    DataBaseManager manager;

    Fragment currentFragment;
    @BindView(R.id.profNav) ImageView navProf;
    @BindView(R.id.medNav) ImageView navMed;
    private boolean checkClientAvatarExist;

    @BindView(R.id.medBottom) LinearLayout med;
    @BindView(R.id.mainImage) ImageView main;
    @BindView(R.id.mainBottom) LinearLayout bottom;
    @BindView(R.id.profileBottom) LinearLayout prof;

    String token, username, email, name, surname, iyn;
    String blood, data, weight, height, allergy, drug, zabo;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    UpdatePresenter presenter;
    HealthCardPresenter healthCardPresenter;

    public static final int IMAGE_CAPTURE = 2;

    private CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);

        if (!SharedPreferencesManager.getUserData(getApplicationContext())) {
            backToMainActivity();
        }

        manager = new DataBaseManagerImpl(getApplication());

        presenter = new UpdateInfoPresenterImpl(this);
        healthCardPresenter = new HealthCardPresenterImpl(this);

        checkClientAvatarExist();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainBottomContainer, new ProfileMainFragment()).commit();
        navProf.setImageResource(R.drawable.nav_prof_checked);
    }

    private void backToMainActivity() {
        startActivity(new Intent(getApplication(), StartActivity.class));
        finishAffinity();
    }

    private void checkClientAvatarExist(){
        checkClientAvatarExist = SharedPreferencesManager.getCheckClientAvatar( this);
        if(!checkClientAvatarExist){
            med.setEnabled(false);
            main.setEnabled(false);
            bottom.setEnabled(false);
            prof.setEnabled(false);
        } else {
            med.setEnabled(true);
            main.setEnabled(true);
            bottom.setEnabled(true);
            prof.setEnabled(true);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileBottom:
                currentFragment = new ProfileMainFragment();
                replaceFragment(currentFragment);
                navProf.setImageResource(R.drawable.nav_prof_checked);
                navMed.setImageResource(R.drawable.med_karta_not_checked);
                break;

            case R.id.mainBottom:
                startMainActivity();
                break;

            case R.id.mainImage:
                startMainActivity();
                break;

            case R.id.medBottom:
                currentFragment = new MedicineFragment();
                replaceFragment(currentFragment);
                navMed.setImageResource(R.drawable.nav_med_checked);
                navProf.setImageResource(R.drawable.nav_prof_not_checked);
                break;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplication(), AlertActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainBottomContainer, fragment).commit();
    }

    @Override
    public void onDataReceived(DeviceData data) {

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMedInfoCorrect(String blood, String data, String weight, String height, String allergy, String med, String zabo) {
        this.blood = blood;
        this.data = data;
        this.weight = weight;
        this.height = height;
        this.allergy = allergy;
        this.drug = med;
        this.zabo = zabo;
    }

    @Override
    public void onSaveMedInfo() {
        healthCardPresenter.saveHealthCardData(blood, data, weight, height, allergy, drug, zabo);
    }

    @Override
    public void onNotSaved() {
        main.setEnabled(false);
        bottom.setEnabled(false);
        med.setEnabled(false);
        prof.setEnabled(false);
    }

    @Override
    public void onSaved() {
        main.setEnabled(true);
        bottom.setEnabled(true);
        med.setEnabled(true);
        prof.setEnabled(true);
    }

    @Override
    public void onUpdateInfoCorrect(String token, String username, String email, String name, String surname, String iin) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.iyn = iin;
    }

    @Override
    public void onUpdate() {
        presenter.updateInfo(token, username, email, name, surname, iyn);
        if (ProfileActivity.savedBitmap != null) {
            imageView.setImageBitmap(ProfileActivity.savedBitmap);
        }
        ProfileActivity.savedBitmap = null;
    }

    @Override
    public void onStopService() {
        stopService(new Intent(this, TrackingService.class));
    }

    @Override
    public void onCameraRequest(CircleImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
