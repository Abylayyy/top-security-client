package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import kz.topsecurity.client.R;
import kz.topsecurity.client.view.registerView.fragmentsView.AdditionalRegistrationFieldsView;

public class AdditionalRegistrationFieldsPresenterImpl implements  AdditionalRegistrationFieldsPresenter{

    AdditionalRegistrationFieldsView view;

    public AdditionalRegistrationFieldsPresenterImpl(AdditionalRegistrationFieldsView view){
        this.view = view;
    }

    @Override
    public void checkFields(String email, String userName) {
        boolean is_contain_error = false;
        if(email!=null && !email.isEmpty())
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
        if(!is_contain_error){
            view.onFieldsCorrect(email,userName);
        }
        else {
            view.onLockRegisterButton();
            view.onFieldsWrong(R.string.field_error);
        }
    }
}
