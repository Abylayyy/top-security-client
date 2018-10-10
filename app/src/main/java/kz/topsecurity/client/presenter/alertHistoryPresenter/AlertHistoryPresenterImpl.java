package kz.topsecurity.client.presenter.alertHistoryPresenter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.model.alertList.AlertsListResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.alertHistoryView.AlertHistoryView;
import kz.topsecurity.client.view.base.BaseView;

public class AlertHistoryPresenterImpl extends BasePresenterImpl<AlertHistoryView> implements AlertHistoryPresenter {

    public AlertHistoryPresenterImpl(AlertHistoryView view) {
        super(view);
    }

    @Override
    public void getAlertHistory(int offset) {
        view.showLoadingDialog();
        Disposable disposable = new RequestService<AlertsListResponse>(new RequestService.RequestResponse<AlertsListResponse>() {
            @Override
            public void onSuccess(AlertsListResponse data) {
                view.hideLoadingDialog();
                if(data!=null && data.getAlerts()!=null && !data.getAlerts().isEmpty())
                    view.onHistoryLoaded(data.getAlerts());
                else
                    view.onHistoryEmpty();
            }

            @Override
            public void onFailed(AlertsListResponse data, int error_message) {
                view.hideLoadingDialog();
                view.onHistoryLoadFailed(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.hideLoadingDialog();
                view.onHistoryLoadFailed(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().getAlertList(RetrofitClient.getRequestToken(), 10, offset));
        compositeDisposable.add(disposable);
    }
}
