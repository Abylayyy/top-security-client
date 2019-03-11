package kz.topsecurity.client.presenter.alertHistoryPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface AlertHistoryPresenter extends BasePresenter {
    void getAlertHistory(int offset);
}
