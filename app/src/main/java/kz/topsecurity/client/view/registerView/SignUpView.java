package kz.topsecurity.client.view.registerView;

import kz.topsecurity.client.model.other.Client;

public interface SignUpView {
    void onRegisterError(int error);
    void onRegisterSuccess(Client client);
    void showLoading();
    void hideLoading();
}
