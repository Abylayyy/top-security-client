package kz.topsecurity.client.presenter.registerPresenter;

public interface SignUpPresenter {
    void register(String phone, String password);
    void detach();
}
