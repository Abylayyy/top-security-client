package kz.topsecurity.client.view.registerView;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.view.base.BaseView;

public interface RegisterView extends BaseView {
    void onRegisterSuccess(Client client);
    void onRegisterFailed(int error_msg);
    void onPhoneFieldError(int textMsgResId);
    void onEmailError(int textMsgResId);
    void onUserNameError(int textMsgResId);
    void onPasswordError(int textMsgResId);
    void onLockRegisterButton();
    void onShowLoading();
    void onHideLoading();
}
