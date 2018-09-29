package kz.topsecurity.top_signal.presenter.restorePassPresenter;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;

public interface RestorePasswordPresenter extends BasePresenter {
    void sendPhoneNumber(String raw_phone , String phone);
    void sendCode(String code);

}
