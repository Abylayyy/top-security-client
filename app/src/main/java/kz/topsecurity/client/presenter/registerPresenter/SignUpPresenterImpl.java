package kz.topsecurity.client.presenter.registerPresenter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.model.register.RegisterResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.registerView.SignUpView;

public class SignUpPresenterImpl implements SignUpPresenter {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SignUpView view;

    public SignUpPresenterImpl(SignUpView view){
        this.view = view;
    }

    @Override
    public void register(String phone, String password) {
        view.showLoading();
        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<RegisterResponse>() {

            @Override
            public void onSuccess(RegisterResponse data) {
                view.onRegisterSuccess(data.getClient());
                view.hideLoading();
            }

            @Override
            public void onFailed(RegisterResponse data, int error_message) {
                view.onRegisterError(error_message);
                view.hideLoading();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.onRegisterError(error_message);
                view.hideLoading();
            }
        }).makeRequest(RetrofitClient.getClientApi().register(phone, password));
        compositeDisposable.add(disposable);
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }


}
