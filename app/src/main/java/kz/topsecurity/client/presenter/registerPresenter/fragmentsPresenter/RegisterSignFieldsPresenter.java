package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

public interface RegisterSignFieldsPresenter {
    void checkFields(String rawphone,String phone , String password, String password_confirm);
    void detach();
}
