package kz.topsecurity.client.presenter.loginPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface LoginPresenter extends BasePresenter {
    void login(String rawphone , String phone , String password, String imei);
}
