package kz.topsecurity.client.domain.ProfileScreen.EditEmailScreen;

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
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;

public class EditEmailActivity extends BaseActivity {

    @BindView(R.id.ed_email) RoundCorneredEditText ed_email;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.tv_email_error) TextView tv_email_error;

    RoundCorneredEditTextHelper emailHelper;
    boolean isError = false;

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.title_activity_edit_email);
        emailHelper = new RoundCorneredEditTextHelper(new StatusListener() {
            @Override
            public void onErrorDeactivated() {
                isError = false;
            }
        },ed_email,tv_email,tv_email_error);
        emailHelper.setMandatory();
        emailHelper.init(this);
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
                changeEmail();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeEmail() {
        if(isError){
            showToast(R.string.correct_errors);
            return;
        }
        if(!isNetworkOnline())
        {
            showToast(R.string.msg_alert_no_internet);
            return;
        }

        String email = ed_email.getText().toString();

        if(email.isEmpty()){
            setEmailError(R.string.field_cant_be_empty);
            return;
        }
        if(!email.contains("@")||!email.contains(".") )
        {
            setEmailError(R.string.email_format_error);
            return;
        }

        makeChangeEmailRequest(email);
    }

    private void makeChangeEmailRequest(String email) {
        showLoadingDialog();
        Client clientData = dataBaseManager.getClientData();
        Disposable success = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse r) {
                if( r.getClient() != null)
                    onEditEmailSuccess(r.getClient());

                else
                    setEmailEditRequestError();
            }

            @Override
            public void onFailed(GetClientResponse data, int error_message) {
                setEmailEditRequestError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setEmailEditRequestError();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .editUserData(RetrofitClient.getRequestToken(), clientData.getUsername(), email));

        compositeDisposable.add(success);
    }

    private void onEditEmailSuccess(Client client) {
        hideProgressDialog();
        showToast(R.string.email_changed_successfully);
        dataBaseManager.saveClientData(client);
        setResult(RESULT_OK);
        finish();
    }

    void setEmailEditRequestError(){
        hideProgressDialog();
        showToast(R.string.error_from_server);
    }

    void setEmailError(int string_msg){
        emailHelper.setError(getString(string_msg));
        isError = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_email_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
