package kz.topsecurity.client.view.mainView;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.view.base.BaseView;

public interface MainView extends BaseView {
    void onAlert(int type);
    void onCancelAlert();
    void onStopAlert();
    void onAlertIsActive();
    void onAlertNotActive();
    void onStoppingAlert();
    void onCallingAlert();
    void setAnimationStatus(int status);
    void setDrawerData(Client clientData);
    void exitFromMainScreen();

    void userPlanChanged(boolean isActive);
}
