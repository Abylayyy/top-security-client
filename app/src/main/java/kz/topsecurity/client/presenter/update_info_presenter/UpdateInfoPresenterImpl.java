package kz.topsecurity.client.presenter.update_info_presenter;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;

public class UpdateInfoPresenterImpl implements UpdatePresenter {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Context context;
    DataBaseManager manager;


    public UpdateInfoPresenterImpl(Context context){
        this.context = context;
        manager = new DataBaseManagerImpl(context);
    }

    @Override
    public void updateInfo(String token, String username, String email, String firstname, String lastname, String iin) {
        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {

            @Override
            public void onSuccess(GetClientResponse data) {
                manager.saveClientData(data.getClient());
            }

            @Override
            public void onFailed(GetClientResponse data, int error_message) {
                showDialog("Не удалось обновить данные");
            }

            @Override
            public void onError(Throwable e, int error_message) {
                showDialog("Ошибка при обновлении данных");
            }
        }).makeRequest(RetrofitClient.getClientApi().editUserData(token, username, email, firstname, lastname, iin));
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
