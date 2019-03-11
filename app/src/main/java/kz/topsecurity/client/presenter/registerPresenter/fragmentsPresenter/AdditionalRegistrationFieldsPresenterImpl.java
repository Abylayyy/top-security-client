package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import io.reactivex.disposables.CompositeDisposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.TextValidator;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.registerView.fragmentsView.AdditionalRegistrationFieldsView;

public class AdditionalRegistrationFieldsPresenterImpl implements  AdditionalRegistrationFieldsPresenter{

    AdditionalRegistrationFieldsView view;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AdditionalRegistrationFieldsPresenterImpl(AdditionalRegistrationFieldsView view){
        this.view = view;
    }

    @Override
    public void checkFields(String email, String user_IIN) {
        boolean is_contain_error = false;
        if(email!=null && !email.isEmpty())
            if(!email.contains("@")||!email.contains(".") )
            {
                view.onEmailError(R.string.email_format_error);
                is_contain_error = true;
            }
        if(user_IIN.isEmpty())
        {
            view.onUser_IIN_Error(R.string.field_cant_be_empty);
            is_contain_error = true;
        }
        else if(user_IIN.length()<12){
            view.onUser_IIN_Error(R.string.user_iin_length_error);
            is_contain_error = true;
        }
        else if(!TextValidator.checkIIN(user_IIN)){
            view.onUser_IIN_Error(R.string.user_iin_format);
            is_contain_error = true;
        }
        if(!is_contain_error){
            checkEmail(email,user_IIN);
        }
        else {
            view.onLockRegisterButton();
            view.onFieldsWrong(R.string.field_error);
        }
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }

    private void checkEmail(String email, String userIIN) {
        new RequestService<BasicResponse>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                view.onFieldsCorrect(email,userIIN);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                view.onEmailError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.onEmailError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi().checkEmail(email));
    }
}
