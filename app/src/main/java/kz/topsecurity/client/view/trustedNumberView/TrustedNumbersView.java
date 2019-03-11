package kz.topsecurity.client.view.trustedNumberView;

import java.util.List;

import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.view.base.BaseView;

public interface TrustedNumbersView extends BaseView {
    void onListLoaded(List<Contact> contactList);
    void onListLoadError();
    void hideLoadingDialog() ;
    void showLoadingDialog();
    void onContactLoadError(int error_message);
    void onContactLoaded(Contact newContact);
    void onDeleteSuccess(int id);
}
