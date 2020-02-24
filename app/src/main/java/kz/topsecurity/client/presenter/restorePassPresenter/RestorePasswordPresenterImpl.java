package kz.topsecurity.client.presenter.restorePassPresenter;

import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.restorePassView.RestorePasswordView;

public class RestorePasswordPresenterImpl extends BasePresenterImpl<RestorePasswordView> implements RestorePasswordPresenter {

    public RestorePasswordPresenterImpl(RestorePasswordView view) {
        super(view);
    }
    private static String sendedPhone;

    @Override
    public void sendPhoneNumber(String raw_phone , String phone) {
        boolean is_contain_error = false;
        if(phone.length()<16 || raw_phone.length()<10)
        {
            view.onPhoneNumberError(R.string.phone_length_error);
            is_contain_error = true;
        }
        if (!is_contain_error)
            requestRestoreCode(phone);
    }

    private void requestRestoreCode(String phone) {
        view.onShowLoadingView();
        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                sendedPhone = phone;
                view.onHideLoadingView();
                view.onTelephoneNumberSendSuccessfully();
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                view.onHideLoadingView();
                view.onTelephoneNumberSendFailed(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.onHideLoadingView();
                view.onTelephoneNumberSendFailed(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().forgetPassword(phone));
        compositeDisposable.add(disposable);
    }

    @Override
    public void sendCode( String code) {
        boolean is_contain_error = false;
        if(code.length()<4){
            is_contain_error = true;
            view.onSmsCodeError(R.string.code_length_error);
        }
        if (!is_contain_error)
            checkSmsCode(sendedPhone, code);
    }

    private void checkSmsCode(String phone,String code) {
        view.onShowLoadingView();
        Disposable forget_password = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                view.onHideLoadingView();
                view.onRestorePasswordSuccess(code);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                view.onHideLoadingView();
                view.onRestorePasswordFailed(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.onHideLoadingView();
                view.onRestorePasswordFailed(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().verificateCode("forget_password",phone, code));
        compositeDisposable.add(forget_password);
    }
}
