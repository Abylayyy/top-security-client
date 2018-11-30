package kz.topsecurity.client.fragments.register.signUpFields;

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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.presenter.registerPresenter.RegisterPresenter;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.RegisterSignFieldsPresenter;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.RegisterSignFieldsPresenterImpl;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.registerView.fragmentsView.RegisterSignFieldsView;

public class RegisterSignFieldsFragment extends Fragment implements RegisterSignFieldsView,StatusListener {

    @BindView(R.id.tv_telephone_number_label) TextView tv_telephone_number_label;
    @BindView(R.id.ed_tel_number) RoundCorneredEditTextWithPhoneMask ed_tel_number;
    @BindView(R.id.tv_phone_number_error) TextView tv_phone_number_error;
    @BindView(R.id.tv_password) TextView tv_password;
    @BindView(R.id.ed_password) RoundCorneredEditText ed_password;
    @BindView(R.id.tv_password_error) TextView tv_password_error;
    @BindView(R.id.tv_confirm_password) TextView tv_confirm_password;
    @BindView(R.id.ed_confirm_password) RoundCorneredEditText ed_confirm_password;
    @BindView(R.id.tv_confirm_password_error) TextView tv_confirm_password_error;
    @BindView(R.id.btn_sign_up) Button btn_sign_up;
    RegisterSignFieldsFragmentCallback mCallback;
    RoundCorneredEditTextHelper phoneNumber_helper,userConfirmPassword_helper,userPassword_helper;
    RegisterSignFieldsPresenter presenter;

    public interface RegisterSignFieldsFragmentCallback{
         void onFieldsCorrect(String phone, String password);
         void showToast(int msg);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (RegisterSignFieldsFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public static RegisterSignFieldsFragment newInstance() {
        return new RegisterSignFieldsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.register_sign_fields_fragment, container, false);
        ButterKnife.bind(inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new RegisterSignFieldsPresenterImpl(this);

        phoneNumber_helper = new RoundCorneredEditTextHelper(this,ed_tel_number,tv_telephone_number_label,tv_phone_number_error);
        userPassword_helper = new RoundCorneredEditTextHelper(this , ed_password , tv_password , tv_password_error);
        userConfirmPassword_helper = new RoundCorneredEditTextHelper(this , ed_confirm_password , tv_confirm_password , tv_confirm_password_error);

        phoneNumber_helper.setMandatory();
        userPassword_helper.setMandatory();
        userConfirmPassword_helper.setMandatory();

        phoneNumber_helper.init(getActivity());
        userPassword_helper.init(getActivity());
        userConfirmPassword_helper.init(getActivity());

        btn_sign_up.setOnClickListener(v->{
            presenter
                    .checkFields(ed_tel_number.getRawText(),
                    ed_tel_number.getText().toString(),
                    ed_password.getText().toString(),
                    ed_confirm_password.getText().toString());
        });
    }

    @Override
    public void onPhoneFieldError(int error) {
        phoneNumber_helper.setError(getString(error));
    }

    @Override
    public void onPasswordError(int error) {
        userPassword_helper.setError(getString(error));
    }

    @Override
    public void onConfirmPasswordError(int error) {
        userConfirmPassword_helper.setError(getString(error));
    }

    @Override
    public void onLockRegisterButton() {
        btn_sign_up.setEnabled(false);
    }

    @Override
    public void onFieldsWrong(int error) {
        if(mCallback!=null){
            mCallback.showToast(error);
        }
    }

    @Override
    public void onFieldsCorrect(String phone, String password) {
        if(mCallback!=null)
            mCallback.onFieldsCorrect(phone,password);
    }

    @Override
    public void onErrorDeactivated() {
        btn_sign_up.setEnabled(true);
    }
}
