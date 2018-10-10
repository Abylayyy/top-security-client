package kz.topsecurity.client.view.alertHistoryView;

import java.util.List;

import kz.topsecurity.client.model.alertList.Alert;
import kz.topsecurity.client.view.base.BaseView;

public interface AlertHistoryView extends BaseView {
    void onHistoryLoaded(List<Alert> history );
    void onHistoryEmpty();
    void onHistoryLoadFailed(int error_msg);
    void hideLoadingDialog() ;
    void showLoadingDialog();

}
