package kz.topsecurity.client.presenter.mainPresenter;

import android.content.Context;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface MainPresenter extends BasePresenter {
    void actionWithCheck();
    void callAlert();
    void cancelAlert();
    void setAlertActive(boolean status);
    void checkStatus();
    void removeHandlerCallbacks();
    void actionAlert();

    void actionCancel();
    void logToken();

    void  updateDrawerData(Context context);

    void exitFromApplication(Context context);

    void checkPlan();

    void callAmbulance();

    void callMeBack();
}
