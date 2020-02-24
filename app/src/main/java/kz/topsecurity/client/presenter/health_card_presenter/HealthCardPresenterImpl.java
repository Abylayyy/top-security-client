package kz.topsecurity.client.presenter.health_card_presenter;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.other.HealthCardPostResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;

public class HealthCardPresenterImpl implements HealthCardPresenter{

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Context context;
    DataBaseManager manager;

    public HealthCardPresenterImpl(Context context) {
        this.context = context;
        manager = new DataBaseManagerImpl(context);
    }

    @Override
    public void saveHealthCardData(String blood, String data, String weight, String height, String allergy, String med, String zabo) {

        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<HealthCardPostResponse>() {
            @Override
            public void onSuccess(HealthCardPostResponse data) {
                if (data.getHealthcard() != null) {
                    manager.updateHealthCard(data.getHealthcard(), context);
                }
            }
            @Override
            public void onFailed(HealthCardPostResponse data, int error_message) {
            }

            @Override
            public void onError(Throwable e, int error_message) {
            }
        }).makeRequest(RetrofitClient.getClientApi().addUserHealthCard(RetrofitClient.getRequestToken(), blood, data, weight, height, allergy, med, zabo));
        compositeDisposable.add(disposable);
    }

    private void updateDatabase() {
        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse data) {
                manager.saveClientData(data.getClient());
            }
            @Override
            public void onFailed(GetClientResponse data, int error_message) {}
            @Override
            public void onError(Throwable e, int error_message) {}

        }).makeRequest(RetrofitClient.getClientApi().getClientData(RetrofitClient.getRequestToken()));
        compositeDisposable.add(disposable);
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("ОК", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
