package kz.topsecurity.top_signal.domain.TrustedNumbersScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.domain.TrustedNumbersScreen.adapter.ItemDecoratorWithDivider;
import kz.topsecurity.top_signal.domain.TrustedNumbersScreen.adapter.TrustedContactsAdapter;
import kz.topsecurity.top_signal.domain.TrustedNumbersScreen.additionalScreen.CreateTrustedNumberActivity;
import kz.topsecurity.top_signal.domain.base.BaseActivity;
import kz.topsecurity.top_signal.model.contact.Contact;
import kz.topsecurity.top_signal.presenter.trustedNumberPresenter.TrustedNumberPresenter;
import kz.topsecurity.top_signal.presenter.trustedNumberPresenter.TrustedNumberPresenterImpl;
import kz.topsecurity.top_signal.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.top_signal.view.trustedNumberView.TrustedNumbersView;

public class TrustedNumbersActivity extends BaseActivity<TrustedNumbersView,TrustedNumberPresenter,TrustedNumberPresenter>
        implements TrustedContactsAdapter.ContactsListAdapter, TrustedNumbersView {

    private static final int CREATE_CONTACT = 818;
    private static final int EDIT_CONTACT = 432;
    public static final String CREATED_CONTACT_ID = "created_contact_id";
    public static final String EDITED_CONTACT_ID = "edited_contact_id";
    @BindView(R.id.rv_trusted_numbers) RecyclerView rv_trusted_numbers;
    @BindView(R.id.tv_empty_list) TextView tv_empty_list;

    LinearLayoutManager mLayoutManager;
    TrustedContactsAdapter mAdapter = new TrustedContactsAdapter(new ArrayList<>(),this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CREATE_CONTACT:{
                    int createdContactID= data.getIntExtra(CREATED_CONTACT_ID, -1);
                    if(createdContactID!=-1){
                        if (presenter!=null){
                            presenter.getTrustedContacts();
                        }
                    }
                    break;
                }
                case EDIT_CONTACT:{
                    int createdContactID= data.getIntExtra(EDITED_CONTACT_ID, -1);
                    if(createdContactID!=-1){
                        if (presenter!=null){
                            presenter.getTrustedContacts();
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_numbers);
        ButterKnife.bind(this);
        initPresenter(new TrustedNumberPresenterImpl(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Доверенный номер");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(TrustedNumbersActivity.this, CreateTrustedNumberActivity.class),CREATE_CONTACT);
             }
        });
        setupRV();
        presenter.getTrustedContacts();
    }

    private void setupRV() {
        rv_trusted_numbers.setHasFixedSize(true);
        rv_trusted_numbers.addItemDecoration(
                new ItemDecoratorWithDivider(this, R.drawable.item_divider));
        mLayoutManager = new LinearLayoutManager(this);
        rv_trusted_numbers.setLayoutManager(mLayoutManager);
        rv_trusted_numbers.setAdapter(mAdapter);
    }

    @Override
    public void onItemSelected(Contact data) {

    }

    @Override
    public void onDeleteItem(Contact contact) {
        showAreYouSureDialog(getString(R.string.are_you_sure_delete_contact), new CustomSimpleDialog.Callback() {
            @Override
            public void onCancelBtnClicked() {
                dissmissAreYouSureDialog();
            }

            @Override
            public void onPositiveBtnClicked() {
                dissmissAreYouSureDialog();
                presenter.deleteContact(contact.getId());
            }
        });
    }

    @Override
    public void onEditItem(Contact contact) {
        Intent intent = new Intent(TrustedNumbersActivity.this, CreateTrustedNumberActivity.class);
        intent.putExtra(CreateTrustedNumberActivity.CONTACT_ID_TO_EDIT,contact.getId());
        intent.putExtra(CreateTrustedNumberActivity.CONTACT_NAME_TO_EDIT,contact.getName());
        intent.putExtra(CreateTrustedNumberActivity.CONTACT_PHONE_TO_EDIT,contact.getPhone());
        startActivityForResult(intent,EDIT_CONTACT);
    }

    @Override
    public void onListLoaded(List<Contact> contactList) {
        if(contactList.isEmpty()){
            tv_empty_list.setVisibility(View.VISIBLE);
            rv_trusted_numbers.setVisibility(View.GONE);
        }
        else{
            tv_empty_list.setVisibility(View.GONE);
            rv_trusted_numbers.setVisibility(View.VISIBLE);
            if(mAdapter!=null)
                mAdapter.updateData(contactList);
        }
    }

    @Override
    public void onListLoadError() {
        showToast(R.string.contacts_load_error);
    }

    @Override
    public void hideLoadingDialog() {
        super.hideProgressDialog();
    }

    @Override
    public void showLoadingDialog() {
        super.showLoadingDialog();
    }

    @Override
    public void onContactLoadError(int error) {
        showToast(error);

    }

    @Override
    public void onContactLoaded(Contact newContact) {
        if(mAdapter!=null){
            tv_empty_list.setVisibility(View.GONE );
            rv_trusted_numbers.setVisibility(View.VISIBLE);
            mAdapter.add(newContact);
        }
    }

    @Override
    public void onDeleteSuccess(int id) {
        mAdapter.removeDataById(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
