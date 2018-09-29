package kz.topsecurity.top_signal.presenter.loginPresenter;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;

public interface LoginPresenter extends BasePresenter {
    void login(String rawphone , String phone , String password, String imei);
}
