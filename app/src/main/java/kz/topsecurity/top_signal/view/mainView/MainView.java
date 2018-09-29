package kz.topsecurity.top_signal.view.mainView;

import kz.topsecurity.top_signal.view.base.BaseView;

public interface MainView extends BaseView {
    void onAlert();
    void onCancelAlert();
    void onStopAlert();
    void onAlertIsActive();
    void onAlertNotActive();
    void onStoppingAlert();
    void onCallingAlert();
}
