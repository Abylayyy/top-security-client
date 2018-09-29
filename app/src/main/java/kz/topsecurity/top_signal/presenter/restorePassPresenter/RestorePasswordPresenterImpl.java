package kz.topsecurity.top_signal.presenter.restorePassPresenter;

import io.reactivex.disposables.Disposable;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.model.other.BasicResponse;
import kz.topsecurity.top_signal.presenter.base.BasePresenterImpl;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.view.restorePassView.RestorePasswordView;

public class RestorePasswordPresenterImpl extends BasePresenterImpl<RestorePasswordView> implements RestorePasswordPresenter {

    public RestorePasswordPresenterImpl(RestorePasswordView view) {
        super(view);
    }

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
    public void sendCode(String code) {
        boolean is_contain_error = false;
        if(code.length()<4){
            is_contain_error = true;
            view.onSmsCodeError(R.string.code_length_error);
        }
        if (!is_contain_error)
            checkSmsCode(code);
    }

    private void checkSmsCode(String code) {
        view.onShowLoadingView();
        Disposable forget_password = new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
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
        }).makeRequest(RetrofitClient.getClientApi().verificateCode("forget_password", code));
        compositeDisposable.add(forget_password);
    }
}
