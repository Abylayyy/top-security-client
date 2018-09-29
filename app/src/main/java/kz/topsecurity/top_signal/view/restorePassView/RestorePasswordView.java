package kz.topsecurity.top_signal.view.restorePassView;

import kz.topsecurity.top_signal.view.base.BaseView;

public interface RestorePasswordView extends BaseView {
    void onTelephoneNumberSendSuccessfully();
    void onTelephoneNumberSendFailed(int error_msg);
    void onRestorePasswordSuccess(String successful_sms_code);
    void onRestorePasswordFailed(int error_msg);
    void onShowLoadingView();
    void onHideLoadingView();
    void onShowRestorePassView();
    void onShowSMSCodeView();
    void onPhoneNumberError(int error_msg);
    void onSmsCodeError(int error_msg);
}
