package kz.topsecurity.client.fragments.register.additionalFields;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.AdditionalRegistrationFieldsPresenter;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.AdditionalRegistrationFieldsPresenterImpl;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextHelper;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.StatusListener;
import kz.topsecurity.client.view.registerView.fragmentsView.AdditionalRegistrationFieldsView;

public class AdditionalRegistrationFieldsFragment extends Fragment implements AdditionalRegistrationFieldsView, StatusListener {

    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.ed_username) RoundCorneredEditText ed_username;
    @BindView(R.id.tv_user_name_error) TextView tv_user_name_error;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.ed_email) RoundCorneredEditText ed_email;
    @BindView(R.id.tv_email_error) TextView tv_email_error;
    @BindView(R.id.tv_privacy_policy) TextView tv_privacy_policy;
    @BindView(R.id.cb_privacy_policy) CheckBox cb_privacy_policy;

    RoundCorneredEditTextHelper userName_helper,userEmail_helper;
    AdditionalRegistrationFieldsPresenter presenter;

    public interface AdditionalRegistrationFieldsCallback {
        void onFieldsCorrect(String userEmail, String userName);
    }


    public static AdditionalRegistrationFieldsFragment newInstance() {
        return new AdditionalRegistrationFieldsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.additional_registration_fields_fragment, container, false);
        ButterKnife.bind(inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new AdditionalRegistrationFieldsPresenterImpl(this);
        userName_helper = new RoundCorneredEditTextHelper(this , ed_username , tv_user_name, tv_user_name_error);
        userEmail_helper = new RoundCorneredEditTextHelper(this , ed_email , tv_email, tv_email_error);
        userName_helper.setMandatory();
        userName_helper.init(getActivity());
        userEmail_helper.init(getActivity());
    }

    @Override
    public void onErrorDeactivated() {

    }

    @Override
    public void onUserNameError(int error) {

    }

    @Override
    public void onEmailError(int error) {

    }

    @Override
    public void onLockRegisterButton() {

    }

    @Override
    public void onFieldsCorrect(String userEmail, String userName) {

    }

    @Override
    public void onFieldsWrong(int error) {

    }
}
