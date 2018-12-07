package kz.topsecurity.client.view.registerView.fragmentsView;

public interface UserNameFieldsView {
    void onFirstNameFieldError(int error);
    void onLastNameFieldError(int error);
    void onPatronymicFieldError(int error);
    void onLockRegisterButton();
    void onFieldsWrong(int error);
    void onFieldsCorrect(String firstname, String lastname, String patronymic);
}
