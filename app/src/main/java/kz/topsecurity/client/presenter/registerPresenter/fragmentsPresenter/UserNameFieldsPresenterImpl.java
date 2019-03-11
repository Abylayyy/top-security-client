package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import kz.topsecurity.client.R;
import kz.topsecurity.client.view.registerView.fragmentsView.UserNameFieldsView;

public class UserNameFieldsPresenterImpl implements UserNameFieldsPresenter {
    UserNameFieldsView view;

    public UserNameFieldsPresenterImpl(UserNameFieldsView view){
        this.view = view;
    }

    @Override
    public void checkFields(String firstname, String lastname, String patronymic) {
        boolean is_contain_error = false;
        if(firstname.isEmpty()){
            view.onFirstNameFieldError(R.string.firstNameFieldCantBeEmpty);
            is_contain_error = true;
        }
        if(lastname.isEmpty()){
            view.onLastNameFieldError(R.string.lastNameFieldCantBeEmpty);
            is_contain_error = true;
        }
//        if(patronymic.isEmpty()){
//            view.onPatronymicFieldError(R.string.patronymicFieldCantBeEmpty);
//            is_contain_error = true;
//        }
        if(is_contain_error){
            view.onFieldsWrong(R.string.field_error);
        }
        else{
            view.onFieldsCorrect(firstname,lastname,patronymic);
        }
    }
}
