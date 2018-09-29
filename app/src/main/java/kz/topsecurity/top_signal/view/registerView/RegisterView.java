package kz.topsecurity.top_signal.view.registerView;

import kz.topsecurity.top_signal.model.other.Client;
import kz.topsecurity.top_signal.view.base.BaseView;

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
