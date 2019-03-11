package kz.topsecurity.client.domain.SetSecretCancelCodeScreen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;

public class SetSecretCancelCodeActivity extends BaseActivity implements StatusListener {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.tv_description) TextView tv_description;
    @BindView(R.id.tv_password_label) TextView tv_password_label;
    @BindView(R.id.ed_password) RoundCorneredEditText ed_password;
    @BindView(R.id.tv_password_error) TextView tv_password_error;
    @BindView(R.id.tv_fake_code_label) TextView tv_fake_code_label;
    @BindView(R.id.ed_fake_code) RoundCorneredEditText ed_fake_code;
    @BindView(R.id.tv_fake_code_error) TextView tv_fake_code_error;

    RoundCorneredEditTextHelper real_code_helper;
    RoundCorneredEditTextHelper fake_code_helper;
    private boolean enable_button = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_secret_cancel_code);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.title_set_secret_code);
        real_code_helper = new RoundCorneredEditTextHelper(this, ed_password,tv_password_label,tv_password_error);
        fake_code_helper = new RoundCorneredEditTextHelper(this, ed_fake_code , tv_fake_code_label , tv_fake_code_error);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:{
                onBackPressed();
                break;
            }
            case R.id.done_action:{
                if(enable_button)
                    setSecretCodes();
                else
                    showToast(R.string.correct_errors);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_email_menu, menu);
        return true;
    }

    private void setSecretCodes() {
        String realCode = ed_password.getText().toString();
        String fakeCode = ed_fake_code.getText().toString();
        if(checkCodes(realCode, fakeCode)){

        }
        else{
            enable_button = false;
            showToast(R.string.correct_errors);
        }
    }

    private boolean checkCodes(String realCode, String fakeCode) {
        boolean isError = false;
        if(realCode==null || realCode.isEmpty()){
            isError = true;
            real_code_helper.setError(getString(R.string.field_cant_be_empty));
        }
        if (fakeCode==null || fakeCode.isEmpty()){
            isError = true;
            fake_code_helper.setError(getString(R.string.field_cant_be_empty));
        }
        if(!isError && fakeCode.equals(realCode)){
            isError = true;
            fake_code_helper.setError(getString(R.string.codes_are_same));
        }

        return isError;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onErrorDeactivated() {
        enable_button = true;
    }
}
