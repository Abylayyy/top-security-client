package kz.topsecurity.client.presenter.registerPresenter;

public interface SignUpPresenter {
    void register( String phone, String email, String IIN, String password , String userPhoto, String firstname, String lastname, String patronymic);
    void detach();
}
