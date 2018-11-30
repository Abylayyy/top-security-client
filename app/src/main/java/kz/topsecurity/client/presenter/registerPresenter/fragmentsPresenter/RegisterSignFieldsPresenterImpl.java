package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import kz.topsecurity.client.R;
import kz.topsecurity.client.view.registerView.fragmentsView.RegisterSignFieldsView;

public class RegisterSignFieldsPresenterImpl implements RegisterSignFieldsPresenter {

    RegisterSignFieldsView view;

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
            view.onFieldsCorrect(phone,password);
        }
        else{
            view.onLockRegisterButton();
            view.onFieldsWrong(R.string.field_error);
        }
    }

}
