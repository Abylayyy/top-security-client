package kz.topsecurity.client.domain.TrustedNumbersScreen.additionalScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.TrustedNumbersScreen.TrustedNumbersActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.model.contact.GetContactsResponse;
import kz.topsecurity.client.model.contact.SaveContactsResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;

public class CreateTrustedNumberActivity extends BaseActivity implements StatusListener {

    public static final String CONTACT_ID_TO_EDIT = "contact_id_to_edit";
    public static final String CONTACT_NAME_TO_EDIT = "contact_name_to_edit";
    public static final String CONTACT_PHONE_TO_EDIT = "contact_phone_to_edit ";
    public static final String CONTACT_DESC_TO_EDIT = "contact_desc_to_edit";

    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.ed_username) RoundCorneredEditText ed_username;
    @BindView(R.id.tv_user_name_error) TextView tv_user_name_error;

    @BindView(R.id.tv_telephone_number_label) TextView tv_telephone_number_label;
    @BindView(R.id.ed_tel_number)
    RoundCorneredEditTextWithPhoneMask ed_tel_number;
    @BindView(R.id.tv_phone_number_error) TextView tv_phone_number_error;

    @BindView(R.id.tv_user_desc) TextView tv_user_desc;
    @BindView(R.id.ed_user_desc) RoundCorneredEditText ed_user_desc;
    @BindView(R.id.tv_user_desc_error) TextView tv_user_desc_error;

    @BindView(R.id.btn_create_contact) Button btn_create_contact;

    RoundCorneredEditTextHelper telephone_helper;
    RoundCorneredEditTextHelper username_helper;
    RoundCorneredEditTextHelper user_desc_helper;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);
    boolean editPhoneNumber = false;
    private int mContactId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trusted_number);
        ButterKnife.bind(this );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupView();
        if(getIntent().getIntExtra(CONTACT_ID_TO_EDIT,-1)!=-1){
            setTitle("Редактировать");
            mContactId = getIntent().getIntExtra(CONTACT_ID_TO_EDIT,-1);
            ed_tel_number.setText(getIntent().getStringExtra(CONTACT_PHONE_TO_EDIT).substring(1));
            ed_username.setText(getIntent().getStringExtra(CONTACT_NAME_TO_EDIT));
            ed_user_desc.setText(getIntent().getStringExtra(CONTACT_DESC_TO_EDIT));
            editPhoneNumber = true;
            btn_create_contact.setText(R.string.save);
        }
        else{
            setTitle("Добавить");
        }
    }

    private void setupView() {
        telephone_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_telephone_number_label,tv_phone_number_error);
        username_helper = new RoundCorneredEditTextHelper(this,ed_username,tv_user_name,tv_user_name_error);
        user_desc_helper = new RoundCorneredEditTextHelper(this,ed_user_desc,tv_user_desc,tv_user_desc_error);
        telephone_helper.init(this);
        username_helper.init(this);
        user_desc_helper.init(this);
        btn_create_contact.setOnClickListener(v->{
            createContact();
        });

    }

    private void createContact(){
        String rawphone = ed_tel_number.getRawText();
        String phone = ed_tel_number.getText().toString();
        String username = ed_username.getText().toString();
        String description = ed_user_desc.getText().toString();

        boolean is_contain_error= false;
        if(phone.length()<16 || rawphone.length()<10)
        {
            telephone_helper.setError(getString(R.string.phone_length_error));
            is_contain_error = true;
            phone = ed_tel_number.getText().toString();
        }
        if(username.isEmpty()){
            username_helper.setError(getString(R.string.field_cant_be_empty));
            is_contain_error = true;
        }
        if(description.isEmpty())
        {
            user_desc_helper.setError(getString(R.string.field_cant_be_empty));
            is_contain_error = true;
        }

        if(!is_contain_error){
            if(!editPhoneNumber)
                createContactRequest(phone,username,description);
            else
                editContactRequest(mContactId, phone,username,description);
        }
        else{
            btn_create_contact.setEnabled(false);
        }
    }

    private void editContactRequest(int id, String phone, String username, String desc) {
        showLoadingDialog();
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<SaveContactsResponse>() {
            @Override
            public void onSuccess(SaveContactsResponse r) {
                hideProgressDialog();
                setEditContactSuccess(r.getContact());
            }

            @Override
            public void onFailed(SaveContactsResponse data, int error_message) {
                setEditContactError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setCreateContactError();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .editContact(RetrofitClient.getRequestToken(),id ,username,phone,desc));

        compositeDisposable.add(subscribe);
    }

    private void setEditContactError() {
        hideProgressDialog();
        showToast(R.string.error_from_server);
    }

    private void setEditContactSuccess(Contact contact) {
        Intent intent = new Intent();
        intent.putExtra(TrustedNumbersActivity.EDITED_CONTACT_ID, contact.getId() );
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void createContactRequest(String phone , String username, String description) {
        showLoadingDialog();
        Disposable success = new RequestService<>(new RequestService.RequestResponse<SaveContactsResponse>() {
            @Override
            public void onSuccess(SaveContactsResponse r) {
                hideProgressDialog();
                setCreateContactSuccess(r.getContact());
            }

            @Override
            public void onFailed(SaveContactsResponse data, int error_message) {
                setCreateContactError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setCreateContactError();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .saveContact(RetrofitClient.getRequestToken(),username,phone,description));


        compositeDisposable.add(success);
    }

    private void setCreateContactError() {
        hideProgressDialog();
        showToast(R.string.error_from_server);
    }

    private void setCreateContactSuccess(Contact contact) {
        Intent intent = new Intent();
        intent.putExtra(TrustedNumbersActivity.CREATED_CONTACT_ID, contact.getId() );
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onErrorDeactivated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_create_contact.setEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
