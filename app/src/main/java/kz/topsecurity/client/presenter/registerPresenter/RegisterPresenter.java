package kz.topsecurity.client.presenter.registerPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface RegisterPresenter extends BasePresenter {
    void register(String rawphone, String phone, String email, String userName, String password );
}
