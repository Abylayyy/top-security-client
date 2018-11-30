package kz.topsecurity.client.domain.RegisterScreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.base.BaseFragmentActivity;
import kz.topsecurity.client.fragments.register.additionalFields.AdditionalRegistrationFieldsFragment;
import kz.topsecurity.client.fragments.register.signUpFields.RegisterSignFieldsFragment;

public class SignUpActivity extends BaseFragmentActivity implements RegisterSignFieldsFragment.RegisterSignFieldsFragmentCallback {

    @BindView(R.id.iv_back)
    ImageView iv_back;

    String currentFragment;

    String userPhone;
    String userPassword;
    String userEmail;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setupToolbar();
        setSignFieldsFragment();
    }

    private void setupToolbar() {
        iv_back.setOnClickListener(v->{
            if(currentFragment.equals(RegisterSignFieldsFragment.class.getSimpleName())){
                goBack();
            }
        });
    }

    private void goBack() {
        Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
        intent.putExtra(StartActivity.SKIP_LOADING_KEY,true);
        startActivity(intent);
        System.gc();
        finish();
    }

    @Override
    public void onFieldsCorrect(String phone, String password) {
        this.userPhone = phone;
        this.userPassword = password;
        setNextFragment();
    }

    private void setNextFragment() {
        if(currentFragment.equals(RegisterSignFieldsFragment.class.getSimpleName())){
            setAdditionalFieldsFragment();
        }
    }

    private void setSignFieldsFragment() {
        RegisterSignFieldsFragment registerSignFieldsFragment = RegisterSignFieldsFragment.newInstance();
        currentFragment = addFragment(registerSignFieldsFragment, RegisterSignFieldsFragment.class.getSimpleName());
    }

    private void setAdditionalFieldsFragment() {
        AdditionalRegistrationFieldsFragment additionalRegistrationFieldsFragment = AdditionalRegistrationFieldsFragment.newInstance();
        currentFragment = addFragment(additionalRegistrationFieldsFragment, AdditionalRegistrationFieldsFragment.class.getSimpleName());
    }

    @Override
    public void showToast(int msg) {
        showToast(getString(msg));
    }
}
