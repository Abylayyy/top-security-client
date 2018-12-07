package kz.topsecurity.client.fragments.register.additionalFields;

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

    @BindView(R.id.tv_user_IIN) TextView tv_user_IIN;
    @BindView(R.id.ed_user_IIN) RoundCorneredEditText ed_user_IIN;
    @BindView(R.id.tv_user_IIN_error) TextView tv_user_IIN_error;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.ed_email) RoundCorneredEditText ed_email;
    @BindView(R.id.tv_email_error) TextView tv_email_error;
    @BindView(R.id.btn_sign_up)
    Button btn_sign_up;

    RoundCorneredEditTextHelper user_IIN_helper,userEmail_helper;
    AdditionalRegistrationFieldsPresenter presenter;

    AdditionalRegistrationFieldsCallback mCallback;

    public interface AdditionalRegistrationFieldsCallback {
        void onAdditionalFieldsCorrect(String userEmail, String userName);
        void onClosed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (AdditionalRegistrationFieldsCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public static AdditionalRegistrationFieldsFragment newInstance() {
        return new AdditionalRegistrationFieldsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.additional_registration_fields_fragment, container, false);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new AdditionalRegistrationFieldsPresenterImpl(this);
        user_IIN_helper = new RoundCorneredEditTextHelper(this , ed_user_IIN , tv_user_IIN, tv_user_IIN_error);
        userEmail_helper = new RoundCorneredEditTextHelper(this , ed_email , tv_email, tv_email_error);
        user_IIN_helper.setMandatory();
        userEmail_helper.setMandatory();
        user_IIN_helper.init(getActivity());
        userEmail_helper.init(getActivity());
        btn_sign_up.setOnClickListener(v->{
            presenter.checkFields(ed_email.getText().toString(),ed_user_IIN.getText().toString());
        });
    }

    @Override
    public void onErrorDeactivated() {

    }

    @Override
    public void onUser_IIN_Error(int error) {
        user_IIN_helper.setError(getString(error));
    }

    @Override
    public void onEmailError(int error) {
        userEmail_helper.setError(getString(error));
    }

    @Override
    public void onLockRegisterButton() {

    }

    @Override
    public void onFieldsCorrect(String userEmail, String userIIN) {
        if (mCallback!=null)
            mCallback.onAdditionalFieldsCorrect(userEmail,userIIN);
    }

    @Override
    public void onFieldsWrong(int error) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if(presenter!=null)
            presenter.detach();
    }
}
