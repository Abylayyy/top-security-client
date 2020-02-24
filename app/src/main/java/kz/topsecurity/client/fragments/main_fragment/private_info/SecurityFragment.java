package kz.topsecurity.client.fragments.main_fragment.private_info;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.pincodeScreen.PinCodeActivity;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */

public class SecurityFragment extends Fragment {
    private String uri;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SecurityFragment() {}

    @BindView(R.id.changePassInfo) TextView changePassword;
    @BindView(R.id.backSecurity) LinearLayout back;

    @BindView(R.id.errCurrentPass) TextView erCurrent;
    @BindView(R.id.errNewPass) TextView erNew;
    @BindView(R.id.errRepeatPass) TextView erRepeat;
    @BindView(R.id.deletePin) TextView deletePin;

    @BindView(R.id.changeCurrentPass) RoundCorneredEditText currPass;
    @BindView(R.id.newCurrentPass) RoundCorneredEditText newCurrPass;
    @BindView(R.id.repeatCurrentPass) RoundCorneredEditText repeatCurrPass;
    @BindView(R.id.profPageImage)
    CircleImageView profImage;

    @BindView(R.id.switchPinCode) Switch pinCodeSwitch;

    @BindView(R.id.view) View v1;
    @BindView(R.id.view4) View v3;
    @BindView(R.id.view5) View v4;

    View[] views, erViews;
    RoundCorneredEditText[] texts;
    int pincode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        views = new View[] {v1, v4, v3};
        erViews = new View[] {erCurrent, erNew, erRepeat};
        texts = new RoundCorneredEditText[] {currPass, newCurrPass, repeatCurrPass};

        setClientAvatar();

        back.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        changePassword.setOnClickListener(v -> {
            if (changePassword.getText().equals("Изменить")) {
                changeProfInfo();
            } else {
                saveAllInfo();
            }
        });

        loadPincode();

        if (pincode == 1) {
            pinCodeSwitch.setChecked(true);
        } else {
            pinCodeSwitch.setChecked(false);
        }

        pinCodeSwitch.setOnClickListener(v -> {
            if (pincode == 0) {
                Intent newIntent = new Intent(getActivity(), PinCodeActivity.class);
                startActivity(newIntent);
            } else {
                Intent resetIntent = new Intent(getActivity(), PinCodeActivity.class);
                resetIntent.putExtra("actionPin", "resetPin");
                startActivity(resetIntent);
            }
        });

        for (int i = 0; i < texts.length; i++) {
            int j = i;
            texts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setColor(views[j], "b6b6b6");
                    setGone(erViews[j]);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void loadPincode() {
        pincode = SharedPreferencesManager.getUserPinExist(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPincode();

        if (pincode == 1) {
            pinCodeSwitch.setChecked(true);
        } else {
            pinCodeSwitch.setChecked(false);
        }
    }

    private void setClientAvatar() {
        DataBaseManager dataBaseManager = new DataBaseManagerImpl(getActivity());
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();

        if(clientData!=null){
            userAvatar = clientData.getPhoto();
        }
        GlideApp.with(getActivity())
                .load(userAvatar)
                .placeholder(R.drawable.big_prof_image)
                .into(profImage);
    }

    private void saveAllInfo() {
        if (checkAllFields(
                currPass.getText().toString().trim(),
                newCurrPass.getText().toString().trim(),
                repeatCurrPass.getText().toString().trim()
        )) {
            changePassword.setText("Изменить");
            changePassword.setTextColor(Color.parseColor("#1F9900"));

            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
            }
            for (RoundCorneredEditText text : texts) {
                text.setEnabled(false);
            }
        }
    }

    private boolean checkAllFields(String currPass, String newPass, String repeatPass) {
        boolean check = false;
        String oldPass = SharedPreferencesManager.getUserPassword(getContext());
        String[] results = {"error", "error", "error"};

        if (TextUtils.isEmpty(currPass) || !currPass.equals(oldPass)) { setVisible(erCurrent); setColor(v1, "EF3B39");} else {
            results[0] = "correct"; setGone(erCurrent); setColor(v1, "b6b6b6");
        }
        if (TextUtils.isEmpty(newPass) || newPass.length() < 6) { setVisible(erNew); setColor(v4, "EF3B39"); } else {
            results[1] = "correct"; setGone(erNew); setColor(v4, "b6b6b6");
        }
        if (!repeatPass.equals(newPass)) { setVisible(erRepeat); setColor(v3, "EF3B39"); } else {
            results[2] = "correct"; setGone(erRepeat); setColor(v3, "b6b6b6");
        }
        if (results[0].equals("correct") && results[1].equals("correct") && results[2].equals("correct")){
            makeChangePassword(currPass, newPass, repeatPass);
            check = true;
        }
        return check;
    }

    private void makeChangePassword(String curr, String new_password, String password_confirmation) {

        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse data) {
                showDialog("Пароль успешно сохранен");
                SharedPreferencesManager.setUserPassword(getContext(), new_password);
                clear();
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                showDialog("Не удалось изменить пароль");
            }

            @Override
            public void onError(Throwable e, int error_message) {
                showDialog("Ошибка при изменении пароля");
            }

        }).makeRequest(RetrofitClient.getClientApi().changePassword(RetrofitClient.getRequestToken(), curr, new_password, password_confirmation));
        compositeDisposable.add(disposable);
    }

    private void clear() {
        currPass.setText("");
        newCurrPass.setText("");
        repeatCurrPass.setText("");
    }

    private void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    private void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setColor(View view, String color) {
        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    private void changeProfInfo() {
        changePassword.setText("Сохранить");
        changePassword.setTextColor(Color.parseColor("#EF3B39"));

        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
        for (RoundCorneredEditText text : texts) {
            text.setEnabled(true);
        }
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton("ОК", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


}
