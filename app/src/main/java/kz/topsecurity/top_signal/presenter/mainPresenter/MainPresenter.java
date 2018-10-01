package kz.topsecurity.top_signal.presenter.mainPresenter;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;

public interface MainPresenter extends BasePresenter {
    void actionWithCheck();
    void callAlert();
    void cancelAlert();
    void setAlertActive(boolean status);
    void checkStatus();

    void actionAlert();

    void actionCancel();
}
