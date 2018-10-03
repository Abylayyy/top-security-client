package kz.topsecurity.client.presenter.registerPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.model.register.RegisterResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.registerView.RegisterView;

public class RegisterPresenterImpl extends BasePresenterImpl<RegisterView> implements RegisterPresenter {

    public RegisterPresenterImpl(RegisterView view) {
        super(view);
    }

    @Override
    public void register(String rawphone, String phone, String email, String userName, String password) {
        boolean is_contain_error= false;
        if(phone.length()<16 || rawphone.length()<10)
        {
            view.onPhoneFieldError(R.string.phone_length_error);
            is_contain_error = true;
        }
        if(!email.contains("@")||!email.contains(".") )
        {
            view.onEmailError(R.string.email_format_error);
            is_contain_error = true;
        }
        if(userName.isEmpty())
        {
            view.onUserNameError(R.string.field_cant_be_empty);
            is_contain_error = true;
        }
        if(password.length()<6){
            view.onPasswordError(R.string.password_length_error);
            is_contain_error = true;
        }
        if(!is_contain_error){
            makeRequest(phone,email,password,userName);
        }
        else{
            view.onLockRegisterButton();
            view.onRegisterFailed(R.string.field_error);
        }
    }

    private void makeRequest(String phone , String email , String password , String name ){
        view.onShowLoading();
        Disposable success = new RequestService<>(new RequestService.RequestResponse<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse r) {
                view.onRegisterSuccess(r.getClient());
                view.onHideLoading();
            }

            @Override
            public void onFailed(RegisterResponse data, int error_message) {
                setError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .register(phone, password , email, name));

        compositeDisposable.add(success);
    }

    private void setError(int error_msg) {
        view.onHideLoading();
        view.onRegisterFailed(error_msg);
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }
}
