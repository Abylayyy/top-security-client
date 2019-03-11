package kz.topsecurity.client.presenter.restorePassPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface RestorePasswordPresenter extends BasePresenter {
    void sendPhoneNumber(String raw_phone , String phone);
    void sendCode(String code);

}
