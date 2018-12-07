package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.registerView.fragmentsView.RegisterSignFieldsView;

public class RegisterSignFieldsPresenterImpl implements RegisterSignFieldsPresenter {

    RegisterSignFieldsView view;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RegisterSignFieldsPresenterImpl(RegisterSignFieldsView view){
        this.view = view;
    }

    @Override
    public void checkFields(String rawphone,String phone, String password, String password_confirm) {
        boolean is_contain_error= false;
        if(phone.length()<16 || rawphone.length()<10)
        {
            view.onPhoneFieldError(R.string.phone_length_error);
            is_contain_error = true;
        }

        if(password.length()<6){
            view.onPasswordError(R.string.password_length_error);
            is_contain_error = true;
        }
        if(!password_confirm.equals(password)){
            view.onConfirmPasswordError(R.string.confirm_password_not_match);
            is_contain_error = true;
        }
        if(!is_contain_error){
            checkPhone(phone,password);
        }
        else{
            view.onLockRegisterButton();
            view.onFieldsWrong(R.string.field_error);
        }
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }

    private void checkPhone(String phone, String password) {
        Disposable disposable = new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                view.onFieldsCorrect(phone,password);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                view.onPhoneFieldError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.onPhoneFieldError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().checkPhone(phone));
        compositeDisposable.add(disposable);
    }

}
