package kz.topsecurity.client.fragments.register.usernameFields;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.UserNameFieldsPresenterImpl;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.registerView.fragmentsView.UserNameFieldsView;

public class UserNameFieldsFragment extends Fragment implements UserNameFieldsView,StatusListener {

    UserNameFieldsPresenterImpl presenter;

    @BindView(R.id.tv_firstname) TextView tv_firstname;
    @BindView(R.id.ed_firstname) RoundCorneredEditText ed_firstname;
    @BindView(R.id.tv_firstname_error) TextView tv_firstname_error;

    @BindView(R.id.tv_lastname) TextView tv_lastname;
    @BindView(R.id.ed_lastname) RoundCorneredEditText ed_lastname;
    @BindView(R.id.tv_lastname_error) TextView tv_lastname_error;

    @BindView(R.id.tv_patronymic) TextView tv_patronymic;
    @BindView(R.id.ed_patronymic) RoundCorneredEditText ed_patronymic;
    @BindView(R.id.tv_patronymic_error) TextView tv_patronymic_error;

    @BindView(R.id.btn_sign_up) Button btn_sign_up;

    RoundCorneredEditTextHelper firstname_helper,lastname_helper, patronymic_helper;

    UserNameFieldsCallback mCallback = null;

    public interface UserNameFieldsCallback{
        void onUserNameFieldsCorrect(String firstname, String lastname, String patronymic);
        void onClosed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (UserNameFieldsCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public static UserNameFieldsFragment newInstance() {
        return new UserNameFieldsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.user_name_fields_fragment, container, false);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new UserNameFieldsPresenterImpl(this);
        firstname_helper = new RoundCorneredEditTextHelper(this , ed_firstname , tv_firstname, tv_firstname_error);
        lastname_helper = new RoundCorneredEditTextHelper(this , ed_lastname, tv_lastname, tv_lastname_error);
        patronymic_helper = new RoundCorneredEditTextHelper(this , ed_patronymic, tv_patronymic, tv_patronymic_error);
        firstname_helper.setMandatory();
        lastname_helper.setMandatory();
        firstname_helper.init(getActivity());
        lastname_helper.init(getActivity());
        patronymic_helper.init(getActivity());

        btn_sign_up.setOnClickListener(v->{
            presenter.checkFields(ed_firstname.getText().toString(),ed_lastname.getText().toString(),ed_patronymic.getText().toString());
        });
    }

    @Override
    public void onFirstNameFieldError(int error) {
        firstname_helper.setError(getString(error));
    }

    @Override
    public void onLastNameFieldError(int error) {
        lastname_helper.setError(getString(error));
    }

    @Override
    public void onPatronymicFieldError(int error) {
        patronymic_helper.setError(getString(error));
    }

    @Override
    public void onLockRegisterButton() {

    }

    @Override
    public void onFieldsWrong(int error) {
    }

    @Override
    public void onFieldsCorrect(String firstname, String lastname, String patronymic) {
        if(mCallback!=null){
            mCallback.onUserNameFieldsCorrect(firstname, lastname, patronymic);
        }
    }

    @Override
    public void onErrorDeactivated() {

    }


}
