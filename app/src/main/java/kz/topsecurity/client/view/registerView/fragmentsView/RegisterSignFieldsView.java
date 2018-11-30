package kz.topsecurity.client.view.registerView.fragmentsView;

public interface RegisterSignFieldsView {
    void onPhoneFieldError(int error);
    void onPasswordError(int error);
    void onConfirmPasswordError(int error);
    void onLockRegisterButton();
    void onFieldsWrong(int error);
    void onFieldsCorrect(String phone, String password);
}
