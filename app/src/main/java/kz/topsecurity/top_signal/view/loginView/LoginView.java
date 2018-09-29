package kz.topsecurity.top_signal.view.loginView;

import kz.topsecurity.top_signal.model.other.Client;
import kz.topsecurity.top_signal.view.base.BaseView;

public interface LoginView extends BaseView {
    void onLoginError(int msg);
    void onLoginSuccess(Client client, String token);
    void onPhoneFieldError(int msg);
    void onPasswordFieldError(int msg);
    void onLockLoginButton();
    void onShowLoading();
    void onHideLoading();
}
