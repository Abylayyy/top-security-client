package kz.topsecurity.top_signal.domain.ProfileScreen.EditPasswordScreen;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.domain.base.BaseActivity;
import kz.topsecurity.top_signal.helper.dataBase.DataBaseManager;
import kz.topsecurity.top_signal.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.top_signal.model.auth.GetClientResponse;
import kz.topsecurity.top_signal.model.other.Client;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText.StatusListener;

public class EditPasswordActivity extends BaseActivity implements StatusListener {

    @BindView(R.id.tv_current_password) TextView tv_current_password;
    @BindView(R.id.ed_current_password) RoundCorneredEditText ed_current_password;
    @BindView(R.id.tv_current_password_error) TextView tv_current_password_error;

    @BindView(R.id.tv_new_password) TextView tv_new_password;
    @BindView(R.id.ed_new_password) RoundCorneredEditText ed_new_password;
    @BindView(R.id.tv_new_password_error) TextView tv_new_password_error;

    @BindView(R.id.tv_repeat_new_password) TextView tv_repeat_new_password;
    @BindView(R.id.ed_repeat_new_password) RoundCorneredEditText ed_repeat_new_password;
    @BindView(R.id.tv_repeat_new_password_error) TextView tv_repeat_new_password_error;

    RoundCorneredEditTextHelper userCurrentPassword_helper;
    RoundCorneredEditTextHelper userNewPassword_helper;
    RoundCorneredEditTextHelper userPasswordConfirmation_helper;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_edit_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userCurrentPassword_helper = new RoundCorneredEditTextHelper(this , ed_current_password , tv_current_password , tv_current_password_error);
        userCurrentPassword_helper.setMandatory();
        userCurrentPassword_helper.init(this);

        userNewPassword_helper = new RoundCorneredEditTextHelper(this , ed_new_password , tv_new_password , tv_new_password_error);
        userNewPassword_helper.setMandatory();
        userNewPassword_helper.init(this);

        userPasswordConfirmation_helper = new RoundCorneredEditTextHelper(this , ed_repeat_new_password , tv_repeat_new_password , tv_repeat_new_password_error);
        userPasswordConfirmation_helper.setMandatory();
        userPasswordConfirmation_helper.init(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
           onBackPressed(); // close this activity and return to preview activity (if there is any)
        }
        else if(itemId == R.id.done_action){
            changePassword();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        if(isError){
            showToast(R.string.correct_errors);
            return;
        }
        if(!isNetworkOnline())
        {
            showToast(R.string.msg_alert_no_internet);
            return;
        }

        String current_password = ed_current_password.getText().toString();
        String new_password = ed_new_password.getText().toString();
        String password_confirmation = ed_repeat_new_password.getText().toString();

        boolean contains_error = false;

        if(current_password.isEmpty() || current_password.length()<6){
            contains_error = true;
            userCurrentPassword_helper.setError(getString(R.string.password_length_error));
        }
        if(new_password.isEmpty() || new_password.length()<6){
            contains_error = true;
            userNewPassword_helper.setError(getString(R.string.password_length_error));
        }
        if(password_confirmation.isEmpty() || password_confirmation.length()<6){
            contains_error = true;
            userPasswordConfirmation_helper.setError(getString(R.string.password_length_error));
        }
        else if(!contains_error && !new_password.equals(password_confirmation)){
            contains_error = true;
            userPasswordConfirmation_helper.setError(getString(R.string.password_do_not_match));
        }
        if(contains_error)
            isError = true;
        else {
            isError = false;
            makeChangePassword(current_password, new_password, password_confirmation);
        }
    }

    private void makeChangePassword(String current_password, String new_password, String password_confirmation) {
        showLoadingDialog();
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse r) {
                if( r.getClient() != null)
                    onChangePasswordSuccess(r.getClient());

                else
                    onChangePasswordFailed();
            }

            @Override
            public void onFailed(GetClientResponse data, int error_message) {
                onChangePasswordFailed();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                onChangePasswordFailed();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .changeUserPassword(RetrofitClient.getRequestToken(),
                        current_password,
                        new_password,
                        password_confirmation));
        compositeDisposable.add(subscribe);
    }

    private void onChangePasswordSuccess(Client data) {
        hideProgressDialog();
        showToast(R.string.password_changed_successfully);
        dataBaseManager.saveClientData(data);
        finish();
    }

    private void onChangePasswordFailed() {
        hideProgressDialog();
        showToast(R.string.error_from_server);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_email_menu, menu);
        return true;
    }

    @Override
    public void onErrorDeactivated() {
        isError = false;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
