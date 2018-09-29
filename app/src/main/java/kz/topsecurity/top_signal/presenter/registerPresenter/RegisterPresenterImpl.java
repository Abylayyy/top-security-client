package kz.topsecurity.top_signal.presenter.registerPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.model.auth.AuthResponse;
import kz.topsecurity.top_signal.model.register.RegisterResponse;
import kz.topsecurity.top_signal.presenter.base.BasePresenterImpl;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.view.registerView.RegisterView;

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
