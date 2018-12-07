package kz.topsecurity.client.view.registerView.fragmentsView;

public interface AdditionalRegistrationFieldsView {
    void onUser_IIN_Error(int error);
    void onEmailError(int error);
    void onLockRegisterButton();
    void onFieldsCorrect(String userEmail, String userIIN);
    void onFieldsWrong(int error);
}
