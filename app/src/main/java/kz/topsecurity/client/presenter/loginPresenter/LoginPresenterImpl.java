package kz.topsecurity.client.presenter.loginPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.TextHelper;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.loginView.LoginView;

public class LoginPresenterImpl extends BasePresenterImpl<LoginView> implements LoginPresenter {

    public LoginPresenterImpl(LoginView view) {
        super(view);
    }

    @Override
    public void login(String rawphone, String phone, String password , String imei) {

        boolean is_contain_error= false;
        if(phone.length()<16 || rawphone.length()<10)
        {
            view.onPhoneFieldError(R.string.phone_length_error);
            is_contain_error = true;
        }
        if(password.length()<6){
            view.onPasswordFieldError(R.string.password_length_error);
            is_contain_error = true;
        }

        if(!is_contain_error){
            makeRequest(phone,password,imei);
        }
        else{
            setError(R.string.field_error);
            view.onLockLoginButton();
        }
    }

    private void makeRequest(String phone, String password, String imei){
        view.onShowLoading();

        Disposable success = new RequestService<>(new RequestService.RequestResponse<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse r) {
                view.onLoginSuccess(r.getClient() , r.getToken().getAccessToken());
                view.onHideLoading();
            }

            @Override
            public void onFailed(AuthResponse data, int error_message) {
                setError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setError(error_message);
            }
            }).makeRequest(RetrofitClient.getClientApi()
                .authorize(phone, password, Constants.CLIENT_DEVICE_TYPE,Constants.CLIENT_DEVICE_PLATFORM_TYPE, imei));

        compositeDisposable.add(success);
    }

    private void setError( int error_msg){
        view.onHideLoading();
        view.onLoginError(error_msg);
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }
}
