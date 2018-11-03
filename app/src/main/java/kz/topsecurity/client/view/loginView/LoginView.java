package kz.topsecurity.client.view.loginView;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.view.base.BaseView;

public interface LoginView extends BaseView {
    void onLoginError(int msg);
    void onLoginSuccess(Client client, String token);
    void onPhoneFieldError(int msg);
    void onPasswordFieldError(int msg);
    void onLockLoginButton();
    void onShowLoading();
    void onHideLoading();

    void onUserNotVerificatedPhone(String phone, int error_message);
}
