package kz.topsecurity.client.presenter.mainPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface MainPresenter extends BasePresenter {
    void actionWithCheck();
    void callAlert();
    void cancelAlert();
    void setAlertActive(boolean status);
    void checkStatus();

    void actionAlert();

    void actionCancel();
}
