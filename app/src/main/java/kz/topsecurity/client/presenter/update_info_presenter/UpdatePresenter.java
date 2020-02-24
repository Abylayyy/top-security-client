package kz.topsecurity.client.presenter.update_info_presenter;

public interface UpdatePresenter {
    void updateInfo(String token, String username, String email, String firstname, String lastname, String iin);
    void detach();
}
