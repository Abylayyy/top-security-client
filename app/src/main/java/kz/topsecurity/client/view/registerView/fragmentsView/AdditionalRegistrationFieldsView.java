package kz.topsecurity.client.view.registerView.fragmentsView;

public interface AdditionalRegistrationFieldsView {
    void onUserNameError(int error);
    void onEmailError(int error);
    void onLockRegisterButton();
    void onFieldsCorrect(String userEmail, String userName);
    void onFieldsWrong(int error);
}
